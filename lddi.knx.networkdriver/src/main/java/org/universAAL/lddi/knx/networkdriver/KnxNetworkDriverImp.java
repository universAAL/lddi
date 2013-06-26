package org.universAAL.lddi.knx.networkdriver;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.devicemodel.KnxDevice;
import org.universAAL.lddi.knx.interfaces.KnxNetwork;
import org.universAAL.lddi.knx.utils.KnxCommand;

/**
 * KnxNetworkDriverImp is the main class of this bundle. It registers as a
 * service in the OSGi framework with the name
 * org.universAAL.lddi.knx.networkdriver.test.KnxNetwork. It manages a list of KNX
 * devices (injected by knx.devicemanager) where the KNX group address is used
 * as key. Incoming sensor events are passed on to the appropriate KNX device
 * (identified by KNX group address).
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 * 
 */
public final class KnxNetworkDriverImp implements ManagedService, KnxNetwork {

	/** OSGi Framework */
	BundleContext context;
	LogService logger;

	private boolean multicast;
	private String multicastIp;
	private int multicastUdpPort;
	private String knxGatewayIp;
	private int knxGatewayPort;
	private String myIp;
	private int myPort;

	KnxCommunication network;
	public ServiceRegistration regServiceKnx = null;

	/**
	 * List of devices per groupAddress key = groupAddress value = Set of
	 * devices
	 */
	private Hashtable<String, Set<KnxDevice>> deviceList;

	/**
	 * Class constructor
	 * 
	 * @param context
	 *            OSGi framework
	 * @param log
	 */
	public KnxNetworkDriverImp(BundleContext context, LogService log) {
		this.context = context;
		this.logger = log;
		this.network = null;

		this.deviceList = new Hashtable<String, Set<KnxDevice>>();

		this.registerManagedService();
		this.logger.log(LogService.LOG_DEBUG, "KnxNetworkDriverImp started!");
	}

	/***
	 * Register this class as Managed Service
	 */
	private void registerManagedService() {
		Properties propManagedService = new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle()
				.getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this,
				propManagedService);
	}

	/**
	 * Update from OSGi Configuration Manager
	 */
	public void updated(@SuppressWarnings("unchecked") Dictionary settings)
			throws ConfigurationException {
		this.logger.log(LogService.LOG_DEBUG, "KnxNetworkDriverImp.updated: "
				+ settings);

		try {
			if (settings != null) {
				this.setMulticast(Boolean.parseBoolean((String)settings.get("multicast")));
				this.setMulticastIp((String) settings.get("multicastIp"));
				this.setMulticastUdpPort(Integer.valueOf((String) settings
						.get("multicastUdpPort")));
				this.setKnxGatewayIp((String) settings.get("knxGatewayIp"));
				this.setKnxGatewayPort(Integer.valueOf((String) settings
						.get("knxGatewayPort")));
				this.setMyIp((String) settings.get("myIp"));
				this.setMyPort(Integer.valueOf((String) settings
						.get("myPort")));
				
				if (this.network != null) {
					this.unRegister();
					this.network = null;
				}

				this.network = new KnxCommunication(this);
				this.network.init();

			} else {
				this.logger.log(LogService.LOG_ERROR,
						"Unconfigured knx network driver!");
			}
		} catch (NumberFormatException nfe) {
			this.logger.log(LogService.LOG_ERROR, "NumberFormatException\n"
					+ nfe.getMessage());
		} catch (Exception e) {
			this.logger.log(LogService.LOG_ERROR, e.getMessage());
		}
	}

	public void networkConnected() {
		this.regServiceKnx = this.context.registerService(KnxNetwork.class
				.getName(), this, null);
	}

	public void networkDisconnected() {
		this.unRegister();
	}

	public LogService getLogger() {
		return this.logger;
	}


	/**
	 * Forward the message from the house to the device; mapping on groupAddress
	 * 
	 * @param groupAddress
	 *            the knx groupAddress
	 * @param b
	 *            knx command/status bytes (representing e.g. on, off)
	 */
	public void newMessageFromHouse(String groupAddress, byte[] event) {
		if (this.deviceList.containsKey(groupAddress)) {
			synchronized (this.deviceList) {
				for (KnxDevice device : this.deviceList.get(groupAddress)) {
					device.newMessageFromHouse(groupAddress, event);
				}
			}
		} else {
			this.logger.log(LogService.LOG_WARNING, "No device available for incoming message to " + groupAddress);
		}
	}

	public void unRegister() {
		// stop reader thread
		this.network.stopCommunication();

		// unregister network service
		if (this.regServiceKnx != null) {
			this.regServiceKnx.unregister();
			this.regServiceKnx = null;
		}
	}

	/**
	 * Devices can register here to get events from the knx bus
	 */
	public void addDevice(String deviceId, KnxDevice device) {

		Set<KnxDevice> devices = this.deviceList.get(deviceId);
		if (devices == null) {
			devices = new HashSet<KnxDevice>();

			synchronized (this.deviceList) {
				this.deviceList.put(deviceId, devices);
			}
		}
		devices.add(device);
		this.logger.log(LogService.LOG_DEBUG,
				"New device added for groupAddress " + deviceId);
	}

	/**
	 * Devices can unregister here to stop getting events from the knx bus
	 */
	public void removeDevice(String deviceId, KnxDevice device) {
		Set<KnxDevice> devices = this.deviceList.get(deviceId);
		devices.remove(device);
		this.logger.log(LogService.LOG_INFO, "Removed device for groupAddress "
				+ deviceId);
	}

	public void sendCommand(String deviceId, boolean command) {
		this.sendCommand(deviceId, command, KnxCommand.VALUE_WRITE);

	}

	public void sendCommand(String device, boolean command,
			KnxCommand commandType) {
		this.network.sendCommand(device, command, commandType);

	}

	public void requestState(String deviceId) {
		this.network.readState(deviceId);

	}

	/**
	 * @return the multicast
	 */
	public boolean isMulticast() {
		return multicast;
	}

	/**
	 * @param multicast the multicast to set
	 */
	public void setMulticast(boolean multicast) {
		this.multicast = multicast;
	}
	
	/**
	 * @param knxGatewayIp the knxGatewayIp to set
	 */
	public void setKnxGatewayIp(String knxGatewayIp) {
		this.knxGatewayIp = knxGatewayIp;
	}

	/**
	 * @return the knxGatewayIp
	 */
	public String getKnxGatewayIp() {
		return knxGatewayIp;
	}

	/**
	 * @param knxGatewayPort the knxGatewayPort to set
	 */
	public void setKnxGatewayPort(int knxGatewayPort) {
		this.knxGatewayPort = knxGatewayPort;
	}

	/**
	 * @return the knxGatewayPort
	 */
	public int getKnxGatewayPort() {
		return knxGatewayPort;
	}

	/**
	 * @param myIp the myIp to set
	 */
	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}

	/**
	 * @return the myIp
	 */
	public String getMyIp() {
		return myIp;
	}

	/**
	 * @return the myPort
	 */
	public int getMyPort() {
		return myPort;
	}

	/**
	 * @param myPort the myPort to set
	 */
	public void setMyPort(int myPort) {
		this.myPort = myPort;
	}

	public String getMulticastIp() {
		return this.multicastIp;
	}

	/**
	 * @param multicastIp the multicastIp to set
	 */
	public void setMulticastIp(String multicastIp) {
		this.multicastIp = multicastIp;
	}

	/**
	 * @return the multicastUdpPort
	 */
	public int getMulticastUdpPort() {
		return multicastUdpPort;
	}

	/**
	 * @param multicastUdpPort the multicastUdpPort to set
	 */
	public void setMulticastUdpPort(int multicastUdpPort) {
		this.multicastUdpPort = multicastUdpPort;
	}

}

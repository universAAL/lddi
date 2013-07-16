/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
     See the NOTICE file distributed with this work for additional
     information regarding copyright ownership

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

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
import org.universAAL.lddi.knx.groupdevicemodel.KnxGroupDevice;
import org.universAAL.lddi.knx.interfaces.IKnxNetwork;
import org.universAAL.lddi.knx.utils.KnxCommand;
import org.universAAL.lddi.knx.utils.KnxEncoder;

/**
 * KnxNetworkDriverImp is the main class of this bundle. It registers as a
 * service in the OSGi framework with the name
 * org.universAAL.lddi.knx.networkdriver.test.KnxNetwork. It manages a list of KNX
 * devices (injected by knx.devicemanager) where the KNX group address is used
 * as key. Incoming sensor events are passed on to the appropriate KNX groupDevice
 * (identified by KNX group address).
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 * 
 */
public final class KnxNetworkDriverImp implements ManagedService, IKnxNetwork {

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
	private Hashtable<String, Set<KnxGroupDevice>> groupDeviceList;

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

		this.groupDeviceList = new Hashtable<String, Set<KnxGroupDevice>>();

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
		this.regServiceKnx = this.context.registerService(IKnxNetwork.class
				.getName(), this, null);
	}

	public void networkDisconnected() {
		this.unRegister();
	}

	public LogService getLogger() {
		return this.logger;
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
	public void addGroupDevice(String deviceId, KnxGroupDevice device) {

		Set<KnxGroupDevice> devices = this.groupDeviceList.get(deviceId);
		if (devices == null) {
			devices = new HashSet<KnxGroupDevice>();

			synchronized (this.groupDeviceList) {
				this.groupDeviceList.put(deviceId, devices);
			}
		}
		devices.add(device);
		this.logger.log(LogService.LOG_DEBUG,
				"New groupDevice added for groupAddress " + deviceId);
	}

	/**
	 * Devices can unregister here to stop getting events from the knx bus
	 */
	public void removeGroupDevice(String deviceId, KnxGroupDevice device) {
		Set<KnxGroupDevice> devices = this.groupDeviceList.get(deviceId);
		devices.remove(device);
		this.logger.log(LogService.LOG_INFO, "Removed groupDevice for groupAddress "
				+ deviceId);
	}


	
	
	


	/**
	 * Forward the message from the house to the groupDevice; mapping on groupAddress
	 * 
	 * @param groupAddress
	 *            the knx groupAddress
	 * @param b
	 *            knx command/status bytes (representing e.g. on, off)
	 */
	public void newMessageFromHouse(String groupAddress, byte[] event) {
		if (this.groupDeviceList.containsKey(groupAddress)) {
			synchronized (this.groupDeviceList) {
				for (KnxGroupDevice device : this.groupDeviceList.get(groupAddress)) {
					device.newMessageFromKnxBus(event);
				}
			}
		} else {
			this.logger.log(LogService.LOG_WARNING, "No groupDevice available for incoming message to " + groupAddress);
		}
	}

	
	/** {@inheritDoc} */
	public void sendMessageToKnxBus(String groupDeviceId, byte[] event) {
		this.logger.log(LogService.LOG_INFO,
				"Got new payload " + KnxEncoder.convertToReadableHex(event) + 
				" for groupDevice " + groupDeviceId + ". Send it to KNX Bus.");

		// TODO: send message to knx bus
		// for now use boolean (dpt1) method
		// extend send methods to use byte[] !!
		
//		if (event.length == 1) {
//			// DPT = 1
//			if (event[0] == 0x0) {
//				this.sendCommand(groupDeviceId, false);
//			} else if (event[0] == 0x1) {
//				this.sendCommand(groupDeviceId, true);
//			} else {
//				this.logger.log(LogService.LOG_ERROR, "Event is wheter 0 nor 1 but has databyte length 1 (DPT1)." +
//						" Not sending to KNX bus! Group Address: " + groupDeviceId);
//				return;
//			}
//		} else {
			// forward command
			this.network.sendCommand(groupDeviceId, event, KnxCommand.VALUE_WRITE);
//		}
	}
	
	// for manual command on OSGi shell
	public void sendCommand(String deviceId, boolean command) {
		this.sendCommand(deviceId, command, KnxCommand.VALUE_WRITE);
	}
	// for manual command on OSGi shell
	public void sendCommand(String device, boolean command, KnxCommand commandType) {
		byte status = 0x0;
		if (command)
			status = 0x1;
		this.network.sendCommand(device, new byte[] {status}, commandType);
	}

//	public void sendCommand(String deviceId, byte[] command) {
//		this.sendCommand(deviceId, command, KnxCommand.VALUE_WRITE);
//	}
//	public void sendCommand(String device, byte[] command, KnxCommand commandType) {
//		this.network.sendCommand(device, command, commandType);
//	}
	
	
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

package org.universAAL.lddi.knx.devicemanager;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.universAAL.lddi.knx.devicemodel.KnxDevice;
import org.universAAL.lddi.knx.devicemodel.KnxDeviceFactory;
import org.universAAL.lddi.knx.interfaces.KnxNetwork;
import org.universAAL.lddi.knx.utils.KnxGroupAddress;

/**
 * This bundle tracks on KnxNetwork service.
 * When this service appears, this bundle is initialized.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDeviceManager implements ManagedService, ServiceTrackerCustomizer {

	private BundleContext context;
	private LogService logger;
	private String knxConfigFile;
	private List<KnxGroupAddress> knxImportedGroupAddresses;
	
	/**
	 * List of registered devices
	 * key = groupAddress
	 * value = org.osgi.framework.ServiceRegistration
	 */
	private Map<String,ServiceRegistration> deviceRegistrationList;

	private Map<String,KnxDevice> deviceList;

	String filterQuery=String.format("(%s=%s)", org.osgi.framework.Constants.OBJECTCLASS,KnxNetwork.class.getName());
	private KnxNetwork network;
	
	public KnxDeviceManager(BundleContext context, LogService log) {
		this.context=context;
		this.logger=log;

		// track on KnxNetwork service
		try {
			ServiceTracker st=new ServiceTracker(context,this.context.createFilter(filterQuery), this);
			st.open();
		} catch (InvalidSyntaxException e) {
			this.logger.log(LogService.LOG_ERROR, "exception",e);
			e.printStackTrace();
		}
	}
	
	/**
	 * KnxNetwork service appeared, initialization of this bundle;
	 * ManagedService registration in OSGi.
	 */
	public Object addingService(ServiceReference reference) {
		this.network=(KnxNetwork)this.context.getService(reference);

		// create my lists
		this.deviceRegistrationList = new HashMap<String,ServiceRegistration>();
		this.deviceList = new HashMap<String, KnxDevice>();

		this.registerManagedService();
		this.logger.log(LogService.LOG_DEBUG,"KnxDeviceManager started!");
		return reference;
	}
	
	/***
	 * Register this class as Managed Service.
	 */
	private void registerManagedService() {
		Properties propManagedService=new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle().getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
	}
	
	/**
	 * KnxNetwork service has been modified: removing my managed service and adding again.
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		removedService(reference, service);
		addingService(reference);
		this.logger.log(LogService.LOG_INFO,"KnxDeviceManager restarted because KnxNetwork service was modified!");
	}

	/**
	 * KnxNetwork service has been removed: removing my managed service,
	 * clear storage objects -> set this bundle to "idle" mode.
	 */
	public void removedService(ServiceReference reference, Object service) {
		// When knx.networkservice disappears: unregister all my devices
		if ( this.deviceRegistrationList != null ) {
			for (ServiceRegistration servReg : this.deviceRegistrationList.values()) {
				servReg.unregister();
			}
		}
		//clear lists
		this.deviceRegistrationList = null;
		this.deviceList = null;
		
		this.context.ungetService(reference);
//		this.unregisterManagedService();
		this.logger.log(LogService.LOG_WARNING,"KnxDeviceManager stopped because KnxNetwork service was removed!");
	}

//	private void unregisterManagedService() {
//		this.myManagedServiceRegistration.unregister();
//	}
	
	/***
	 * Get updated from ConfigurationAdmin:
	 * get configuration file from ETS4,
	 * extract groupAddress information,
	 * create virtual KNX devices,
	 * and register them as device services in OSGi. 
	 */
	@SuppressWarnings("unchecked")
	public void updated(Dictionary properties) throws ConfigurationException {
		this.logger.log(LogService.LOG_DEBUG, "KnxDeviceManager.updated: " + properties);

		if (properties != null){
			this.knxConfigFile = (String) properties.get("knxConfigFile");

			try {

				if (knxConfigFile != null && knxConfigFile != "") {
					InputStream is = new FileInputStream(knxConfigFile);
					this.knxImportedGroupAddresses = new KnxImporter(logger).importETS4Configuration(is);
					if ( this.knxImportedGroupAddresses.isEmpty() ) {
						this.logger.log(LogService.LOG_WARNING,
								"No KNX devices found in configuration!!!!");
					} else {
						this.logger.log(LogService.LOG_DEBUG,
								"Knx devices found in configuration: "
										+ this.knxImportedGroupAddresses
												.toString());
					}
					
					//Step through device list
					for ( KnxGroupAddress knxGroupAddress : knxImportedGroupAddresses ) {
						
						if ( checkKnxGroupAddress(knxGroupAddress) ) {
							
							ServiceRegistration knxGA = this.deviceRegistrationList.get(knxGroupAddress.getGroupAddress());
							if ( knxGA != null ) {
								// device service is already registered
								// unregister
								knxGA.unregister();
								// and delete from list
								this.deviceRegistrationList.remove(knxGroupAddress.getGroupAddress());
							}
							
							int dptMainNumber = Integer.parseInt(knxGroupAddress.getDptMain());

							// create appropriate device from dpt main number
							KnxDevice knxDevice = KnxDeviceFactory.getKnxDevice(dptMainNumber);
							
							// startover with next device if no appropriate KnxDevice implementation found!
							if (knxDevice==null) {
								this.logger.log(LogService.LOG_WARNING, "KNX data type " +
										knxGroupAddress.getDpt() + " is not supported yet!" +
										" Skipping device " + knxGroupAddress.getGroupAddress() + "!");
								continue;
							}
							
							// set instance alive
							knxDevice.setParams(knxGroupAddress, this.network, this.logger);
							
							// register device in OSGi registry
							Properties propDeviceService=new Properties();

							propDeviceService.put(
									org.osgi.service.device.Constants.DEVICE_CATEGORY, 
									knxDevice.getDeviceCategory().toString());
							// more possible properties: description, serial, id
							
							ServiceRegistration deviceServiceReg = this.context.registerService(
									org.osgi.service.device.Device.class.getName(), knxDevice, 
									propDeviceService);
							
							this.logger.log(LogService.LOG_INFO, "Registered KNX device " +
									knxDevice.getDeviceId() + " (" + knxDevice.getDeviceLocationType() +
									": " + knxDevice.getDeviceLocation() + ") in OSGi registry under " +
									"device category: " + knxDevice.getDeviceCategory());
							
							// save this device registration to my list
							this.deviceRegistrationList.put(knxGroupAddress.getGroupAddress(),deviceServiceReg);
							this.deviceList.put(knxGroupAddress.getGroupAddress(), knxDevice);
							
						} else {
							this.logger.log(LogService.LOG_ERROR, "KNX device with group address " +
									knxGroupAddress.getGroupAddress() + " has incorrect DPT property.");
						}
					}
				} else {
					this.logger.log(LogService.LOG_ERROR, "KNX configuration file name is empty!");
				}

			} catch (FileNotFoundException e) {
				this.logger.log(LogService.LOG_ERROR, "KNX configuration xml file " +
						knxConfigFile + " could not be opened!");
				e.printStackTrace();
			} catch (NumberFormatException e) {
				this.logger.log(LogService.LOG_ERROR, "Parsing KNX datapoint type failed!");
				e.printStackTrace();
			} catch (Exception e) {
				this.logger.log(LogService.LOG_ERROR, "Ups, something went wrong....");
				e.printStackTrace();
			}

		} else {
			this.logger.log(LogService.LOG_ERROR, "Property file for knx.devicemanager not found!");
		}
		
	}

	/**
	 * check for null properties
	 * @param knxGroupAddress
	 * @return true if OK
	 */
	private boolean checkKnxGroupAddress(KnxGroupAddress knxGroupAddress) {
		if ( knxGroupAddress.getDpt() != null && knxGroupAddress.getDpt().contains(".") &&
				knxGroupAddress.getGroupAddress() != null && knxGroupAddress.getGroupAddress().contains("/")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * remove all device references in network driver
	 */
	public void stop() {
		
		//this.unregisterManagedService();	is done automatically during bundle stop

		if ( this.deviceList != null ) {
			Iterator<String> it = this.deviceList.keySet().iterator();
			while ( it.hasNext() ) {
				String deviceId = it.next();
				KnxDevice dev = this.deviceList.get(deviceId);
				this.network.removeDevice(deviceId, dev);
			}
		}
		this.logger.log(LogService.LOG_WARNING,"KnxDeviceManager stopped!");
	}

}
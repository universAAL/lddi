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

import org.universAAL.lddi.knx.groupdevicemodel.KnxGroupDevice;
import org.universAAL.lddi.knx.groupdevicemodel.KnxGroupDeviceFactory;
import org.universAAL.lddi.knx.interfaces.IKnxNetwork;
import org.universAAL.lddi.knx.utils.KnxGroupAddress;

/**
 * This bundle tracks on IKnxNetwork service.
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
	private Map<String,ServiceRegistration> groupDeviceRegistrationList;

	private Map<String,KnxGroupDevice> groupDeviceList;

	String filterQuery=String.format("(%s=%s)", org.osgi.framework.Constants.OBJECTCLASS,IKnxNetwork.class.getName());
	private IKnxNetwork network;
	
	public KnxDeviceManager(BundleContext context, LogService log) {
		this.context=context;
		this.logger=log;

		// track on IKnxNetwork service
		try {
			ServiceTracker st=new ServiceTracker(context,this.context.createFilter(filterQuery), this);
			st.open();
		} catch (InvalidSyntaxException e) {
			this.logger.log(LogService.LOG_ERROR, "ServiceTracker Problem: " + e.getMessage());
//			e.printStackTrace();
		}
	}
	
	/**
	 * IKnxNetwork service appeared, initialization of this bundle;
	 * ManagedService registration in OSGi.
	 */
	public Object addingService(ServiceReference reference) {
		this.network=(IKnxNetwork)this.context.getService(reference);

		// create my lists
		this.groupDeviceRegistrationList = new HashMap<String,ServiceRegistration>();
		this.groupDeviceList = new HashMap<String, KnxGroupDevice>();

		this.registerManagedService();
		
//		System.out.println("KnxDeviceManager started!");

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
	 * IKnxNetwork service has been modified: removing my managed service and adding again.
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		removedService(reference, service);
		addingService(reference);
		this.logger.log(LogService.LOG_INFO,"KnxDeviceManager restarted because IKnxNetwork service was modified!");
	}

	/**
	 * IKnxNetwork service has been removed: removing my managed service,
	 * clear storage objects -> set this bundle to "idle" mode.
	 */
	public void removedService(ServiceReference reference, Object service) {
		// When knx.networkservice disappears: unregister all my devices
		if ( this.groupDeviceRegistrationList != null ) {
			for (ServiceRegistration servReg : this.groupDeviceRegistrationList.values()) {
				servReg.unregister();
			}
		}
		//clear lists
		this.groupDeviceRegistrationList = null;
		this.groupDeviceList = null;
		
		this.context.ungetService(reference);
//		this.unregisterManagedService();
		this.logger.log(LogService.LOG_WARNING,"KnxDeviceManager stopped because IKnxNetwork service was removed!");
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
	 * 
	 * What if updated again all devices are removed and then the new config is processed.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public void updated(Dictionary properties) throws ConfigurationException {
		this.logger.log(LogService.LOG_DEBUG, "KnxDeviceManager.updated: " + properties);
		
		// call stop for case of update at runtime
		stop();
		
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
					
					//Step through groupDevice list
					for ( KnxGroupAddress knxGroupAddress : knxImportedGroupAddresses ) {
						
						if ( checkKnxGroupAddress(knxGroupAddress) ) {
							
							ServiceRegistration knxGA = this.groupDeviceRegistrationList.get(knxGroupAddress.getGroupAddress());
							if ( knxGA != null ) {
								// groupDevice service is already registered
								// unregister
								knxGA.unregister();
								// and delete from list
								this.groupDeviceRegistrationList.remove(knxGroupAddress.getGroupAddress());
							}
							
							int dptMainNumber = Integer.parseInt(knxGroupAddress.getDptMain());

							// create appropriate groupDevice from dpt main number
							KnxGroupDevice knxGroupDevice = KnxGroupDeviceFactory.getKnxGroupDevice(dptMainNumber);
							
							// startover with next groupDevice if no appropriate KnxGroupDevice implementation found!
							if (knxGroupDevice==null) {
								this.logger.log(LogService.LOG_WARNING, "KNX data type " +
										knxGroupAddress.getDpt() + " is not supported yet!" +
										" Skipping groupDevice " + knxGroupAddress.getGroupAddress() + "!");
								continue;
							}
							
							// set instance alive
							knxGroupDevice.setParams(knxGroupAddress, this.network, this.logger);
							
							// register groupDevice in OSGi registry
							Properties propDeviceService=new Properties();

							propDeviceService.put(
									org.osgi.service.device.Constants.DEVICE_CATEGORY, 
									knxGroupDevice.getGroupDeviceCategory().toString());
							// more possible properties: description, serial, id
							
							ServiceRegistration deviceServiceReg = this.context.registerService(
									org.osgi.service.device.Device.class.getName(), knxGroupDevice, 
									propDeviceService);
							
							this.logger.log(LogService.LOG_INFO, "Registered KNX groupDevice " +
									knxGroupDevice.getGroupDeviceId() + 
//									" (" + knxGroupDevice.getDeviceLocationType() +
//									": " + knxGroupDevice.getDeviceLocation() + ") " +
									" in OSGi registry under groupDevice category: " + 
									knxGroupDevice.getGroupDeviceCategory());
							
							// save this groupDevice registration to my list
							this.groupDeviceRegistrationList.put(knxGroupAddress.getGroupAddress(),deviceServiceReg);
							this.groupDeviceList.put(knxGroupAddress.getGroupAddress(), knxGroupDevice);
							
						} else {
							this.logger.log(LogService.LOG_ERROR, "KNX groupDevice with group address " +
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
	 * remove all groupDevice references in network driver
	 */
	public void stop() {
		
		//this.unregisterManagedService();	is done automatically during bundle stop

		if ( this.groupDeviceList != null ) {
			Iterator<String> it = this.groupDeviceList.keySet().iterator();
			while ( it.hasNext() ) {
				String groupDeviceId = it.next();
				KnxGroupDevice dev = this.groupDeviceList.get(groupDeviceId);
				this.network.removeGroupDevice(groupDeviceId, dev);
				//this.deviceList.remove(deviceId);
				
				//unregister service from OSGI registry
				this.groupDeviceRegistrationList.get(groupDeviceId).unregister();
				this.groupDeviceRegistrationList.remove(groupDeviceId);
			}
			this.groupDeviceList.clear();
			this.groupDeviceRegistrationList.clear();
		}
//		if (this.deviceList.size() != 0 || !this.deviceList.isEmpty())
//			this.logger.log(LogService.LOG_WARNING, "groupDeviceList is not empty after stopping DevMan! Still count "
//					+ this.deviceList.size() + " devices");

	}

	
}

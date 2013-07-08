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

package org.universAAL.lddi.iso11073.activityhub.driver;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073UsageSensor;
import org.universAAL.lddi.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * This Driver class manages driver instances for UsageSensor devices.
 * 
 * when an attached device service is unregistered:
 * drivers must take the appropriate action to release this device service
 * and peform any necessary cleanup, as described in their device category spec
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Iso11073UsageSensorDriver implements Driver {

	private BundleContext context;
	private LogService logger;
	private ServiceTracker tracker;
	private ActivityHubDriverClient client;

	private static final String MY_DRIVER_ID = "org.universAAL.iso11073.usagesensor.0.0.1";

	private ServiceRegistration regDriver;
	
	// Set of driver instances 
	private Set<Iso11073UsageSensorInstance> connectedDriver;


	/**
	 * @param context
	 * @param logTracker
	 */
	public Iso11073UsageSensorDriver(ActivityHubDriverClient client,
			BundleContext context) {
		this.client=client;
		this.context=context;
		this.logger=client.getLogger();
		
		this.connectedDriver = new HashSet<Iso11073UsageSensorInstance>();
		
		this.registerDriver();
	}

	
	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#match(org.osgi.framework.ServiceReference)
	 */
	public int match(ServiceReference reference) throws Exception {
		// reference = device service
		int matchValue=Device.MATCH_NONE;
		String deviceCategory = null;
		try {
			deviceCategory = (String)reference.getProperty(Constants.DEVICE_CATEGORY);
		} catch (ClassCastException e) {
			this.logger.log(LogService.LOG_DEBUG, "Could not cast DEVICE_CATEGORY of requesting" +
			" device to String. No match!");
			return matchValue;
		}
		
		// match check
		// more possible properties to match: description, serial, id
		if ( deviceCategory.equals(Iso11073UsageSensor.MY_DEVICE_CATEGORY) ) {
			matchValue = Iso11073UsageSensor.MATCH_CLASS;
		} else {
			this.logger.log(LogService.LOG_DEBUG, "Requesting device service " + deviceCategory +
			" doesn't match with driver. No match!");
		}
		
		
		return matchValue; //must be > 0 to match
	}


	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#attach(org.osgi.framework.ServiceReference)
	 */
	public String attach(ServiceReference reference) throws Exception {

		// create "driving" instance
		Iso11073UsageSensorInstance instance = new Iso11073UsageSensorInstance(
				this.context, client, this.logger);
		
		// init service tracker on device service for instance
		tracker = new ServiceTracker(this.context, reference, instance);
		tracker.open();
		
		synchronized(this.connectedDriver){
			if ( ! this.connectedDriver.add(instance) )
				this.logger.log(LogService.LOG_ERROR, "Duplicate Element in HashSet connectedDriver");
		}

		return null; // if attachment is correct
	}
	
	
	/**
	 * register this driver in OSGi registry
	 */
	private void registerDriver() {
		Properties propDriver=new Properties();
		propDriver.put(Constants.DRIVER_ID, MY_DRIVER_ID );
		this.regDriver=this.context.registerService(Driver.class.getName(), this, 
				propDriver);
		
		if ( this.regDriver != null )
			this.logger.log(LogService.LOG_INFO, "Driver for Iso11073-UsageSensor registered!");
	}

	
	/**
	 * @return the connectedDriver
	 */
	public Set<Iso11073UsageSensorInstance> getConnectedDriver() {
		return connectedDriver;
	}


}

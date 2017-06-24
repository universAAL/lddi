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

package org.universAAL.lddi.exporter.activityhub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.exporter.activityhub.driver.ActivityHubDriverManager;
import org.universAAL.lddi.lib.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.lddi.lib.activityhub.driver.interfaces.ActivityHubDriverClient;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Instantiates all ActivityHub drivers from ISO11073 library. The drivers call
 * back and register themselves in the driverList.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class AHManager implements ActivityHubDriverClient {

	private BundleContext context;
	private LogService logger;

	/**
	 * stores activityHubInstances for deviceCategories.
	 *
	 * key = deviceCategory value = ActivityHubDriver
	 */
	private Hashtable<ActivityHubDeviceCategory, Set<ActivityHubDriver>> driverListForCategory;

	/**
	 * stores the activityHubInstance (there should be just one!) for each
	 * deviceId.
	 *
	 * key = deviceId value = ActivityHubDriver
	 */
	private Map<String, ActivityHubDriver> driverList;

	private ArrayList<AHContextPublisher> listeners = new ArrayList<AHContextPublisher>();

	/*
	 * Constructor
	 */
	public AHManager(BundleContext context, LogService logger) {
		this.context = context;
		this.logger = logger;
		this.driverListForCategory = new Hashtable<ActivityHubDeviceCategory, Set<ActivityHubDriver>>();
		this.driverList = new TreeMap<String, ActivityHubDriver>();

		// start all ActivityHub drivers
		ActivityHubDriverManager.startAllDrivers(this, this.context);

		// this.logger.log(LogService.LOG_INFO, "I hope all ActivityHub drivers
		// are" +
		// " online now.............");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.
	 * ActivityHubDriverClient#addDriver(java.lang.String,
	 * org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void addDriver(String deviceId, ActivityHubDeviceCategory deviceCategory,
			ActivityHubDriver activityHubDriver) {

		// ActivityHubDriver driver = this.driverList.get(deviceId);

		ActivityHubDriver oldDriver = null;
		synchronized (this.driverList) {
			oldDriver = this.driverList.put(deviceId, activityHubDriver);
			activityHubDriver.getDevice().getDeviceCategory().getTypeCode();
		}
		if (oldDriver != null) {
			this.logger.log(LogService.LOG_WARNING, "An existing ActivityHub driver "
					+ "is now replaced by a new one for device " + deviceId + " and category: " + deviceCategory);
		}

		// my unique device service id
		// String myId = activityHubDriver.getDevice().getDeviceCategory() +
		// activityHubDriver.getDevice().getDeviceId();

		Set<ActivityHubDriver> driversListForCat = this.driverListForCategory.get(deviceCategory);
		if (driversListForCat == null) {
			driversListForCat = new HashSet<ActivityHubDriver>();

			synchronized (this.driverListForCategory) {
				this.driverListForCategory.put(deviceCategory, driversListForCat);
			}
		}
		driversListForCat.add(activityHubDriver);

		this.logger.log(LogService.LOG_INFO,
				"new ActivityHub driver added for device " + deviceId + " and category: " + deviceCategory);

	}

	/**
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#incomingSensorEvent(java.lang.String,
	 *      byte[])
	 *
	 *      Just passing the incoming sensor event to uAAL-MW related class (->
	 *      context provider). No storage of event here!
	 *
	 * @param deviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param activityHubDeviceCategory
	 *            (one category for each activityhub sensor type)
	 * @param event
	 *            code (sensor type dependent!)
	 */
	public void incomingSensorEvent(String deviceId, ActivityHubDeviceCategory activityHubDeviceCategory, int event) {
		this.logger.log(LogService.LOG_INFO, "Client received sensor event: " + event);

		for (Iterator<AHContextPublisher> i = listeners.iterator(); i.hasNext();)
			((AHContextPublisher) i.next()).activityHubSensorStateChanged(deviceId, activityHubDeviceCategory, event);
	}

	// public void sendContextEvent() {
	// for (Iterator<AHContextPublisher> i = listeners.iterator(); i.hasNext();)
	// ((AHContextPublisher) i.next()).sendContextEvent();
	//
	// //lampStateChanged(lampID,myLampDB[lampID].loc, false)
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.
	 * ActivityHubDriverClient#removeDriver(java.lang.String,
	 * org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void removeDriver(String deviceId, ActivityHubDriver activityHubDriver) {
		this.driverList.remove(deviceId);
		this.logger.log(LogService.LOG_INFO, "removed ActivityHub driver for " + "device " + deviceId);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.
	 * ActivityHubDriverClient#getLogger()
	 */
	public LogService getLogger() {
		return this.logger;
	}

	/**
	 * Returns null if no location available!
	 *
	 * @param deviceId
	 * @return ActivityHubLocation
	 */
	public ActivityHubLocation getDeviceLocation(String deviceId) {

		ActivityHubDriver driver = this.driverList.get(deviceId);
		ActivityHubLocation loc = driver.getDevice().getDeviceLocation();
		if (loc == null) {
			this.logger.log(LogService.LOG_WARNING, "No location specified for " + "device: " + deviceId);
		} else {
			this.logger.log(LogService.LOG_DEBUG, "Location for " + "device: " + deviceId + ": " + loc.toString());
		}
		return loc;
	}

	/**
	 * @param deviceId
	 * @return boolean
	 */
	public boolean validateDevice(String deviceId) {
		this.logger.log(LogService.LOG_DEBUG, "deviceIds in driverList: " + this.driverList.keySet().toString());

		if (this.driverList.get(deviceId) == null)
			return false;
		return true;
	}

	/**
	 * Find the driver for the requested device according to unique deviceId.
	 * Returns -1 if no driver found for this device
	 *
	 * @param deviceId
	 * @return integer value of last device event from device dependent event
	 *         enumeration
	 */
	public int getLastDeviceEvent(String deviceId) {
		// return last incoming device event from driver instance
		ActivityHubDriver driver = this.driverList.get(deviceId);
		if (driver != null) {
			this.logger.log(LogService.LOG_INFO,
					"Last sensor event found for " + "device: " + deviceId + ": " + driver.getLastSensorEvent());
			return driver.getLastSensorEvent();
		} else {
			this.logger.log(LogService.LOG_WARNING, "No recent sensor event found for " + "device: " + deviceId);
			return -1;
		}
	}

	/**
	 * store listener for context bus connection.
	 *
	 * @param aHContextPublisher
	 */
	public void addListener(AHContextPublisher aHContextPublisher) {
		listeners.add(aHContextPublisher);
	}

	/**
	 * @param aHContextPublisher
	 */
	public void removeListener(AHContextPublisher aHContextPublisher) {
		listeners.remove(aHContextPublisher);
	}

	/**
	 * copy deviceId(String) and ActivityHub device category(Integer) into
	 * sensorList parameter for all available ActivityHub sensors.
	 *
	 * @param sensorList
	 */
	public void getActivityHubSensorList(Map<String, Integer> sensorList) {
		synchronized (driverList) {
			synchronized (sensorList) {
				for (Entry<String, ActivityHubDriver> entry : driverList.entrySet()) {
					// Iterator<Entry<String,ActivityHubDriver>> it =
					// this.driverList.entrySet().iterator();
					// while (it.hasNext()) {
					// Entry<String,ActivityHubDriver> entry = it.next();
					sensorList.put(entry.getKey(),
							Integer.valueOf(entry.getValue().getDevice().getDeviceCategory().getTypeCode()));
				}
			}
		}
	}

}

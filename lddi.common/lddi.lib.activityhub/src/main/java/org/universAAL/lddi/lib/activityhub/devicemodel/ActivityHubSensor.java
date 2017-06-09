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

package org.universAAL.lddi.lib.activityhub.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubBaseDeviceCategory;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Activity hub sensor base class
 * 
 * Now all ActivityHub sensors are implemented as on/off sensors having
 * setSensorEventOn and setSensorEventOff methods. TODO: check if it's
 * possible/better to use any value setSensorEvent(int sensorEvent); for KNX
 * datapoint type 1.*** (which are used mainly during development) on/off is
 * sufficient
 * 
 * Generic sensor properties flags for activity hub sensors are not implement
 * yet! (Because they are not supported by KNX sensors)
 * auto-presence-received(16) (For sensors that have "heartbeat" operational
 * status: indicates that the "heartbeat" has been seen and is ok. This flag
 * shall be reset if Auto-Presence-Failed is set.) auto-presence-failed(17) (For
 * sensors that have "heartbeat" operational status: indicates that the
 * "heartbeat" has not been seen as expected. This flag shall be reset if
 * Auto-Presence-Received is set.) low-battery(18) (Indicates the sensor is in
 * the low battery condition. This determination is unique to the sensor.)
 * fault(19) (Indicates that the sensor is in a fault condition and needs
 * attention. This determination is unique to the sensor.) end-of-life(20)
 * (Indicates that the sensor has reached end of life and needs replacement.
 * This indication is unique to the sensor.)
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public abstract class ActivityHubSensor implements Device {

	private LogService logger;
	private ActivityHubDeviceCategory deviceCategory;
	private ActivityHubLocation deviceLocation;

	/** any kind of deviceID; for KNX devices this is the group address */
	private String deviceId = null;

	/**
	 * all inherited classes must use the same member name, even though they
	 * have different types
	 */
	protected Object lastSensorEvent;

	/** reference to my driver instance; can be just one! */
	protected ActivityHubBaseDeviceCategory driver;

	/**
	 * Constructor
	 * 
	 * @param deviceCategory
	 * @param deviceLocation
	 * @param deviceId
	 * @param logger
	 */
	public ActivityHubSensor(ActivityHubDeviceCategory deviceCategory, ActivityHubLocation deviceLocation,
			String deviceId, LogService logger) {
		this.deviceCategory = deviceCategory;
		this.deviceLocation = deviceLocation;
		this.deviceId = deviceId;
		this.logger = logger;
	}

	/** store a driver reference for this device */
	public void addDriver(ActivityHubBaseDeviceCategory driverInstance) {
		this.driver = driverInstance;
	}

	/** remove the driver reference of this device */
	public void removeDriver() {
		this.driver = null;
	}

	/**
	 * set sensor event to on (e.g. switch-on) if input comes from an 1-bit
	 * sensor
	 * 
	 * called by refinement driver
	 * 
	 * is on/off feasible for all ActivityHubSensors?
	 */
	public abstract void setSensorEventOn();

	/**
	 * set sensor event to off (e.g. switch-off) if input comes from an 1-bit
	 * sensor
	 */
	public abstract void setSensorEventOff();

	/**
	 * send sensor event to driver (upper layer)
	 */
	protected void sendEvent(int event) {
		this.driver.incomingSensorEvent(event);
	}

	/**
	 * @return the sensorEvent
	 */
	public abstract int getSensorEventValue();

	// ?????????????????
	// /**
	// * @param sensorEvent the sensorEvent to set
	// */
	// public abstract void setSensorEvent(int sensorEvent);

	/**
	 * @return the deviceCategory
	 */
	public ActivityHubDeviceCategory getDeviceCategory() {
		return deviceCategory;
	}

	// Only allowed in constructor
	// /**
	// * @param deviceCategory the deviceCategory to set
	// */
	// public void setDeviceCategory(ActivityHubDeviceCategory deviceCategory) {
	// this.deviceCategory = deviceCategory;
	// }

	/**
	 * @return the deviceLocation
	 */
	public ActivityHubLocation getDeviceLocation() {
		return deviceLocation;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	// Only allowed in constructor
	// /**
	// * @param deviceId the deviceId to set
	// */
	// public void setDeviceId(String deviceId) {
	// this.deviceId = deviceId;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.device.Device#noDriverFound()
	 */
	public void noDriverFound() {

		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for ActivityHub device: "
				+ this.deviceCategory + " - " + this.deviceId);
	}

}

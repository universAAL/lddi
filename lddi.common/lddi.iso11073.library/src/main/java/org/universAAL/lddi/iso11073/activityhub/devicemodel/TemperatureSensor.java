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

package org.universAAL.lddi.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073TemperatureSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a temperature sensor according to
 * ISO 11073 - Part 10471 (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification):
 * - high temperature detected
 * - low temperature detected
 * - rate of change too fast (optional)
 * - no condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set.
 * Later, current sensor value can be set to HIGH_TEMPERATURE_DETECTED and LOW_TEMPERATURE_DETECTED
 * Event RATE_OF_CHANGE_TOO_FAST is not implemented yet!
 * 
 * @author Thomas Fuxreiter 
 */
public class TemperatureSensor extends ActivityHubSensor implements
		Iso11073TemperatureSensor {

	private TemperatureSensorEvent lastSensorEvent;

	/**
	 * @param deviceCategory
	 * @param deviceLocation
	 * @param deviceId
	 * @param logger
	 */
	public TemperatureSensor(ActivityHubDeviceCategory deviceCategory,
			ActivityHubLocation deviceLocation, String deviceId,
			LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);

		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = TemperatureSensorEvent.NO_CONDITION_DETECTED;
}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#getSensorEventValue()
	 */
	@Override
	public int getSensorEventValue() {
		return this.lastSensorEvent.value();
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEventOff()
	 */
	@Override
	public void setSensorEventOff() {
		this.lastSensorEvent = TemperatureSensorEvent.LOW_TEMPERATURE_DETECTED;
		this.sendEvent(TemperatureSensorEvent.LOW_TEMPERATURE_DETECTED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEventOn()
	 */
	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = TemperatureSensorEvent.HIGH_TEMPERATURE_DETECTED;
		this.sendEvent(TemperatureSensorEvent.HIGH_TEMPERATURE_DETECTED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubBaseDeviceCategory#incomingSensorEvent(int)
	 */
	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not! 
	}

}

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

import org.osgi.service.log.LogService;
import org.universAAL.lddi.lib.activityhub.devicecategory.Iso11073UsageSensor;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a usage sensor according to ISO 11073 - Part 10471
 * (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification): - usage started //
 * bed/chair in - usage ended // bed/chair out - expected use start violation
 * (optional) // expected usage not started - expected use stop violation
 * (optional) // usage continued beyond expected usage end - absence violation
 * (optional) // absent for too long a period during expected usage - no
 * condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set. Later, current sensor value can be
 * set to USAGE_STARTED and USAGE_ENDED Events EXPECTED_USE_START_VIOLATION,
 * EXPECTED_USE_STOP_VIOLATION and ABSENCE_VIOLATION are not implemented yet!
 * 
 * @author Thomas Fuxreiter
 */
public class UsageSensor extends ActivityHubSensor implements Iso11073UsageSensor {

	private UsageSensorEvent lastSensorEvent;

	/**
	 * @param deviceCategory
	 * @param deviceLocation
	 * @param deviceId
	 * @param logger
	 */
	public UsageSensor(ActivityHubDeviceCategory deviceCategory, ActivityHubLocation deviceLocation, String deviceId,
			LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);

		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = UsageSensorEvent.NO_CONDITION_DETECTED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.universAAL.lddi.lib.activityhub.devicemodel.ActivityHubSensor#
	 * getSensorEventValue()
	 */
	@Override
	public int getSensorEventValue() {
		return this.lastSensorEvent.value();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.universAAL.lddi.lib.activityhub.devicemodel.ActivityHubSensor#
	 * setSensorEventOff()
	 */
	@Override
	public void setSensorEventOff() {
		this.lastSensorEvent = UsageSensorEvent.USAGE_ENDED;
		this.sendEvent(UsageSensorEvent.USAGE_ENDED.value());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.universAAL.lddi.lib.activityhub.devicemodel.ActivityHubSensor#
	 * setSensorEventOn()
	 */
	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = UsageSensorEvent.USAGE_STARTED;
		this.sendEvent(UsageSensorEvent.USAGE_STARTED.value());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.universAAL.lddi.lib.activityhub.devicecategory.
	 * ActivityHubBaseDeviceCategory#incomingSensorEvent(int)
	 */
	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not!
	}

}

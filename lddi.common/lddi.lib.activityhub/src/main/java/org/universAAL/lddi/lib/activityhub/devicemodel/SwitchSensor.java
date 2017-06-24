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
import org.universAAL.lddi.lib.activityhub.devicecategory.Iso11073SwitchSensor;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a switch sensor according to ISO 11073 - Part 10471
 * (Indepentend living activity hub).
 *
 * Specific sensor events (from standard specification): - switch on - switch
 * off - no condition detected (optional)
 *
 * Initially NO_CONDITION_DETECTED is set. Later, current sensor value can be
 * set to SWITCH_ON and SWITCH_OFF
 *
 * @author Thomas Fuxreiter
 */
public class SwitchSensor extends ActivityHubSensor implements Iso11073SwitchSensor {

	private SwitchSensorEvent lastsensorEvent;

	public SwitchSensor(ActivityHubDeviceCategory deviceCategory, ActivityHubLocation deviceLocation, String deviceId,
			LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);

		// init value is NO_CONDITION_DETECTED
		this.lastsensorEvent = SwitchSensorEvent.NO_CONDITION_DETECTED;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.universAAL.lddi.lib.activityhub.devicemodel.ActivityHubSensor#
	 * getSensorEventValue()
	 */
	@Override
	public int getSensorEventValue() {
		return this.lastsensorEvent.value();
	}

	// /* (non-Javadoc)
	// * @see
	// org.universAAL.lddi.lib.activityhub.devicemodel.ActivityHubSensor#setSensorEvent(int)
	// */
	// @Override
	// public void setSensorEvent(int sensorEvent) {
	// this.lastsensorEvent =
	// SwitchSensorEvent.getSwitchSensorEvent(sensorEvent);
	// }
	//
	// public void setSensorEvent(SwitchSensorEvent sse) {
	// this.lastsensorEvent = sse;
	// }

	@Override
	public void setSensorEventOff() {
		this.lastSensorEvent = SwitchSensorEvent.SWITCH_OFF;
		this.sendEvent(SwitchSensorEvent.SWITCH_OFF.value());
	}

	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = SwitchSensorEvent.SWITCH_ON;
		this.sendEvent(SwitchSensorEvent.SWITCH_ON.value());
	}

	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not!
	}

}

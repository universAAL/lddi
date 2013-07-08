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
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073MotionSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a motion sensor according to ISO 11073 - 
 * Part 10471 (Independent living activity hub), edition 2010-05-01
 * 
 * Specific sensor events (from standard specification):
 * - motion detected
 * - motion detected delayed (optional)
 * - tamper detected (optional)
 * - no condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set.
 * Later, current sensor value can be set to MOTION_DETECTED and NO_CONDITION_DETECTED.
 * Events MOTION_DETECTED_DELAYED and TAMPER_DETECTED are not implemented yet!
 * 
 * @author Thomas Fuxreiter
 */
public class MotionSensor extends ActivityHubSensor implements Iso11073MotionSensor {
	
	protected MotionSensorEvent lastSensorEvent;
 
	public MotionSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);
		
		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = MotionSensorEvent.NO_CONDITION_DETECTED;
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#getSensorEventValue()
	 */
	@Override
	public int getSensorEventValue() {
		return this.lastSensorEvent.value();
	}

//	/* (non-Javadoc)
//	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEvent(int)
//	 */
//	@Override
//	public void setSensorEvent(int sensorEvent) {
//		this.lastSensorEvent = MotionSensorEvent.getMotionSensorEvent(sensorEvent);
//	}

//	public void setSensorEvent(MotionSensorEvent mse) {
//		this.lastSensorEvent = mse;
//	}

	@Override
	public void setSensorEventOff() {
		this.lastSensorEvent = MotionSensorEvent.NO_CONDITION_DETECTED;
		this.sendEvent(MotionSensorEvent.NO_CONDITION_DETECTED.value());
	}

	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = MotionSensorEvent.MOTION_DETECTED;
		this.sendEvent(MotionSensorEvent.MOTION_DETECTED.value());
	}

	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not!
	}
}

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
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073ContactClosureSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a contact closure sensor according to
 * ISO 11073 - Part 10471 (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification):
 * - contact opened
 * - contact closed
 * - no condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set.
 * Later, current sensor value can be set to CONTACT_OPENED and CONTACT_CLOSED
 * 
 * @author Thomas Fuxreiter 
 */
public class ContactClosureSensor extends ActivityHubSensor implements Iso11073ContactClosureSensor{

	//public static String MY_DEVICE_CATEGORY = "ISO11073_CONTACTCLOSURESENSOR";
	private ContactClosureSensorEvent lastSensorEvent;

//	public ContactClosureSensor() {
//	}
	public ContactClosureSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);
		
		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = ContactClosureSensorEvent.NO_CONDITION_DETECTED;
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
//		this.lastsensorEvent = ContactClosureSensorEvent.getContactClosureSensorEvent(sensorEvent);
//	}
//
//	public void setSensorEvent(ContactClosureSensorEvent ccse) {
//		this.lastsensorEvent = ccse;
//	}

	@Override
	public void setSensorEventOff() {
		this.lastSensorEvent = ContactClosureSensorEvent.CONTACT_OPENED;
		this.sendEvent(ContactClosureSensorEvent.CONTACT_OPENED.value());
	}

	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = ContactClosureSensorEvent.CONTACT_CLOSED;
		this.sendEvent(ContactClosureSensorEvent.CONTACT_CLOSED.value());
	}

	
	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not! 
	}

}

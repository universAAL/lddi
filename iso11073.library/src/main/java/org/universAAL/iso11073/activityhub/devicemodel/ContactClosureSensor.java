package org.universAAL.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;



/**
 * 
 * Do we really need this class or should we use just ActivityHubSensor ???
 * 
 * 
 * Representation of a contact closure sensor according to
 * ISO 11073 - Part 10471 (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification):
 * - contact opened
 * - contact closed
 * - no condition detected (optional)
 * 
 * TODO: Implement generic sensor properties flags for activity hub sensors
 * 
 * @author Thomas Fuxreiter 
 */
public class ContactClosureSensor extends ActivityHubSensor {

	//public static String MY_DEVICE_CATEGORY = "ISO11073_CONTACTCLOSURESENSOR";
	private ContactClosureSensorEvent lastsensorEvent;

//	public ContactClosureSensor() {
//	}
	public ContactClosureSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);
		
		// init value is NO_CONDITION_DETECTED
		this.lastsensorEvent = ContactClosureSensorEvent.NO_CONDITION_DETECTED;
	}

	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.devicemodel.ActivityHubSensor#getSensorEventValue()
	 */
	@Override
	public int getSensorEventValue() {
		return this.lastsensorEvent.value();
	}

	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEvent(int)
	 */
	@Override
	public void setSensorEvent(int sensorEvent) {
		this.lastsensorEvent = ContactClosureSensorEvent.getContactClosureSensorEvent(sensorEvent);
	}

	public void setSensorEvent(ContactClosureSensorEvent ccse) {
		this.lastsensorEvent = ccse;
	}

}

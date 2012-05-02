package org.universAAL.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;


/**
 * Representation of a motion sensor according to ISO 11073 - 
 * Part 10471 (Indepentend living activity hub), edition 2010-05-01
 * 
 * Specific sensor events (from standard specification):
 * - motion detected
 * - motion detected delayed (optional)
 * - tamper detected (optional)
 * - no condition detected (optional)
 * 
 * TODO: Implement generic sensor properties flags for activity hub sensors
 * 
 * @author Thomas Fuxreiter
 */
public class MotionSensor extends ActivityHubSensor {

	private MotionSensorEvent lastsensorEvent;
 
	public MotionSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);
		
		// init value is NO_CONDITION_DETECTED
		this.lastsensorEvent = MotionSensorEvent.NO_CONDITION_DETECTED;
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
		this.lastsensorEvent = MotionSensorEvent.getMotionSensorEvent(sensorEvent);
	}

	public void setSensorEvent(MotionSensorEvent mse) {
		this.lastsensorEvent = mse;
	}
}

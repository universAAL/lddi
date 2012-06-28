package org.universAAL.lddi.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073MotionSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

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

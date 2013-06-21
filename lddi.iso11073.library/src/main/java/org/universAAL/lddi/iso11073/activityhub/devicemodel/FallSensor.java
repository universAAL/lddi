package org.universAAL.lddi.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073FallSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a fall sensor according to
 * ISO 11073 - Part 10471 (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification):
 * - fall detected
 * - no condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set.
 * Later, current sensor value can be set to FALL_DETECTED and back to NO_CONDITION_DETECTED
 * 
 * @author Thomas Fuxreiter 
 */
public class FallSensor extends ActivityHubSensor implements Iso11073FallSensor {

	private FallSensorEvent lastSensorEvent;

	/**
	 * @param deviceCategory
	 * @param deviceLocation
	 * @param deviceId
	 * @param logger
	 */
	public FallSensor(ActivityHubDeviceCategory deviceCategory,
			ActivityHubLocation deviceLocation, String deviceId,
			LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);

		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = FallSensorEvent.NO_CONDITION_DETECTED;
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
		this.lastSensorEvent = FallSensorEvent.NO_CONDITION_DETECTED;
		this.sendEvent(FallSensorEvent.NO_CONDITION_DETECTED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEventOn()
	 */
	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = FallSensorEvent.FALL_DETECTED;
		this.sendEvent(FallSensorEvent.FALL_DETECTED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubBaseDeviceCategory#incomingSensorEvent(int)
	 */
	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not! 
	}


}

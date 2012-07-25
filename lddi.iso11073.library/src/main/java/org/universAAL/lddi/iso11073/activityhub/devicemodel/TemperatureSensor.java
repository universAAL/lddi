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
		// TODO Auto-generated method stub

	}

}

package org.universAAL.lddi.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073UsageSensor;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Representation of a usage sensor according to
 * ISO 11073 - Part 10471 (Indepentend living activity hub).
 * 
 * Specific sensor events (from standard specification):
 * - usage started		// bed/chair in
 * - usage ended		// bed/chair out
 * - expected use start violation (optional)	// expected usage not started
 * - expected use stop violation (optional)		// usage continued beyond expected usage end
 * - absence violation (optional)				// absent for too long a period during expected usage
 * - no condition detected (optional)
 * 
 * Initially NO_CONDITION_DETECTED is set.
 * Later, current sensor value can be set to USAGE_STARTED and USAGE_ENDED
 * Events EXPECTED_USE_START_VIOLATION, EXPECTED_USE_STOP_VIOLATION and ABSENCE_VIOLATION are not implemented yet!
 * 
 * @author Thomas Fuxreiter 
 */
public class UsageSensor extends ActivityHubSensor implements
		Iso11073UsageSensor {

	private UsageSensorEvent lastSensorEvent;

	/**
	 * @param deviceCategory
	 * @param deviceLocation
	 * @param deviceId
	 * @param logger
	 */
	public UsageSensor(ActivityHubDeviceCategory deviceCategory,
			ActivityHubLocation deviceLocation, String deviceId,
			LogService logger) {
		super(deviceCategory, deviceLocation, deviceId, logger);

		// init value is NO_CONDITION_DETECTED
		this.lastSensorEvent = UsageSensorEvent.NO_CONDITION_DETECTED;
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
		this.lastSensorEvent = UsageSensorEvent.USAGE_ENDED;
		this.sendEvent(UsageSensorEvent.USAGE_ENDED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor#setSensorEventOn()
	 */
	@Override
	public void setSensorEventOn() {
		this.lastSensorEvent = UsageSensorEvent.USAGE_STARTED;
		this.sendEvent(UsageSensorEvent.USAGE_STARTED.value());
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubBaseDeviceCategory#incomingSensorEvent(int)
	 */
	public void incomingSensorEvent(int event) {
		// driver instances must implement this method; device instances not! 
	}

}

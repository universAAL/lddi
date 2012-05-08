package org.universAAL.iso11073.activityhub.devicecategory;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface ActivityHubBaseDeviceCategory {
	/**
	 * The specific drivers have to implement this method to receive messages from knx devices
	 * @param event Integer representation of a specific ActivityHubSensorEvent.
	 * example MotionSensor: motion-detected = 0; no-condition-detected = 3
	 */
	public void incomingSensorEvent( int event );
}

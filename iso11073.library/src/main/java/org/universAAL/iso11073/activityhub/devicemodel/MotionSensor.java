package org.universAAL.iso11073.activityhub.devicemodel;


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
public class MotionSensor extends ActivityHubDevice {

	//public static String MY_DEVICE_CATEGORY = "ISO11073_MOTIONSENSOR";

	public MotionSensor() {
	}

}

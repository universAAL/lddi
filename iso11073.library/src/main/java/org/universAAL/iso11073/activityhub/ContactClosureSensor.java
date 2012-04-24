package org.universAAL.iso11073.activityhub;


/**
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

	public static String MY_DEVICE_CATEGORY = "ISO11073_CONTACTCLOSURE";

	public ContactClosureSensor() {
	}

}

package org.universAAL.iso11073.activityhub.devicecategory;

import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;

/**
 * OSGi DeviceCategory for Activityhub ContactClosure sensor
 * 
 * In general DeviceCategories specify:
 * - rules and interfaces needed for the communication between device service
 * and driver service. Devices must implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface Iso11073ContactClosureSensor {

	// used in driver match method
	public static String MY_DEVICE_CATEGORY = ActivityHubDeviceCategory.
		MDC_AI_TYPE_SENSOR_CONTACTCLOSURE.toString();

	// from OSGi DAS Spec
	public static int MATCH_SERIAL	= 10;	// an exact match including the serial number
	public static int MATCH_VERSION	= 8;	// matches the right class, make model and version
	public static int MATCH_MODEL	= 6;	// matches the right class and make model
	public static int MATCH_MAKE	= 4;	// matches the make
	public static int MATCH_CLASS	= 2;	// only matches the class

	// example properties
	// from where should we get this data?
	public static String CLASS 	= "-"; // class description
	public static String MODEL 			= "-"; // definition of the model, e.g. audio, video, serial
	public static String MANUFACTURER 	= "-";
	public static String REVISION		= "-";
	public static String SERIAL			= "-";
	
	// example methods
//	void sendPacket( byte [] data);
	
	boolean receiveSensorEvent(int value);

}

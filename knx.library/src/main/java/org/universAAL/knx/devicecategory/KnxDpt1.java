package org.universAAL.knx.devicecategory;

/**
 * DeviceCategory for KNX datapoint type 1.***
 * 
 * In general DeviceCategories specify:
 * - rules and interfaces needed for the communication between device service
 * and driver service. Devices must implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values
 * 
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public interface KnxDpt1 {
	
	public static String MY_DEVICE_CATEGORY = "KnxDpt1";
	
	// from OSGi DAS Spec
	public static int MATCH_SERIAL	= 10;	// an exact match including the serial number
	public static int MATCH_VERSION	= 8;	// matches the right class, make model and version
	public static int MATCH_MODEL	= 6;	// matches the right class and make model
	public static int MATCH_MAKE	= 4;	// matches the make
	public static int MATCH_CLASS	= 2;	// only matches the class
	
	// from DOG:
//	public static int MATCH_TYPE=100;
//	public static int MATCH_SUB_TYPE=50;
//	public static int MATCH_MANUFACTURER=0;

	// example properties
	// this data may not be available from ETS4 import!
	public static String CLASS 	= "-"; // class description
	public static String MODEL 			= "-"; // definition of the model, e.g. audio, video, serial
	public static String MANUFACTURER 	= "-";
	public static String REVISION		= "-";
	public static String SERIAL			= "-";
	
	// example methods
	void sendPacket( byte [] data);
	
	byte [] receivePacket(long timeout);
	
}

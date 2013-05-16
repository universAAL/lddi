package org.universAAL.lddi.knx.devicecategory;

import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;

/**
 * Base DeviceCategory for KNX datapoint types B1 (1 bit).
 * 
 * In general DeviceCategories specify:
 * - rules and interfaces needed for the communication between device service
 * and driver service. Both of them implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface KnxDpt1 extends KnxBaseDeviceCategory {
	
	public static KnxDeviceCategory MY_DEVICE_CATEGORY = KnxDeviceCategory.KNX_DPT_1; 
		// "KnxDpt1";
	
	// from OSGi DAS Spec
	public static int MATCH_SERIAL	= 10;	// an exact match including the serial number
	public static int MATCH_VERSION	= 8;	// matches the right class, make model and version
	public static int MATCH_MODEL	= 6;	// matches the right class and make model
	public static int MATCH_MAKE	= 4;	// matches the make
	public static int MATCH_CLASS	= 2;	// only matches the class
	

	// example properties
	// this data may not be available from ETS4 import!
	public static String CLASS 	= "-"; // class description
	public static String MODEL 			= "-"; // definition of the model, e.g. audio, video, serial
	public static String MANUFACTURER 	= "-";
	public static String REVISION		= "-";
	public static String SERIAL			= "-";
	

	// default on/off constants for all dpt1 devices
	public static byte DEFAULT_VALUE_ON =  (byte) 1;
	public static byte DEFAULT_VALUE_OFF = (byte) 0;

//	// constants for specific dpt1 devices

//	/** 1.001 - DPT_Switch */
//	/** 1 = on */
//	public static byte DEFAULT_VALUE_ON_1_001 =  DEFAULT_VALUE_ON;
//	/** 0 = off */
//	public static byte DEFAULT_VALUE_OFF_1_001 = DEFAULT_VALUE_OFF;
//	
//	/** 1.005 - DPT_Alarm */
//	/** 1 = alarm */
//	public static byte DEFAULT_VALUE_ON_1_005 =  DEFAULT_VALUE_ON;
//	/** 0 = no alarm */
//	public static byte DEFAULT_VALUE_OFF_1_005 = DEFAULT_VALUE_OFF;
//
//	/** 1.009 - DPT_OpenClose */
//	/** 1 = close */
//	public static byte DEFAULT_VALUE_ON_1_009 =  DEFAULT_VALUE_ON;
//	/** 0 = open */
//	public static byte DEFAULT_VALUE_OFF_1_009 = DEFAULT_VALUE_OFF;

}

package org.universAAL.lddi.knx.devicecategory;

import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;

/**
 * Base DeviceCategory for KNX datapoint type “2-Octet Float Value”.
 * 
 * Specification from KNX Datapoint Types v1.07.00 AS :
 * 
 * Format: 2 octets: F16
 * 				MSB			LSB
 * float value |-------- --------|
 * encoding 	MEEEEMMM MMMMMMMM
 * FloatValue = (0,01*M)*2(E)
 * E = [0 … 15]
 * M = [-2 048 … 2 047], two’s complement notation
 * For all Datapoint Types 9.xxx, the encoded value 7FFFh shall always be used to denote invalid data.
 * Possible values are from 0 - 255.
 * Range: [-671 088,64 … 670 760,96]
 * PDT: PDT_KNX_FLOAT
 * 
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
public interface KnxDpt9 extends KnxBaseDeviceCategory {
	
	public static KnxDeviceCategory MY_DEVICE_CATEGORY = KnxDeviceCategory.KNX_DPT_9; 
		//"KnxDpt9";
	
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
	

	// default max/min constants for all dpt9 devices
	public static byte[] DEFAULT_MAX_VALUE = {(byte) 0x07, (byte) 0xFF}; // 2047 two’s complement notation!
	public static byte[] DEFAULT_MIN_VALUE = {(byte) 0x80, (byte) 0x00}; // -2048 two’s complement notation!
	public static byte[] DEFAULT_INVALID_VALUE = {(byte) 0x7F, (byte) 0xFF}; // all bits 1 except MSb

	
	// constants for specific dpt9 devices
	/** 9.001 - DPT_Value_Temp
	 * From -273°C - 670760°C - Resolution: 0,01°C
	 */
	public static short RESOLUTION_9_001 =  1/100;
	
	
    // methods that should be implemented in driver
    /**
     * Calculate float value from knx message payload.
     * 				MSB			LSB
     * float value |-------- --------|
     * encoding 	MEEEEMMM MMMMMMMM
     * FloatValue = (0,01*M)*2(E)
     * E = [0 … 15]
     * M = [-2 048 … 2 047], two’s complement notation
     */
    public float calculateFloatValue(byte[] payload);



}

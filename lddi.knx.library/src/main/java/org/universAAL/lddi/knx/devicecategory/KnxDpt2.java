package org.universAAL.lddi.knx.devicecategory;

/**
 * Base DeviceCategory for KNX datapoint types B2 (2 bit).
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
public interface KnxDpt2 extends KnxBaseDeviceCategory {

	public static String MY_DEVICE_CATEGORY = "KnxDpt2";
	
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

	// 2-bit encoding
	// c v
	// 0 0 No control
	// 0 1 No control
	// 1 0 Control. Function value 0
	// 1 1 Control. Function value 1
	//
	// datapoint sub types (similar to Dpt1):
	//	2.001 DPT_Switch_Control G
	//	2.002 DPT_Bool_Control G
	//	2.003 DPT_Enable_Control FB
	//	2.004 DPT_Ramp_Control FB
	//	2.005 DPT_Alarm_Control FB
	//	2.006 DPT_BinaryValue_Control FB
	//	2.007 DPT_Step_Control FB
	//	2.008 DPT_Direction1_Control FB
	//	2.009 DPT_Direction2_Control FB
	//	2.010 DPT_Start_Control FB
	//	2.011 DPT_State_Control FB
	//	2.012 DPT_Invert_Control FB


	// default on/off constants for all dpt2 devices
	public static byte DEFAULT_VALUE_OFF_CONTROL_OFF =(byte) 0x80;
	public static byte DEFAULT_VALUE_ON_CONTROL_OFF = (byte) 0x81;
	public static byte DEFAULT_VALUE_OFF_CONTROL_ON = (byte) 0x82;
	public static byte DEFAULT_VALUE_ON_CONTROL_ON =  (byte) 0x83;
	
}

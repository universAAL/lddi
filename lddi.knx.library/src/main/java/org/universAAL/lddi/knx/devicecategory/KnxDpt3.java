package org.universAAL.lddi.knx.devicecategory;

/**
 * Base DeviceCategory for KNX datapoint type B1U3 (4-Bit). Possible values are
 * Bit 4: c = {0,1} (Decrease, Increase).
 * 
 * Bit 1-3: StepCode = {000b ... 111b} (The amount of intervals into which the
 * range of 0 % … 100 % is subdivided, or the break indication) 
 * 
 * 001b ... 111b:
 * Step Number of intervals = 2^(stepcode-1)
 * Maximum resolution is 64 = 2^(7-1)

 * 000b:  Break 
 * 
 * In general DeviceCategories specify: - rules and interfaces needed for the
 * communication between device service and driver service. Both of them
 * implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics
 * (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface KnxDpt3 extends KnxBaseDeviceCategory {

    public static String MY_DEVICE_CATEGORY = "KnxDpt3";

    // from OSGi DAS Spec
    public static int MATCH_SERIAL = 10; // an exact match including the serial
    // number
    public static int MATCH_VERSION = 8; // matches the right class, make model
    // and version
    public static int MATCH_MODEL = 6; // matches the right class and make model
    public static int MATCH_MAKE = 4; // matches the make
    public static int MATCH_CLASS = 2; // only matches the class

    // example properties
    // this data may not be available from ETS4 import!
    public static String CLASS = "-"; // class description
    public static String MODEL = "-"; // definition of the model, e.g. audio,
    // video, serial
    public static String MANUFACTURER = "-";
    public static String REVISION = "-";
    public static String SERIAL = "-";

    
    // default constants for all dpt3 devices
    public static byte STEPCODE_BREAK = (byte) 0;
    
    
    // constants for specific dpt3 devices
    /**
     * 3.007 - DPT_Control_Dimming
     */
    public static byte DECREASE_3_007 = (byte) 0;
    public static byte INCREASE_3_007 = (byte) 1;

    /**
     * 3.008 - DPT_Control_Blinds
     * This DPT can be used both for the relative positioning of the vertical blinds 
     * positions as well as for the relative positioning of the angle of the slats.
     */
    public static byte UP_3_008 = (byte) 0;
    public static byte DOWN_3_008 = (byte) 1;
 
    
    // methods that should be implemented in driver
    /**
     * Calculate Step number of interval from stepcode.
     * stepcode is bits 1-3
     * Step Number of intervals = 2^(stepcode-1)
     */
    public int calculateStepNumberOfInterval(byte stepcode);

}

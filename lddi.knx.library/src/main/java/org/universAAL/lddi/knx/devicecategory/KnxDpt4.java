package org.universAAL.lddi.knx.devicecategory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;

/**
 * Base DeviceCategory for KNX datapoint type Character Set (8-Bit Unsigned
 * Value). Possible values are from 0 - 255.
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
public interface KnxDpt4 extends KnxBaseDeviceCategory {

	public static KnxDeviceCategory MY_DEVICE_CATEGORY = KnxDeviceCategory.KNX_DPT_4; 
//    public static String MY_DEVICE_CATEGORY = "KnxDpt4";

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

    // default max/min constants for all dpt4 devices
    // does this make sense???
    public static byte DEFAULT_MAX_VALUE = (byte) 0xFF;
    public static byte DEFAULT_MIN_VALUE = (byte) 0x00;

    // constants for specific dpt4 devices according to RFC2978
    /**
     * 4.001 - DPT_Char_ASCII (ANSI_X3.4-1968) Range: 0 - 127 (The most
     * significant bit shall always be 0!)
     * Is this field necessary???
     */
    public static boolean CHARACTER_SET_ASCII = false;

    /**
     * 4.002 - DPT_Char_8859_1 (ISO_8859-1:1987) Range: 0 - 255
     * Is this field necessary???
     */
    public static boolean CHARACTER_SET_8859_1 = false;

    // methods that should be implemented in driver
    public String convertHexToString(CharacterSet characterSet);
//    http://www.mkyong.com/java/how-to-convert-hex-to-ascii-in-java/

    public enum CharacterSet {
	CHARACTER_SET_ASCII(1), CHARACTER_SET_8859_1(2);

	private int typecode;

	private static final Map<Integer, CharacterSet> lookup = new HashMap<Integer, CharacterSet>();

	static {
	    for (CharacterSet s : EnumSet
		    .allOf(CharacterSet.class))
		lookup.put(s.getTypeCode(), s);
	}

	private CharacterSet(int typecode) {
	    this.typecode = typecode;
	}

	public int getTypeCode() {
	    return typecode;
	}

	public static CharacterSet get(int code) {
	    return lookup.get(code);
	}
    }
}

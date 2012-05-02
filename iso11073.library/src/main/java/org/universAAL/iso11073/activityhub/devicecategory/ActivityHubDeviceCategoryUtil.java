package org.universAAL.iso11073.activityhub.devicecategory;
 
 /**
  * Definition of device categories from ISO 11073-10471 Nomenclature
  * plus util mehtods.
  * 
  * @author Thomas Fuxreiter (foex@gmx.at)
  */
public abstract class ActivityHubDeviceCategoryUtil {

	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static ActivityHubDeviceCategory toActivityHubDevice(String str)
    {
        try {
            return ActivityHubDeviceCategory.valueOf(str);
        } 
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * device category format from ISO 11073-10471 Nomenclature
     */
    public enum ActivityHubDeviceCategory {
    	MDC_AI_TYPE_SENSOR_FALL,
    	MDC_AI_TYPE_SENSOR_PERS,
    	MDC_AI_TYPE_SENSOR_SMOKE,
    	MDC_AI_TYPE_SENSOR_CO,
    	MDC_AI_TYPE_SENSOR_WATER,
    	MDC_AI_TYPE_SENSOR_GAS,
    	MDC_AI_TYPE_SENSOR_MOTION,
    	MDC_AI_TYPE_SENSOR_PROPEXIT,
    	MDC_AI_TYPE_SENSOR_ENURESIS,
    	MDC_AI_TYPE_SENSOR_CONTACTCLOSURE,
    	MDC_AI_TYPE_SENSOR_USAGE,
    	MDC_AI_TYPE_SENSOR_SWITCH,
    	MDC_AI_TYPE_SENSOR_DOSAGE,
    	MDC_AI_TYPE_SENSOR_TEMP

    	//	ISO11073_CONTACTCLOSURESENSOR,
    	//	ISO11073_MOTIONSENSOR,
    	//	ISO11073_SWITCHSENSOR
    }

	
}

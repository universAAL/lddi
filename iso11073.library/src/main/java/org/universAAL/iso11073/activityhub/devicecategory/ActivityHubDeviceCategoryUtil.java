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
    	MDC_AI_TYPE_SENSOR_FALL(1),
    	MDC_AI_TYPE_SENSOR_PERS(2),
    	MDC_AI_TYPE_SENSOR_SMOKE(3),
    	MDC_AI_TYPE_SENSOR_CO(4),
    	MDC_AI_TYPE_SENSOR_WATER(5),
    	MDC_AI_TYPE_SENSOR_GAS(6),
    	MDC_AI_TYPE_SENSOR_MOTION(7),
    	MDC_AI_TYPE_SENSOR_PROPEXIT(8),
    	MDC_AI_TYPE_SENSOR_ENURESIS(9),
    	MDC_AI_TYPE_SENSOR_CONTACTCLOSURE(10),
    	MDC_AI_TYPE_SENSOR_USAGE(11),
    	MDC_AI_TYPE_SENSOR_SWITCH(12),
    	MDC_AI_TYPE_SENSOR_DOSAGE(13),
    	MDC_AI_TYPE_SENSOR_TEMP(14);

    	private int typecode;
    	
    	private ActivityHubDeviceCategory(int typecode) {
    		this.typecode = typecode;
    	}
    	
    	public int getTypeCode() {
    		return typecode;
    	}
    	
    	//	ISO11073_CONTACTCLOSURESENSOR,
    	//	ISO11073_MOTIONSENSOR,
    	//	ISO11073_SWITCHSENSOR
    }

	
}

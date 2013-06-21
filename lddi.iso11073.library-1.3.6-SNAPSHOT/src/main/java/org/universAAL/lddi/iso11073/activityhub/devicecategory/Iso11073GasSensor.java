package org.universAAL.lddi.iso11073.activityhub.devicecategory;

import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;

/**
 * OSGi DeviceCategory for Activityhub Gas sensor
 * 
 * In general DeviceCategories specify:
 * - rules and interfaces needed for the communication between device service
 * and driver service. Devices must implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values used by DeviceManager
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface Iso11073GasSensor extends ActivityHubBaseDeviceCategory {

	// used in driver match method
	public static String MY_DEVICE_CATEGORY = ActivityHubDeviceCategory.
		MDC_AI_TYPE_SENSOR_GAS.toString();
	
}

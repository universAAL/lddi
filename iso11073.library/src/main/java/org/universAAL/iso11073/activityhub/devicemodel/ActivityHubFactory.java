package org.universAAL.iso11073.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;


/**
 * Factory for the ActivityHub sensors according to ISO 11073 - 
 * Part 10471 (Indepentend living activity hub), edition 2010-05-01
 * 
 * @author Thomas Fuxreiter
 */
public class ActivityHubFactory {
	
    public static ActivityHubSensor createInstance(ActivityHubDeviceCategory deviceName,
    		ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
	
    	switch (
//    			ActivityHubDeviceCategoryUtil.toActivityHubDevice(
    					deviceName
//    					)
    					) {
    	
//    		case MDC_AI_TYPE_SENSOR_FALL:
//    			return new ActivityHubSensor(deviceName, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_PERS:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_SMOKE:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_CO:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_WATER:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_GAS:
//    			return new FallSensor();
    		case MDC_AI_TYPE_SENSOR_MOTION:
    			return new MotionSensor(deviceName, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_PROPEXIT:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_ENURESIS:
//    			return new FallSensor();
    		case MDC_AI_TYPE_SENSOR_CONTACTCLOSURE:
    			return new ContactClosureSensor(deviceName, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_USAGE:
//    			return new FallSensor();
    		case MDC_AI_TYPE_SENSOR_SWITCH:
    			return new SwitchSensor(deviceName, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_DOSAGE:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_TEMP:
//    			return new FallSensor();
    	}

    	return null;
    }
}

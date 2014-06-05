/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
     See the NOTICE file distributed with this work for additional
     information regarding copyright ownership

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

package org.universAAL.lddi.lib.activityhub.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;


/**
 * Factory for the ActivityHub sensors according to ISO 11073 - 
 * Part 10471 (Indepentend living activity hub), edition 2010-05-01
 * 
 * @author Thomas Fuxreiter
 */
public class ActivityHubFactory {
	
    public static ActivityHubSensor createInstance(ActivityHubDeviceCategory deviceCategory,
    		ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
	
    	switch (
//    			ActivityHubDeviceCategoryUtil.toActivityHubDevice(
    					deviceCategory
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
    			return new MotionSensor(deviceCategory, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_PROPEXIT:
//    			return new FallSensor();
//    		case MDC_AI_TYPE_SENSOR_ENURESIS:
//    			return new FallSensor();
    		case MDC_AI_TYPE_SENSOR_CONTACTCLOSURE:
    			return new ContactClosureSensor(deviceCategory, deviceLocation, deviceId, logger);
    		case MDC_AI_TYPE_SENSOR_USAGE:
    			return new UsageSensor(deviceCategory, deviceLocation, deviceId, logger);
    		case MDC_AI_TYPE_SENSOR_SWITCH:
    			return new SwitchSensor(deviceCategory, deviceLocation, deviceId, logger);
//    		case MDC_AI_TYPE_SENSOR_DOSAGE:
//    			return new FallSensor();
    		case MDC_AI_TYPE_SENSOR_TEMP:
    			return new TemperatureSensor(deviceCategory, deviceLocation, deviceId, logger);
    	}

    	return null;
    }
}

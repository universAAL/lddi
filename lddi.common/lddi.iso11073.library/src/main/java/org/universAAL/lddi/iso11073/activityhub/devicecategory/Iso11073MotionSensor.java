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

package org.universAAL.lddi.iso11073.activityhub.devicecategory;

import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;

/**
 * OSGi DeviceCategory for Activityhub motion sensor
 * 
 * In general DeviceCategories specify:
 * - rules and interfaces needed for the communication between device service
 * and driver service. Devices must implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface Iso11073MotionSensor extends ActivityHubBaseDeviceCategory {

	// used in driver match method
	public static String MY_DEVICE_CATEGORY = ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_MOTION.toString(); 
		//"ISO11073_MOTIONSENSOR";

//	// from OSGi DAS Spec
//	public static int MATCH_SERIAL	= 10;	// an exact match including the serial number
//	public static int MATCH_VERSION	= 8;	// matches the right class, make model and version
//	public static int MATCH_MODEL	= 6;	// matches the right class and make model
//	public static int MATCH_MAKE	= 4;	// matches the make
//	public static int MATCH_CLASS	= 2;	// only matches the class
//
//	// example properties
//	// from where should we get this data?
//	public static String CLASS 	= "-"; // class description
//	public static String MODEL 			= "-"; // definition of the model, e.g. audio, video, serial
//	public static String MANUFACTURER 	= "-";
//	public static String REVISION		= "-";
//	public static String SERIAL			= "-";

}

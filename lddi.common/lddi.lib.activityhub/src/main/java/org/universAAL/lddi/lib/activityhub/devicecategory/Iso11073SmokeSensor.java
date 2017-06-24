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

package org.universAAL.lddi.lib.activityhub.devicecategory;

import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;

/**
 * OSGi DeviceCategory for Activityhub Smoke sensor
 *
 * In general DeviceCategories specify: - rules and interfaces needed for the
 * communication between device service and driver service. Devices must
 * implement this IF.
 *
 * - a set of service registration properties, their data types and semantics
 * (mandatory or optional)
 *
 * - a range of match values used by DeviceManager
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface Iso11073SmokeSensor extends ActivityHubBaseDeviceCategory {

	// used in driver match method
	public static String MY_DEVICE_CATEGORY = ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_SMOKE.toString();

}

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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Definition of device categories from ISO 11073-10471 Nomenclature plus util
 * mehtods.
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
	public static ActivityHubDeviceCategory toActivityHubDevice(String str) {
		try {
			return ActivityHubDeviceCategory.valueOf(str);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * device category format from ISO 11073-10471 Nomenclature
	 */
	public enum ActivityHubDeviceCategory {
		// each constant implicitly calls a constructor
		MDC_AI_TYPE_SENSOR_FALL(1), MDC_AI_TYPE_SENSOR_PERS(2), MDC_AI_TYPE_SENSOR_SMOKE(3), MDC_AI_TYPE_SENSOR_CO(
				4), MDC_AI_TYPE_SENSOR_WATER(5), MDC_AI_TYPE_SENSOR_GAS(6), MDC_AI_TYPE_SENSOR_MOTION(
						7), MDC_AI_TYPE_SENSOR_PROPEXIT(8), MDC_AI_TYPE_SENSOR_ENURESIS(
								9), MDC_AI_TYPE_SENSOR_CONTACTCLOSURE(10), MDC_AI_TYPE_SENSOR_USAGE(
										11), MDC_AI_TYPE_SENSOR_SWITCH(
												12), MDC_AI_TYPE_SENSOR_DOSAGE(13), MDC_AI_TYPE_SENSOR_TEMP(14);

		private int typecode;

		private static final Map<Integer, ActivityHubDeviceCategory> lookup = new HashMap<Integer, ActivityHubDeviceCategory>();

		static {
			for (ActivityHubDeviceCategory s : EnumSet.allOf(ActivityHubDeviceCategory.class))
				lookup.put(s.getTypeCode(), s);
		}

		// Constructor
		private ActivityHubDeviceCategory(int typecode) {
			this.typecode = typecode;
		}

		public int getTypeCode() {
			return typecode;
		}

		public static ActivityHubDeviceCategory get(int code) {
			return lookup.get(code);
		}
	}
}

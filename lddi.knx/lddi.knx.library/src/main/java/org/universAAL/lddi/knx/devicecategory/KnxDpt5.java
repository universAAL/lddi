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

package org.universAAL.lddi.knx.devicecategory;

import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;

/**
 * Base DeviceCategory for KNX datapoint type U8 (8-Bit Unsigned Value).
 * Possible values are from 0 - 255.
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
public interface KnxDpt5
// extends KnxBaseDeviceCategory
{

	public static KnxDeviceCategory MY_DEVICE_CATEGORY = KnxDeviceCategory.KNX_DPT_5;
	// public static String MY_DEVICE_CATEGORY = "KnxDpt5";

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

	// default max/min constants for all dpt5 devices
	public static byte DEFAULT_MAX_VALUE = (byte) 0xFF;
	public static byte DEFAULT_MIN_VALUE = (byte) 0x00;

	// constants for specific dpt5 devices
	/**
	 * 5.001 - DPT_Scaling From 0 - 100% - This gives a resolution of about 0,4%
	 * (100 / 256 = 0,390625) 80h = 50%; FFh = 100%
	 */
	public static float RESOLUTION_5_001 = (float)100 / 256;

	/**
	 * 5.003 - DPT_Angle From 0 - 360°- This gives a resolution of about 1,4°
	 * (360 / 256 = 1,40625)
	 */
	public static float RESOLUTION_5_003 = (float)360 / 256;

	/**
	 * 5.004 - DPT_Percent_U8 From 0 - 255% !! - Resolution is 1 32h = 50%; 64h
	 * = 100%; FFh = 255%
	 */
	public static float RESOLUTION_5_004 = 1f;

	/**
	 * 5.005 - DPT_DecimalFactor no spec !!
	 */

	/**
	 * 5.010 - DPT_Value_1_Ucount From 0 - 255 - Resolution is 1 //64h = 100
	 * counter pulses; FFh = 255 counter pulses
	 */
	public static float RESOLUTION_5_010 = 1f;

	/**
	 * 5.006 - DPT_Tariff This DPT shall be used for reading and setting tariff
	 * information. A large number of different tariffs are defined and these
	 * are specific to the country and even to the supplier. Therefore, the
	 * mapping between a tariff and this DPT is not standardised. For usability
	 * and interpretability of the tariff information by the end user, the
	 * product description should give clear information about this mapping
	 */

}

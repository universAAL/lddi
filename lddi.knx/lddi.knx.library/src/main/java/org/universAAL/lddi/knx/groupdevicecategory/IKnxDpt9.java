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

package org.universAAL.lddi.knx.groupdevicecategory;

import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;

/**
 * Base GroupDeviceCategory for KNX datapoint type "2-Octet Float Value".
 *
 * Specification from KNX Datapoint Types v1.07.00 AS :
 *
 * Format: 2 octets: F16 MSB LSB float value |-------- --------| encoding
 * MEEEEMMM MMMMMMMM FloatValue = (0,01*M)*2(E) E = [0 : 15] M = [-2 048 : 2
 * 047], two's complement notation For all Datapoint Types 9.xxx, the encoded
 * value 7FFFh shall always be used to denote invalid data. Possible values are
 * from 0 - 255. Range: [-671 088,64 : 670 760,96] PDT: PDT_KNX_FLOAT
 *
 *
 * In general GroupDeviceCategories specify: - rules and interfaces needed for
 * the communication between OSGi groupDevice service and driver service. Both
 * of them (groupDevice and driver) implement this IF.
 *
 * - a set of service registration properties, their data types and semantics
 * (mandatory or optional)
 *
 * - a range of match values
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IKnxDpt9 {

	public static KnxGroupDeviceCategory MY_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_9;
	// "IKnxDpt9";

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

	// default max/min constants for all dpt9 devices
	public static byte[] DEFAULT_MAX_VALUE = { (byte) 0x07, (byte) 0xFF }; // 2047
																			// two's
																			// complement
																			// notation!
	public static byte[] DEFAULT_MIN_VALUE = { (byte) 0x80, (byte) 0x00 }; // -2048
																			// two's
																			// complement
																			// notation!
	public static byte[] DEFAULT_INVALID_VALUE = { (byte) 0x7F, (byte) 0xFF }; // all
																				// bits
																				// 1
																				// except
																				// MSb

	// constants for specific dpt9 devices
	/**
	 * 9.001 - DPT_Value_Temp From -273&deg;C to +670760&deg;C - Resolution:
	 * 0,01&deg;C
	 */
	public static float RESOLUTION_9_001 = (float) 1 / 100;

}

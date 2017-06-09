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
 * Base GroupDeviceCategory for KNX datapoint types B2 (2 bit).
 * 
 * c = control bit v = value bit (according to DPT 1.xxx)
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
public interface IKnxDpt2 {

	public static KnxGroupDeviceCategory MY_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_2;
	// public static String MY_DEVICE_CATEGORY = "IKnxDpt2";

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

	// 2-bit encoding
	// c v
	// 0 0 No control
	// 0 1 No control
	// 1 0 Control. Function value 0
	// 1 1 Control. Function value 1
	//
	// datapoint sub types (similar to Dpt1):
	// 2.001 DPT_Switch_Control G
	// 2.002 DPT_Bool_Control G
	// 2.003 DPT_Enable_Control FB
	// 2.004 DPT_Ramp_Control FB
	// 2.005 DPT_Alarm_Control FB
	// 2.006 DPT_BinaryValue_Control FB
	// 2.007 DPT_Step_Control FB
	// 2.008 DPT_Direction1_Control FB
	// 2.009 DPT_Direction2_Control FB
	// 2.010 DPT_Start_Control FB
	// 2.011 DPT_State_Control FB
	// 2.012 DPT_Invert_Control FB

	// default on/off constants for all dpt2 devices
	public static byte DEFAULT_VALUE_OFF_CONTROL_OFF = (byte) 0x80;
	public static byte DEFAULT_VALUE_ON_CONTROL_OFF = (byte) 0x81;
	public static byte DEFAULT_VALUE_OFF_CONTROL_ON = (byte) 0x82;
	public static byte DEFAULT_VALUE_ON_CONTROL_ON = (byte) 0x83;

}

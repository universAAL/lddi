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
 * Base GroupDeviceCategory for KNX datapoint types B1 (1 bit).
 * 
 * In general GroupDeviceCategories specify:
 * - rules and interfaces needed for the communication between OSGi groupDevice service
 * and driver service. Both of them (groupDevice and driver) implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IKnxDpt1
{
	
	public static KnxGroupDeviceCategory MY_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_1; 
		// "IKnxDpt1";
	
	// from OSGi DAS Spec
	public static int MATCH_SERIAL	= 10;	// an exact match including the serial number
	public static int MATCH_VERSION	= 8;	// matches the right class, make model and version
	public static int MATCH_MODEL	= 6;	// matches the right class and make model
	public static int MATCH_MAKE	= 4;	// matches the make
	public static int MATCH_CLASS	= 2;	// only matches the class
	

	// example properties
	// this data may not be available from ETS4 import!
	public static String CLASS 	= "-"; // class description
	public static String MODEL 			= "-"; // definition of the model, e.g. audio, video, serial
	public static String MANUFACTURER 	= "-";
	public static String REVISION		= "-";
	public static String SERIAL			= "-";
	

	// default on/off constants for all dpt1 devices
	public static byte DEFAULT_VALUE_ON =  (byte) 1;
	public static byte DEFAULT_VALUE_OFF = (byte) 0;

//	// constants for specific dpt1 devices

//	/** 1.001 - DPT_Switch */
//	/** 1 = on */
//	public static byte DEFAULT_VALUE_ON_1_001 =  DEFAULT_VALUE_ON;
//	/** 0 = off */
//	public static byte DEFAULT_VALUE_OFF_1_001 = DEFAULT_VALUE_OFF;
//	
//	/** 1.005 - DPT_Alarm */
//	/** 1 = alarm */
//	public static byte DEFAULT_VALUE_ON_1_005 =  DEFAULT_VALUE_ON;
//	/** 0 = no alarm */
//	public static byte DEFAULT_VALUE_OFF_1_005 = DEFAULT_VALUE_OFF;
//
//	/** 1.009 - DPT_OpenClose */
//	/** 1 = close */
//	public static byte DEFAULT_VALUE_ON_1_009 =  DEFAULT_VALUE_ON;
//	/** 0 = open */
//	public static byte DEFAULT_VALUE_OFF_1_009 = DEFAULT_VALUE_OFF;

}

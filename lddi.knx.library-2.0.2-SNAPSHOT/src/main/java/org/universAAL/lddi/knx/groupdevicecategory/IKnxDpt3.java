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
 * Base GroupDeviceCategory for KNX datapoint type B1U3 (4-Bit). Possible values are
 * Bit 4: c = {0,1} (Decrease, Increase).
 * 
 * Bit 1-3: StepCode = {000b ... 111b} (The amount of intervals into which the
 * range of 0 % … 100 % is subdivided, or the break indication) 
 * 
 * 001b ... 111b:
 * Step Number of intervals = 2^(stepcode-1)
 * Maximum resolution is 64 = 2^(7-1)

 * 000b:  Break 
 * 
 * In general GroupDeviceCategories specify: 
 * - rules and interfaces needed for the communication between OSGi groupDevice service and driver service. Both of them (groupDevice and driver) 
 * implement this IF.
 * 
 * - a set of service registration properties, their data types and semantics
 * (mandatory or optional)
 * 
 * - a range of match values
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IKnxDpt3
{

	public static KnxGroupDeviceCategory MY_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_3; 
//    public static String MY_DEVICE_CATEGORY = "IKnxDpt3";

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

    
    // default constants for all dpt3 devices
    public static String STEPCODE_BREAK = "break";
    
    
    // constants for specific dpt3 devices
    /**
     * 3.007 - DPT_Control_Dimming
     */
    public static String INCREASE_3_007 = "increase";
    public static String DECREASE_3_007 = "decrease";

    /**
     * 3.008 - DPT_Control_Blinds
     * This DPT can be used both for the relative positioning of the vertical blinds 
     * positions as well as for the relative positioning of the angle of the slats.
     */
    public static String UP_3_008 = "up";
    public static String DOWN_3_008 = "down";
 
}

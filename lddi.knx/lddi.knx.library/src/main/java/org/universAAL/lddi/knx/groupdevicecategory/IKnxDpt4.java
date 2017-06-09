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

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;

/**
 * Base GroupDeviceCategory for KNX datapoint type Character Set (8-Bit Unsigned
 * Value). Possible values are from 0 - 255.
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
public interface IKnxDpt4 {

	public static KnxGroupDeviceCategory MY_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_4;
	// public static String MY_DEVICE_CATEGORY = "IKnxDpt4";

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

	// default max/min constants for all dpt4 devices
	// does this make sense???
	public static byte DEFAULT_MAX_VALUE = (byte) 0xFF;
	public static byte DEFAULT_MIN_VALUE = (byte) 0x00;

	// constants for specific dpt4 devices according to RFC2978
	/**
	 * 4.001 - DPT_Char_ASCII (ANSI_X3.4-1968) Range: 0 - 127 (The most
	 * significant bit shall always be 0!) Is this field necessary???
	 */
	public static boolean CHARACTER_SET_ASCII = false;

	/**
	 * 4.002 - DPT_Char_8859_1 (ISO_8859-1:1987) Range: 0 - 255 Is this field
	 * necessary???
	 */
	public static boolean CHARACTER_SET_8859_1 = false;

	public enum CharacterSet {
		CHARACTER_SET_ASCII(1), CHARACTER_SET_8859_1(2);

		private int typecode;

		private static final Map<Integer, CharacterSet> lookup = new HashMap<Integer, CharacterSet>();

		static {
			for (CharacterSet s : EnumSet.allOf(CharacterSet.class))
				lookup.put(s.getTypeCode(), s);
		}

		private CharacterSet(int typecode) {
			this.typecode = typecode;
		}

		public int getTypeCode() {
			return typecode;
		}

		public static CharacterSet get(int code) {
			return lookup.get(code);
		}
	}
}

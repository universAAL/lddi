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

package org.universAAL.lddi.knx.groupdevicemodel;

import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt3;

/**
 * Concrete implementation of KNX group devices for KNX data type 3.***.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt3GroupDevice extends KnxGroupDevice implements IKnxDpt3 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt3GroupDevice() {
		super(MY_DEVICE_CATEGORY);
	}

	/**
	 * Calculate step code from knx message payload. last 4-bit of data byte
	 * encoding |xxxx cSSS| c = {0,1} control SSS = [000b - 111b] Stepcode
	 *
	 * @return null if the datapoint type is not implemented. Now implemented:
	 *         3.007 and 3.008
	 */
	public static String calculateStepCode(byte payload, int datapointTypeSubNumber) {
		byte c = (byte) ((payload & 0x08) >> 3);

		// for the step code only the last bit is important
		byte S = (byte) (payload & 0x01);

		switch (datapointTypeSubNumber) {

		case 7: // dimming
			switch (c) {
			case 0: // getting darker
				if (S == 1)
					return DECREASE_3_007;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			case 1: // getting brighter
				if (S == 1)
					return INCREASE_3_007;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			}

		case 8: // blinds
			switch (c) {
			case 0: // moving down
				if (S == 1)
					return DOWN_3_008;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			case 1: // moving up
				if (S == 1)
					return UP_3_008;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			}

		default:
			return null;
		}
	}

	/**
	 * Calculate Step number of interval from stepcode. stepcode are bits 1-3
	 * Step Number of intervals = 2^(stepcode-1); max. 64
	 */
	public static int calculateStepNumberOfInterval(byte payload) {
		// not implemented yet!
		return 0;
	}

}

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

import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt5;

/**
 * Concrete implementation of KNX group devices for KNX data type 5.***.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt5GroupDevice extends KnxGroupDevice implements IKnxDpt5 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt5GroupDevice() {
		super(MY_DEVICE_CATEGORY);
	}

	/**
	 * calculate percentage according to datapoint sub-type. 8-bit extra data
	 * byte; e.g. 80:b3; 80:33
	 *
	 * @return -1 if there is no detailed specification of the given
	 *         datapointTypeSubNumber in the KNX standard.
	 * @return 0 if the given datapointTypeSubNumber is not defined in the KNX
	 *         standard.
	 */
	public static float calculatePercentage(byte[] payload, int datapointTypeSubNumber) {
		int i_value = payload[1] & 0xFF; // get unsigned byte

		switch (datapointTypeSubNumber) {
		case 1:
			return (float) i_value * RESOLUTION_5_001;
		case 3:
			return (float) i_value * RESOLUTION_5_003;
		case 4:
			return (float) i_value * RESOLUTION_5_004;
		case 5:
			return -1f;
		case 6:
			return -1f;
		case 10:
			return (float) i_value * RESOLUTION_5_010;
		default:
			return 0f;
		}
	}

	/**
	 * calculate byte from float value according to datapoint sub-type. 8-bit
	 * extra data byte; e.g. 00:b3; 00:33
	 *
	 * @return null if there is no detailed specification of the given
	 *         datapointTypeSubNumber in the KNX standard, or the given
	 *         datapointTypeSubNumber is not defined in the KNX standard.
	 */
	public static byte[] createPayloadFromFloatValue(float value, int datapointTypeSubNumber) {
		// apci byte (always 0 here) + payload (1 byte)
		byte[] ret = new byte[] { 0, 0 };

		switch (datapointTypeSubNumber) {
		case 1:
			ret[1] = (byte) (value / RESOLUTION_5_001);
			return ret;
		case 3:
			ret[1] = (byte) Math.round(value * RESOLUTION_5_003);
			return ret;
		case 4:
			ret[1] = (byte) Math.round(value * RESOLUTION_5_004);
			return ret;
		case 5:
			return null;
		case 6:
			return null;
		case 10:
			ret[1] = (byte) Math.round(value * RESOLUTION_5_010);
			return ret;
		default:
			return null;
		}
	}
}

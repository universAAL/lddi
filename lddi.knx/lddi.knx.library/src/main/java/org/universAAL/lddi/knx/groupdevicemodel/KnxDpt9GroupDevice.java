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

import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt9;

/**
 * Concrete implementation of KNX group devices for KNX data type 9.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9GroupDevice extends KnxGroupDevice implements IKnxDpt9 {

    /**
     * empty constructor for factory
     */
    public KnxDpt9GroupDevice() {
    	super(MY_DEVICE_CATEGORY);
    }


	/**
	 * Calculate float value from knx message payload.
     * 				MSB			LSB
     * float value |-------- --------|
     * encoding 	MEEEEMMM MMMMMMMM
     * FloatValue = (0,01*M)*2(E)
     * E = [0 : 15]
     * M = [-2 048 : 2 047], two's complement notation
     * 
	 */
	public static float calculateFloatValue(byte[] payload) {
		// there are 3 bytes payload for a temperature event where the last 2 are important
		byte MSB = payload[1]; 
		byte LSB = payload[2];
		
		byte M_MSB = (byte) (MSB & 0x87);
		byte M_LSB = (byte) (LSB & 0xFF);
		
		byte E = (byte) ((MSB & 0x78) >> 3);

		int e = Integer.parseInt( Byte.toString(E) );
		
		short m = (short) (M_MSB << 8 | (M_LSB & 0xFF));
		
		float result = (float) ((0.01*m)*(Math.pow(2, e)));
		//System.out.println("*****************************float result: " + result);
		return result;
	}
	
	
	public static byte[] createPayloadFromFloatValue(float value) {
		
		return null;
	}
}

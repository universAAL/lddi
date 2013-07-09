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

package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt5;

/**
 * Concrete implementation of KNX devices for KNX data type 5.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt5Device extends KnxDevice implements KnxDpt5 {

    /**
     * empty constructor for factory
     */
    public KnxDpt5Device() {
    	super(MY_DEVICE_CATEGORY);
    }

    /**
     * calculate percentage according to datapoint sub-type
     * 8-bit extra data byte; e.g. 80:b3; 80:33
     * 
     * @return -1 if there is no detailed specification of the given datapointTypeSubNumber in the KNX standard.
     * @return 0 if the given datapointTypeSubNumber is not defined in the KNX standard.
     */
	public static float calculatePercentage(byte[] payload, int datapointTypeSubNumber) {
		int i_value = payload[0] & 0xFF; // get unsigned byte
		
		switch (datapointTypeSubNumber) {
		case 1:
			return (float)i_value * RESOLUTION_5_001; 
		case 3:
			return (float)i_value * RESOLUTION_5_003;
		case 4:
			return (float)i_value * RESOLUTION_5_004;
		case 5:
			return -1f;
		case 6:
			return -1f;
		case 10:
			return (float)i_value * RESOLUTION_5_010;
		default:
			return 0f;
		}
		
	}

}

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

import org.universAAL.lddi.knx.devicecategory.KnxDpt9;

/**
 * Concrete implementation of KNX devices for KNX data type 9.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9Device extends KnxDevice implements KnxDpt9 {

    /**
     * empty constructor for factory
     */
    public KnxDpt9Device() {
    	super(MY_DEVICE_CATEGORY);
    }

    @Deprecated
    public void newMessageFromKnxBus(byte[] event) {
    	// not used in device; this method is called in driver!
    }

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicecategory.KnxDpt9#calculateFloatValue(byte[])
	 */
    @Deprecated
	public float calculateFloatValue(byte[] payload) {
    	// not used in device; this method is called in driver!
		return 0;
	}
}

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

import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt1;

/**
 * Concrete implementation of KNX group devices for KNX data type 1.***.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1GroupDevice extends KnxGroupDevice implements IKnxDpt1 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt1GroupDevice() {
		super(MY_DEVICE_CATEGORY);
	}

}

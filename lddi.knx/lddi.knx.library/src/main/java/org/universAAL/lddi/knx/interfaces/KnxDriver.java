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

package org.universAAL.lddi.knx.interfaces;

import org.universAAL.lddi.knx.groupdevicemodel.KnxGroupDevice;

/**
 * This abstract class is designed to help developing a knx driver.
 * It stores information about the coupled groupDevice.
 * It provides an service tracker for the attached groupDevice service.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public abstract class KnxDriver {
	
	// the groupDevice I am driving
	public KnxGroupDevice groupDevice;

	/** upper layer instance */
	protected IKnxDriverClient client; //->uAAL bus/exporter 

	public KnxDriver() {
	}
	
	public KnxDriver(IKnxDriverClient client) {
		this.client = client;
	}
	
	/**
	 * store the groupDevice
	 * link this driver to the groupDevice
	 * @param the groupDevice to set
	 */
	public final boolean setgroupDevice(KnxGroupDevice groupDevice) {
		this.groupDevice = groupDevice;

		// add connected driver to driverList in client, if available
		if ( this.client != null )
			this.client.addDriver(this.groupDevice.getGroupDeviceId(), this.groupDevice.getGroupDeviceCategory(), this);
		
		return attachDriver();
	}
	
	/**
 	 * coupling this driver to groupDevice reference
 	 * method is abstract because of cast to groupDevice category IF
	 * @param id
	 */
	//protected abstract boolean attachDriver();
	
	
	/**
 	 * coupling this driver to groupDevice reference
	 * @param id
	 */
	protected boolean attachDriver() {
		if (this.groupDevice != null) {
			this.groupDevice.addDriver( (IKnxReceiveMessage) this);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * decoupling this driver from groupDevice reference
	 */
	public final void detachDriver() {
		if (this.groupDevice != null)
			this.groupDevice.removeDriver();
	}
	
	/**
	 * Remove this driver from the driver list in knx network driver
	 * 
	 * @param groupDevice the groupDevice to remove
	 */
	public final void removeDriver() {
		// remove driver from driverList in my consuming client
		this.client.removeDriver(this.groupDevice.getGroupDeviceId(), this);
	}
	
    /**
     * This method remove, if present, the "0x" prefix of the hexValue variable
     * @param hexValue string containing an hex value
     * @return the same string without prefix
     */
	public static String clearHexValue(String hexValue) {
		String correctHexValue;
		if(hexValue.startsWith("0x")){
			correctHexValue=hexValue.substring(2);
		}else
		{
			correctHexValue=hexValue;
		}
		return correctHexValue;
	}

}

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

package org.universAAL.lddi.lib.activityhub.driver.interfaces;

/**
 * An actuator device is controllable. It should implement this interface.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IActivityHubActuator {
	/***
	 * The specific drivers instances have to implement this method to receive
	 * events from the consuming bundles (e.g. uAAL context bus events)
	 * 
	 * @param deviceAddress
	 *            address of the device or the group that fire the message
	 * @param message
	 *            array of byte containing the information of the status or
	 *            command
	 */
	public abstract void newActuatorCommand(String deviceId, byte[] message);

}

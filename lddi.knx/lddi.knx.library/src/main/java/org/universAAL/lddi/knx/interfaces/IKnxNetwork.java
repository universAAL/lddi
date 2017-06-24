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
import org.universAAL.lddi.knx.utils.KnxCommand;

public interface IKnxNetwork {
	public static String MANUFACTURER = "KONNEX";
	public static String GROUP_ADDRESS = "groupAddress";
	// public static String NOTIFICATION_ADDRESS = "notificationAddress";
	public static String COMMAND_NAME = "realName";
	public static String COMMAND_VALUE = "hexValue";

	public static String TIME = "time";
	public static char DAFAULT_READ_CHAR = '4';
	public static char DAFAULT_STATUS_CHAR = '8';

	// public static Object NOTIFICATION_NAME = "notificationName";
	public void addGroupDevice(String groupDeviceId, KnxGroupDevice groupDevice);

	public void removeGroupDevice(String groupDeviceId, KnxGroupDevice groupDevice);
	// public KnxConfiguration parseConfiguration(Properties configuration);

	// messaging
	/**
	 * Sending KNX message to KNX bus.
	 *
	 * @param groupDeviceId
	 *            KNX group address (e.g. 1/2/3)
	 * @param event
	 *            payload starting with apci/data byte!
	 */
	public void sendMessageToKnxBus(String groupDeviceId, byte[] event);

	public void requestState(String groupDeviceId);

	// manual commands on OSGi shell
	public void sendCommand(String groupDeviceId, boolean command);

	public void sendCommand(String groupDeviceId, boolean command, KnxCommand commandType);
}

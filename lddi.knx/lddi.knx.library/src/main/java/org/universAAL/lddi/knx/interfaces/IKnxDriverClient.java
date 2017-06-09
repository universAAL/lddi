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

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;

/**
 * Applications using KNXDriver should implement this IF to get event messages
 * from real Sensors (incoming) and to send events to Actuators (outgoing).
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IKnxDriverClient {

	/** couple KNX driver to upper layer */
	public void addDriver(String groupDeviceId, KnxGroupDeviceCategory groupDeviceCategory, KnxDriver knxDriver);

	public void removeDriver(String groupDeviceId, KnxDriver knxDriver);

	public LogService getLogger();

	/**
	 * get event message from KNX DPT1 driver (bool value).
	 * 
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param datapointTypeMainNubmer
	 *            (i.e. 1 for 1.018)
	 * @param datapointTypeSubNubmer
	 *            (i.e. 18 for 1.018)
	 * @param value
	 *            (on/off)
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, int datapointTypeSubNubmer,
			boolean value);

	/**
	 * send event message to KNX DPT1 driver (bool value).
	 * 
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param value
	 *            (on/off)
	 */
	public void sendSensorEvent(String groupDeviceId, boolean value);

	/**
	 * get event message from KNX DPT3 driver (String code).
	 * 
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param datapointTypeMainNubmer
	 *            (i.e. 1 for 1.018)
	 * @param datapointTypeSubNubmer
	 *            (i.e. 18 for 1.018)
	 * @param code
	 *            (e.g. break, increase, decrease)
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, int datapointTypeSubNubmer,
			String code);

	/**
	 * send event message to KNX DPT3 driver (String code).
	 *
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param code
	 *            (e.g. break, increase, decrease)
	 */
	public void sendSensorEvent(String groupDeviceId, String code);

	/**
	 * get event message from KNX DPT5 or DPT9 driver (float value).
	 * 
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param datapointTypeMainNubmer
	 *            (i.e. 1 for 1.018)
	 * @param datapointTypeSubNubmer
	 *            (i.e. 18 for 1.018)
	 * @param value
	 *            (e.g. temperature value 25.2 or dimming percentage 70.5)
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, int datapointTypeSubNubmer,
			float value);

	/**
	 * send event message to KNX DPT5 or DPT9 driver (float value).
	 * 
	 * @param groupDeviceId
	 *            (e.g. knx group address 1/2/3)
	 * @param value
	 *            (e.g. temperature value 25.2 or dimming percentage 70.5)
	 */
	public void sendSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, int datapointTypeSubNubmer,
			float value);

}

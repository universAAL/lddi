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

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;
import org.universAAL.lddi.knx.interfaces.IKnxReceiveMessage;
import org.universAAL.lddi.knx.interfaces.IKnxNetwork;
import org.universAAL.lddi.knx.interfaces.IKnxSendMessage;
import org.universAAL.lddi.knx.utils.*;

/**
 * One KNX group groupDevice represents one groupAddress (with additional
 * properties) from ETS4 XML export. This groupDevice is registered in OSGi
 * framework.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public abstract class KnxGroupDevice implements Device, IKnxReceiveMessage, IKnxSendMessage {

	/** OSGi DAS properties */
	public KnxGroupDeviceCategory groupDeviceCategory;
	/** intended for end users */
	// public String deviceDescription;
	/** unique serial number for this groupDevice */
	// private String deviceSerial;
	/**
	 * should be set; every time the same hardware is plugged in, the same PIDs
	 * are used
	 */
	// private String servicePid;

	private String groupDeviceId = "-";
	/** including groupAddress and dpt */
	private KnxGroupAddress knxGroupDeviceProperties;

	// private static String KNX_DEVICE_CATEGORY_PREFIX = "KnxDpt";

	protected LogService logger;
	protected IKnxNetwork network;

	/** reference to my driver instance; can be just one! */
	protected IKnxReceiveMessage driver;

	/**
	 * empty constructor for factory
	 */
	public KnxGroupDevice(KnxGroupDeviceCategory knxGroupDeviceCategory) {
		this.groupDeviceCategory = knxGroupDeviceCategory;
	}

	/**
	 * Fill empty groupDevice with parameters and set it alive
	 *
	 * @param knxGroupAddress
	 * @param network
	 * @param logger2
	 */
	public void setParams(KnxGroupAddress knxGroupAddress, IKnxNetwork network, LogService logger) {
		this.knxGroupDeviceProperties = knxGroupAddress;
		this.network = network;
		this.logger = logger;

		// this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX +
		// this.knxDeviceProperties.getDptMain();

		// this.deviceDescription;
		// this.deviceSerial;
		// this.servicePid;

		this.groupDeviceId = this.knxGroupDeviceProperties.getGroupAddress();

		// add groupDevice to deviceList in knx.networkdriver
		this.network.addGroupDevice(this.groupDeviceId, this);

		this.logger.log(LogService.LOG_DEBUG, "Registered groupDevice " + groupDeviceId + " in knx.networkdriver.");
	}

	/** store a driver reference for this groupDevice */
	public void addDriver(IKnxReceiveMessage driverInstance) {
		this.driver = driverInstance;
	}

	/** remove the driver reference of this groupDevice */
	public void removeDriver() {
		this.driver = null;
	}

	/** {@inheritDoc} */
	public void newMessageFromKnxBus(byte[] event) {

		this.logger.log(LogService.LOG_INFO,
				"Device " + this.getGroupDeviceId() + " got value: " + KnxEncoder.convertToReadableHex(event));
		// String.format("%02X", Arrays.toString(value)));

		if (this.driver != null)
			this.driver.newMessageFromKnxBus(event);
		else
			this.logger.log(LogService.LOG_WARNING,
					"No driver for groupDevice " + this.getGroupDeviceId() + " coupled! Cannot forward knx message!");
	}

	/** {@inheritDoc} */
	public void sendMessageToKnxBus(byte[] event) {

		if (this.network != null) {
			this.logger.log(LogService.LOG_INFO, "GroupDevice " + this.getGroupDeviceId() + " forwards payload value "
					+ KnxEncoder.convertToReadableHex(event) + " to Knx network driver");
			this.network.sendMessageToKnxBus(this.groupDeviceId, event);
		} else
			this.logger.log(LogService.LOG_WARNING, "KNX network driver not available! Cannot forward knx message!");
	}

	public void noDriverFound() {
		this.logger.log(LogService.LOG_WARNING,
				"No suitable drivers were found for KNX groupDevice: " + knxGroupDeviceProperties.getGroupAddress());
	}

	/**
	 * @return knxGroupAddress as String "M/S/D"
	 */
	public String getGroupAddress() {
		return this.knxGroupDeviceProperties.getGroupAddress();
	}

	/**
	 * @return knx datapoint type as String "M.mmm"
	 */
	public String getDatapointType() {
		return this.knxGroupDeviceProperties.getDpt();
	}

	/**
	 * Returns 0 on error. {@code for dpt "1.018" this method will return 1}
	 *
	 * @return knx datapoint main type
	 */
	public int getDatapointTypeMainNumber() {
		try {
			return Integer.parseInt(this.knxGroupDeviceProperties.getDptMain());
		} catch (NumberFormatException e) {
			this.logger.log(LogService.LOG_ERROR,
					"Error on converting main datatype number of knx groupDevice " + this.groupDeviceId);
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Returns 0 on error. {@code for dpt "1.018" this method will return 18}
	 *
	 * @return knx datapoint minor type
	 */
	public int getDatapointTypeSubNumber() {
		try {
			return Integer.parseInt(this.knxGroupDeviceProperties.getDptSub());
		} catch (NumberFormatException e) {
			this.logger.log(LogService.LOG_ERROR, "Error on converting minor datatype number of knx groupDevice "
					+ this.groupDeviceId + " with dpt " + this.getDatapointType());
			e.printStackTrace();
		}
		return 0;
	}

	// /**
	// * @return name of the location, e.g. Main room, Living room
	// */
	// public String getDeviceLocation() {
	// return this.knxGroupDeviceProperties.getBpName();
	// }
	//
	// /**
	// * @return type of the location, e.g. Floor, Room, Corridor, Cabinet,
	// Stairway or Building Part
	// */
	// public String getDeviceLocationType() {
	// return this.knxGroupDeviceProperties.getBpType();
	// }
	//
	// /**
	// * @return description of the location, e.g. Room, Corridor
	// */
	// public String getDeviceLocationDescription() {
	// return this.knxGroupDeviceProperties.getBpDescription();
	// }

	/**
	 * @return groupDeviceCategory from KnxGroupDeviceCategory Enum (i.e.
	 *         KNX_DPT_1)
	 */
	public KnxGroupDeviceCategory getGroupDeviceCategory() {
		return groupDeviceCategory;
	}

	/**
	 * @return the groupDeviceId
	 */
	public String getGroupDeviceId() {
		return groupDeviceId;
	}

	// /**
	// * @return the deviceDescription
	// */
	// public String getDeviceDescription() {
	// return deviceDescription;
	// }

	// /**
	// * @return the deviceSerial
	// */
	// public String getDeviceSerial() {
	// return deviceSerial;
	// }
	//
	// /**
	// * @return the servicePid
	// */
	// public String getServicePid() {
	// return servicePid;
	// }

}

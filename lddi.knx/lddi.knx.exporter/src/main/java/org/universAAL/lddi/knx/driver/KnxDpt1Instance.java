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

package org.universAAL.lddi.knx.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt1;
import org.universAAL.lddi.knx.groupdevicemodel.KnxDpt1GroupDevice;
import org.universAAL.lddi.knx.interfaces.IKnxReceiveMessage;
import org.universAAL.lddi.knx.interfaces.IKnxSendMessage;
import org.universAAL.lddi.knx.interfaces.KnxDriver;

/**
 * Working instance of the IKnxDpt1 driver. Registers a service/device in OSGi
 * registry. Tracks on the KNX groupDevice service passed in the attach method
 * in KnxDpt1Driver class. This instance is passed to the consuming client (e.g.
 * uAAL exporter bundle). When the KNX groupDevice service disappears, this
 * driver is removed from the consuming client and from the groupDevice.
 *
 * This driver handles knx 1-bit events (knx datapoint 1), which is on/off.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Instance extends KnxDriver
		implements IKnxDpt1, IKnxReceiveMessage, ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;
	private KnxDpt1Driver parent;

	/**
	 * @param c
	 *            OSGi BundleContext
	 * @param sr
	 *            Service reference of KNX groupDevice
	 * @param client
	 *            Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public KnxDpt1Instance(KnxDpt1Driver parent_) {
		// BundleContext context, IKnxDriverClient client,
		// LogService logger) {
		super(parent_.client);

		this.parent = parent_;
		this.context = parent_.context;
		this.logger = parent_.logger;
	}

	/**
	 * track on my groupDevice
	 *
	 * @param IKnxDpt1
	 *            groupDevice service
	 * @return The service object to be tracked for the ServiceReference object
	 *         or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {

		KnxDpt1GroupDevice knxDev = (KnxDpt1GroupDevice) this.context.getService(reference);

		if (knxDev == null)
			this.logger.log(LogService.LOG_ERROR, "knxDev is null for some reason!");

		/** now couple my driver to the groupDevice */
		if (this.setgroupDevice(knxDev))
			this.logger.log(LogService.LOG_INFO, "Successfully coupled " + IKnxDpt1.MY_DEVICE_CATEGORY
					+ " driver to groupDevice " + this.groupDevice.getGroupDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + IKnxDpt1.MY_DEVICE_CATEGORY
					+ " driver to groupDevice " + this.groupDevice.getGroupDeviceId());
			return null;
		}

		// JavaDoc: @return The service object to be tracked for the
		// ServiceReference object or null if the ServiceReference object should
		// not be tracked.
		return knxDev;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.
	 * framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO,
				"Tracked knx groupDevice service was modified. " + "Going to update the KnxDpt1Instance");
		removedService(reference, service);
		addingService(reference);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.
	 * framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		// removed groupDevice service
		this.context.ungetService(reference);
		this.detachDriver();
		this.removeDriver();

		KnxDpt1GroupDevice knxDev = (KnxDpt1GroupDevice) service;
		// KnxDpt1GroupDevice knxDev = (KnxDpt1GroupDevice)
		// this.context.getService(reference);
		this.parent.connectedDriverInstanceMap.remove(knxDev.getGroupAddress());
	}

	/**
	 * Calculate readable measurement value (boolean) from given byte array
	 * according to KNX DPT 1. Call client.
	 *
	 * @see org.universAAL.lddi.knx.interfaces.IKnxReceiveMessage#newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		this.logger.log(LogService.LOG_INFO,
				"Driver " + IKnxDpt1.MY_DEVICE_CATEGORY + " for groupDevice " + this.groupDevice.getGroupAddress()
						+ " with knx datapoint type " + this.groupDevice.getDatapointType()
						+ " received new knx message " + Integer.toHexString(event[0]));

		/**
		 * KNX datapoint type 1.*** is a 1-bit signal; therefore only on/off is
		 * forwarded
		 */
		if (event[0] == DEFAULT_VALUE_OFF) {
			this.client.incomingSensorEvent(this.groupDevice.getGroupAddress(),
					this.groupDevice.getDatapointTypeMainNumber(), this.groupDevice.getDatapointTypeSubNumber(), false);
		} else if (event[0] == DEFAULT_VALUE_ON) {
			this.client.incomingSensorEvent(this.groupDevice.getGroupAddress(),
					this.groupDevice.getDatapointTypeMainNumber(), this.groupDevice.getDatapointTypeSubNumber(), true);
		} else {
			this.logger.log(LogService.LOG_ERROR, "No matches on incoming Event " + Integer.toHexString(event[0])
					+ " from groupDevice " + this.groupDevice.getGroupAddress());
			return;
		}

	}

	/**
	 * Transfer boolean event to KNX byte array (payload). Call driver.
	 */
	public void sendMessageToKnxBus(boolean event) {

		byte value;// = (byte) (event ? 1 : 0);
		if (event)
			value = DEFAULT_VALUE_ON;
		else
			value = DEFAULT_VALUE_OFF;

		this.logger.log(LogService.LOG_INFO,
				"Driver " + IKnxDpt1.MY_DEVICE_CATEGORY + " sends new knx message " + value + " to groupDevice "
						+ this.groupDevice.getGroupAddress() + " (with knx datapoint type "
						+ this.groupDevice.getDatapointType() + ")");

		this.groupDevice.sendMessageToKnxBus(new byte[] { value });

	}

}

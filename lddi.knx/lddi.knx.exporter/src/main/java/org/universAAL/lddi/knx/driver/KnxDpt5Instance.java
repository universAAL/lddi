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
import org.universAAL.lddi.knx.devicecategory.KnxDpt5;
import org.universAAL.lddi.knx.devicemodel.KnxDpt5Device;
import org.universAAL.lddi.knx.interfaces.IKnxReceiveMessage;
import org.universAAL.lddi.knx.interfaces.KnxDriver;

/**
 * Working instance of the KnxDpt5 driver. Registers a service/device in OSGi
 * registry. Tracks on the KNX device service passed in the attach method in
 * KnxDpt5Driver class. This instance is passed to the consuming client (e.g.
 * uAAL exporter bundle). When the KNX device service disappears, this driver is
 * removed from the consuming client and detached from the device.
 * 
 * This driver handles knx 4-bit unsigned int events (knx datapoint 5), which is
 * dimming step and blind step.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt5Instance extends KnxDriver implements KnxDpt5, IKnxReceiveMessage,
		ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;
	private KnxDpt5Driver parent;

	/**
	 * @param c
	 *            OSGi BundleContext
	 * @param sr
	 *            Service reference of KNX device
	 * @param client
	 *            Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public KnxDpt5Instance(KnxDpt5Driver parent_) {
		// BundleContext context, IKnxDriverClient client,
		// LogService logger) {
		super(parent_.client);

		this.parent = parent_;
		this.context = parent_.context;
		this.logger = parent_.logger;
	}

	/**
	 * Empty constructor for Unit Tests.
	 */
	public KnxDpt5Instance() {
	};

	/**
	 * track on my device
	 * 
	 * @param KnxDpt5
	 *            device service
	 * @return The service object to be tracked for the ServiceReference object
	 *         or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {
		KnxDpt5Device knxDev = (KnxDpt5Device) this.context
				.getService(reference);

		if (knxDev == null)
			this.logger.log(LogService.LOG_ERROR,
					"knxDev is null for some reason!");

		/** now couple my driver to the device */
		if (this.setDevice(knxDev))
			this.logger.log(LogService.LOG_INFO, "Successfully coupled "
					+ KnxDpt5.MY_DEVICE_CATEGORY + " driver to device "
					+ this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling "
					+ KnxDpt5.MY_DEVICE_CATEGORY + " driver to device "
					+ this.device.getDeviceId() + ". No appropriate "
					+ "device created!");
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
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi
	 * .framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO,
				"Tracked knx device service was modified. "
						+ "Going to update the KnxDpt5Instance");
		removedService(reference, service);
		addingService(reference);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi
	 * .framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		// removed device service
		this.context.ungetService(reference);
		this.detachDriver();
		this.removeDriver();		
		
		KnxDpt5Device knxDev = (KnxDpt5Device) service;
		this.parent.connectedDriverInstanceMap.remove(knxDev.getGroupAddress());
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.universAAL.lddi.knx.interfaces.IKnxReceiveMessage#
	 * newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		float percentage = KnxDpt5Device.calculatePercentage(event, this.device.getDatapointTypeSubNumber());
				
		if (percentage == 0) {
			this.logger.log(LogService.LOG_WARNING, "Driver " + KnxDpt5.MY_DEVICE_CATEGORY + " for device " + 
					this.device.getGroupAddress() + " says: knx datapoint type " + this.device.getDatapointType() +
					" is not implemented!");
			return;
		} else if (percentage == -1) {
			this.logger.log(LogService.LOG_WARNING, "Driver " + KnxDpt5.MY_DEVICE_CATEGORY + " for device " + 
					this.device.getGroupAddress() + " says: no detailed specification of knx datapoint type " + this.device.getDatapointType() +
					" in the KNX standard!");
			return;
		}
			
		this.logger.log(LogService.LOG_INFO, "Driver " + KnxDpt5.MY_DEVICE_CATEGORY + " for device " + 
				this.device.getGroupAddress() + " with knx datapoint type " + this.device.getDatapointType() +
				" received new percentage value " + percentage );
		
		this.client.incomingSensorEvent( this.device.getGroupAddress(), 
				this.device.getDatapointTypeMainNumber(), this.device.getDatapointTypeSubNumber(),
				percentage);
	}

}

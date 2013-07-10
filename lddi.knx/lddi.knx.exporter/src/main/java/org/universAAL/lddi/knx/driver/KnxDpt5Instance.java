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
import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt5;
import org.universAAL.lddi.knx.groupdevicemodel.KnxDpt5GroupDevice;
import org.universAAL.lddi.knx.interfaces.IKnxReceiveMessage;
import org.universAAL.lddi.knx.interfaces.KnxDriver;

/**
 * Working instance of the IKnxDpt5 driver. Registers a service/device in OSGi
 * registry. Tracks on the KNX groupDevice service passed in the attach method in
 * KnxDpt5Driver class. This instance is passed to the consuming client (e.g.
 * uAAL exporter bundle). When the KNX groupDevice service disappears, this driver is
 * removed from the consuming client and detached from the groupDevice.
 * 
 * This driver handles knx 4-bit unsigned int events (knx datapoint 5), which is
 * dimming step and blind step.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt5Instance extends KnxDriver implements IKnxDpt5, IKnxReceiveMessage,
		ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;
	private KnxDpt5Driver parent;

	/**
	 * @param c
	 *            OSGi BundleContext
	 * @param sr
	 *            Service reference of KNX groupDevice
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
	 * track on my groupDevice
	 * 
	 * @param IKnxDpt5
	 *            groupDevice service
	 * @return The service object to be tracked for the ServiceReference object
	 *         or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {
		KnxDpt5GroupDevice knxDev = (KnxDpt5GroupDevice) this.context
				.getService(reference);

		if (knxDev == null)
			this.logger.log(LogService.LOG_ERROR,
					"knxDev is null for some reason!");

		/** now couple my driver to the groupDevice */
		if (this.setgroupDevice(knxDev))
			this.logger.log(LogService.LOG_INFO, "Successfully coupled "
					+ IKnxDpt5.MY_DEVICE_CATEGORY + " driver to groupDevice "
					+ this.groupDevice.getGroupDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling "
					+ IKnxDpt5.MY_DEVICE_CATEGORY + " driver to groupDevice "
					+ this.groupDevice.getGroupDeviceId());
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
				"Tracked knx groupDevice service was modified. "
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
		// removed groupDevice service
		this.context.ungetService(reference);
		this.detachDriver();
		this.removeDriver();		
		
		KnxDpt5GroupDevice knxDev = (KnxDpt5GroupDevice) service;
		this.parent.connectedDriverInstanceMap.remove(knxDev.getGroupAddress());
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.universAAL.lddi.knx.interfaces.IKnxReceiveMessage#
	 * newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		float percentage = KnxDpt5GroupDevice.calculatePercentage(event, this.groupDevice.getDatapointTypeSubNumber());
				
		if (percentage == 0) {
			this.logger.log(LogService.LOG_WARNING, "Driver " + IKnxDpt5.MY_DEVICE_CATEGORY + " for groupDevice " + 
					this.groupDevice.getGroupAddress() + " says: knx datapoint type " + this.groupDevice.getDatapointType() +
					" is not implemented!");
			return;
		} else if (percentage == -1) {
			this.logger.log(LogService.LOG_WARNING, "Driver " + IKnxDpt5.MY_DEVICE_CATEGORY + " for groupDevice " + 
					this.groupDevice.getGroupAddress() + " says: no detailed specification of knx datapoint type " + this.groupDevice.getDatapointType() +
					" in the KNX standard!");
			return;
		}
			
		this.logger.log(LogService.LOG_INFO, "Driver " + IKnxDpt5.MY_DEVICE_CATEGORY + " for groupDevice " + 
				this.groupDevice.getGroupAddress() + " with knx datapoint type " + this.groupDevice.getDatapointType() +
				" received new percentage value " + percentage );
		
		this.client.incomingSensorEvent( this.groupDevice.getGroupAddress(), 
				this.groupDevice.getDatapointTypeMainNumber(), this.groupDevice.getDatapointTypeSubNumber(),
				percentage);
	}

}

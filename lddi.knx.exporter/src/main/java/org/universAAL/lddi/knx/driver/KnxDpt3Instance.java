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
import org.universAAL.lddi.knx.devicecategory.KnxDpt3;
import org.universAAL.lddi.knx.devicecategory.KnxDpt9;
import org.universAAL.lddi.knx.devicemodel.KnxDpt3Device;
import org.universAAL.lddi.knx.devicemodel.KnxDpt9Device;
import org.universAAL.lddi.knx.interfaces.KnxDriver;

/**
 * Working instance of the KnxDpt3 driver. Registers a service/device in OSGi
 * registry. Tracks on the KNX device service passed in the attach method in
 * KnxDpt3Driver class. This instance is passed to the consuming client (e.g.
 * uAAL exporter bundle). When the KNX device service disappears, this driver is
 * removed from the consuming client and from the device.
 * 
 * This driver handles knx 4-bit unsigned int events (knx datapoint 3), which is
 * dimmer step and shutter step.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt3Instance extends KnxDriver implements KnxDpt3,
		ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;
	private KnxDpt3Driver parent;

	/**
	 * @param c
	 *            OSGi BundleContext
	 * @param sr
	 *            Service reference of KNX device
	 * @param client
	 *            Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public KnxDpt3Instance(KnxDpt3Driver parent_) {
		// BundleContext context, KnxDriverClient client,
		// LogService logger) {
		super(parent_.client);

		this.parent = parent_;
		this.context = parent_.context;
		this.logger = parent_.logger;
	}

	/**
	 * Empty constructor for Unit Tests.
	 */
	public KnxDpt3Instance() {
	};

	/**
	 * track on my device
	 * 
	 * @param KnxDpt3
	 *            device service
	 * @return The service object to be tracked for the ServiceReference object
	 *         or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {
		KnxDpt3Device knxDev = (KnxDpt3Device) this.context
				.getService(reference);

		if (knxDev == null)
			this.logger.log(LogService.LOG_ERROR,
					"knxDev is null for some reason!");

		/** now couple my driver to the device */
		if (this.setDevice(knxDev))
			this.logger.log(LogService.LOG_INFO, "Successfully coupled "
					+ KnxDpt3.MY_DEVICE_CATEGORY + " driver to device "
					+ this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling "
					+ KnxDpt3.MY_DEVICE_CATEGORY + " driver to device "
					+ this.device.getDeviceId() + ". No appropriate "
					+ "ISO device created!");
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
						+ "Going to update the KnxDpt3Instance");
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
		
//		KnxDpt3Device knxDev = (KnxDpt3Device) this.context.getService(reference);
		KnxDpt3Device knxDev = (KnxDpt3Device) service;
		this.parent.connectedDriverInstanceMap.remove(knxDev.getGroupAddress());
	}
	
	
	/**
	 * Calculate step code from knx message payload.
     * last 4-bit of data byte
     * encoding |xxxx cSSS|
     * c = {0,1} control
     * SSS = [000b - 111b] Stepcode
     * 
     * @return null if the datapoint type is not implemented. Now implemented: 3.007 and 3.008
	 */
	public String calculateStepCode(byte payload) {
		byte c = (byte) ((payload & 0x08) >> 3);
		
		// for the step code only the last bit is important
		byte S = (byte) (payload & 0x01);
		
		switch (this.device.getDatapointTypeSubNumber()) {
		
		case 7: // dimming
			switch (c){
			case 0: // getting darker
				if (S == 1)
					return DECREASE_3_007;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			case 1: // getting brighter
				if (S == 1)
					return INCREASE_3_007;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;				
			}

		case 8: //blinds
			switch (c){
			case 0: // moving down
				if (S == 1)
					return DOWN_3_008;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;
			case 1: // moving up
				if (S == 1)
					return UP_3_008;
				else if (S == 0)
					return STEPCODE_BREAK;
				break;				
			}

		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory#
	 * newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		String code = calculateStepCode(event[0]);
		
		if (code.isEmpty()) {
			this.logger.log(LogService.LOG_WARNING, "Driver " + KnxDpt3.MY_DEVICE_CATEGORY + " for device " + 
					this.device.getGroupAddress() + " says: knx datapoint type " + this.device.getDatapointType() +
					" is not implemented!");
			return;
		}
			
		this.logger.log(LogService.LOG_INFO, "Driver " + KnxDpt3.MY_DEVICE_CATEGORY + " for device " + 
				this.device.getGroupAddress() + " with knx datapoint type " + this.device.getDatapointType() +
				" received new step code " + code );
		
		this.client.incomingSensorEventDpt3( this.device.getGroupAddress(), 
				this.device.getDatapointTypeMainNumber(), this.device.getDatapointTypeSubNumber(),
				code);
	}

	
	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicecategory.KnxDpt3#calculateStepNumberOfInterval(byte)
	 */
	public int calculateStepNumberOfInterval(byte stepcode) {
		// not implemented yet
		return 0;
	}

}

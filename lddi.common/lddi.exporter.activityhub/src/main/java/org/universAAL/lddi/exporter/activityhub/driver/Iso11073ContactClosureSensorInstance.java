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

package org.universAAL.lddi.exporter.activityhub.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.lddi.lib.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.lddi.lib.activityhub.driver.interfaces.ActivityHubDriverClient;
import org.universAAL.lddi.lib.activityhub.devicecategory.Iso11073ContactClosureSensor;
import org.universAAL.lddi.lib.activityhub.devicemodel.ContactClosureSensor;
import org.universAAL.lddi.lib.activityhub.devicemodel.ContactClosureSensorEvent;

/**
 * Working instance of the ActivityHub ContactClosure driver. Tracks on the
 * ContactClosureSensor device service passed in the attach method in
 * Iso11073ContactClosureSensorDriver class. This instance is passed to the
 * consuming client (e.g. uAAL exporter bundle). When the ContactClosureSensor
 * device service disappears, this driver is removed from the consuming client
 * and from the device.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class Iso11073ContactClosureSensorInstance extends ActivityHubDriver
		implements Iso11073ContactClosureSensor, ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	// private ContactClosureSensorEvent lastSensorEvent;

	/**
	 * @param c
	 *            OSGi BundleContext
	 * @param sr
	 *            Service reference of ISO device
	 * @param client
	 *            Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public Iso11073ContactClosureSensorInstance(BundleContext c, ActivityHubDriverClient client, LogService log) {
		super(client);

		this.context = c;
		this.logger = log;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver#
	 * getLastSensorEvent()
	 */
	@Override
	public int getLastSensorEvent() {
		return this.device.getSensorEventValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.
	 * framework.ServiceReference)
	 */
	/**
	 * @param ActivityHubSensor
	 *            service
	 */
	public Object addingService(ServiceReference reference) {

		ContactClosureSensor ccs = (ContactClosureSensor) this.context.getService(reference);

		/** now couple my driver to the device */
		if (this.setDevice(ccs))
			this.logger.log(LogService.LOG_INFO,
					"Successfully coupled " + Iso11073ContactClosureSensor.MY_DEVICE_CATEGORY + " driver to device "
							+ this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + Iso11073ContactClosureSensor.MY_DEVICE_CATEGORY
					+ " driver to device " + this.device.getDeviceId() + ". No appropriate " + "ISO device created!");
			return null;
		}

		// return null; JavaDoc: @return The service object to be tracked for
		// the ServiceReference object or null if the ServiceReference object
		// should not be tracked.
		return ccs;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.
	 * framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked ActivityHub device service was modified. "
				+ "Going to update the Iso11073ContactClosureSensorInstance");
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
		// removed device service
		this.context.ungetService(reference);
		this.detachDriver();
		this.removeDriver();
	}

	/**
	 * forward event to client
	 */
	public void incomingSensorEvent(int event) {

		this.logger.log(LogService.LOG_INFO,
				"Driver " + Iso11073ContactClosureSensor.MY_DEVICE_CATEGORY + " for device " + this.device.getDeviceId()
						+ " received new event "
						+ ContactClosureSensorEvent.getContactClosureSensorEvent(event).toString());

		try {
			this.client.incomingSensorEvent(this.device.getDeviceId(), this.device.getDeviceCategory(), event);
		} catch (AssertionError ae) {
			this.logger.log(LogService.LOG_ERROR,
					"No suitable ContactClosureSensorEvent found " + "for value: " + event);
			ae.printStackTrace();
		}
	}

}

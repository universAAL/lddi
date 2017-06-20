/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
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

package org.universAAL.lddi.fs20.exporter;

import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.universAAL.lddi.fs20.devicemodel.FS20Device;
import org.universAAL.lddi.fs20.devicemodel.FS20RGBSADevice;
import org.universAAL.lddi.fs20.devicemodel.FS20FMSDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20PIRxDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20SIGDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20STDevice;
import org.universAAL.lddi.fs20.exporter.util.LogTracker;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;

/**
 * This bundle notices OSGi service events. If a new service is registered the
 * received device will be stored. If a service is modified it will send a uAAL
 * context event.
 * 
 * Also it notices service requests from uAAL and handles them.
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class Activator implements BundleActivator, ServiceListener {

	public static BundleContext context = null;
	public static ModuleContext mc = null;
	private LogTracker logTracker;
	private Thread thread;
	private FS20ContextPublisher publisher;

	private static Vector<FS20RGBSADevice> displays = new Vector<FS20RGBSADevice>();
	private static Vector<FS20PIRxDevice> motionsensors = new Vector<FS20PIRxDevice>();
	private static Vector<FS20SIGDevice> gongs = new Vector<FS20SIGDevice>();
	private static Vector<FS20FMSDevice> fms = new Vector<FS20FMSDevice>();
	private static Vector<FS20STDevice> switches = new Vector<FS20STDevice>();

	public void start(BundleContext context) throws Exception {
		Activator.context = context;

		context.addServiceListener(this);

		Activator.mc = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { context });

		// use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();

		publisher = new FS20ContextPublisher(mc);

		new Thread() {
			public void run() {

				new FS20Server(mc);
			}
		}.run();
	}

	public void stop(BundleContext arg0) throws Exception {
		context.removeServiceListener(this);
		thread.interrupt();
	}

	/**
	 * Listens on OSGi service events and handles them If a new service is
	 * registered the received device will be stored. If a service is modified
	 * it will send a uAAL context event.
	 */
	public void serviceChanged(ServiceEvent event) {
		String[] objectClass = (String[]) event.getServiceReference().getProperty("objectClass");

		if (event.getType() == ServiceEvent.REGISTERED) {
			if (FS20Device.class.isInstance(context.getService(event.getServiceReference()))) {
				FS20Device device = (FS20Device) context.getService(event.getServiceReference());

				switch (device.getDeviceType()) {
				case FS20PIRx:
					FS20PIRxDevice fs20PIRx = (FS20PIRxDevice) device;
					motionsensors.add(fs20PIRx);
					break;
				case FS20FMS:
					FS20FMSDevice fs20FMS = (FS20FMSDevice) device;
					fms.add(fs20FMS);
					break;
				case FS20RGBSA:
					FS20RGBSADevice fs20DISPLAY = (FS20RGBSADevice) device;
					displays.add(fs20DISPLAY);
					break;
				case FS20SIG:
					FS20SIGDevice fs20SIG = (FS20SIGDevice) device;
					gongs.add(fs20SIG);
					break;
				case FS20ST:
					FS20STDevice fs20ST = (FS20STDevice) device;
					switches.add(fs20ST);
					break;

				}
			}
		} else if (event.getType() == ServiceEvent.UNREGISTERING) {
			// TODO: Unregister
		} else if (event.getType() == ServiceEvent.MODIFIED) {
			if (FS20Device.class.isInstance(context.getService(event.getServiceReference()))) {
				FS20Device device = (FS20Device) context.getService(event.getServiceReference());

				switch (device.getDeviceType()) {
				case FS20PIRx:
					publisher.publishContextEvent(device,
							Integer.parseInt((String) event.getServiceReference().getProperty("value")));
					break;
				case FS20FMS:
					publisher.publishContextEvent(device,
							Integer.parseInt((String) event.getServiceReference().getProperty("value")));
					break;
				}
			}
		}

	}

	public static Vector<FS20RGBSADevice> getDisplays() {
		return displays;
	}

	public static Vector<FS20PIRxDevice> getMotionsensors() {
		return motionsensors;
	}

	public static Vector<FS20SIGDevice> getGongs() {
		return gongs;
	}

	public static Vector<FS20FMSDevice> getFMSs() {
		return fms;
	}

	public static Vector<FS20STDevice> getSwitches() {
		return switches;
	}
}

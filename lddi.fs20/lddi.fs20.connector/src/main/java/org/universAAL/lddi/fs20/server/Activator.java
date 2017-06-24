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

package org.universAAL.lddi.fs20.server;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.universAAL.lddi.fs20.connect.FHZ1000PC;
import org.universAAL.lddi.fs20.connect.FS20Listener;
import org.universAAL.lddi.fs20.util.LogTracker;

/**
 * Activator class, it establishes a connection to the FS20 network, starts a
 * FS20 event listener and initializes to read all FS20 devices out of property
 * files
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class Activator implements BundleActivator {

	private HashMap<String, ServiceRegistration> registrations = new HashMap<String, ServiceRegistration>();

	private LogTracker logTracker;

	private static FHZ1000PC connection;

	private static BundleContext context;

	private static FS20Listener eventlistener;

	public static BundleContext getContext() {
		return context;
	};

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception {

		Activator.context = context;

		logTracker = new LogTracker(context);
		logTracker.open();

		connection = new FHZ1000PC("");
		connection.fs20Init();

		eventlistener = new FS20Listener(context);

		registrations = DeviceReader.readDevices(context, connection);

		eventlistener.setDevices(registrations);

		new Thread() {
			public void start() {
				eventlistener.Init(connection, logTracker);
			}
		}.start();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		connection.unregisterEventListener();
	}

	/**
	 * Get all registered devices as ServiceRegistrations
	 *
	 * @return returns all ServiceRegistrations
	 */
	public HashMap<String, ServiceRegistration> getAllDeviceRegistrations() {
		return registrations;
	}

}

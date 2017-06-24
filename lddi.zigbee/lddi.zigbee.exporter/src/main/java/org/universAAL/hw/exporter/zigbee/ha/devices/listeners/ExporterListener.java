/*
 Copyright 2008-2014 ITACA-TSB, http://www.tsb.upv.es
 Instituto Tecnologico de Aplicaciones de Comunicacion
 Avanzadas - Grupo Tecnologias para la Salud y el
 Bienestar (TSB)

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
package org.universAAL.hw.exporter.zigbee.ha.devices.listeners;

import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.universAAL.middleware.service.ServiceCallee;

public abstract class ExporterListener implements ServiceListener {

	protected static String filter;
	protected Object discoveryLock = new Object();
	protected BundleContext context;
	protected HashMap<ServiceReference, ServiceCallee> setOfDevices;

	/**
	 * Constructor to be used in the exporter. Configures the listener and
	 * performs initial search.
	 *
	 * @param context
	 *            The OSGi context
	 * @throws InvalidSyntaxException
	 *             If the service to listen defined in the class is not
	 *             appropriate
	 */
	public ExporterListener(BundleContext context) throws InvalidSyntaxException {
		this.context = context;
		synchronized (discoveryLock) {
			try {
				context.addServiceListener(this, filter);
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
			setOfDevices = new HashMap<ServiceReference, ServiceCallee>();
			ServiceReference[] srs = context.getServiceReferences(null, filter);
			if (srs != null) {
				for (int i = 0; i < srs.length; i++) {
					registeruAALService(srs[i]);
				}
			}
		}
	}

	public void serviceChanged(ServiceEvent event) {
		synchronized (discoveryLock) {
			ServiceReference sr = event.getServiceReference();
			switch (event.getType()) {
			case ServiceEvent.REGISTERED:
				registeruAALService(sr);
				break;
			case ServiceEvent.MODIFIED:
				// never modified
				break;
			case ServiceEvent.UNREGISTERING:
				unregisteruAALService(sr);
				break;
			}
		}
	}

	/**
	 * Registers a service reference as a new instance of exported device.
	 *
	 * @param sr
	 *            The service reference identifying the instance to register.
	 */
	protected abstract void registeruAALService(ServiceReference sr);

	/**
	 * Disconnect a single instance of exported device.
	 *
	 * @param sr
	 *            The service reference identifying the instance to disconnect.
	 */
	protected abstract void unregisteruAALService(ServiceReference sr);

	/**
	 * Disconnects and removes all instantiated exported devices of this type.
	 */
	public abstract void unregisteruAALService();

}

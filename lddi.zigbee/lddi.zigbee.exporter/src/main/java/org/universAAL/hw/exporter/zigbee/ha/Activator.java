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

package org.universAAL.hw.exporter.zigbee.ha;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;
import org.universAAL.hw.exporter.zigbee.ha.devices.listeners.*;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;

public class Activator implements BundleActivator {
	public static BundleContext context = null;
	public static ModuleContext moduleContext = null;
	private ServiceListener[] listeners = new ServiceListener[6];
	public static final String PROPS_FILE = "ZB.properties";
	public static final String COMMENTS = "This file stores location information for ZigBee HW devices";
	public static final String UNINITIALIZED_SUFFIX = "Unintialized";

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.moduleContext = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { context });
		listeners[0] = new DimmerLightListener(context);
		listeners[1] = new OnOffLightListener(context);
		listeners[2] = new PresenceDetectorListener(context);
		listeners[3] = new TemperatureSensorListener(context);
		listeners[4] = new OccupancySensorListener(context);
		listeners[5] = new IASZoneListener(context);
	}

	public void stop(BundleContext arg0) throws Exception {
		((DimmerLightListener) listeners[0]).unregisteruAALService();
		((OnOffLightListener) listeners[1]).unregisteruAALService();
		((PresenceDetectorListener) listeners[2]).unregisteruAALService();
		((TemperatureSensorListener) listeners[3]).unregisteruAALService();
		((OccupancySensorListener) listeners[4]).unregisteruAALService();
		((IASZoneListener) listeners[5]).unregisteruAALService();
	}

	/**
	 * Saves commissioning location properties of devices in a preset file.
	 * 
	 * The current format of property=value stored is:
	 * <code>FullDeviceID=URIsuffixOfRoom</code>
	 * 
	 * @param prop
	 */
	public static synchronized void setProperties(Properties prop) {
		try {
			FileWriter out;
			out = new FileWriter(PROPS_FILE);
			prop.store(out, COMMENTS);
			out.close();
		} catch (Exception e) {
			LogUtils.logError(moduleContext, Activator.class, "setProperties",
					new String[] { "Could not set properties file: {}" }, e);
		}
	}

	/**
	 * Gets the commissioning location properties of already known devices from
	 * a preset file.
	 * 
	 * The current format of property=value stored is:
	 * <code>FullDeviceID=URIsuffixOfRoom</code>
	 * 
	 * If the file does not exists, it is created.
	 * 
	 * @return The <code>Properties</code> object with the commissioning data.
	 */
	public static synchronized Properties getProperties() {
		Properties prop = new Properties();
		try {
			prop = new Properties();
			InputStream in = new FileInputStream(PROPS_FILE);
			prop.load(in);
			in.close();
		} catch (java.io.FileNotFoundException e) {
			LogUtils.logError(moduleContext, Activator.class, "getProperties",
					new String[] { "Properties file does not exist; generating default..." }, e);
			setProperties(prop);
		} catch (Exception e) {
			LogUtils.logError(moduleContext, Activator.class, "getProperties",
					new String[] { "Could not access properties file: {}" }, e);
		}
		return prop;
	}

}

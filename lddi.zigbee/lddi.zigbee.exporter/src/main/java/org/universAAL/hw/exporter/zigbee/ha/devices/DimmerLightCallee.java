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

package org.universAAL.hw.exporter.zigbee.ha.devices;

import it.cnr.isti.zigbee.ha.cluster.glue.general.event.CurrentLevelEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.general.event.CurrentLevelListener;
import it.cnr.isti.zigbee.ha.device.api.lighting.DimmableLight;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;

import java.util.Properties;

import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.device.LightActuator;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.phThing.DeviceService;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class DimmerLightCallee extends ExporterDimmerCallee implements CurrentLevelListener {
	static {
		NAMESPACE = "http://ontology.igd.fhg.de/ZBDimmerLightingServer.owl#";
	}

	private DimmableLight zbDevice;
	LightActuator ontologyDevice;
	private DefaultContextPublisher cp;

	/**
	 * Constructor to be used in the exporter, which sets up all the exporting
	 * process.
	 * 
	 * @param context
	 *            The OSGi context
	 * @param serv
	 *            The OSGi service backing the interaction with the device in
	 *            the abstraction layer
	 */
	public DimmerLightCallee(ModuleContext context, DimmableLight serv) {
		super(context, null);
		LogUtils.logDebug(Activator.moduleContext, DimmerLightCallee.class, "DimmerLightCallee",
				new String[] { "Ready to subscribe" }, null);
		zbDevice = serv;

		// Info
		String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier().replace("\"", "");
		String deviceURI = NAMESPACE + "actuator" + deviceSuffix;
		ontologyDevice = new LightActuator(deviceURI);
		// Commissioning
		String locationSuffix = Activator.getProperties().getProperty(deviceSuffix);
		if (locationSuffix != null && !locationSuffix.equals(Activator.UNINITIALIZED_SUFFIX)) {
			ontologyDevice.setLocation(new Room(Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX + locationSuffix));
		} else {
			Properties prop = Activator.getProperties();
			prop.setProperty(deviceSuffix, Activator.UNINITIALIZED_SUFFIX);
			Activator.setProperties(prop);
		}
		// Serv reg
		newProfiles = getServiceProfiles(NAMESPACE, DeviceService.MY_URI, ontologyDevice);
		this.addNewServiceProfiles(newProfiles);
		// Context reg
		ContextProvider info = new ContextProvider(NAMESPACE + "zbLightingContextProvider");
		info.setType(ContextProviderType.controller);
		cp = new DefaultContextPublisher(context, info);
		// ZB reg
		if (zbDevice.getLevelControl().subscribe(this)) {
			LogUtils.logDebug(Activator.moduleContext, DimmerLightCallee.class, "DimmerLightCallee",
					new String[] { "Subscribed" }, null);
		} else {
			LogUtils.logError(Activator.moduleContext, DimmerLightCallee.class, "DimmerLightCallee",
					new String[] { "Failed to Subscribe!!!" }, null);
		}
	}

	@Override
	public Integer executeGet() {
		LogUtils.logDebug(Activator.moduleContext, DimmerLightCallee.class, "getStatus",
				new String[] { "The service called was 'get the status'" }, null);
		try {
			return (Integer) zbDevice.getLevelControl().getCurrentLevel().getValue();
		} catch (ZigBeeClusterException e) {
			return null;
		}
	}

	@Override
	public boolean executeSet(Integer value) {
		LogUtils.logDebug(Activator.moduleContext, DimmerLightCallee.class, "setStatus",
				new String[] { "The service called was 'set the status' " + value }, null);
		try {
			zbDevice.getLevelControl().moveToLevel(value.shortValue(), 10);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void changedCurrentLevel(CurrentLevelEvent event) {
		LogUtils.logDebug(Activator.moduleContext, DimmerLightCallee.class, "changedCurrentLevel",
				new String[] { "Changed-Event received" }, null);
		LightActuator ls = ontologyDevice;
		ls.setValue(event.getEvent());
		cp.publish(new ContextEvent(ls, LightActuator.PROP_HAS_VALUE));
	}

}

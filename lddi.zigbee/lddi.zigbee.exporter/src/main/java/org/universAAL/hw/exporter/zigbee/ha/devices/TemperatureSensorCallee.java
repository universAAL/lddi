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

import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.MeasuredValueEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.MeasuredValueListener;
import it.cnr.isti.zigbee.ha.device.api.hvac.TemperatureSensor;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;

import java.util.Properties;

import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.phThing.DeviceService;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 *
 * @author alfiva
 *
 */
public class TemperatureSensorCallee extends ExporterSensorCallee implements MeasuredValueListener {
	static {
		NAMESPACE = "http://ontology.igd.fhg.de/ZBTemperatureServer.owl#";
	}

	// static final String DEVICE_URI_PREFIX =
	// TemperatureSensorService.SERVER_NAMESPACE
	// + "zbTemperatureSensor";
	// static final String INPUT_DEVICE_URI =
	// TemperatureSensorService.SERVER_NAMESPACE
	// + "temperatureSensorURI";

	private TemperatureSensor zbDevice;
	private org.universAAL.ontology.device.TemperatureSensor ontologyDevice;
	private DefaultContextPublisher cp;

	// private ServiceProfile[] newProfiles = TemperatureSensorService.profiles;

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
	public TemperatureSensorCallee(ModuleContext context, TemperatureSensor serv) {
		super(context, null);
		LogUtils.logDebug(Activator.moduleContext, TemperatureSensorCallee.class, "TemperatureSensorCallee",
				new String[] { "Ready to subscribe" }, null);
		zbDevice = serv;

		// Info Setup
		// TODO replace the deprecated
		String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier().replace("\"", "");
		String deviceURI = NAMESPACE + "sensor" + deviceSuffix;
		ontologyDevice = new org.universAAL.ontology.device.TemperatureSensor(deviceURI);
		// Commissioning
		String locationSuffix = Activator.getProperties().getProperty(deviceSuffix);
		if (locationSuffix != null && !locationSuffix.equals(Activator.UNINITIALIZED_SUFFIX)) {
			ontologyDevice.setLocation(new Room(Constants.MIDDLEWARE_LOCAL_ID_PREFIX + locationSuffix));
		} else {
			Properties prop = Activator.getProperties();
			prop.setProperty(deviceSuffix, Activator.UNINITIALIZED_SUFFIX);
			Activator.setProperties(prop);
		}
		// Serv reg
		newProfiles = getServiceProfiles(NAMESPACE, DeviceService.MY_URI, ontologyDevice);
		this.addNewServiceProfiles(newProfiles);

		// Context reg
		ContextProvider info = new ContextProvider(NAMESPACE + "zbTemperatureContextProvider");
		info.setType(ContextProviderType.gauge);
		cp = new DefaultContextPublisher(context, info);

		// ZB reg
		if (zbDevice.getTemperatureMeasurement().subscribe(this)) {
			LogUtils.logDebug(Activator.moduleContext, TemperatureSensorCallee.class, "TemperatureSensorCallee",
					new String[] { "Subscribed" }, null);
		} else {
			LogUtils.logError(Activator.moduleContext, TemperatureSensorCallee.class, "TemperatureSensorCallee",
					new String[] { "Failed to Subscribe!!!" }, null);
		}
	}

	@Override
	protected ServiceResponse getValue() {
		LogUtils.logDebug(Activator.moduleContext, TemperatureSensorCallee.class, "getValue",
				new String[] { "The service called was 'get the status'" }, null);
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		try {
			sr.addOutput(new ProcessOutput(OUT_GET_VALUE,
					(Float) zbDevice.getTemperatureMeasurement().getMeasuredValue().getValue()));
		} catch (ZigBeeClusterException e) {
			LogUtils.logError(Activator.moduleContext, IASZoneCallee.class, "getValue",
					new String[] { "Error getting the value: ZB error" }, e);
			ServiceResponse response = new ServiceResponse(CallStatus.serviceSpecificFailure);
			response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "ZB error!"));
			return response;
		} catch (ClassCastException e) {
			LogUtils.logError(Activator.moduleContext, IASZoneCallee.class, "getValue",
					new String[] { "Error getting the value: Unexpected value" }, e);
			ServiceResponse response = new ServiceResponse(CallStatus.serviceSpecificFailure);
			response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Unexpected value!"));
			return response;
		}
		return sr;
	}

	public void changedMeasuredValue(MeasuredValueEvent event) {
		LogUtils.logDebug(Activator.moduleContext, TemperatureSensorCallee.class, "changedMeasuredValue",
				new String[] { "Changed-Event received" }, null);
		org.universAAL.ontology.device.TemperatureSensor sensor = ontologyDevice;
		sensor.setValue(event.getEvent());
		cp.publish(new ContextEvent(sensor, org.universAAL.ontology.device.TemperatureSensor.PROP_HAS_VALUE));
	}

}

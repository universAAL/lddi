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
import org.universAAL.hw.exporter.zigbee.ha.services.TemperatureSensorService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessInput;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.location.indoor.Room;
import org.universAAL.ontology.weather.TempSensor;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class TemperatureSensorCallee extends ServiceCallee implements
	MeasuredValueListener {
    static final String DEVICE_URI_PREFIX = TemperatureSensorService.SERVER_NAMESPACE
	    + "zbTemperatureSensor";
    static final String INPUT_DEVICE_URI = TemperatureSensorService.SERVER_NAMESPACE
	    + "temperatureSensorURI";

    private TemperatureSensor zbDevice;
    private DefaultContextPublisher cp;
    TempSensor ontologyDevice;

    private ServiceProfile[] newProfiles = TemperatureSensorService.profiles;

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
	LogUtils.logDebug(Activator.moduleContext,
		TemperatureSensorCallee.class, "TemperatureSensorCallee",
		new String[] { "Ready to subscribe" }, null);
	zbDevice = serv;

	// Commissioning
	String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier()
		.replace("\"", "");
	String deviceURI = DEVICE_URI_PREFIX + deviceSuffix;
	ontologyDevice = new TempSensor(deviceURI);
	String locationSuffix = Activator.getProperties().getProperty(
		deviceSuffix);
	if (locationSuffix != null
		&& !locationSuffix.equals(Activator.UNINITIALIZED_SUFFIX)) {
	    ontologyDevice
		    .setLocation(new Room(
			    Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX
				    + locationSuffix));
	} else {
	    Properties prop = Activator.getProperties();
	    prop.setProperty(deviceSuffix, Activator.UNINITIALIZED_SUFFIX);
	    Activator.setProperties(prop);
	}
	// Serv reg
	ServiceProfile[] newProfiles = TemperatureSensorService.profiles;
	ProcessInput input = ProcessInput.toInput(ontologyDevice);
	newProfiles[0].addInput(input);
	this.addNewRegParams(newProfiles);

	ContextProvider info = new ContextProvider(
		TemperatureSensorService.SERVER_NAMESPACE
			+ "zbTemperatureContextProvider");
	info.setType(ContextProviderType.gauge);
	cp = new DefaultContextPublisher(context, info);
	if (zbDevice.getTemperatureMeasurement().subscribe(this)) {
	    LogUtils.logDebug(Activator.moduleContext,
		    TemperatureSensorCallee.class, "TemperatureSensorCallee",
		    new String[] { "Subscribed" }, null);
	} else {
	    LogUtils.logError(Activator.moduleContext,
		    TemperatureSensorCallee.class, "TemperatureSensorCallee",
		    new String[] { "Failed to Subscribe!!!" }, null);
	}
    }

    /**
     * Disconnects this exported device from the middleware.
     * 
     */
    public void unregister() {
	this.removeMatchingRegParams(newProfiles);
    }

    public void communicationChannelBroken() {
	unregister();
    }

    public ServiceResponse handleCall(ServiceCall call) {
	LogUtils.logDebug(Activator.moduleContext,
		TemperatureSensorCallee.class, "handleCall",
		new String[] { "Received a call" }, null);
	ServiceResponse response;
	if (call == null) {
	    response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Null Call!"));
	    return response;
	}

	String operation = call.getProcessURI();
	if (operation == null) {
	    response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		    "Null Operation!"));
	    return response;
	}

	if (operation.startsWith(TemperatureSensorService.SERVICE_GET_VALUE)) {
	    return getValue();
	} else {
	    response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		    "Invlaid Operation!"));
	    return response;
	}
    }

    private ServiceResponse getValue() {
	LogUtils.logDebug(Activator.moduleContext,
		TemperatureSensorCallee.class, "getValue",
		new String[] { "The service called was 'get the status'" },
		null);
	try {
	    ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
	    sr.addOutput(new ProcessOutput(
		    TemperatureSensorService.OUTPUT_VALUE, (Float) zbDevice
			    .getTemperatureMeasurement().getMeasuredValue()
			    .getValue()));
	    return sr;
	} catch (ZigBeeClusterException e) {
	    e.printStackTrace();
	    return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
    }

    // @Override
    public void changedMeasuredValue(MeasuredValueEvent event) {
	LogUtils.logDebug(Activator.moduleContext,
		TemperatureSensorCallee.class, "changedMeasuredValue",
		new String[] { "Changed-Event received" }, null);
	TempSensor sensor = ontologyDevice;
	sensor.setMeasuredValue(event.getEvent());
	cp.publish(new ContextEvent(sensor, TempSensor.PROP_MEASURED_VALUE));
    }

}

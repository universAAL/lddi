/*
 Copyright 2008-2011 ITACA-TSB, http://www.tsb.upv.es
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

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.services.TemperatureSensorService;
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

public class TemperatureSensorCallee extends ServiceCallee implements
	MeasuredValueListener {
    static final String DEVICE_URI_PREFIX = TemperatureSensorService.SERVER_NAMESPACE
	    + "zbTemperatureSensor";
    static final String INPUT_DEVICE_URI = TemperatureSensorService.SERVER_NAMESPACE
	    + "temperatureSensorURI";
    private final static Logger log = LoggerFactory
	    .getLogger(TemperatureSensorCallee.class);

    private TemperatureSensor zbDevice;
    private DefaultContextPublisher cp;
    TempSensor ontologyDevice;

    private ServiceProfile[] newProfiles = TemperatureSensorService.profiles;

    public TemperatureSensorCallee(BundleContext context, TemperatureSensor serv) {
	super(context, null);
	log.debug("Ready to subscribe");
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
	    log.debug("Subscribed");
	} else {
	    log.error("Failed to Subscribe!!!");
	}
    }

    public void unregister() {
	this.removeMatchingRegParams(newProfiles);
    }

    public void communicationChannelBroken() {
	unregister();
    }

    public ServiceResponse handleCall(ServiceCall call) {
	log.debug("Received a call");
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
	log.debug("The service called was 'get the status'");
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
	log.debug("Changed-Event received");
	TempSensor sensor = ontologyDevice;
	sensor.setMeasuredValue(event.getEvent());
	cp.publish(new ContextEvent(sensor, TempSensor.PROP_MEASURED_VALUE));
    }

}

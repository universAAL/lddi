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

import it.cnr.isti.zigbee.ha.cluster.glue.general.event.CurrentLevelEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.general.event.CurrentLevelListener;
import it.cnr.isti.zigbee.ha.device.api.lighting.DimmableLight;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.services.DimmerLightService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.TypeMapper;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessInput;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.lighting.LightSource;
import org.universAAL.ontology.location.indoor.Room;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class DimmerLightCallee extends ServiceCallee implements
	CurrentLevelListener {

    static final String DEVICE_URI_PREFIX = DimmerLightService.LIGHTING_SERVER_NAMESPACE
	    + "zbLamp";
    static final String INPUT_DEVICE_URI = DimmerLightService.LIGHTING_SERVER_NAMESPACE
	    + "lampURI";

    private final static Logger log = LoggerFactory
	    .getLogger(DimmerLightCallee.class);
    private DimmableLight zbDevice;
    private DefaultContextPublisher cp;
    LightSource ontologyDevice;

    private ServiceProfile[] newProfiles = DimmerLightService.profiles;

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
	log.debug("Ready to subscribe");
	zbDevice = serv;

	// Commissioning
	String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier()
		.replace("\"", "");
	String deviceURI = DEVICE_URI_PREFIX + deviceSuffix;
	ontologyDevice = new LightSource(deviceURI);
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
	ServiceProfile[] newProfiles = DimmerLightService.profiles;

	ProcessInput input = new ProcessInput(DimmerLightService.INPUT_LAMP);
	input.setParameterType(LightSource.MY_URI);
	input.setCardinality(1, 0);

	ProcessInput inputb = new ProcessInput(
		DimmerLightService.INPUT_LAMP_BRIGHTNESS);
	inputb.setParameterType(TypeMapper.getDatatypeURI(Integer.class));
	inputb.setCardinality(1, 1);

	Restriction r = Restriction.getFixedValueRestriction(
		DimmerLightService.PROP_CONTROLS, ontologyDevice);

	newProfiles[0].addInput(input);
	newProfiles[0].getTheService().addInstanceLevelRestriction(r,
		new String[] { DimmerLightService.PROP_CONTROLS });
	newProfiles[1].addInput(input);
	newProfiles[1].getTheService().addInstanceLevelRestriction(r,
		new String[] { DimmerLightService.PROP_CONTROLS });
	newProfiles[2].addInput(input);
	newProfiles[2].getTheService().addInstanceLevelRestriction(r,
		new String[] { DimmerLightService.PROP_CONTROLS });
	newProfiles[3].addInput(input);
	newProfiles[3].addInput(inputb);
	newProfiles[3].getTheService().addInstanceLevelRestriction(r,
		new String[] { DimmerLightService.PROP_CONTROLS });
	newProfiles[3].addChangeEffect(new String[] {
		DimmerLightService.PROP_CONTROLS,
		LightSource.PROP_SOURCE_BRIGHTNESS },
		inputb.asVariableReference());
	this.addNewRegParams(newProfiles);
	// CP
	ContextProvider info = new ContextProvider(
		DimmerLightService.LIGHTING_SERVER_NAMESPACE
			+ "zbLightingContextProvider");
	info.setType(ContextProviderType.controller);
	cp = new DefaultContextPublisher(context, info);
	if (zbDevice.getLevelControl().subscribe(this)) {
	    log.debug("Subscribed");
	} else {
	    log.error("Failed to Subscribe!!!");
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

	if (operation.startsWith(DimmerLightService.SERVICE_GET_ON_OFF)) {
	    return getStatus();
	} else if (operation.startsWith(DimmerLightService.SERVICE_TURN_ON)) {
	    return setOnStatus();
	} else if (operation.startsWith(DimmerLightService.SERVICE_TURN_OFF)) {
	    return setOffStatus();
	} else if (operation.startsWith(DimmerLightService.SERVICE_SET_DIMMER)) {
	    // return setStatus((Integer) call.getInputValue(INPUT_LAMP_VALUE));
	    Integer setvalue = (Integer) call
		    .getInputValue(DimmerLightService.INPUT_LAMP_BRIGHTNESS);
	    if (setvalue.intValue() == 0) {
		return setOnStatus();
	    } else if (setvalue.intValue() == 100) {
		return setOffStatus();
	    } else if (0 <= setvalue.intValue() && setvalue.intValue() <= 100) {
		int val = setvalue.intValue() * (254) / (100);
		return setStatus(val);
	    } else {
		log.error("Input dimmer value not permitted (0 to 100 only)");
		return new ServiceResponse(CallStatus.serviceSpecificFailure);
	    }
	} else {
	    response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		    "Invlaid Operation!"));
	    return response;
	}
    }

    private ServiceResponse setOffStatus() {
	log.debug("The service called was 'set the status OFF'");
	try {
	    zbDevice.getLevelControl().moveToLevel(Short.parseShort("0"), 20);
	    return new ServiceResponse(CallStatus.succeeded);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
    }

    private ServiceResponse setOnStatus() {
	log.debug("The service called was 'set the status ON'");
	try {
	    zbDevice.getLevelControl().moveToLevel(
		    Short.decode("0xFE").shortValue(), 20);
	    return new ServiceResponse(CallStatus.succeeded);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
    }

    private ServiceResponse setStatus(int value) {
	log.debug("The service called was 'set the status' " + value);
	try {
	    zbDevice.getLevelControl().moveToLevel((short) value, 10);
	    return new ServiceResponse(CallStatus.succeeded);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
    }

    private ServiceResponse getStatus() {
	log.debug("The service called was 'get the status'");
	try {
	    ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
	    sr.addOutput(new ProcessOutput(
		    DimmerLightService.OUTPUT_LAMP_BRIGHTNESS,
		    (Integer) zbDevice.getLevelControl().getCurrentLevel()
			    .getValue()));
	    return sr;
	} catch (ZigBeeClusterException e) {
	    e.printStackTrace();
	    return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
    }

    // @Override
    public void changedCurrentLevel(CurrentLevelEvent event) {
	log.debug("Changed-Event received");
	LightSource ls = ontologyDevice;
	ls.setBrightness(event.getEvent());
	cp.publish(new ContextEvent(ls, LightSource.PROP_SOURCE_BRIGHTNESS));
    }

}

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

import it.cnr.isti.zigbee.ha.cluster.glue.general.event.OnOffEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.general.event.OnOffListener;
import it.cnr.isti.zigbee.ha.device.api.lighting.OnOffLight;
import it.cnr.isti.zigbee.ha.driver.core.ZigBeeHAException;

import java.util.Properties;

import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
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
public class OnOffLightCallee extends ExporterActuatorCallee implements
	OnOffListener {

    static {
	NAMESPACE = "http://ontology.igd.fhg.de/ZBLightingServer.owl#";
    }

    private OnOffLight zbDevice;
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
    public OnOffLightCallee(ModuleContext context, OnOffLight serv) {
	super(context, null);
	LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		"OnOffLightCallee", new String[] { "Ready to subscribe" }, null);
	zbDevice = serv;
	// Info
	String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier()
		.replace("\"", "");
	String deviceURI = NAMESPACE + "actuator" + deviceSuffix;
	ontologyDevice = new LightActuator(deviceURI);
	// Commissioning
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
	newProfiles = getServiceProfiles(NAMESPACE, DeviceService.MY_URI,
		ontologyDevice);
	this.addNewServiceProfiles(newProfiles);
	// Context reg
	ContextProvider info = new ContextProvider(NAMESPACE
		+ "zbLightingContextProvider");
	info.setType(ContextProviderType.controller);
	cp = new DefaultContextPublisher(context, info);
	// ZB reg
	if (zbDevice.getOnOff().subscribe(this)) {
	    LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		    "OnOffLightCallee", new String[] { "Subscribed" }, null);
	} else {
	    LogUtils.logError(Activator.moduleContext, OnOffLightCallee.class,
		    "OnOffLightCallee",
		    new String[] { "Failed to Subscribe!!!" }, null);
	}
    }

    // Overriden because OnOffLight in uAAL has integer value (kind-of-dimmer)
    // No need to change profiles because no restriction is made on the output
    @Override
    public ServiceResponse handleCall(ServiceCall call) {
	ServiceResponse response;
	if (call == null) {
	    return null;
	}
	String operation = call.getProcessURI();
	if (operation == null) {
	    return null;
	}
	if (operation.startsWith(NAMESPACE + SERVICE_GET_ON_OFF)) {
	    Boolean result = executeGet();
	    if (result != null) {
		response = new ServiceResponse(CallStatus.succeeded);
		response.addOutput(new ProcessOutput(
			NAMESPACE + OUT_GET_ON_OFF,
			result.booleanValue() ? Integer.valueOf(100) : Integer
				.valueOf(0)));// Here is the change
		return response;
	    } else {
		response = new ServiceResponse(
			CallStatus.serviceSpecificFailure);
		return response;
	    }
	}

	if (operation.startsWith(NAMESPACE + SERVICE_TURN_OFF)) {
	    if (executeOff()) {
		return new ServiceResponse(CallStatus.succeeded);
	    } else {
		response = new ServiceResponse(
			CallStatus.serviceSpecificFailure);
		return response;
	    }
	}

	if (operation.startsWith(NAMESPACE + SERVICE_TURN_ON)) {
	    if (executeOn()) {
		return new ServiceResponse(CallStatus.succeeded);
	    } else {
		response = new ServiceResponse(
			CallStatus.serviceSpecificFailure);
		return response;
	    }
	}

	response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	response.addOutput(new ProcessOutput(
		ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		"The service requested has not been implemented in this simple editor callee"));
	return response;
    }

    @Override
    public boolean executeOn() {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		"setOnStatus",
		new String[] { "The service called was 'set the status ON'" },
		null);
	try {
	    zbDevice.getOnOff().on();
	    return true;
	} catch (ZigBeeHAException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    @Override
    public boolean executeOff() {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		"setOffStatus",
		new String[] { "The service called was 'set the status OFF'" },
		null);
	try {
	    zbDevice.getOnOff().off();
	    return true;
	} catch (ZigBeeHAException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    @Override
    public Boolean executeGet() {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		"getOnOffStatus",
		new String[] { "The service called was 'get the status'" },
		null);
	try {
	    return zbDevice.getOnOff().getOnOff();
	} catch (ZigBeeHAException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public void changedOnOff(OnOffEvent event) {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightCallee.class,
		"changedOnOff", new String[] { "Changed-Event received" }, null);
	LightActuator ls = ontologyDevice;
	ls.setHasValue(event.getEvent() ? 100 : 0);
	cp.publish(new ContextEvent(ls, LightActuator.PROP_HAS_VALUE));
    }

}

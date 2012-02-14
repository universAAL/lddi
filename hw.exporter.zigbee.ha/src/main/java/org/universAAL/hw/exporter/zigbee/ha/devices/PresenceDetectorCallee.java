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

import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.OccupancyEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.OccupancyListener;
import it.cnr.isti.zigbee.ha.device.api.lighting.OccupancySensor;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.services.PresenceDetectorService;
import org.universAAL.middleware.container.ModuleContext;
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
import org.universAAL.ontology.device.home.PresenceDetector;
import org.universAAL.ontology.location.indoor.Room;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class PresenceDetectorCallee extends ServiceCallee implements
	OccupancyListener {
    static final String DEVICE_URI_PREFIX = PresenceDetectorService.PRESENCE_SERVER_NAMESPACE
	    + "zbPresenceDetector";
    static final String INPUT_DEVICE_URI = PresenceDetectorService.PRESENCE_SERVER_NAMESPACE
	    + "presenceDetectorURI";

    private final static Logger log = LoggerFactory
	    .getLogger(PresenceDetectorCallee.class);
    private OccupancySensor zbDevice;
    private DefaultContextPublisher cp;
    PresenceDetector ontologyDevice;

    private ServiceProfile[] newProfiles = PresenceDetectorService.profiles;

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
    public PresenceDetectorCallee(ModuleContext context, OccupancySensor serv) {
	super(context, null);
	log.debug("Ready to subscribe");
	zbDevice = serv;
	// Commissioning
	String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier()
		.replace("\"", "");
	String deviceURI = DEVICE_URI_PREFIX + deviceSuffix;
	ontologyDevice = new PresenceDetector(deviceURI);
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
	ServiceProfile[] newProfiles = PresenceDetectorService.profiles;
	ProcessInput input = ProcessInput.toInput(ontologyDevice);
	newProfiles[0].addInput(input);
	this.addNewRegParams(newProfiles);

	ContextProvider info = new ContextProvider(
		PresenceDetectorService.PRESENCE_SERVER_NAMESPACE
			+ "zbPresenceDetectorContextProvider");
	info.setType(ContextProviderType.gauge);
	cp = new DefaultContextPublisher(context, info);

	zbDevice.getOccupacySensing().subscribe(this);
	log.debug("Subscribed");

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

	if (operation.startsWith(PresenceDetectorService.SERVICE_GET_PRESENCE)) {
	    return getPresence();
	} else {
	    response = new ServiceResponse(CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		    "Invlaid Operation!"));
	    return response;
	}
    }

    private ServiceResponse getPresence() {
	log.debug("The service called was 'get the status'");
	ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
	Boolean finalValue = new Boolean(false);
	try {
	    finalValue = (Boolean) zbDevice.getOccupacySensing().getOccupancy()
		    .getValue();
	} catch (ZigBeeClusterException e) {
	    log.error("Error getting the value of the occupancy: {}", e);
	}
	// TODO: check if it works
	sr.addOutput(new ProcessOutput(PresenceDetectorService.OUTPUT_PRESENCE,
		finalValue));
	return sr;
    }

    // @Override
    public void changedOccupancy(OccupancyEvent event) {
	log.debug("Changed-Event received");
	PresenceDetector pd = ontologyDevice;
	pd.setMeasuredValue(event.getEvent() > 0);
	cp.publish(new ContextEvent(pd, PresenceDetector.PROP_MEASURED_VALUE));
    }
}

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

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owl.Service;
import org.universAAL.middleware.service.owls.process.ProcessInput;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.Actuator;
import org.universAAL.ontology.phThing.DeviceService;

public abstract class ExporterActuatorCallee extends ServiceCallee{
    
    /**
     * Service suffix.
     */
    public static final String SERVICE_GET_ON_OFF = "servActuatorGet";
    /**
     * Argument suffix.
     */
    public static final String OUT_GET_ON_OFF = "outputActuatorGet";
    /**
     * Service suffix.
     */
    public static final String SERVICE_TURN_OFF = "servActuatorOff";
    /**
     * Service suffix.
     */
    public static final String SERVICE_TURN_ON = "servActuatorOn";
    /**
     * Argument suffix.
     */
    public static final String IN_DEVICE = "inputActuatorAll";
    
    protected static String NAMESPACE;
    protected ServiceProfile[] newProfiles;
    
    protected ExporterActuatorCallee(ModuleContext context,
	    ServiceProfile[] realizedServices) {
	super(context, realizedServices);
    }
    
    public void unregister() {
	this.removeMatchingProfiles(newProfiles);
    }

    public void communicationChannelBroken() {
	unregister();
    }

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
			NAMESPACE + OUT_GET_ON_OFF, result));
		return response;
	    }else{
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
    
    /**
     * When a SET ON service request is received, this method is called
     * automatically.
     * 
     * @return <code>true</code> if the actuator could be set to ON
     */
    public abstract boolean executeOn();

    /**
     * When a SET OFF service request is received, this method is called
     * automatically.
     * 
     * @return <code>true</code> if the actuator could be set to OFF
     */
    public abstract boolean executeOff();

    /**
     * When a GET STATUS service request is received, this method is called
     * automatically.
     * 
     * @return The Boolean value representing the status property of the
     *         actuator.
     */
    public abstract Boolean executeGet();
    
    public static ServiceProfile[] getServiceProfiles(String namespace,
	    String ontologyURI, Actuator actuator) {

	ServiceProfile[] profiles = new ServiceProfile[3];

	PropertyPath ppath = new PropertyPath(null, true, new String[] {
		DeviceService.PROP_CONTROLS, Actuator.PROP_HAS_VALUE });

	ProcessInput input = new ProcessInput(namespace + IN_DEVICE);
	input.setParameterType(actuator.getClassURI());
	input.setCardinality(1, 0);

	MergedRestriction r = MergedRestriction.getFixedValueRestriction(
		DeviceService.PROP_CONTROLS, actuator);

	Service getOnOff = (Service) OntologyManagement.getInstance()
		.getResource(ontologyURI, namespace + SERVICE_GET_ON_OFF);
	profiles[0] = getOnOff.getProfile();
	ProcessOutput output = new ProcessOutput(namespace + OUT_GET_ON_OFF);
	output.setCardinality(1, 1);
	profiles[0].addOutput(output);
	profiles[0].addSimpleOutputBinding(output, ppath.getThePath());
	profiles[0].addInput(input);
	profiles[0].getTheService().addInstanceLevelRestriction(r,
		new String[] { DeviceService.PROP_CONTROLS });

	Service turnOff = (Service) OntologyManagement.getInstance()
		.getResource(ontologyURI, namespace + SERVICE_TURN_OFF);
	profiles[1] = turnOff.getProfile();
	profiles[1].addChangeEffect(ppath.getThePath(), Boolean.valueOf(false));
	profiles[1].addInput(input);
	profiles[1].getTheService().addInstanceLevelRestriction(r,
		new String[] { DeviceService.PROP_CONTROLS });

	Service turnOn = (Service) OntologyManagement.getInstance()
		.getResource(ontologyURI, namespace + SERVICE_TURN_ON);
	profiles[2] = turnOn.getProfile();
	profiles[2].addChangeEffect(ppath.getThePath(), Boolean.valueOf(true));
	profiles[2].addInput(input);
	profiles[2].getTheService().addInstanceLevelRestriction(r,
		new String[] { DeviceService.PROP_CONTROLS });

	return profiles;
    }

}

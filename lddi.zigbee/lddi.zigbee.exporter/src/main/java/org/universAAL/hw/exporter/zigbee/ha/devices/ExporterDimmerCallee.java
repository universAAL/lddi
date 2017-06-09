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
import org.universAAL.middleware.rdf.TypeMapper;
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

public abstract class ExporterDimmerCallee extends ServiceCallee {
	// TODO: Add also setON/OFF? Integer or true?
	/**
	 * Service suffix.
	 */
	public static final String SERVICE_GET_VALUE = "servDimmerGet";
	/**
	 * Argument suffix.
	 */
	public static final String OUT_GET_VLAUE = "outputActuatorGet";
	/**
	 * Service suffix.
	 */
	public static final String SERVICE_SET_VALUE = "servDimmerSet";
	/**
	 * Service suffix.
	 */
	public static final String IN_SET_VALUE = "inputDimmerSet";
	/**
	 * Argument suffix.
	 */
	public static final String IN_DEVICE = "inputDimmerAll";

	protected static String NAMESPACE;
	protected ServiceProfile[] newProfiles;

	protected ExporterDimmerCallee(ModuleContext context, ServiceProfile[] realizedServices) {
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
		if (operation.startsWith(NAMESPACE + SERVICE_GET_VALUE)) {
			Integer result = executeGet();
			if (result != null) {
				response = new ServiceResponse(CallStatus.succeeded);
				response.addOutput(new ProcessOutput(NAMESPACE + OUT_GET_VLAUE, result));
				return response;
			} else {
				response = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return response;
			}
		}

		if (operation.startsWith(NAMESPACE + SERVICE_SET_VALUE)) {
			Integer value = (Integer) call.getInputValue(IN_SET_VALUE);
			if (executeSet(value)) {
				return new ServiceResponse(CallStatus.succeeded);
			} else {
				response = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return response;
			}
		}

		response = new ServiceResponse(CallStatus.serviceSpecificFailure);
		response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
				"The service requested has not been implemented in this simple dimmer callee"));
		return response;
	}

	/**
	 * When a GET STATUS service request is received, this method is called
	 * automatically.
	 * 
	 * @return The Integer value representing the status property of the
	 *         actuator.
	 */
	public abstract Integer executeGet();

	/**
	 * When a SET STATUS service request is received, this method is called
	 * automatically.
	 * 
	 * @param value
	 *            The Integer representing the dimmed value
	 * @return The Boolean value representing the status property of the
	 *         actuator.
	 */
	public abstract boolean executeSet(Integer value);

	public static ServiceProfile[] getServiceProfiles(String namespace, String ontologyURI, Actuator actuator) {

		ServiceProfile[] profiles = new ServiceProfile[2];

		PropertyPath ppath = new PropertyPath(null, true,
				new String[] { DeviceService.PROP_CONTROLS, Actuator.PROP_HAS_VALUE });

		ProcessInput input = new ProcessInput(namespace + IN_DEVICE);
		input.setParameterType(actuator.getClassURI());
		input.setCardinality(1, 0);

		ProcessInput inputValue = new ProcessInput(namespace + IN_SET_VALUE);
		inputValue.setParameterType(TypeMapper.getDatatypeURI(Integer.class));
		inputValue.setCardinality(1, 0);

		MergedRestriction r = MergedRestriction.getFixedValueRestriction(DeviceService.PROP_CONTROLS, actuator);

		Service getValue = (Service) OntologyManagement.getInstance().getResource(ontologyURI,
				namespace + SERVICE_GET_VALUE);
		profiles[0] = getValue.getProfile();
		ProcessOutput output = new ProcessOutput(namespace + OUT_GET_VLAUE);
		output.setCardinality(1, 1);
		profiles[0].addOutput(output);
		profiles[0].addSimpleOutputBinding(output, ppath.getThePath());
		profiles[0].addInput(input);
		profiles[0].getTheService().addInstanceLevelRestriction(r, new String[] { DeviceService.PROP_CONTROLS });

		Service setValue = (Service) OntologyManagement.getInstance().getResource(ontologyURI,
				namespace + SERVICE_SET_VALUE);
		profiles[1] = setValue.getProfile();
		profiles[1].addInput(input);
		profiles[1].addChangeEffect(ppath.getThePath(), inputValue.asVariableReference());
		profiles[1].getTheService().addInstanceLevelRestriction(r, new String[] { DeviceService.PROP_CONTROLS });

		return profiles;
	}

}

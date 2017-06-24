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
import org.universAAL.ontology.device.Sensor;
import org.universAAL.ontology.phThing.DeviceService;

public abstract class ExporterSensorCallee extends ServiceCallee {

	/**
	 * Service suffix.
	 */
	public static final String SERVICE_GET_VALUE = "servSensorGet";
	/**
	 * Argument suffix.
	 */
	public static final String OUT_GET_VALUE = "outputSensorGet";
	/**
	 * Argument suffix.
	 */
	public static final String IN_DEVICE = "inputSensor";

	protected static String NAMESPACE;
	protected ServiceProfile[] newProfiles;

	protected ExporterSensorCallee(ModuleContext context, ServiceProfile[] realizedServices) {
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
			response = new ServiceResponse(CallStatus.serviceSpecificFailure);
			response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Null Call!"));
			return response;
		}

		String operation = call.getProcessURI();
		if (operation == null) {
			response = new ServiceResponse(CallStatus.serviceSpecificFailure);
			response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Null Operation!"));
			return response;
		}

		if (operation.startsWith(NAMESPACE + SERVICE_GET_VALUE)) {
			return getValue();
		} else {
			response = new ServiceResponse(CallStatus.serviceSpecificFailure);
			response.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invlaid Operation!"));
			return response;
		}
	}

	/**
	 * This is a sensor, so it must provide its sensed value when asked.
	 *
	 * @return The Service Response representing the requested output, as
	 *         defined by the appropriate Service Ontology.
	 */
	protected abstract ServiceResponse getValue();

	public static ServiceProfile[] getServiceProfiles(String namespace, String ontologyURI, Sensor sensor) {

		ServiceProfile[] profiles = new ServiceProfile[1];

		PropertyPath ppath = new PropertyPath(null, true,
				new String[] { DeviceService.PROP_CONTROLS, Sensor.PROP_HAS_VALUE });

		ProcessInput input = new ProcessInput(namespace + IN_DEVICE);
		input.setParameterType(sensor.getClassURI());
		input.setCardinality(1, 0);

		MergedRestriction r = MergedRestriction.getFixedValueRestriction(DeviceService.PROP_CONTROLS, sensor);

		Service getOnOff = (Service) OntologyManagement.getInstance().getResource(ontologyURI,
				namespace + SERVICE_GET_VALUE);
		profiles[0] = getOnOff.getProfile();
		ProcessOutput output = new ProcessOutput(namespace + OUT_GET_VALUE);
		output.setCardinality(1, 1);
		profiles[0].addOutput(output);
		profiles[0].addSimpleOutputBinding(output, ppath.getThePath());
		profiles[0].addInput(input);
		profiles[0].getTheService().addInstanceLevelRestriction(r, new String[] { DeviceService.PROP_CONTROLS });

		return profiles;
	}

}

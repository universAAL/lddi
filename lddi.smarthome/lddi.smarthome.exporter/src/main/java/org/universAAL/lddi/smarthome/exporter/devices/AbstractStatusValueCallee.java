/*
	Copyright 2016 ITACA-SABIEN, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion
	Avanzadas - Grupo Tecnologias para la Salud y el
	Bienestar (SABIEN)

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
package org.universAAL.lddi.smarthome.exporter.devices;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owl.Service;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.ValueDevice;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.phThing.DeviceService;

public abstract class AbstractStatusValueCallee extends AbstractCallee {
	/**
	 * Service suffix.
	 */
	public static final String SERVICE_GET_VALUE = "servGetValue";
	/**
	 * Argument suffix.
	 */
	public static final String OUT_GET_VALUE = "outGetValue";
	/**
	 * Service suffix.
	 */
	public static final String SERVICE_SET_VALUE = "servSetValue";
	/**
	 * Service suffix.
	 */
	public static final String IN_SET_VALUE = "inSetValue";
	/**
	 * Argument suffix.
	 */
	public static final String IN_DEVICE = "inDevice";

	protected String namespace;
	protected ServiceProfile[] newProfiles;

	protected AbstractStatusValueCallee(ModuleContext context, ServiceProfile[] realizedServices, String namespace) {
		super(context, realizedServices);
		this.namespace = namespace;
	}

	public void unregister() {
		this.removeMatchingProfiles(newProfiles);
		this.close();
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
		if (!((Device) call.getInputValue(namespace + IN_DEVICE)).getURI().equals(ontDevice.getURI())) {
			return new ServiceResponse(CallStatus.denied);
		}
		if (operation.startsWith(namespace + SERVICE_GET_VALUE)) {
			StatusValue result = executeGet();
			if (result != null) {
				response = new ServiceResponse(CallStatus.succeeded);
				response.addOutput(new ProcessOutput(namespace + OUT_GET_VALUE, result));
				return response;
			} else {
				response = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return response;
			}
		}

		if (operation.startsWith(namespace + SERVICE_SET_VALUE)) {
			StatusValue value = (StatusValue) call.getInputValue(namespace + IN_SET_VALUE);
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
	 * @return The StatusValue value representing the status property of the
	 *         actuator.
	 */
	public abstract StatusValue executeGet();

	/**
	 * When a SET STATUS service request is received, this method is called
	 * automatically.
	 *
	 * @param value
	 *            The StatusValue representing the dimmed value
	 * @return true if succeeded
	 */
	public abstract boolean executeSet(StatusValue value);

	/**
	 * Get the typical service profiles for a controller: GET/SET the has value
	 * prop.
	 *
	 * @param namespace
	 *            Must be the same as the one set in the constructor
	 * @param actuator
	 *            The instance of the ontological representation of the device
	 * @return
	 */
	public static ServiceProfile[] getServiceProfiles(String namespace, Device instance) {
		ServiceProfile[] profiles = new ServiceProfile[2];
		profiles[0] = getServiceProfileGET(namespace, instance);
		profiles[1] = getServiceProfileSET(namespace, instance);
		return profiles;
	}

	public static ServiceProfile getServiceProfileGET(String namespace, Device instance) {
		Service getValue = (Service) OntologyManagement.getInstance().getResource(DeviceService.MY_URI,
				namespace + SERVICE_GET_VALUE);
		getValue.addFilteringInput(namespace + IN_DEVICE, instance.getClassURI(), 0, 1,
				new String[] { DeviceService.PROP_CONTROLS });
		getValue.addOutput(namespace + OUT_GET_VALUE, StatusValue.MY_URI, 1, 1,
				new String[] { DeviceService.PROP_CONTROLS, ValueDevice.PROP_HAS_VALUE });
		return getValue.getProfile();
	}

	public static ServiceProfile getServiceProfileSET(String namespace, Device instance) {
		Service setValue = (Service) OntologyManagement.getInstance().getResource(DeviceService.MY_URI,
				namespace + SERVICE_SET_VALUE);
		setValue.addFilteringInput(namespace + IN_DEVICE, instance.getClassURI(), 0, 1,
				new String[] { DeviceService.PROP_CONTROLS });
		setValue.addInputWithChangeEffect(namespace + IN_SET_VALUE, StatusValue.MY_URI, 1, 1,
				new String[] { DeviceService.PROP_CONTROLS, ValueDevice.PROP_HAS_VALUE });
		return setValue.getProfile();
	}

}

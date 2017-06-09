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

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.OpenClosedType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.WaterFlowSensor;

public class WaterflowSensorWrapper extends AbstractStatusValueCallee {
	private DefaultContextPublisher cp;

	public WaterflowSensorWrapper(ModuleContext context, String itemName) {
		super(context,
				new ServiceProfile[] { getServiceProfileGET(Activator.NAMESPACE + itemName + "handler",
						new WaterFlowSensor(Activator.NAMESPACE + itemName)) },
				Activator.NAMESPACE + itemName + "handler");

		Activator.logD("WaterflowSensorWrapper", "Ready to subscribe");
		shDeviceName = itemName;

		// URI must be the same declared in the super constructor
		String deviceURI = Activator.NAMESPACE + itemName;
		ontDevice = new WaterFlowSensor(deviceURI);

		// Commissioning
		// TODO Set location based on tags?

		// Context reg
		ContextProvider info = new ContextProvider(deviceURI + "Provider");
		info.setType(ContextProviderType.gauge);
		ContextEventPattern cep = new ContextEventPattern();
		MergedRestriction subjectRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_SUBJECT,
				ontDevice);
		MergedRestriction predicateRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE, WaterFlowSensor.PROP_HAS_VALUE);
		// TODO Object restr
		cep.addRestriction(subjectRestriction);
		cep.addRestriction(predicateRestriction);
		info.setProvidedEvents(new ContextEventPattern[] { cep });
		cp = new DefaultContextPublisher(context, info);
	}

	@Override
	public StatusValue executeGet() {
		OpenClosedType value = (OpenClosedType) Activator.getOpenhab().get(shDeviceName)
				.getStateAs((Class<? extends State>) OpenClosedType.class);
		Activator.logD("getStatus", "The service called was 'get the status'");
		if (value == null)
			return null;
		return (value.compareTo(OpenClosedType.CLOSED) == 0) ? StatusValue.Activated : StatusValue.NotActivated;
	}

	@Override
	public boolean executeSet(StatusValue value) {
		return false;// Sensor, cannot set
	}

	public void publish(Event event) {
		Boolean theValue = null;
		Activator.logD("changedCurrentLevel", "Changed-Event received");
		if (event instanceof ItemStateEvent) {
			ItemStateEvent stateEvent = (ItemStateEvent) event;
			State s = stateEvent.getItemState();
			if (s instanceof OpenClosedType) {
				theValue = Boolean.valueOf(((OpenClosedType) s).compareTo(OpenClosedType.CLOSED) == 0);
			}
		}
		if (theValue != null) {
			WaterFlowSensor d = (WaterFlowSensor) ontDevice;
			d.setValue(theValue.booleanValue() ? StatusValue.Activated : StatusValue.NotActivated);
			cp.publish(new ContextEvent(d, WaterFlowSensor.PROP_HAS_VALUE));
		} // else dont bother TODO log
	}

	public void unregister() {
		super.unregister();
		cp.close();
	}

}

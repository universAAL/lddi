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
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchController;

public class SwitchControllerWrapper extends AbstractStatusValueCallee {
	private DefaultContextPublisher cp;

	public SwitchControllerWrapper(ModuleContext context, String itemName) {
		super(context,
				getServiceProfiles(Activator.NAMESPACE + itemName + "handler",
						new SwitchController(Activator.NAMESPACE + itemName)),
				Activator.NAMESPACE + itemName + "handler");

		Activator.logD("SwitchControllerWrapper", "Ready to subscribe");
		shDeviceName = itemName;

		// URI must be the same declared in the super constructor
		String deviceURI = Activator.NAMESPACE + itemName;
		ontDevice = new SwitchController(deviceURI);

		// Commissioning
		// TODO Set location based on tags?

		// Context reg
		ContextProvider info = new ContextProvider(deviceURI + "Provider");
		info.setType(ContextProviderType.controller);
		ContextEventPattern cep = new ContextEventPattern();
		MergedRestriction subjectRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_SUBJECT,
				ontDevice);
		MergedRestriction predicateRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE, SwitchController.PROP_HAS_VALUE);
		// TODO Object restr
		cep.addRestriction(subjectRestriction);
		cep.addRestriction(predicateRestriction);
		info.setProvidedEvents(new ContextEventPattern[] { cep });
		cp = new DefaultContextPublisher(context, info);
	}

	@Override
	public StatusValue executeGet() {
		OnOffType value = (OnOffType) Activator.getOpenhab().get(shDeviceName)
				.getStateAs((Class<? extends State>) OnOffType.class);
		Activator.logD("getStatus", "The service called was 'get the status'");
		if (value == null)
			return null;
		return (value.compareTo(OnOffType.ON) == 0) ? StatusValue.Activated : StatusValue.NotActivated;
	}

	@Override
	public boolean executeSet(StatusValue value) {
		Activator.logD("setStatus", "The service called was 'set the status' " + value);

		try {
			ItemCommandEvent itemCommandEvent = ItemEventFactory.createCommandEvent(shDeviceName,
					value.equals(StatusValue.Activated) ? OnOffType.ON : OnOffType.OFF);
			Activator.getPub().post(itemCommandEvent);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public void publish(Event event) {
		Boolean theValue = null;
		Activator.logD("changedCurrentLevel", "Changed-Event received");
		if (event instanceof ItemStateEvent) {
			ItemStateEvent stateEvent = (ItemStateEvent) event;
			State s = stateEvent.getItemState();
			if (s instanceof OnOffType) {
				theValue = Boolean.valueOf(((OnOffType) s).compareTo(OnOffType.ON) == 0);
			}
		}
		if (theValue != null) {
			SwitchController d = (SwitchController) ontDevice;
			d.setValue(theValue.booleanValue() ? StatusValue.Activated : StatusValue.NotActivated);
			cp.publish(new ContextEvent(d, SwitchController.PROP_HAS_VALUE));
		} // else dont bother TODO log
	}

	public void unregister() {
		super.unregister();
		cp.close();
	}

}

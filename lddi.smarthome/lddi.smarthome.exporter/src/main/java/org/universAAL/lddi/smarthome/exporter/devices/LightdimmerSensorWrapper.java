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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
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
import org.universAAL.ontology.device.LightSensor;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class LightdimmerSensorWrapper extends AbstractIntegerCallee {
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
	public LightdimmerSensorWrapper(ModuleContext context, String itemName) {
		super(context, new ServiceProfile[] { getServiceProfileGET(Activator.NAMESPACE + itemName + "handler",
				new LightSensor(Activator.NAMESPACE + itemName)) }, Activator.NAMESPACE + itemName + "handler");

		Activator.logD("LightdimmerSensorWrapper", "Ready to subscribe");
		shDeviceName = itemName;

		// URI must be the same declared in the super constructor
		String deviceURI = Activator.NAMESPACE + itemName;
		ontDevice = new LightSensor(deviceURI);

		// Commissioning
		// TODO Set location based on tags?

		// Context reg
		ContextProvider info = new ContextProvider(deviceURI + "Provider");
		info.setType(ContextProviderType.controller);
		ContextEventPattern cep = new ContextEventPattern();
		MergedRestriction subjectRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_SUBJECT,
				ontDevice);
		MergedRestriction predicateRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE, LightSensor.PROP_HAS_VALUE);
		// TODO Object restr
		cep.addRestriction(subjectRestriction);
		cep.addRestriction(predicateRestriction);
		info.setProvidedEvents(new ContextEventPattern[] { cep });
		cp = new DefaultContextPublisher(context, info);
	}

	@Override
	public Integer executeGet() {
		PercentType value = (PercentType) Activator.getOpenhab().get(shDeviceName)
				.getStateAs((Class<? extends State>) PercentType.class);
		Activator.logD("getStatus", "The service called was 'get the status'");
		if (value == null)
			return null;
		return Integer.valueOf(value.intValue());
	}

	@Override
	public boolean executeSet(Integer value) {
		return false;
	}

	public void publish(Event event) {
		Integer theValue = null;
		Activator.logD("changedCurrentLevel", "Changed-Event received");
		if (event instanceof ItemStateEvent) {
			ItemStateEvent stateEvent = (ItemStateEvent) event;
			State s = stateEvent.getItemState();
			if (s instanceof PercentType) {
				theValue = Integer.valueOf(((PercentType) s).intValue());
			} else if (s instanceof OnOffType) {
				if (((OnOffType) s).compareTo(OnOffType.OFF) == 0) {
					theValue = Integer.valueOf(0);
				} else {
					theValue = Integer.valueOf(100);
				}
			}
		}
		if (theValue != null) {
			LightSensor d = (LightSensor) ontDevice;
			d.setValue(theValue.intValue());
			cp.publish(new ContextEvent(d, LightSensor.PROP_HAS_VALUE));
		} // else dont bother TODO log
	}

	public void unregister() {
		super.unregister();
		cp.close();
	}

}

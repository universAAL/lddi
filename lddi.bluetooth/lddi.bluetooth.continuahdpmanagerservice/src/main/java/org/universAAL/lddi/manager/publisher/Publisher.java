/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
/**
 * x073 Continua agent publisher (agent events will be published over universAAL bus)
 *
 * @author Angel Martinez-Cavero
 * @version 0
 *
 * TSB Technologies for Health and Well-being
 */

// Package
package org.universAAL.lddi.manager.publisher;

// Imports
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.healthmeasurement.owl.BloodPressure;
import org.universAAL.ontology.healthmeasurement.owl.HeartRate;
import org.universAAL.ontology.healthmeasurement.owl.PersonWeight;
import org.universAAL.ontology.measurement.Measurement;
import org.universAAL.ontology.personalhealthdevice.BloodPressureSensor;
import org.universAAL.ontology.personalhealthdevice.HeartRateSensor;
import org.universAAL.ontology.personalhealthdevice.WeighingScale;

// Main class
public class Publisher {

	// Atributes
	// Default context publisher
	private ContextPublisher cp;
	// Context provider info (provider type)
	ContextProvider cpInfo = new ContextProvider();
	// Module context
	ModuleContext mc;
	// URI prefix
	public static final String PUBLISHER_URI_PREFIX = "http://ontology.universAAL.org/ContinuaBTPersonalHealtDevice.owl#";

	// Constructor

	/**
	 * Publisher contructor
	 *
	 * @param context
	 *            - framework bundle context
	 */
	public Publisher(BundleContext context) {
		// Instantiate the context provider info with a valid provider URI
		cpInfo = new ContextProvider(PUBLISHER_URI_PREFIX + "personalHealthDeviceContextProvider");
		mc = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { context });
		// Set to type gauge (only publishes data information it senses)
		cpInfo.setType(ContextProviderType.gauge);
		// Set the provided events to unknown with an empty pattern
		cpInfo.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		// Create and register the context publisher
		cp = new DefaultContextPublisher(mc, cpInfo);
	}

	// Methods

	/** Publish weighting scale events to universAAL bus */
	public void publishWeightEvent(int weight) {
		WeighingScale ws = new WeighingScale(PUBLISHER_URI_PREFIX + "continuaBTWeighingScale");
		PersonWeight m_ws = new PersonWeight();
		m_ws.setProperty(Measurement.PROP_VALUE, Float.valueOf(weight));
		ws.setProperty(WeighingScale.PROP_HAS_VALUE, m_ws);
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_HAS_VALUE));
	}

	/** Publish blood pressure events to universAAL bus */
	public void publishBloodPressureEvent(int sys, int dia, int hr) {
		BloodPressure value = new BloodPressure(PUBLISHER_URI_PREFIX + "BloodPressureMeasurement");

		Measurement systolic = new Measurement();
		systolic.setValue(Float.valueOf(sys));

		Measurement diastolic = new Measurement();
		diastolic.setValue(Float.valueOf(dia));

		value.setSyst(systolic);
		value.setDias(diastolic);

		BloodPressureSensor sensor = new BloodPressureSensor(PUBLISHER_URI_PREFIX + "continuaBTBloodPressureSensor");
		sensor.setValue(value);

		cp.publish(new ContextEvent(sensor, BloodPressureSensor.PROP_HAS_VALUE));

		HeartRate hrvalue = new HeartRate();
		hrvalue.setProperty(Measurement.PROP_VALUE, Integer.valueOf(hr));
		HeartRateSensor hrsensor = new HeartRateSensor(PUBLISHER_URI_PREFIX + "continuaBTHeartRateSensor");
		hrsensor.setValue(hrvalue);

		cp.publish(new ContextEvent(hrsensor, BloodPressureSensor.PROP_HAS_VALUE));
	}
}
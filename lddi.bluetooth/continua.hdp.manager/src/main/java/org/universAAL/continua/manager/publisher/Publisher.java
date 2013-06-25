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
 * x073 Continua agent publisher (agent events will be published over uAAL bus)
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package
package org.universAAL.continua.manager.publisher;

// Imports
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.measurement.Measurement;
import org.universAAL.ontology.personalhealthdevice.BloodPressureMeasurement;
import org.universAAL.ontology.personalhealthdevice.BloodPressureMonitor;
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
	public static final String PUBLISHER_URI_PREFIX = 
		"http://http://ontology.universAAL.org/PersonalHealtDeviceSimulator.owl#";

	// Constructor
	
	/** Publisher contructor 
	 *  @param context - framework bundle context
	 * */
	public Publisher(BundleContext context) {
		// Instantiate the context provider info with a valid provider URI
		cpInfo = new ContextProvider(PUBLISHER_URI_PREFIX + "PersonalHealthDeviceContextProvider");
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { context });
		// Set to type gauge (only publishes data information it senses)
		cpInfo.setType(ContextProviderType.gauge);
		// Set the provided events to unknown with an empty pattern
		cpInfo.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		// Create and register the context publisher
		cp = new DefaultContextPublisher(mc,cpInfo);
	}
	
	// Methods
	
	/** Publish weighting scale events to uAAL bus */	
	public void publishWeightEvent(int weight) {		
		WeighingScale ws = new WeighingScale(PUBLISHER_URI_PREFIX + "WeighingScale");		
		Measurement m_ws = new Measurement(PUBLISHER_URI_PREFIX + "Measurement_Weight");
		m_ws.setValue(String.valueOf(weight));
		ws.setProperty(WeighingScale.PROP_HAS_MEASURED_WEIGHT,m_ws);		
		cp.publish(new ContextEvent(ws,WeighingScale.PROP_HAS_MEASURED_WEIGHT));		
	}
	
	/** Publish blood pressure events to uAAL bus */
	public void publishBloodPressureEvent(int sys,int dia,int hr) {		
		BloodPressureMeasurement bpme = new BloodPressureMeasurement(PUBLISHER_URI_PREFIX + "BloodPressureMeasurement");
		Measurement m_sys = new Measurement(PUBLISHER_URI_PREFIX + "Measurement_Systolic");
		m_sys.setValue(String.valueOf(sys));
		bpme.setProperty(BloodPressureMeasurement.PROP_HAS_MEASURED_BPSYS,m_sys);
		Measurement m_dia = new Measurement( PUBLISHER_URI_PREFIX + "Measurement_Diastolic");
		m_dia.setValue(String.valueOf(dia));
		bpme.setProperty(BloodPressureMeasurement.PROP_HAS_MEASURED_BPDIA,m_dia);
		Measurement m_hr = new Measurement( PUBLISHER_URI_PREFIX + "Measurement_Heartrate");
		m_hr.setValue(String.valueOf(hr));
		bpme.setProperty(BloodPressureMeasurement.PROP_HAS_MEASURED_HEARTRATE, m_hr); 
		BloodPressureMonitor bpmo = new BloodPressureMonitor( PUBLISHER_URI_PREFIX + "BloodPressureMonitor" );
		bpmo.setProperty(BloodPressureMonitor.PROP_HAS_MEASUREMENT, bpme);
		cp.publish(new ContextEvent(bpmo,BloodPressureMonitor.PROP_HAS_MEASUREMENT));
	}
}
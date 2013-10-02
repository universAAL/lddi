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
 * x073 Continua agent context subscriber (agent events will be received over uAAL bus)
 * 
 * @author Angel Martinez-Cavero, Thomas Fuxreiter
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package
package org.universAAL.lddi.subscriber.test.gui;

// Imports
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextSubscriber;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.personalhealthdevice.BloodPressureMeasurement;
import org.universAAL.ontology.personalhealthdevice.BloodPressureMonitor;
import org.universAAL.ontology.personalhealthdevice.WeighingScale;

// Main class
public class Subscriber extends ContextSubscriber {

	// Attributes
	/** Bundle context object */
	private BundleContext ctx = null;
	
	/** Main GUI object */
	private GUI gui = null;
	
	/** Agent event received */
	private String remoteDevice = null;
	
	/** Object instance */
	private Subscriber INSTANCE = null;
	
	/** Measured data */
	private String sysMeasuredValue = null, diaMeasuredValue = null, hrMeasuredValue = null;
	private String weightMeasuredValue = null;	

	// Constructor
	public Subscriber(ModuleContext mc,BundleContext bc) {
		super(mc,getContextSubscriptionParams());	
		ctx = bc;
		INSTANCE = this;
	}
	
	// Methods

	/** Define the context event pattern to be subscribed */	 
    private static ContextEventPattern[] getContextSubscriptionParams() {
    	// Blood pressure device context event
    	ContextEventPattern cep1 = new ContextEventPattern();
    	cep1.addRestriction(MergedRestriction.getAllValuesRestriction(ContextEvent.PROP_RDF_SUBJECT,BloodPressureMonitor.MY_URI));
    	// Weighing scale context event
    	ContextEventPattern cep2 = new ContextEventPattern();
    	cep2.addRestriction(MergedRestriction.getAllValuesRestriction(ContextEvent.PROP_RDF_SUBJECT,WeighingScale.MY_URI));
    	
    	return new ContextEventPattern[] {cep1,cep2 };
    }
    
	/** Receive and process context events */	 
	@Override
	public void handleContextEvent(ContextEvent event) {		
		// Blood pressure monitor event
		if (event.getRDFObject() instanceof BloodPressureMeasurement) {
			System.out.println("Received Blood pressure monitor event");
			BloodPressureMeasurement bpme = (BloodPressureMeasurement) event.getRDFObject();
			showReceivedEventInfo(event);	
			sysMeasuredValue = bpme.getMeasuredBPSys().getValue().toString();
			diaMeasuredValue = bpme.getMeasuredBPDia().getValue().toString();
			hrMeasuredValue = bpme.getMeasuredHeartRate().getValue().toString();
			remoteDevice = "bloodPressure";
		// Weighing scale event	
		} else if (event.getRDFSubject() instanceof WeighingScale) {
			double temp = -1;
			System.out.println("Received Weighing scale event");
			WeighingScale ws = (WeighingScale) event.getRDFSubject();
			showReceivedEventInfo(event);
			temp = Double.parseDouble(ws.getMeasuredWeight().getValue().toString());
			if(temp >= 1000)
				weightMeasuredValue = ""+ (temp/1000);
			else
				weightMeasuredValue = ws.getMeasuredWeight().getValue().toString();
			remoteDevice = "weightingScale";
		}
		// Create and show main GUI frame
		new Thread(){
			public void run() {
				gui = new GUI(ctx,remoteDevice,getInstance());								
				gui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
				gui.addWindowListener(new WindowAdapter() {				
					public void windowClosing(WindowEvent e) {
						gui.closeGUI();
					}
				});
				gui.setVisible(true);
			}
		}.start();
	}
	
	/** */
	public void showReceivedEventInfo(ContextEvent event) {		
		System.out.println("Subject: "+event.getSubjectURI());
		System.out.println("Subject type: "+event.getSubjectTypeURI());
		System.out.println("Predicate: "+event.getRDFPredicate());
	}
	
	/** Getters */
	
	public Subscriber getInstance() {
		return INSTANCE;
	}
	
	public String getSysMeasuredValue() {
		return sysMeasuredValue;
	}

	public String getDiaMeasuredValue() {
		return diaMeasuredValue;
	}

	public String getHrMeasuredValue() {
		return hrMeasuredValue;
	}

	public String getWeightMeasuredValue() {
		return weightMeasuredValue;
	}
	/** Communication channel broken */
	@Override
	public void communicationChannelBroken() {
		//TODO something if proceed
	}
}
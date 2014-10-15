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
 * Continua HDP manager service descriptor
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.lddi.hdp;

// Imports
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.ResourceFactory;
import org.universAAL.middleware.service.owls.process.ProcessInput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.continua.ContinuaHealthDevice;
import org.universAAL.ontology.continua.ContinuaHealthManager;

// Main class
public class ProvidedContinuaHdpManagerService extends ContinuaHealthManager {

	// Attributes
	
	/** Number of services our service callee is goint to offer */
	public static final ServiceProfile[] profiles = new ServiceProfile[4];
	
	/** URI for the namespace and the service callee as itself */
	public static final String CONTINUA_HDP_MANAGER_NAMESPACE = "http://www.tsbtecnologias.es/ContinuaHdpManager.owl#";
	public static final String MY_URI = CONTINUA_HDP_MANAGER_NAMESPACE + "ProvidedContinuaHdpManagerService";
	
	/** We need to define one URI per each service we plan to provide */
	// Continua HDP manager on
	public static final String SERVICE_SWITCH_ON = CONTINUA_HDP_MANAGER_NAMESPACE+"switchOn";
	// Continua HDP manager off
	public static final String SERVICE_SWITCH_OFF = CONTINUA_HDP_MANAGER_NAMESPACE+"switchOff";	
	// Input Continua Health device (include MAC address and data type)
	public static final String INPUT_CONTINUA_HEALTH_DEVICE = CONTINUA_HDP_MANAGER_NAMESPACE+"ContinuaHealthDevice";	
			
	// Registration process to the uAAL mw
	static {
		OntologyManagement.getInstance().register(Activator.mc,
				new SimpleOntology(MY_URI,ContinuaHealthManager.MY_URI,new ResourceFactory() {
							public Resource createInstance(String classURI,String instanceURI,int factoryIndex) {
								return new ProvidedContinuaHdpManagerService(instanceURI);
							}
						}));
		
		// Start service
		ProvidedContinuaHdpManagerService switchOn = new ProvidedContinuaHdpManagerService(SERVICE_SWITCH_ON);
		ProcessInput input1 = new ProcessInput(INPUT_CONTINUA_HEALTH_DEVICE);
		input1.setParameterType(ContinuaHealthDevice.MY_URI);		
		switchOn.addFilteringInput(INPUT_CONTINUA_HEALTH_DEVICE,ContinuaHealthDevice.MY_URI,1,1,
				new String[] {ContinuaHealthManager.PROP_HAS_CONTINUA_DEVICE});		
		switchOn.myProfile.addAddEffect(
				new String[] {ContinuaHealthManager.PROP_HAS_CONTINUA_DEVICE},input1);
		profiles[0] = switchOn.myProfile;
		// Stop service
		ProvidedContinuaHdpManagerService switchOff = new ProvidedContinuaHdpManagerService(SERVICE_SWITCH_OFF);
		ProcessInput input2 = new ProcessInput(INPUT_CONTINUA_HEALTH_DEVICE);
		input2.setParameterType(ContinuaHealthDevice.MY_URI);		
		switchOff.addFilteringInput(INPUT_CONTINUA_HEALTH_DEVICE,ContinuaHealthDevice.MY_URI,1,1,
				new String[] {ContinuaHealthManager.PROP_HAS_NOT_CONTINUA_DEVICE});		
		switchOff.myProfile.addChangeEffect(
				new String[] {ContinuaHealthManager.PROP_HAS_NOT_CONTINUA_DEVICE},input2);
		profiles[1] = switchOff.myProfile;
	}
	
	// Constructor
	private ProvidedContinuaHdpManagerService(String instanceURI) {
		super(instanceURI);
	}
}
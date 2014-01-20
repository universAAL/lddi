/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
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
package org.universAAL.lddi.knx.exporter;

import java.util.Hashtable;

import org.universAAL.middleware.owl.Enumeration;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.ResourceFactory;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.LightController;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchController;
import org.universAAL.ontology.phThing.Device;
import org.universAAL.ontology.phThing.DeviceService;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxServiceCalleeProvidedService extends DeviceService {

	public static final String KNX_SERVER_NAMESPACE
	= "http://ontology.universAAL.org/KnxServer.owl#";
	public static final String MY_URI
	= KNX_SERVER_NAMESPACE + "KnxService";

	// a service for determining the device type
	static final String SERVICE_GET_CONTROLLED_DEVICES
	= KNX_SERVER_NAMESPACE + "getControlledDevices";
	// a service for switching a device off
	static final String SERVICE_SWITCH_OFF
	= KNX_SERVER_NAMESPACE + "switchOff";
	// a service for switching a device on
	static final String SERVICE_SWITCH_ON
	= KNX_SERVER_NAMESPACE + "switchOn";

	static final String INPUT_DEVICE_URI = KNX_SERVER_NAMESPACE + "deviceURI";
	static final String OUTPUT_CONTROLLED_DEVICES = KNX_SERVER_NAMESPACE + "controlledDevices";
	
	public static ServiceProfile[] profiles = new ServiceProfile[3];
	private static Hashtable serverLevelRestrictions = new Hashtable();


	static {
		OntologyManagement.getInstance().register(Activator.mc,
				new SimpleOntology(MY_URI, DeviceService.MY_URI,
						new ResourceFactory() {
//					@Override
					public Resource createInstance(String classURI,
							String instanceURI, int factoryIndex) {
						return new KnxServiceCalleeProvidedService(instanceURI);
					}
				}));
		
		String[] ppControls = new String[] { DeviceService.PROP_CONTROLS};
		String[] ppStatus = new String[] { DeviceService.PROP_CONTROLS, SwitchController.PROP_HAS_VALUE};
		
		addRestriction(
				MergedRestriction.getAllValuesRestrictionWithCardinality(
				SwitchController.PROP_HAS_VALUE, // URI of the property
				new Enumeration(new Integer[] { new Integer(0), new Integer(100) }), // Type
				1, // min. cardinality
				1 // max. cardinality
				),
				ppStatus,
				serverLevelRestrictions
				);				
				
		KnxServiceCalleeProvidedService getControlledDevices = new KnxServiceCalleeProvidedService(SERVICE_GET_CONTROLLED_DEVICES);
		getControlledDevices.addOutput(OUTPUT_CONTROLLED_DEVICES, Device.MY_URI, 0, 0, ppControls);
		profiles[0] = getControlledDevices.myProfile;
		
		KnxServiceCalleeProvidedService switchOff = new KnxServiceCalleeProvidedService(SERVICE_SWITCH_OFF);
		switchOff.addFilteringInput(INPUT_DEVICE_URI, SwitchController.MY_URI, 1, 1, ppControls);
		switchOff.myProfile.addChangeEffect(ppStatus, StatusValue.NOT_ACTIVATED);
		profiles[1] = switchOff.myProfile;
		
		KnxServiceCalleeProvidedService switchOn = new KnxServiceCalleeProvidedService(SERVICE_SWITCH_ON);
		switchOn.addFilteringInput(INPUT_DEVICE_URI, SwitchController.MY_URI, 1, 1, ppControls);
		switchOn.myProfile.addChangeEffect(ppStatus, StatusValue.ACTIVATED);
		profiles[2] = switchOn.myProfile;
		
	}
	
	
	private KnxServiceCalleeProvidedService(String uri) {
		super(uri);
	}
	
}

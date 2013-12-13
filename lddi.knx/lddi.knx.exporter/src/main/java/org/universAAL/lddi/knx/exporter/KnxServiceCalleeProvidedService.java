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
	static final String SERVICE_TURN_OFF
	= KNX_SERVER_NAMESPACE + "turnOff";
	// a service for switching a device on
	static final String SERVICE_TURN_ON
	= KNX_SERVER_NAMESPACE + "turnOn";

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
		String[] ppBrightness = new String[] { DeviceService.PROP_CONTROLS, LightController.PROP_HAS_VALUE};
		
		addRestriction(
				MergedRestriction.getAllValuesRestrictionWithCardinality(
				LightController.PROP_HAS_VALUE, // URI of the property
				new Enumeration(new Integer[] { new Integer(0), new Integer(100) }), // Type
				1, // min. cardinality
				1 // max. cardinality
				),
				ppBrightness,
				serverLevelRestrictions
				);				
				
		KnxServiceCalleeProvidedService getControlledDevices = new KnxServiceCalleeProvidedService(SERVICE_GET_CONTROLLED_DEVICES);
		getControlledDevices.addOutput(OUTPUT_CONTROLLED_DEVICES, Device.MY_URI, 0, 0, ppControls);
		profiles[0] = getControlledDevices.myProfile;
		
		KnxServiceCalleeProvidedService turnOffLight = new KnxServiceCalleeProvidedService(SERVICE_TURN_OFF);
		turnOffLight.addFilteringInput(INPUT_DEVICE_URI, LightController.MY_URI, 1, 1, ppControls);
		turnOffLight.myProfile.addChangeEffect(ppBrightness, new Integer(0));
		profiles[1] = turnOffLight.myProfile;
		
		KnxServiceCalleeProvidedService turnOnLight = new KnxServiceCalleeProvidedService(SERVICE_TURN_ON);
		turnOnLight.addFilteringInput(INPUT_DEVICE_URI, LightController.MY_URI, 1, 1, ppControls);
		turnOnLight.myProfile.addChangeEffect(ppBrightness, new Integer(100));
		profiles[2] = turnOnLight.myProfile;
		
	}
	
	
	private KnxServiceCalleeProvidedService(String uri) {
		super(uri);
	}
	
}

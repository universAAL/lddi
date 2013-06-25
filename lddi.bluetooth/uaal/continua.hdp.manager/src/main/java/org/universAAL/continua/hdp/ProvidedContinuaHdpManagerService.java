/**
 * Continua HDP manager service descriptor
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.continua.hdp;

// Imports
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.impl.ResourceFactoryImpl;
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
		OntologyManagement.getInstance().register(
				new SimpleOntology(MY_URI,ContinuaHealthManager.MY_URI,new ResourceFactoryImpl() {
							@Override
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
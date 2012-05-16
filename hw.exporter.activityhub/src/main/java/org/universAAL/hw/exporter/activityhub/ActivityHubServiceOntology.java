package org.universAAL.hw.exporter.activityhub;

import java.util.Hashtable;

import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.owl.SimpleOntology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.rdf.impl.ResourceFactoryImpl;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.activityhub.ActivityHub;
import org.universAAL.ontology.activityhub.ActivityHubSensor;
import org.universAAL.ontology.activityhub.ActivityHubSensorEvent;
import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.phThing.PhysicalThing;

/**
 * Definition of service for the uAAL service bus (service profiles) 
 * for available ActivityHub devices. 
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubServiceOntology  extends ActivityHub {

	// define namespace URIs
	public static final String ACTIVITYHUB_SERVER_NAMESPACE = 
		Resource.uAAL_NAMESPACE_PREFIX + "ActivityHubServer.owl#";
	
	public static final String MY_URI =	
		ACTIVITYHUB_SERVER_NAMESPACE + "ActivityHubService";

	public static final String DEVICE_URI_PREFIX = 
    	ActivityHubServiceOntology.ACTIVITYHUB_SERVER_NAMESPACE + 
    	"controlledActivityHubDevice";

	// define service URIs
	static final String SERVICE_GET_CONTROLLED_ACTIVITYHUB_SENSORS =
		ACTIVITYHUB_SERVER_NAMESPACE + "getControlledActivityHubSensors";
	
	static final String SERVICE_GET_ACTIVITYHUB_SENSOR_INFO = 
		ACTIVITYHUB_SERVER_NAMESPACE + "getActivityHubSensorInfo";

	// define service parameter URIs
	static final String OUTPUT_CONTROLLED_ACTIVITYHUB_SENSORS =
		ACTIVITYHUB_SERVER_NAMESPACE + "controlledActivityHubSensors";
	
    static final String INPUT_SENSOR_URI = 
    	ACTIVITYHUB_SERVER_NAMESPACE + "deviceURI";

    static final String OUTPUT_SENSOR_EVENT = 
    	ACTIVITYHUB_SERVER_NAMESPACE + "event";

    static final String OUTPUT_SENSOR_LOCATION = 
    	ACTIVITYHUB_SERVER_NAMESPACE + "location";
    
    
	// declaration of uaal bus service profiles
    static final ServiceProfile[] profiles = new ServiceProfile[2];

    private static Hashtable serverLightingRestrictions = new Hashtable();


	/** 
	 * we need to register all classes in the ontology for the serialization of the object 
	 */
    static {
		OntologyManagement.getInstance().register(
				new SimpleOntology(MY_URI, ActivityHub.MY_URI,
						new ResourceFactoryImpl() {
					@Override
					public Resource createInstance(String classURI,
							String instanceURI, int factoryIndex) {
						return new ActivityHubServiceOntology(instanceURI);
					}
				})
		);

		/** 
		 * Help structures to define property paths used more than once below
		 */
		String[] ppControls = new String[] { ActivityHub.PROP_CONTROLS };
//		String[] ppLocation = new String[] { ActivityHub.PROP_CONTROLS, 
//				ActivityHubSensor.PROP_PHYSICAL_LOCATION };

		
		// Copied/refactored from smp.lighting.server !! don't know if this MUST be done !?
		/**
		 * class-level restrictions"
		 */
		// that are inherent to the underlying service component (ActivityHubBusServer)

		// Before adding our own restrictions, we first "inherit" the
		// restrictions defined by the superclass
		addRestriction((MergedRestriction) ActivityHub.getClassRestrictionsOnProperty(
				ActivityHub.MY_URI, ActivityHub.PROP_CONTROLS).copy(),
				ppControls, serverLightingRestrictions);
		
		// ActivityHub controls ActivityHubSensors
		addRestriction(MergedRestriction.getAllValuesRestriction(
				ActivityHub.PROP_CONTROLS, ActivityHubSensor.MY_URI), 
				ppControls, serverLightingRestrictions);
		
		/**
		 * create the service description #1 to be registered with the service bus
		 */
		// Create the service-object for retrieving the controlled light bulbs
		ActivityHubServiceOntology getControlledActivityHubSensors = new ActivityHubServiceOntology(
			SERVICE_GET_CONTROLLED_ACTIVITYHUB_SENSORS);
		// Add an output with the given URI (parameter #1) and the following
		// additional info to the service-profile:
		// - it delivers an indefinite number (parameters #3 & #4) of
		//   ActivityHubSensor (parameter #2) objects
		// - that are those controlled by this class of services (parameter #5)
		// Note that because no filtering has been defined, the output will
		// contain all of the controlled ActivityHub Sensors
		getControlledActivityHubSensors.addOutput(OUTPUT_CONTROLLED_ACTIVITYHUB_SENSORS,
			ActivityHubSensor.MY_URI, 0, 0, ppControls);
		
		// we are finished and can add this profile to the list of service
		// profiles to be registered with the service bus
		profiles[0] = getControlledActivityHubSensors.myProfile;

		
		/**
		 * create the service description #2 to be registered with the service bus
		 */
		// Create the service-object for retrieving info about the location and
		// state of one specific ActivityHub device
		ActivityHubServiceOntology getActivityHubSensorInfo = new ActivityHubServiceOntology(
				SERVICE_GET_ACTIVITYHUB_SENSOR_INFO);
		// Add an input with the given URI (parameter #1) and the following
		// additional info to the service-profile:
		// - it will be used to restrict the scope of the process results (cf.
		//   "Filtering" in the method name)
		// - it must be exactly one (parameters #3 & #4) AdaptorPlugActuator (parameter
		//   #2) object
		// - that is used to select the managed adaptor plug (parameter #5) to
		//   be considered in the scope of the process results
		// Note that 'addFilteringInput' works based on equality, i.e. from all
		// objects addressed by 'ppControls' only those are selected that have
		// the same identity as the value passed for this input parameter
		
		
		//kann ich hier den wirklichen sensor ermitteln???
//		getActivityHubSensorInfo.addFilteringInput(INPUT_SENSOR_URI, 
//				ActivityHubSensor.MY_URI, 1, 1,  new String[] { ActivityHub.PROP_CONTROLS });
		
		
		//if ( )
		
		// one of the results of using this service is the delivery of info
		// about the status (parameter #5) of the adaptor plug in the scope
		// (cf. the input parameter); this info will be a single (parameters #3
		// & #4) AdaptorPlugActuatorEvent (parameter #2) that is assigned to an
		// output parameter identifiable by the given URI (parameter 1)
//		getActivityHubSensorInfo.addOutput(OUTPUT_SENSOR_EVENT, 
//				ActivityHubSensorEvent.MY_URI, 1, 1, 
//				new String[] { ActivityHub.PROP_CONTROLS, ActivityHubSensor.PROP_LASTEVENT });
		// another result of using this service is the delivery of info about
		// the location (parameter #5) of the adaptor plug in the scope (cf. the
		// input parameter); this info will be a single (parameters #3 & #4)
		// object of type Location (parameter #2) that is assigned to an output
		// parameter identifiable by the given URI (parameter 1)
		getActivityHubSensorInfo.addOutput(OUTPUT_SENSOR_LOCATION, Location.MY_URI, 1, 1,
				new String[] { ActivityHub.PROP_CONTROLS, PhysicalThing.PROP_PHYSICAL_LOCATION });
		// we are finished and can add this profile to the list of service
		// profiles to be registered with the service bus
		profiles[1] = getActivityHubSensorInfo.myProfile;

	}

	private ActivityHubServiceOntology(String uri) {
		super(uri);
	}
	
}

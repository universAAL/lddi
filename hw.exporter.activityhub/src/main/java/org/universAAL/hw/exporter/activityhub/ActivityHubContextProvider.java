package org.universAAL.hw.exporter.activityhub;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.TypeURI;
import org.universAAL.ontology.activityhub.ActivityHubSensor;
import org.universAAL.ontology.activityhub.ActivityHubSensorEvent;
import org.universAAL.ontology.activityhub.factory.ActivityHubEventFactory;
import org.universAAL.ontology.activityhub.factory.ActivityHubFactory;


/**
 * Provides context event patterns for the uAAL context bus
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubContextProvider {

    private ContextPublisher cp;
    private ActivityHubBusServer theServer;
	private LogService logger;
	private static ActivityHubFactory factory = new ActivityHubFactory();
	//private static ActivityHubEventFactory eventFactory = new ActivityHubEventFactory();
	

	public ActivityHubContextProvider(ModuleContext mc, ActivityHubBusServer busServer) {
		this.theServer = busServer;
		this.logger = busServer.getLogger();
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				ActivityHubServiceOntology.ACTIVITYHUB_SERVER_NAMESPACE
				+ "ActivityHubContextProvider");
		info.setType(ContextProviderType.controller);
		info.setProvidedEvents(providedEvents());
		cp = new DefaultContextPublisher(mc, info);
		
		theServer.addListener(this);
		
		this.logger.log(LogService.LOG_INFO, "Activated ActivityHub ContextEvent Patterns");
	}

    private static ContextEventPattern[] providedEvents() {
    	//look at sample server...
    	//which events (subjects, predicates, objects) ???
    	
    	// theServer controls only ActivityHub Sensors
    	
    	// subject: ActivityHubSensor
    	// predicate: PROP_MEASURED_VALUE from phTing.Sensor
    	// object: ActivityHubSensorEvent
    	
    	// the following is to say that the subject of my context events is
    	// always one single instance of ActivityHubSensor
    	MergedRestriction subjectRestriction = MergedRestriction
    		.getAllValuesRestrictionWithCardinality(
    			ContextEvent.PROP_RDF_SUBJECT,
    			new TypeURI(ActivityHubSensor.MY_URI, false)
    			, 1, 1);
    	
    	// the event is always about the change of measured value
    	MergedRestriction predicateRestriction = MergedRestriction
    		.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
    				ActivityHubSensor.PROP_MEASURED_VALUE);

    	// the reported value will always be of type ActivityHubSensorEvent
    	MergedRestriction objectRestriction = MergedRestriction
    		.getAllValuesRestrictionWithCardinality(
    			ContextEvent.PROP_RDF_OBJECT, 
    			new TypeURI(ActivityHubSensorEvent.MY_URI, false)
    			, 1, 1);
    	
    	ContextEventPattern cep1 = new ContextEventPattern();
    	cep1.addRestriction(subjectRestriction);
    	cep1.addRestriction(predicateRestriction);
    	cep1.addRestriction(objectRestriction);
    	
    	//ContextEventPattern cep2 = new ContextEventPattern();
    	// do we need another pattern?

		return new ContextEventPattern[] { cep1 };
    }
    
    public void activityHubSensorStateChanged(String deviceId, 
    		ActivityHubDeviceCategory activityHubDeviceCategory, int event) {
    	
		// TODO create context event patterns above

    	
		// create instanceURI with trailing deviceId (is different from static SensorConceptURI!)
    	String instanceURI = constructActivityHubSensorURIfromLocalID(deviceId);
    	
		// Use a factory for creation of ontology ISO-SENSOR; switch on activityHubDeviceCategory
    	
    	// There is a factory in the ActivityHub ontology switching on factoryIndex (int)
    	// If we want to use this factory we need to know the mapping factoryIndex for the device categories!
    	// They are defined in ActivityHubOntology

    	int factoryIndex = activityHubDeviceCategory.getTypeCode();
    	
    	//create new AH sensor from ontology
    	ActivityHubSensor ahs = (ActivityHubSensor) factory.createInstance(null, instanceURI, factoryIndex);
    	
    	// create correct eventURI
    	// event factory switching on device category, passing event (int)
    	ActivityHubSensorEvent eventURI = ActivityHubEventFactory.createInstance(factoryIndex, event);
    	ahs.setMeasuredValue(eventURI);
    	
    	// Set the properties of the sensor (location and measurement)
//    	ahs.setLocation(new Room(constructLocationURIfromLocalID(loc)));
    	
    	
    	
		// create appropriate event

    	
    	// finally create an context event and publish it with the ActivityHubSensor
    	// as subject and the property that changed as predicate
    	cp.publish(new ContextEvent(ahs, ActivityHubSensor.PROP_MEASURED_VALUE));
    	
    }
    
    private static String constructActivityHubSensorURIfromLocalID(String localID) {
    	return ActivityHubServiceOntology.DEVICE_URI_PREFIX + localID;
    }
    
//    /*
//     * Only this method publishes events to the context bus
//     */
//    public void lampStateChanged(int lampID, String loc, boolean isOn) {
//	// Create an object that defines a specific lamp
//	LightSource ls = new LightSource(constructLampURIfromLocalID(lampID));
//	// Set the properties of the light (location and brightness)
//	ls.setLocation(new Room(constructLocationURIfromLocalID(loc)));
//	ls.setBrightness(isOn ? 100 : 0);
//	LogUtils
//		.logInfo(
//			Activator.mc,
//			LightingProvider.class,
//			"lampStateChanged",
//			new Object[] { "publishing a context event on the state of a lamp!" },
//			null);
//	// finally create an context event and publish it with the light source
//	// as subject and the property that changed as predicate
//	cp.publish(new ContextEvent(ls, LightSource.PROP_SOURCE_BRIGHTNESS));
//    }
    
}

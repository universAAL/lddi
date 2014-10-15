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

package org.universAAL.lddi.exporter.activityhub;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.lib.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
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
import org.universAAL.ontology.activityhub.factory.ActivityHubFactory;
import org.universAAL.ontology.activityhub.factory.ActivityHubEventFactory;


/**
 * Provides context event patterns for the uAAL context bus
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class AHContextPublisher {

    private DefaultContextPublisher cp;
    private AHManager theServer;
	private LogService logger;
	private static ActivityHubFactory factory = new ActivityHubFactory();
	//private static ActivityHubEventFactory eventFactory = new ActivityHubEventFactory();
	

	public AHContextPublisher(AHManager busServer) {
		this.theServer = busServer;
		this.logger = busServer.getLogger();
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				AHServiceOntology.ACTIVITYHUB_SERVER_NAMESPACE
				+ "AHContextPublisher");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(providedEvents());
		cp = new DefaultContextPublisher(Activator.mc, info);
		
		theServer.addListener(this);
		
		this.logger.log(LogService.LOG_INFO, "Activated ActivityHub ContextEvent Patterns");
	}

    private static ContextEventPattern[] providedEvents() {
    	//look at sample server...
    	//which events (subjects, predicates, objects) ???
    	
    	// ahManager controls only ActivityHub Sensors
    	
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
//    	MergedRestriction objectRestriction = MergedRestriction
//    		.getAllValuesRestrictionWithCardinality(
//    			ContextEvent.PROP_RDF_OBJECT, 
//    			new TypeURI(ActivityHubSensorEvent.MY_URI, false)
//    			, 1, 1);
    	
    	ContextEventPattern cep1 = new ContextEventPattern();
    	cep1.addRestriction(subjectRestriction);
    	cep1.addRestriction(predicateRestriction);
//    	cep1.addRestriction(objectRestriction);
    	
    	//ContextEventPattern cep2 = new ContextEventPattern();
    	// do we need another pattern?
    	ContextEventPattern cep2 = new ContextEventPattern();

		return new ContextEventPattern[] { cep1, cep2 };
    }
    
    /**
     * publish activityhub sensor event on the uAAL context bus
     * 
     * @param deviceId
     * @param activityHubDeviceCategory
     * @param event
     */
    public void activityHubSensorStateChanged(String deviceId, 
    		ActivityHubDeviceCategory activityHubDeviceCategory, int event) {
    	
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
    	
    	LogUtils.logInfo(Activator.mc, AHContextPublisher.class,
			"activityHubSensorStateChanged", new Object[] { "publishing a context event on the state of a " +
					"activityhub sensor!" }, null);
    	
    	// finally create an context event and publish it with the ActivityHubSensor
    	// as subject and the property that changed as predicate
    	cp.publish(new ContextEvent(ahs, ActivityHubSensor.PROP_MEASURED_VALUE));
    	
    }
    
    private static String constructActivityHubSensorURIfromLocalID(String localID) {
    	return AHServiceOntology.DEVICE_URI_PREFIX + localID;
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

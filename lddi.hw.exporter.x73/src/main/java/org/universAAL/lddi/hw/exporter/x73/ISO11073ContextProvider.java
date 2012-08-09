package org.universAAL.lddi.hw.exporter.x73;

import org.osgi.service.log.LogService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.TypeURI;
import org.universAAL.ontology.X73Ontology;
import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.phThing.Sensor;
import org.universAAL.ontology.x73.BodyWeight;
import org.universAAL.ontology.x73.MDS;
import org.universAAL.ontology.x73.MDSAttribute;
import org.universAAL.ontology.x73.WeighingScale;
import org.universAAL.ontology.x73.X73Factory;

/**
 * Provides context event patterns for the uAAL context bus
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ISO11073ContextProvider {

    private ContextPublisher cp;
	private ISO11073DBusServer theServer;
	private LogService logger;
	private static X73Factory factory = new X73Factory();

	public ISO11073ContextProvider(ModuleContext mc,
			ISO11073DBusServer x73Server) {
		
		this.theServer = x73Server;
		this.logger = x73Server.getLogger();
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				X73Ontology.NAMESPACE
				+ "x73ContextProvider");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(getWeighingCEP());
		// Set the provided events to unknown with an empty pattern
		//info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
		
		//theServer.addListener(this);
		
		this.logger.log(LogService.LOG_INFO, "Activated x73 ContextEvent Patterns");
	}

	private static ContextEventPattern[] getWeighingCEP() {
			
		MergedRestriction subjectRestriction = MergedRestriction
				.getAllValuesRestriction(ContextEvent.PROP_RDF_SUBJECT,
						WeighingScale.MY_URI);
		
		MergedRestriction predicateRestriction = MergedRestriction
				.getAllValuesRestriction(ContextEvent.PROP_RDF_PREDICATE,
						WeighingScale.PROP_HAS_MEASURED_WEIGHT);
		
		/*MergedRestriction objectRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_OBJECT,
						MDSAttribute.basicNuObservedValue);
		*/
		ContextEventPattern cep_weight = new ContextEventPattern();
		cep_weight.addRestriction(subjectRestriction);
		cep_weight.addRestriction(predicateRestriction);
		//cep_weight.addRestriction(objectRestriction);
	
		return new ContextEventPattern[] { cep_weight };
	}
	
	public void publishEvent(String weight) {		
		Sensor ws = new Sensor("http://www.tsbtecnologias.es/WeighingScale.owl#WeighingScale");
		ws.setLocation(new Location("http://www.tsbtecnologias.es/location.owl#TSBlocation","TSB"));
		ws.setProperty(Sensor.PROP_MEASURED_VALUE,weight);		
		cp.publish(new ContextEvent(ws,Sensor.PROP_MEASURED_VALUE));
	}	

	//called by MyAgent.Disassociated
	public void measureWeight(String deviceId, String measuredWeight) {

		this.logger.log(LogService.LOG_INFO, "test1");
				
		publishEvent(measuredWeight);
		
		this.logger.log(LogService.LOG_INFO, "test2");
		
		WeighingScale ws = new WeighingScale(deviceId);
		BodyWeight bw = new BodyWeight();
		//set BodyWeight
		ws.setHasMeasuredWeight(bw);
		
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_HAS_MEASURED_WEIGHT));
		
		
		// create instanceURI with trailing deviceId (is different from static SensorConceptURI!)
		//String instanceURI = constructx73URIfromLocalID(deviceId);
	
		// Use a factory for creation of ontology
		//create new x73 device from ontology
//		MDS mds = (MDS) factory.createInstance(null, instanceURI, factoryIndex);
		
		// create correct eventURI
		// event factory switching on device category, passing event (int)
//	MDSAttribute mdsAttrURI = ActivityHubEventFactory.createInstance(factoryIndex, event);
//	mds.setMeasuredValue(mdsAttrURI);
	
		// create appropriate event
		LogUtils.logInfo(Activator.moduleContext, ISO11073ContextProvider.class,
			"x73StateChanged", new Object[] { "publishing a context event on the state of a " +
					"x73 device!" }, null);
		
		// finally create an context event and publish it with the ActivityHubSensor
		// as subject and the property that changed as predicate
//TODO
//		cp.publish(new ContextEvent(mds, MDS.PROP_MEASURED_VALUE));
		
	}
	
	private static String constructx73URIfromLocalID(String localID) {
		return X73Ontology.NAMESPACE + localID;
	}

}
package org.universAAL.hwexporter.activityhub.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextSubscriber;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.activityhub.ActivityHubSensor;

/**
 * This class handles ontological context events by subscribing specific event patterns
 * on the uAAL context bus
 * This class is stateless; no objects (sensors) are stored here
 * LogUtil from uAAL-Middleware is used here
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class MyActivityHubContextListener extends ContextSubscriber {

	/** This is the client application anchor; which knows nothing about Ontologies :-) */
	private ActivityHubClient ahc = null;

	// For log output on GUI
	DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Define the context event pattern: all Events where subject is instance of ActivityHubSensor
	 * @return ContextEventPattern[]
	 */
    private static ContextEventPattern[] getContextSubscriptionParams() {
    	// I am interested in all events with an activity hub sensor as subject
    	ContextEventPattern cep = new ContextEventPattern();
    	cep.addRestriction(MergedRestriction.getAllValuesRestriction(
    		ContextEvent.PROP_RDF_SUBJECT, ActivityHubSensor.MY_URI));
    	return new ContextEventPattern[] { cep };
    }

	/**
	 * Constructor
	 * Registration of context event patterns on the uAAL context bus
	 * @param mc uAAL Middleware ModuleContext
	 * @param ahc link to client application
	 */
	MyActivityHubContextListener(ModuleContext mc, ActivityHubClient ahc) {
		// the constructor register us to the bus
		super(mc, getContextSubscriptionParams());
		LogUtils.logInfo(ArtifactIntegrationTest.mc, MyActivityHubContextListener.class,
				"constructor",new Object[] { "context event patterns are registered now" },null);
		this.ahc = ahc;
	}


	/**
	 * receive and process centext events
	 * log to console
	 * send event details to GUI as String[]
	 * 
	 * @param event from context bus
	 */
	@Override
	public void handleContextEvent(ContextEvent event) {
		LogUtils.logInfo(ArtifactIntegrationTest.mc, MyActivityHubContextListener.class,
			"handleContextEvent", new Object[] {
			"\n*****************************\n",
			"Received context event:\n", "    Subject     = ",
			event.getSubjectURI(), "\n", "    Subject type= ",
			event.getSubjectTypeURI(), "\n", "    Predicate   = ",
			event.getRDFPredicate(), "\n", "    Object      = ",
			event.getRDFObject() } , null);

		String[] log = new String[] {	
				"************ Last received context event: " +
				dfm.format(new Date(System.currentTimeMillis())),
				"  Subject     = " + event.getSubjectURI(),
				"  Subject type= " + event.getSubjectTypeURI(),
				"  Predicate   = " + event.getRDFPredicate(),
				"  Object      = " + event.getRDFObject() };
		
		this.ahc.showContextEvent( log );
	}

	@Override
	public void communicationChannelBroken() {
		LogUtils.logWarn(ArtifactIntegrationTest.mc, MyActivityHubContextListener.class,
				"communicationChannelBroken",null,null);
	}
}

package org.universAAL.continua.weighingscale.publisher;
/**
 * x073 Continua agent publisher (agent events will be published over uAAL bus)
 * 
 * @author Angel Martinez-Cavero (thx to Miguel-Angel Llorente)
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */


// Imports
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.x73.MDS;
import org.universAAL.ontology.x73.WeighingScale;

// Main class
public class Publisher {
	
	// Atributes
	// Default context publisher
	private ContextPublisher cp;
	// Context provider info (provider type)
	ContextProvider cpInfo = new ContextProvider();
	//
	ModuleContext mc;

	// Constructor
	
	/** Publisher contructor 
	 *  @param context - framework bundle context
	 * */
	public Publisher(BundleContext context) {
		// Instantiate the context provider info with a valid provider URI
		cpInfo = new ContextProvider("http://www.tsbtecnologias.es/ContextProvider.owl#weighingScalePublisher");
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] { context });
		// Set to type gauge (only publishes data information it senses)
		cpInfo.setType(ContextProviderType.gauge);
		// Set the provided events to unknown with an empty pattern
		cpInfo.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		// Create and register the context publisher
		cp = new DefaultContextPublisher(mc,cpInfo);
	}
	
	// Methods
	/** Publish weighting scale events to uAAL bus.  
	 *  @param weight - weight measured value
	 * */
	public void publishEvent(String weight) {	
		System.out.println("[TEST] WS event published to uaal context bus");
//		WeighingScale ws = new WeighingScale("http://www.tsbtecnologias.es/WeighingScale.owl#WeighingScale");		
//		ws.setLocation(new Location("http://www.tsbtecnologias.es/location.owl#TSBlocation","TSB"));		
//		ws.setProperty(WeighingScale.PROP_HAS_MEASURED_WEIGHT,weight);
//		System.out.println("I'm alive - 1");
//		cp.publish(new ContextEvent(ws,WeighingScale.PROP_HAS_MEASURED_WEIGHT));
//		System.out.println("I'm alive - 2");
	}	
}
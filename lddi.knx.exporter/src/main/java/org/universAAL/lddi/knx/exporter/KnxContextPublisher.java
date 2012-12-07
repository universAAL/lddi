package org.universAAL.lddi.knx.exporter;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.device.TemperatureSensor;

/**
 * Sends context events to uAAL context bus.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxContextPublisher {

	ModuleContext mc;
	private KnxManager knxManager;

	// Default context publisher
	private ContextPublisher cp;
	// Context provider info (provider type)
	ContextProvider cpInfo = new ContextProvider();

	public static final String KNX_SERVER_NAMESPACE = 
		Resource.uAAL_NAMESPACE_PREFIX + "KNXManager.owl#";

	/**
	 * Constructor.
	 * @param mc
	 * @param knxManager
	 */
	public KnxContextPublisher(ModuleContext mc, KnxManager knxManager) {
		this.mc = mc;
		this.knxManager = knxManager;
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(KNX_SERVER_NAMESPACE + "KNXContextPublisher");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
		
		knxManager.addListener(this);
		
		LogUtils.logDebug(mc, KnxContextPublisher.class, "Constructor", new Object[] {
			"Activated ActivityHub ContextEvent Patterns" }, null);
	}

	/**
	 * A proper ontology class is chosen according to main and sub type.
	 * The event is published on the uAAL context bus.
	 * 
	 * @param deviceId
	 * @param datapointTypeMainNubmer
	 * @param datapointTypeSubNubmer
	 * @param value
	 */
	public void publishKnxEvent(String deviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, float value) {
		
		LogUtils.logDebug(mc, KnxContextPublisher.class, "publishKnxEvent", new Object[] {
			"Event for device " + deviceId + " with datapoint main type " + datapointTypeMainNubmer +
			". sub type " + datapointTypeSubNubmer + " - float value: " + value}, null);

		// Hardcoded Test for Temperature events
		TemperatureSensor ts = new TemperatureSensor(KNX_SERVER_NAMESPACE + "KNXTemperatureSensor" + deviceId);
		ts.setProperty(TemperatureSensor.PROP_HAS_VALUE, value);
		//		ws.setLocation(new Location("http://www.tsbtecnologias.es/location.owl#TSBlocation","TSB"));		
		cp.publish(new ContextEvent(ts,TemperatureSensor.PROP_HAS_VALUE));
	}	

}

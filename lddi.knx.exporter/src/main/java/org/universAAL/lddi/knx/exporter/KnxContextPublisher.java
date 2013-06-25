package org.universAAL.lddi.knx.exporter;

import org.universAAL.lddi.knx.exporter.KnxToDeviceOntologyMappingFactory.DeviceOntologyType;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.device.ContactSensor;
import org.universAAL.ontology.device.MotionSensor;
import org.universAAL.ontology.device.MotionValue;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchActuator;
import org.universAAL.ontology.device.SwitchController;
import org.universAAL.ontology.device.SwitchSensor;
import org.universAAL.ontology.device.TemperatureSensor;
import org.universAAL.ontology.device.ValueDevice;

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
	//ContextProvider cpInfo = new ContextProvider();

	public static final String KNX_SERVER_NAMESPACE = Resource.uAAL_NAMESPACE_PREFIX
			+ "KNXManager.owl#";

	/**
	 * Constructor.
	 * 
	 * @param mc
	 * @param knxManager
	 */
	public KnxContextPublisher(ModuleContext mc, KnxManager knxManager) {
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		this.mc = mc;
		this.knxManager = knxManager;

		// prepare for context publishing
		ContextProvider info = new ContextProvider(KNX_SERVER_NAMESPACE
				+ "KNXContextPublisher");
		info.setType(ContextProviderType.gauge);
		info
				.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);

		knxManager.addListener(this);

		LogUtils.logDebug(mc, KnxContextPublisher.class, "Constructor",
				new Object[] { "Activated ActivityHub ContextEvent Patterns" },
				null);
	}

	/**
	 * A proper ontology class is chosen according to main and sub type. The
	 * event is published on the uAAL context bus.
	 * 
	 * @param deviceId
	 * @param datapointTypeMainNubmer
	 * @param datapointTypeSubNubmer
	 * @param value
	 */
	public void publishKnxEvent(String deviceId, int datapointTypeMainNubmer,
			int datapointTypeSubNubmer, float value) {

		LogUtils
				.logDebug(mc, KnxContextPublisher.class, "publishKnxEvent",
						new Object[] { "Event for device " + deviceId
								+ " with datapoint main type "
								+ datapointTypeMainNubmer + ". sub type "
								+ datapointTypeSubNubmer + " - float value: "
								+ value }, null);

		// Hardcoded Test for Temperature events
		TemperatureSensor ts = new TemperatureSensor(KNX_SERVER_NAMESPACE
				+ "KNXTemperatureSensor" + deviceId);
		ts.setProperty(TemperatureSensor.PROP_HAS_VALUE, value);
		// ws.setLocation(new
		// Location("http://www.tsbtecnologias.es/location.owl#TSBlocation","TSB"));
		cp.publish(new ContextEvent(ts, TemperatureSensor.PROP_HAS_VALUE));
	}

	/**
	 * A proper ontology class is chosen according to main and sub type.
	 * Boolean value is mapped to status ACTIVATED or NOTACTIVATED.
	 * The event is published on the uAAL context bus.
	 * 
	 * @param deviceId
	 * @param datapointTypeMainNubmer
	 * @param datapointTypeSubNubmer
	 * @param value
	 */
	public void publishKnxEvent(String deviceId, int datapointTypeMainNubmer,
			int datapointTypeSubNubmer, boolean value) {

		LogUtils.logDebug(mc, KnxContextPublisher.class, "publishKnxEvent",
						new Object[] { "Event for device " + deviceId
								+ " with datapoint main type "
								+ datapointTypeMainNubmer + ". sub type "
								+ datapointTypeSubNubmer + " - boolean value: "
								+ value }, null);

		ValueDevice device = KnxToDeviceOntologyMappingFactory
				.getDeviceOntologyInstanceForKnxDpt(datapointTypeMainNubmer,
						datapointTypeSubNubmer, deviceId, DeviceOntologyType.Controller);

		// map boolean status to StatusValue or other ...Value from Device Ontology
		if (device == null) {
			LogUtils.logError(mc, KnxContextPublisher.class, "publishKnxEvent", 
					new Object[] { "Device is null!"}, null);
			return;
			
		} else if (device instanceof MotionSensor){
			device.setProperty(ValueDevice.PROP_HAS_VALUE, 
					value ? MotionValue.Detected : MotionValue.NotDetected);

		} else { // For all other sensors which have StatusValue
			device.setProperty(ValueDevice.PROP_HAS_VALUE, 
				value? StatusValue.Activated : StatusValue.NotActivated);
		}

		LogUtils.logDebug(mc, KnxContextPublisher.class, "publishKnxEvent", 
				new Object[] { "Sending context event for device " + device.getURI() +
					" - with event: " + device.getProperty(ValueDevice.PROP_HAS_VALUE)}, 
				null);

		// send it!
		cp.publish(new ContextEvent(device, ValueDevice.PROP_HAS_VALUE));
		
	}

}

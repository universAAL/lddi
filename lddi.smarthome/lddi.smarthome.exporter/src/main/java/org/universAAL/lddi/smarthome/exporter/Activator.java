package org.universAAL.lddi.smarthome.exporter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.universAAL.lddi.smarthome.exporter.devices.*;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.device.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemRegistry;

public class Activator implements BundleActivator {
	private static BundleContext osgiContext = null;
	private static ModuleContext moduleContext = null;
	private static ItemRegistry openhab;

	private static HashMap<String, GenericDevice> setOfDevices;
	private EventSubscriber sub;
	private ServiceRegistration sr;
	private static EventPublisher pub;
	// TODO use ints and switch()
	public static final String ITEM_TYPE_CONTACT = "Contact";
	public static final String ITEM_TYPE_DIMMER = "Dimmer";
	public static final String ITEM_TYPE_NUMBER = "Number";
	public static final String ITEM_TYPE_SHUTTER = "Rollershutter";
	public static final String ITEM_TYPE_SWITCH = "Switch";

	private Properties types;

	public static final String NAMESPACE = Resource.NAMESPACE_PREFIX + "ESHExporter.owl#";

	public void start(BundleContext bcontext) throws Exception {
		// Set contexts
		osgiContext = bcontext;
		moduleContext = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });
		// Get SH Item Registry
		String filter = "(objectclass=" + ItemRegistry.class.getName() + ")";
		ServiceReference[] references = bcontext.getServiceReferences((String) null, filter);
		if (references.length > 0) {
			openhab = (ItemRegistry) bcontext.getService(references[0]);
		}
		// Get SH Event Publisher
		filter = "(objectclass=" + EventPublisher.class.getName() + ")";
		references = bcontext.getServiceReferences((String) null, filter);
		if (references.length > 0) {
			pub = (EventPublisher) bcontext.getService(references[0]);
		}
		// Load openhab-uaal default mapping, if any
		String configfile = System.getProperty("org.universaal.lddi.smarthome.config");
		types = new Properties();
		InputStream in;
		try {
			in = new FileInputStream(configfile);
			types.load(in);
			in.close();
		} catch (Exception e) {
			LogUtils.logWarn(moduleContext, getClass(), "start", e.getMessage());
		}

		// Scan for devices
		setOfDevices = new HashMap<String, GenericDevice>();
		new Thread() {
			// TODO this can be much better, and shouldnt be in the activator
			public void run() {
				Collection<Item> all = openhab.getAll();
				for (Item item : all) {
					String type = item.getType();
					String subtype = readSubType(item);
					if (ITEM_TYPE_CONTACT.equals(type)) {
						if (WaterFlowSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new WaterflowSensorWrapper(moduleContext, item.getName()));
						} else if (WindowSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new WindowSensorWrapper(moduleContext, item.getName()));
						} else if (PresenceSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new PresenceSensorWrapper(moduleContext, item.getName()));
						} else {
							setOfDevices.put(item.getName(), new ContactSensorWrapper(moduleContext, item.getName()));
						}
					} else if (ITEM_TYPE_DIMMER.equals(type)) {
						if (LightController.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightdimmerControllerWrapper(moduleContext, item.getName()));
						} else if (LightActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightdimmerActuatorWrapper(moduleContext, item.getName()));
						} else if (LightSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightdimmerSensorWrapper(moduleContext, item.getName()));
						} else if (DimmerActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new DimmerActuatorWrapper(moduleContext, item.getName()));
						} else if (DimmerSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new DimmerSensorWrapper(moduleContext, item.getName()));
						} else {
							setOfDevices.put(item.getName(),
									new DimmerControllerWrapper(moduleContext, item.getName()));
						}
					}
					if (ITEM_TYPE_NUMBER.equals(type)) {
						setOfDevices.put(item.getName(), new TemperatureSensorWrapper(moduleContext, item.getName()));
					}
					if (ITEM_TYPE_SHUTTER.equals(type)) {
						if (CurtainActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new CurtainActuatorWrapper(moduleContext, item.getName()));
						} else if (CurtainSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new CurtainSensorWrapper(moduleContext, item.getName()));
						} else if (CurtainController.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new CurtainControllerWrapper(moduleContext, item.getName()));
						} else if (BlindActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new BlindActuatorWrapper(moduleContext, item.getName()));
						} else if (BlindSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new BlindSensorWrapper(moduleContext, item.getName()));
						} else {
							setOfDevices.put(item.getName(), new BlindControllerWrapper(moduleContext, item.getName()));
						}
					}
					if (ITEM_TYPE_SWITCH.equals(type)) {
						if (LightController.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightswitchControllerWrapper(moduleContext, item.getName()));
						} else if (LightActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightswitchActuatorWrapper(moduleContext, item.getName()));
						} else if (LightSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(),
									new LightswitchSensorWrapper(moduleContext, item.getName()));
						} else if (SwitchActuator.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new SwitchActuatorWrapper(moduleContext, item.getName()));
						} else if (SwitchSensor.MY_URI.equals(subtype)) {
							setOfDevices.put(item.getName(), new SwitchSensorWrapper(moduleContext, item.getName()));
						} else {
							setOfDevices.put(item.getName(),
									new SwitchControllerWrapper(moduleContext, item.getName()));
						}
					}
				}
				sub = new Receiver();
				sr = osgiContext.registerService(EventSubscriber.class.getName(), sub, null);
			}
		}.start();
	}

	public void stop(BundleContext arg0) throws Exception {
		sr.unregister();
		Iterator<String> iter = setOfDevices.keySet().iterator();
		for (; iter.hasNext();) {
			String name = (String) iter.next();
			((GenericDevice) setOfDevices.get(name)).unregister();
			iter.remove();
		}
		setOfDevices.clear();
	}

	public static BundleContext getOsgiContext() {
		return osgiContext;
	}

	public static ModuleContext getModuleContext() {
		return moduleContext;
	}

	public static ItemRegistry getOpenhab() {
		return openhab;
	}

	public static EventPublisher getPub() {
		return pub;
	}

	public static HashMap<String, GenericDevice> getSetOfDevices() {
		return setOfDevices;
	}

	public static void logD(String method, String log) {
		LogUtils.logDebug(moduleContext, Activator.class, method, new String[] { log }, null);
	}

	public String readSubType(Item item) {
		Set<String> tags = item.getTags();
		if (tags != null && !tags.isEmpty()) {
			return tags.iterator().next();
		} else {
			return types.getProperty(item.getName());
		}
	}
}

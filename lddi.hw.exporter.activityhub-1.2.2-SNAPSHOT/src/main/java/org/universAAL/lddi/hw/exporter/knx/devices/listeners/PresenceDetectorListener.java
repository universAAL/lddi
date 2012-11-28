//package org.universAAL.hw.exporter.knx.devices.listeners;
//
//import it.cnr.isti.zigbee.ha.device.api.lighting.OccupancySensor;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
//import org.osgi.framework.BundleContext;
//import org.osgi.framework.Constants;
//import org.osgi.framework.InvalidSyntaxException;
//import org.osgi.framework.ServiceEvent;
//import org.osgi.framework.ServiceListener;
//import org.osgi.framework.ServiceReference;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.universAAL.hw.exporter.zigbee.ha.Activator;
//import org.universAAL.hw.exporter.zigbee.ha.devices.PresenceDetectorCallee;
//
///**
// * OSGi Service Listener that looks for a specific service published by the
// * abstraction layer and creates and updates the appropriate exporter callee.
// * 
// * @author alfiva
// * @author Thomas Fuxreiter
// * 
// */
//public class PresenceDetectorListener implements ServiceListener {
//	
//	// Sensortype analog to OccupancySensor from ISO Standard must be registered 
//	// by abstraction layer!
//	
//    private final static String filter = "(" + Constants.OBJECTCLASS + "="
//	    + OccupancySensor.class.getName() + ")";
//    private Object discovery = new Object();
//    private BundleContext context;
//    private HashMap presenceDetectorDevices;
//    private ServiceReference[] srs;
//    private final static Logger log = LoggerFactory
//	    .getLogger(PresenceDetectorListener.class);
//
//    /**
//     * Constructor to be used in the exporter. Configures the listener and
//     * performs initial search.
//     * 
//     * @param context
//     *            The OSGi context
//     * @throws InvalidSyntaxException
//     *             If the service to listen defined in the class is not
//     *             appropriate
//     */
//    public PresenceDetectorListener(BundleContext context)
//	    throws InvalidSyntaxException {
//	this.context = context;
//	synchronized (discovery) {
//	    try {
//		context.addServiceListener(this, filter);
//	    } catch (InvalidSyntaxException e) {
//		e.printStackTrace();
//	    }
//	    presenceDetectorDevices = new HashMap();
//	    srs = context.getServiceReferences(null, filter);
//	    if (srs != null) {
//		log.debug("Detected a new device(s) by {} ", this.getClass()
//			.getName());
//		for (int i = 0; i < srs.length; i++) {
//		    doRegisteruAALService(srs[i]);
//		}
//	    }
//	}
//    }
//
//    public void serviceChanged(ServiceEvent event) {
//	synchronized (discovery) {
//	    ServiceReference sr = event.getServiceReference();
//	    switch (event.getType()) {
//	    case ServiceEvent.REGISTERED: {
//		doRegisteruAALService(sr);
//	    }
//		;
//		break;
//
//	    case ServiceEvent.MODIFIED: {
//		// never modified
//	    }
//		;
//		break;
//
//	    case ServiceEvent.UNREGISTERING: {
//		douAALUnregistering(sr);
//	    }
//		;
//		break;
//	    }
//	}
//
//    }
//
//    private void doRegisteruAALService(ServiceReference sr) {
//	log.debug("Creating a instance of device in uAAL");
//	OccupancySensor PresenceDetectorService = (OccupancySensor) context
//		.getService(sr);
//	presenceDetectorDevices.put(sr, new PresenceDetectorCallee(
//		Activator.mc, PresenceDetectorService));
//    }
//
//    private void douAALUnregistering(ServiceReference sr) {
//	log.debug("Removing a instance of device in uAAL");
//	((PresenceDetectorCallee) presenceDetectorDevices.remove(sr))
//		.unregister();
//	context.ungetService(sr);
//    }
//
//    /**
//     * Disconnects and removes all instantiated exported devices of this type.
//     */
//    public void douAALUnregistering() {
//	log.debug("Removing all instances of these devices in uAAL");
//	Iterator iter = presenceDetectorDevices.keySet().iterator();
//	for (; iter.hasNext();) {
//	    ServiceReference sref = (ServiceReference) iter.next();
//	    ((PresenceDetectorCallee) presenceDetectorDevices.get(sref))
//		    .unregister();
//	    iter.remove();
//	    context.ungetService(sref);
//	}
//	presenceDetectorDevices.clear();
//    }
//}

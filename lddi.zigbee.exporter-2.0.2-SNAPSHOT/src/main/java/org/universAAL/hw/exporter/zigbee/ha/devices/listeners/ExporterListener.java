package org.universAAL.hw.exporter.zigbee.ha.devices.listeners;

import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.universAAL.middleware.service.ServiceCallee;

public abstract class ExporterListener implements ServiceListener {

    protected static String filter;
    protected Object discoveryLock = new Object();
    protected BundleContext context;
    protected HashMap<ServiceReference, ServiceCallee> setOfDevices;

    /**
     * Constructor to be used in the exporter. Configures the listener and
     * performs initial search.
     * 
     * @param context
     *            The OSGi context
     * @throws InvalidSyntaxException
     *             If the service to listen defined in the class is not
     *             appropriate
     */
    public ExporterListener(BundleContext context)
	    throws InvalidSyntaxException {
	this.context = context;
	synchronized (discoveryLock) {
	    try {
		context.addServiceListener(this, filter);
	    } catch (InvalidSyntaxException e) {
		e.printStackTrace();
	    }
	    setOfDevices = new HashMap<ServiceReference, ServiceCallee>();
	    ServiceReference[] srs = context.getServiceReferences(null, filter);
	    if (srs != null) {
		for (int i = 0; i < srs.length; i++) {
		    registeruAALService(srs[i]);
		}
	    }
	}
    }

    public void serviceChanged(ServiceEvent event) {
	synchronized (discoveryLock) {
	    ServiceReference sr = event.getServiceReference();
	    switch (event.getType()) {
	    case ServiceEvent.REGISTERED:
		registeruAALService(sr);
		break;
	    case ServiceEvent.MODIFIED:
		// never modified
		break;
	    case ServiceEvent.UNREGISTERING:
		unregisteruAALService(sr);
		break;
	    }
	}
    }

    /**
     * Registers a service reference as a new instance of exported device.
     * 
     * @param sr
     *            The service reference identifying the instance to register.
     */
    protected abstract void registeruAALService(ServiceReference sr);

    /**
     * Disconnect a single instance of exported device.
     * 
     * @param sr
     *            The service reference identifying the instance to disconnect.
     */
    protected abstract void unregisteruAALService(ServiceReference sr);

    /**
     * Disconnects and removes all instantiated exported devices of this type.
     */
    public abstract void unregisteruAALService();

}

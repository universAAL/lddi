package org.universAAL.hw.exporter.activityhub;

import org.universAAL.hw.exporter.activityhub.util.LogTracker;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceListener;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;

import org.universAAL.iso11073.activityhub.driver.ActivityHubDriverManager;


/**
 * What is this bundle for:
 * Listens for ActivityHub device services (OSGi registry) and registers them as 
 * uAAL device services on the service bus.
 * 
 * Creates context patterns and handles context events (uAAL context bus)
 * for those devices.
 * 
 * Yeah!
 * 
 * @author Thomas Fuxreiter
 *
 */

public class Activator implements BundleActivator {
	
    public static BundleContext context = null;
    public static ModuleContext moduleContext = null;
	private LogTracker logTracker;
    
    //no service listeners needed!
//    private ServiceListener[] listeners = new ServiceListener[4];

    // need any configuration??
//    public static final String PROPS_FILE = "KNX.properties";
    
//    public static final String COMMENTS = "This file stores location information for KNX HW devices";
//    public static final String UNINITIALIZED_SUFFIX = "Unintialized";

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.moduleContext = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { context });
		
		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
		
		new ActivityHubDriverClientImp(context, logTracker);
		
	}

	public void stop(BundleContext arg0) throws Exception {
		// TODO: change to KNX device listeners //
//		((DimmerLightListener) listeners[0]).douAALUnregistering();
//		((OnOffLightListener) listeners[1]).douAALUnregistering();
//		((PresenceDetectorListener) listeners[2]).douAALUnregistering();
//		((TemperatureSensorListener) listeners[3]).douAALUnregistering();
	}


//    /**
//     * Saves commissioning location properties of devices in a preset file.
//     * 
//     * The current format of property=value stored is:
//     * <code>FullDeviceID=URIsuffixOfRoom</code>
//     * 
//     * @param prop
//     */
//    public static synchronized void setProperties(Properties prop) {
//	try {
//	    FileWriter out;
//	    out = new FileWriter(PROPS_FILE);
//	    prop.store(out, COMMENTS);
//	    out.close();
//	} catch (Exception e) {
//	    LogUtils.logError(moduleContext, Activator.class, "setProperties",
//		    new String[] { "Could not set properties file: {}" }, e);
//	}
//    }

//    /**
//     * Gets the commissioning location properties of already known devices from
//     * a preset file.
//     * 
//     * The current format of property=value stored is:
//     * <code>FullDeviceID=URIsuffixOfRoom</code>
//     * 
//     * If the file does not exists, it is created.
//     * 
//     * @return The <code>Properties</code> object with the commissioning data.
//     */
//    public static synchronized Properties getProperties() {
//	Properties prop = new Properties();
//	try {
//	    prop = new Properties();
//	    InputStream in = new FileInputStream(PROPS_FILE);
//	    prop.load(in);
//	    in.close();
//	} catch (java.io.FileNotFoundException e) {
//	    LogUtils.logError(
//		    moduleContext,
//		    Activator.class,
//		    "getProperties",
//		    new String[] { "Properties file does not exist; generating default..." },
//		    e);
//	    setProperties(prop);
//	} catch (Exception e) {
//	    LogUtils.logError(moduleContext, Activator.class, "getProperties",
//		    new String[] { "Could not access properties file: {}" }, e);
//	}
//	return prop;
//    }
	
}

package org.universAAL.knx.devicemanager;

import org.universAAL.knx.devicemanager.util.LogTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/***
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Activator implements BundleActivator {

	private LogTracker logTracker;
	private KnxDeviceManager knxDeviceManager;
	
	public void start(BundleContext context) throws Exception {
		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
		
		knxDeviceManager = new KnxDeviceManager(context, logTracker);
	}


	public void stop(BundleContext context) throws Exception {
		// TODO deregister managed service and device services if not done automatically
	}

}

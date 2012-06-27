package org.universAAL.lddi.knx.dpt1refinementdriver.iso11073;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.lddi.knx.dpt1refinementdriver.iso11073.util.LogTracker;

/***
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Activator implements BundleActivator {

	private KnxDpt1RefinementDriver knxDpt1RefinementDriver;
	private LogTracker logTracker;
	
	public void start(BundleContext context) throws Exception {
		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
		
		knxDpt1RefinementDriver = new KnxDpt1RefinementDriver(context, logTracker);
	}


	public void stop(BundleContext context) throws Exception {
		// the OSGi framework automatically unregisters any services
		// registered by this bundle when it is deactivated 
		
		// references must be removed manually
		knxDpt1RefinementDriver.stop();
		knxDpt1RefinementDriver = null;
	}

}

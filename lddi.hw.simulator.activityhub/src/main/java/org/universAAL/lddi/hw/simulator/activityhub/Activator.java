package org.universAAL.lddi.hw.simulator.activityhub;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.lddi.hw.simulator.activityhub.util.LogTracker;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

/**
 * This bundle is a simulator for ActivityHub events. It creates random events
 * for various ActivityHub Sensor types and sends them to the uAAL context bus.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */

public class Activator implements BundleActivator {

	public static BundleContext context = null;
	public static ModuleContext mc = null;
	private AHSimulator ahSimulator;
	// private AHServiceProvider serviceProvider;
	// private AHContextPublisher contextProvider;
	private LogTracker logTracker;

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.mc = uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context });

		// use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();

		// init server
		ahSimulator = new AHSimulator(context, logTracker, mc);

	}

	public void stop(BundleContext arg0) throws Exception {
	}

}

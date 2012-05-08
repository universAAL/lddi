package org.universAAL.hw.exporter.activityhub;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.hw.exporter.activityhub.util.LogTracker;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;


/**
 * This bundle listens for ActivityHub device services (OSGi registry) 
 * and provides access to them by registering services in the uAAL service bus.
 * 
 * Creates context patterns and handles context events (uAAL context bus)
 * for those devices.
 * 
 * @author Thomas Fuxreiter
 *
 */

public class Activator implements BundleActivator {
	
    public static BundleContext context = null;
    public static ModuleContext moduleContext = null;
    private ActivityHubBusServer busServer;
    private ActivityHubServiceProvider serviceProvider;
    private LogTracker logTracker;
	private Thread thread;

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.moduleContext = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { context });
		
		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
		
		// init server
		busServer = new ActivityHubBusServer(context, logTracker);
		
		// start uAAL service provider
		MyThread runnable = new MyThread(); 
		thread=new Thread(runnable);
		thread.start();
	}

	public void stop(BundleContext arg0) throws Exception {
		thread.interrupt();
	}

	/**
	 * Runnable helper class for starting ActivityHubServiceProvider
	 * 
	 * @author fuxreitert
	 *
	 */
	class MyThread implements Runnable{
		public MyThread() {
		}
		public void run() {
			serviceProvider = new ActivityHubServiceProvider(moduleContext, busServer);			
		}
	}
}

package org.universAAL.lddi.hw.exporter.activityhub;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.lddi.hw.exporter.activityhub.util.LogTracker;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;


/**
 * This bundle listens for ActivityHub device services (OSGi registry) 
 * and provides access to them by offering/registering services on the uAAL service bus.
 * 
 * Creates context patterns and handles context events (uAAL context bus)
 * for those devices.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */

public class Activator implements BundleActivator {
	
    public static BundleContext context = null;
    public static ModuleContext mc = null;
    private ActivityHubBusServer busServer;
//    private ActivityHubServiceProvider serviceProvider;
//    private ActivityHubContextProvider contextProvider;
    private LogTracker logTracker;
	private Thread thread;

	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		Activator.mc = uAALBundleContainer.THE_CONTAINER
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
	 * Runnable helper class for starting ActivityHubServiceProvider.
	 * 
	 * @author Thomas Fuxreiter (foex@gmx.at)
	 *
	 */
	class MyThread implements Runnable{
		public MyThread() {
		}
		public void run() {
//			serviceProvider = 
				new ActivityHubServiceProvider(mc, busServer);
//			contextProvider = 
				new ActivityHubContextProvider(mc, busServer);
		}
	}
}

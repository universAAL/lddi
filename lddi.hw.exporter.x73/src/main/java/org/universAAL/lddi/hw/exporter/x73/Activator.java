package org.universAAL.lddi.hw.exporter.x73;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.hw.exporter.x73.util.LogTracker;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

/**
 * This bundle connects to the local operating system dbus and gathers measurements
 * from ISO 11073 devices. The measurements are exposed to the uAAL context bus. 
 * 
 * @author Thomas Fuxreiter
 *
 */
public class Activator implements BundleActivator {

	public static BundleContext context = null;
    public static ModuleContext moduleContext = null;
    private LogTracker logTracker;
	private Thread thread;
//    private ISO11073ServiceProvider serviceProvider;
    private ISO11073DBusServer x73Server;
    private ISO11073ContextProvider contextProvider;
	
	public void start(BundleContext context) throws Exception {
		//if(1==1)
		//	throw new RuntimeException("Test in Activator: " + "http://ontology.universAAL.org/X73.owl#BloodPressureMonitor");

		
		
		Activator.context = context;
		Activator.moduleContext = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { context });

		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
        LogService logservice = (LogService) logTracker.getService();

		// init server
		x73Server = new ISO11073DBusServer(context, logTracker);
		
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
			//serviceProvider = new ActivityHubServiceProvider(moduleContext, busServer);
			contextProvider = new ISO11073ContextProvider(moduleContext, x73Server);
			System.out.println("pre contextProvider in Activator");
			x73Server.setContextProvider(contextProvider);
			System.out.println("post contextProvider in Activator");
			//contextProvider.measureWeight("test-dev", "test-value");
		}
	}
	
//	static {
//		try {			
//			System.out.println("111 D-Bus connection");
//			System.loadLibrary("unix-java");			
//			System.out.println("222 D-Bus connection");
//		}catch(Exception ex) {
//			System.out.println("Unable to load native library. Please, check your path and OSGi manifest settings...");
//		}
//	}
}

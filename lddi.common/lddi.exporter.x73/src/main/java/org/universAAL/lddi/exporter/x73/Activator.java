/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
     See the NOTICE file distributed with this work for additional
     information regarding copyright ownership

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

package org.universAAL.lddi.exporter.x73;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.exporter.x73.util.LogTracker;
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
			x73Server.setContextProvider(contextProvider);
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

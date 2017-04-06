/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
package org.universAAL.lddi.zwave.exporter;

import java.io.IOException;
import java.util.Timer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.lddi.zwave.exporter.MotionContact.MotionContactSensorPublisher;
import org.universAAL.lddi.zwave.exporter.PowerConsumption.PowerReader;
import org.universAAL.lddi.zwave.exporter.Server.MotionDecoderFactory;
import org.universAAL.lddi.zwave.exporter.Server.MotionServer;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

public class Activator implements BundleActivator {
	public static BundleContext osgiContext = null;
	public static ModuleContext context = null;
	private MotionContactSensorPublisher motionPublisher = null;
	private BundleContext ctx;
	
	public void start(BundleContext bcontext) throws Exception {
		ctx = bcontext;
		context = uAALBundleContainer.THE_CONTAINER
			.registerModule(new Object[] { bcontext });
		new Thread(){
			public void run(){   
				System.out.print("Running power reader \n");
				Timer t = new Timer();
				t.schedule(new PowerReader(ctx), 0, 60*1000);
			}			
		}.start();
		new Thread(){
			public void run(){   
				System.out.print("Running movement detector\n");
				motionPublisher = new MotionContactSensorPublisher(ctx);
				
				MotionDecoderFactory factory = new MotionDecoderFactory(motionPublisher);
				MotionServer motionServer = new MotionServer(factory, 53007);
				try {
					System.out.print("STARTING SERVER");
					motionServer.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}			
		}.start();
		
	}

	public void stop(BundleContext arg0) throws Exception {
		
	}

}

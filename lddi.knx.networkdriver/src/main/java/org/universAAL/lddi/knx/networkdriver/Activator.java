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

package org.universAAL.lddi.knx.networkdriver;

import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.lddi.knx.networkdriver.util.KnxShellCommand;
import org.universAAL.lddi.knx.networkdriver.util.LogTracker;

/***
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Activator implements BundleActivator {

	public static BundleContext context = null;
	public static KnxNetworkDriverImp networkDriver;
	private LogTracker logTracker;
	
	@SuppressWarnings("unchecked")
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
		
		//use a service Tracker for LogService
		logTracker = new LogTracker(context);
		logTracker.open();
		
//        ServiceReference ref = context.getServiceReference(LogService.class.getName());
//        if (ref != null)
//        {
//            logger = (LogService) context.getService(ref);
////            System.out.println("******** LogService found! *********");
//            logger.log(LogService.LOG_INFO, "KNX network driver started!");
//
//        }		
//        else
//        	System.out.println("[KNX Network Driver] WARNING: No LogService instance found!");
//        
        networkDriver=new KnxNetworkDriverImp(context,logTracker);

        // Register Gogo shell command
        Hashtable props = new Hashtable();
        props.put("osgi.command.scope", "uaal");
        props.put("osgi.command.function", new String[] {"knxcommand"});
        context.registerService(
            KnxShellCommand.class.getName(), new KnxShellCommand(networkDriver), props);
}


	public void stop(BundleContext context) throws Exception {
		networkDriver.unRegister();
	}

}

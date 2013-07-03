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

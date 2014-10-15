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

package org.universAAL.lddi.knx.refinementdriver.dpt1.activityhub.util;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class LogTracker extends ServiceTracker implements LogService {

	@Override
	public Object addingService(ServiceReference reference) {
		Object ret = super.addingService(reference);
//		System.out.println("stdout: [KNX.DPT1REFINEMENTDRIVER.ISO11073] LogService added!");
		log(LOG_DEBUG,"LogService added!");
		return ret;
	}

	@Override
	public void modifiedService(ServiceReference reference, Object service) {
		super.modifiedService(reference, service);
//		System.out.println("stdout: [KNX.DEVICEMANAGER] LogService modified!");
		log(LOG_DEBUG,"LogService modified!");
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
//		System.out.println("stdout: [KNX.DEVICEMANAGER] LogService removed!");
		log(LOG_DEBUG,"LogService removed!");
	}

	public LogTracker(BundleContext context) {
		super(context, LogService.class.getName(), null);
	}

	public void log(int level, String message) {
		log(null, level, message, null);
	}

	public void log(int level, String message, Throwable exception) {
		log(null, level, message, exception);
	}

	public void log(ServiceReference sr, int level, String message) {
		log(sr, level, message, null);
	}

	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		LogService log = (LogService) getService(); //obtain optional, unary LogService
		if (log != null) {
			log.log(sr, level, message, exception);
		}
	}

}

package org.universAAL.lddi.exporter.activityhub.test;
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

//package org.universAAL.lddi.exporter.activityhub.test;
//
//import org.osgi.framework.Constants;
//import org.osgi.framework.ServiceReference;
//import org.osgi.service.device.Driver;
//import org.universAAL.itests.IntegrationTest;
//import org.universAAL.lddi.exporter.activityhub.Activator;
//import org.universAAL.middleware.container.utils.LogUtils;
//
///**
// * 
// * @author Thomas Fuxreiter (foex@gmx.at)
// * 
// */
//public class HwExporterActivityHubIT extends IntegrationTest {
//
//    public HwExporterActivityHubIT() {
//    	setBundleConfLocation("conf");
//    }
//
//    public void testComposite() {
//    	logAllBundles();
//    }
//    
////	public void testActivityHubClient() throws Exception {
////		
////		/** Test1: Are there any ISO drivers started; should be 4 at this time **/
////		ServiceReference[] sr = Activator.context.getServiceReferences(Driver.class.getName(), null);
////		int c = 0;
////		for(ServiceReference r : sr) {
////			String driverName = (String) r.getProperty(org.osgi.service.device.Constants.DRIVER_ID);
////			if ( driverName.contains("org.universAAL.iso11073.") ) c++;
////		}
////		logInfo("Count of ISO Drivers: %d", c);
////		Assert.isTrue(c == 4);
////
////		
////		// cannot test sending context events; cp.publish returns nothing!
////	}
//
//
//    /**
//     * Helper method for logging.
//     * 
//     * @param msg
//     */
//    protected void logInfo(String format, Object... args) {
//	StackTraceElement callingMethod = Thread.currentThread()
//		.getStackTrace()[2];
//	LogUtils.logInfo(Activator.mc, getClass(), callingMethod
//		.getMethodName(), new Object[] { formatMsg(format, args) },
//		null);
//    }
//
//    /**
//     * Helper method for logging.
//     * 
//     * @param msg
//     */
//    protected void logError(Throwable t, String format, Object... args) {
//	StackTraceElement callingMethod = Thread.currentThread()
//		.getStackTrace()[2];
//	LogUtils.logError(Activator.mc, getClass(), callingMethod
//		.getMethodName(), new Object[] { formatMsg(format, args) }, t);
//    }    
//   
//    /**
//     * Verifies that runtime platform has correctly started. It prints basic
//     * information about framework (vendor, version) and lists installed
//     * bundles.
//     * 
//     * @throws Exception
//     */
////    public void testOsgiPlatformStarts() throws Exception {
////	logInfo("FRAMEWORK_VENDOR %s", bundleContext
////		.getProperty(Constants.FRAMEWORK_VENDOR));
////	logInfo("FRAMEWORK_VERSION %s", bundleContext
////		.getProperty(Constants.FRAMEWORK_VERSION));
////	logInfo("FRAMEWORK_EXECUTIONENVIRONMENT %s", bundleContext
////		.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
////
////	logInfo("!!!!!!! Listing bundles in integration test !!!!!!!");
////	for (int i = 0; i < bundleContext.getBundles().length; i++) {
////	    logInfo("name: " + bundleContext.getBundles()[i].getSymbolicName());
////	}
////	logInfo("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
////    }
//
//}

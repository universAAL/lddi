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

//package org.universAAL.lddi.knx.networkdriver.test;
//
//// http://team.ops4j.org/wiki/display/paxexam/Getting+Started+with+OSGi+Tests
//
////import static org.junit.Assert.*;
//
////import javax.inject.Inject;
//
////import org.junit.Test;
//import org.osgi.framework.Constants;
////import org.junit.runner.RunWith;
////import org.ops4j.pax.exam.Configuration;
////import org.ops4j.pax.exam.Option;
////import org.ops4j.pax.exam.junit.JUnit4TestRunner;
//////import org.ops4j.pax.exam.options.FrameworkStartLevelOption;
////import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
////import org.ops4j.pax.exam.spi.reactors.PerSuite;
////import org.osgi.framework.BundleContext;
//////import org.universAAL.lddi.knx.networkdriver.test.util.LogTracker;
////
////import static org.ops4j.pax.exam.CoreOptions.*;
//import org.universAAL.itests.IntegrationTest;
//import org.universAAL.lddi.knx.networkdriver.Activator;
//import org.universAAL.lddi.knx.utils.KnxCommand;
//import org.universAAL.middleware.container.ModuleContext;
//import org.universAAL.middleware.container.osgi.uAALBundleContainer;
//import org.universAAL.middleware.container.utils.LogUtils;
//
///**
// * Tests are by default disabled in the main middleware pom file (mw.pom).
// * To enable them an argument "-DskipTests=false" has to be added to the "mvn" invocation in the command line.
// * 
// * @author Thomas Fuxreiter (foex@gmx.at)
// */
////@RunWith(JUnit4TestRunner.class)
////@ExamReactorStrategy(PerSuite.class)
//public class KnxNetworkdriverFunctionalTest extends IntegrationTest{
//
//    public static ModuleContext mc = null;
//
//	public KnxNetworkdriverFunctionalTest() {
//    	setBundleConfLocation("conf");
//    	setLogLevel(2);
//
//    	// set ModuleContext for logging
//		mc = uAALBundleContainer.THE_CONTAINER
//				.registerModule(new Object[] { Activator.context });
//	}
//
//    /**
//     * Helper method for logging.
//     * 
//     * @param msg
//     */
//	protected void logInfo(String format, Object... args) {
//		StackTraceElement callingMethod = Thread.currentThread()
//				.getStackTrace()[2];
//		LogUtils.logInfo(mc, getClass(), callingMethod
//				.getMethodName(), new Object[] { formatMsg(format, args) },
//				null);
//	}
//    
//    /**
//     * Helper method for logging.
//     * 
//     * @param msg
//     */
//	protected void logError(Throwable t, String format, Object... args) {
//		StackTraceElement callingMethod = Thread.currentThread()
//				.getStackTrace()[2];
//		LogUtils.logError(mc, getClass(), callingMethod
//				.getMethodName(), new Object[] { formatMsg(format, args) }, t);
//	}
//
//	/**
//	 * Helper method: waits until KNX Communication is set up (this is needed
//	 * because the client starts in a separate thread) and OSGi service is
//	 * registered.
//	 */
//	protected void waitForClient() {
//		for (int i = 0; i < 20; i++) {
//			if (Activator.networkDriver.regServiceKnx != null)
//				break;
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void testComposite() {
//		logAllBundles();
//	}
//
//    /**
//     * Verifies that runtime platform has correctly started. It prints basic
//     * information about framework (vendor, version) and lists installed
//     * bundles.
//     * 
//     * @throws Exception
//     */
//	public void testOsgiPlatformStarts() throws Exception {
//		logInfo("FRAMEWORK_VENDOR %s", bundleContext
//				.getProperty(Constants.FRAMEWORK_VENDOR));
//		logInfo("FRAMEWORK_VERSION %s", bundleContext
//				.getProperty(Constants.FRAMEWORK_VERSION));
//		logInfo("FRAMEWORK_EXECUTIONENVIRONMENT %s", bundleContext
//				.getProperty(Constants.FRAMEWORK_EXECUTIONENVIRONMENT));
//
//		logInfo("!!!!!!! Listing bundles in integration test !!!!!!!");
//		for (int i = 0; i < bundleContext.getBundles().length; i++) {
//			logInfo("name: " + bundleContext.getBundles()[i].getSymbolicName());
//		}
//		logInfo("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//	}
//
////	@Test
//	public void testKNXCommunication() {
////		This is done automatically during bundle start
////		KnxNetworkDriverImp netDrv = new KnxNetworkDriverImp(Activator.context, null); 
//		
//		Activator.networkDriver.sendCommand("0/0/2", true, KnxCommand.VALUE_WRITE);		
//		wait1s();
//		Activator.networkDriver.requestState("0/0/2");
//		wait1s();
//		Activator.networkDriver.sendCommand("0/0/2", false, KnxCommand.VALUE_WRITE);
//		wait1s();
//		Activator.networkDriver.requestState("0/0/2");
//		wait1s();
//
//		//requests to knx device address directly gives no response!!
//		//Activator.networkDriver.requestState("1.1.5");
//	}
//	
//	void wait1s() {
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//	
////	private LogTracker logTracker;
//	
////	@Inject
////    private BundleContext bc;
////	
////	@Configuration
////	public Option[] config() {
////
////		return options(
////				workingDirectory("wd"),
//////				new FrameworkStartLevelOption(5),
//////				frameworks( felix().version("2.0.1") ),
////				systemProperty("bundles.configuration.location").value("../conf/services"),
////				//mavenBundle("com.example.myproject", "myproject-api", "1.0.0-SNAPSHOT"),
////				//bundle("http://www.example.com/repository/foo-1.2.3.jar"),
////				junitBundles()); // provisions JUnit and its dependencies as OSGi bundles to the test container.
////	}
//
//}

package it.polito.elite.domotics.dog2.knxnetworkdriver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.knx.networkdriver.util.LogTracker;

/***
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Activator implements BundleActivator {

	KnxNetworkDriverImp networkDriver;
	LogService logger;
	private LogTracker logTracker;
	
	public void start(BundleContext context) throws Exception {
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
	}


	public void stop(BundleContext context) throws Exception {
		networkDriver.unRegister();
	}

}

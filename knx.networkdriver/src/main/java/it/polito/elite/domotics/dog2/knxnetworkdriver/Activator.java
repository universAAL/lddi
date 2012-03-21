package it.polito.elite.domotics.dog2.knxnetworkdriver;

import org.universAAL.knx.networkdriver.util.LogTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

/***
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Activator implements BundleActivator {

	KnxNetworkDriverImp driver;
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
        driver=new KnxNetworkDriverImp(context,logTracker);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		//TODO stop thread KnxCommunication
//		driver.network.stop();
//		driver.network.interrupt();

		driver.unRegister();
	}

}

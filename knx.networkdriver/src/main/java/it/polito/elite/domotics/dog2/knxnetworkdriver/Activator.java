package it.polito.elite.domotics.dog2.knxnetworkdriver;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

public class Activator implements BundleActivator {

	KnxNetworkDriverImp driver;
	LogService logger;
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
        ServiceReference ref = context.getServiceReference(LogService.class.getName());
        if (ref != null)
        {
            logger = (LogService) context.getService(ref);
//            System.out.println("******** LogService found! *********");
            logger.log(LogService.LOG_INFO, "KNX network driver started!");

        }		
        else
        	System.out.println("[KNX Network Driver] WARNING: No LogService instance found!");
        
        driver=new KnxNetworkDriverImp(context,logger);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		driver.unRegister();
	}

}

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

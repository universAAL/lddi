package org.universAAL.lddi.abstraction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.managers.api.ConfigurationManager;
 
public class Activator implements BundleActivator {
    public static BundleContext osgiContext = null;
    public static ModuleContext context = null;
    static Object[] edConverterParams = new Object[] { ExternalDataConverter.class.getName() };
    static Object[] cgwSharingParams = new Object[] { CommunicationGateway.class.getName() };
    public static ConfigurationManager confMgr;
    
    public void start(BundleContext bcontext) throws Exception {
        Activator.osgiContext = bcontext;
        Activator.context = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });

        confMgr = (ConfigurationManager) context.getContainer().fetchSharedObject(context,
                new Object[] { ConfigurationManager.class.getName() });
    }
 
    public void stop(BundleContext arg0) throws Exception {
    	// TODO
    }
}

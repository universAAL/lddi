package org.universAAL.lddi.abstraction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
 
public class Activator implements BundleActivator {
    public static BundleContext osgiContext = null;
    public static ModuleContext context = null;
    static Object[] edConverterParams = new Object[] { ExternalDataConverter.class.getName() };
    
    public void start(BundleContext bcontext) throws Exception {
        Activator.osgiContext = bcontext;
        Activator.context = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });
    }
 
    public void stop(BundleContext arg0) throws Exception {
    	// TODO
    }
}

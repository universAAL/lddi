package org.universAAL.lddi.abstraction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.managers.api.ConfigurationEditor;
import org.universAAL.middleware.managers.api.ConfigurationManager;
 
public class Activator implements BundleActivator {
    static ModuleContext context = null;
    public static final Object[] edConverterParams = new Object[] { ExternalDataConverter.class.getName() };
    public static final Object[] cgwSharingParams = new Object[] { CommunicationGateway.class.getName() };
    static ConfigurationManager confMgr;
    static ConfigurationEditor confEditor;
	
	private static BundleContext osgiBC = null;
    
    public void start(BundleContext bcontext) throws Exception {
    	Activator.osgiBC = bcontext;
        Activator.context = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });

        confMgr = (ConfigurationManager) context.getContainer().fetchSharedObject(context,
                new Object[] { ConfigurationManager.class.getName() });
        confEditor = (ConfigurationEditor) context.getContainer().fetchSharedObject(context,
        		new Object[] { ConfigurationEditor.class.getName() });
    }
 
    public void stop(BundleContext arg0) throws Exception {
    	// TODO
    }
	
	public static ModuleContext getMC() {
		return context;
	}
	
	public static ConfigurationEditor getConfigEditor() {
		return confEditor;
	}
	
	public static ConfigurationManager getConfigManager() {
		return confMgr;
	}
	
	static Object getSharedObjectRemoveHook(Object o) {
		try {
			ServiceReference[] srs = osgiBC.getServiceReferences(o.getClass().getName(), null);
			for (ServiceReference sr : srs)
				if (osgiBC.getService(sr) == o)
					return sr;
		} catch (Exception e) {}
		return new Object();
	}
}

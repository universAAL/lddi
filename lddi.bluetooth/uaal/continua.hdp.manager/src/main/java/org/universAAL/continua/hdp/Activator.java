/**
 * Activator for the Continua HDP manager service.
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.continua.hdp;

// Imports
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;

// Main class
public class Activator implements BundleActivator {

	// Attributes
	
	private ContinuaHdpManagerProvider provider = null;
	public static ModuleContext mc;	

	// Methods
	
	/** Start method */
	public void start(final BundleContext context) throws Exception {
		// Config
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] {context});		
		provider = new ContinuaHdpManagerProvider(context);
		// Log
		LogUtils.logInfo(mc,getClass(),"start",new String[] {"Start Continua HDP manager"},null);
	}

	/** Stop method */
	public void stop(BundleContext arg0) throws Exception {
		// Log
		LogUtils.logInfo(mc,getClass(),"stop",new String[] {"Stop Continua HDP manager"},null);
		// Closing references
		if(provider != null) {
			provider.close();
			provider = null;
		}				
	}
}
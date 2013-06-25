/**
 * Activator for the Continua HDP manager subscriber.
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.continua.subscriber.test.main;

// Imports
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.continua.subscriber.test.gui.Subscriber;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

// Main class
public class Activator implements BundleActivator {

	// Attributes
	/** Bundle context object */
	private BundleContext ctx = null;
	private ModuleContext mc = null;
	
	/** Subscriber object */
	private Subscriber sbc = null;
	
	// Methods
	/** Start */
	public void start(BundleContext context) throws Exception {	
		ctx = context;
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] {context});
		if(mc != null)
			sbc = new Subscriber(mc,ctx);			
		else
			ctx.getBundle().stop();			
	}

	/** Stop */
	public void stop(BundleContext arg0) throws Exception {
		System.out.println("Stop uAAL Continua Manager service subscriber test");		
		if(mc.canBeStopped(mc)) {
			System.out.println("Module context can be stopped!");
			mc.stop(mc);
			mc = null;
		} else {
			System.out.println("Module context can NOT be stopped!");
		}
		sbc = null;
		ctx = null;		
	}	
}
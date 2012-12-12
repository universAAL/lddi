/**
 * Activator for the Continua HDP manager caller.
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.continua.caller.test.main;

// Imports
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.universAAL.continua.caller.test.gui.GUI;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;

// Main class
public class Activator implements BundleActivator {

	// Attributes    
	/** Main GUI object */
	private GUI gui = null;	
	
	/** Bundle context object */
	private BundleContext ctx = null;
	private ModuleContext mc = null;
	
	// Methods
	/** Start */
	public void start(BundleContext context) throws Exception {	
		ctx = context;
		mc = uAALBundleContainer.THE_CONTAINER.registerModule(new Object[] {context});
		// Create and show main GUI frame
		gui = new GUI(ctx,mc);								
		gui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
		gui.addWindowListener(new WindowAdapter() {				
			public void windowClosing(WindowEvent e) {				
				try {
					ctx.getBundle().stop();
				} catch (BundleException e1) {					
					e1.printStackTrace();
				}				
			}
		});
		gui.setVisible(true);
	}

	/** Stop */
	public void stop(BundleContext arg0) throws Exception {
		System.out.println("Stop uAAL Continua Manager service caller test");	
		gui.stopGUI();
		if(mc.canBeStopped(mc)) {
			System.out.println("Module context can be stopped!");
			mc.stop(mc);
			mc = null;
		} else {
			System.out.println("Module context can NOT be stopped!");
		}
		ctx = null;		
	}	
}
/**
 * x073 Continua agent publisher (agent events will be published over uAAL bus)
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package
package org.universAAL.continua.manager.publisher;

// Imports
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.universAAL.continua.manager.gui.GUI;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;



// Main class
public class Activator implements BundleActivator {
	
//	/Continua Health Manager|http://www.tsbtecnologias.es|http://ontologies.universAAL.com/CONTINUAHEALTHMANAGERUI.owl#ContinuaManager

	// Attributes    
	/** Main GUI object */
	private GUI gui = null;	
	
//	/** Bundle context object */
//	private BundleContext ctx = null;	
	
	private ModuleContext mdlContext;
	private BundleContext bndContext;
	private ServiceProvider service;
	public static boolean dllReadyLatch = true;
	
	// Methods
	/** Start */
	public void start(BundleContext context) throws Exception {	
		mdlContext = uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context });				
		// Create and show main GUI frame
		bndContext = context;		
		gui = new GUI(bndContext);								
		gui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
		gui.addWindowListener(new WindowAdapter() {				
			public void windowClosing(WindowEvent e) {			
				gui.setVisible(false);
			}
		});
		//TODO cambiar para la demo true -> false
		gui.setVisible(false);		
		// Service callee
		service = new ServiceProvider(mdlContext,gui);		
	}

	/** Stop */
	public void stop(BundleContext arg0) throws Exception {		
		gui.setVisible(false);
		dllReadyLatch = true;
		//gui.stopGUI();		
//		bndContext = null;
//		mdlContext = null;
	}	
	
	void stopSafe(final Bundle bundle) {		
		new Thread() {
			public void run() {
				try {
					bundle.stop();
				} catch (BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
}
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
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.universAAL.continua.manager.gui.GUI;;

// Main class
public class Activator implements BundleActivator {

	// Attributes    
	/** Main GUI object */
	private GUI gui = null;	
	
	/** Bundle context object */
	private BundleContext ctx = null;	
	
	// Methods
	/** Start */
	public void start(BundleContext context) throws Exception {	
		ctx = context;	
		// Create and show main GUI frame
		gui = new GUI(ctx);								
		gui.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
		gui.addWindowListener(new WindowAdapter() {				
			public void windowClosing(WindowEvent e) {
				gui.stopGUI();
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
		gui.stopGUI();	
		ctx = null;
	}	
}
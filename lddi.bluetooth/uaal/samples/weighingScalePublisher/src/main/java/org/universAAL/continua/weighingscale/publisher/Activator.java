package org.universAAL.continua.weighingscale.publisher;
/**
 * x073 Continua agent publisher (agent events will be published over uAAL bus)
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */


// Imports
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

// Main class
public class Activator implements BundleActivator {

	// Attributes

	// Publisher object to send events
	private Publisher uaalPublisher = null;	

	// HDP manager object
	private hdpManager manager = null;	
	
	//
	private BundleContext ctx = null;    

	// Methods
	/** Start */
	public void start(BundleContext context) throws Exception {		
		ctx = context;
		new Thread(){
			public void run(){   
				uaalPublisher = new Publisher(ctx);
				// Start manager and wait for agent events
				manager = new hdpManager(uaalPublisher);
				manager.init();					
			}			
		}.start();		
	}

	/** Stop */
	public void stop(BundleContext arg0) throws Exception {		
		manager.exit();		
		ctx = null;
		uaalPublisher = null;
		manager = null;	
	}
}
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
import org.universAAL.continua.manager.gui.GUI;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owl.InitialServiceDialog;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;

// Main class
public class ServiceProvider extends ServiceCallee {	

	private static final String NAMESPACE = "http://ontologies.universAAL.com/CONTINUAHEALTHMANAGERUI.owl#";
	private static final String MY_URI = NAMESPACE + "ContinuaHealthManager";
	private static final String PROF_START_UI = NAMESPACE + "ContinuaHealthManager_UI";
	
	ModuleContext ctx;	
	private GUI gui = null;
	
	/** Constructor */

	public ServiceProvider(ModuleContext mc,GUI g) {		
		this(mc,getProfiles());
		gui = g;
	}
	
	public ServiceProvider(ModuleContext context,ServiceProfile[] realizedServices) {
		super(context,realizedServices);
		this.ctx = context;		
	}

	private static ServiceProfile[] getProfiles() {
		// Profile to start the application from the UI main menu		
		ServiceProfile prof = InitialServiceDialog.createInitialDialogProfile(MY_URI,
									"http://www.tsbtecnologias.es","Continua Health Manager",PROF_START_UI);		
		return new ServiceProfile[] { prof };
	}

//	public ServiceResponse handleCall(ServiceCall call) {
//		if(call != null) {
//			String operation = call.getProcessURI();
//			if(operation != null && operation.startsWith(PROF_START_UI)) {
//				Object inputUser = call.getProperty(ServiceRequest.PROP_uAAL_INVOLVED_HUMAN_USER);
//				// Create and show main GUI frame				
//				gui.setVisible(true);				
//				return new ServiceResponse(CallStatus.succeeded);
//			}
//		}
//		return null;		
//	}
	
	public ServiceResponse handleCall(ServiceCall call) {
//		  System.out.println("linea 1");
		    if(call != null) {
//		  System.out.println("linea 2");
		   String operation = call.getProcessURI();
//		   System.out.println("linea 3");
		   if(operation != null && operation.startsWith(PROF_START_UI)) {
//		   System.out.println("linea 4");
		    Object inputUser = call.getProperty(ServiceRequest.PROP_uAAL_INVOLVED_HUMAN_USER);
//		    System.out.println("linea 5");
		    // Create and show main GUI frame    
		    gui.setVisible(true);  
//		System.out.println("linea 6");    
		    return new ServiceResponse(CallStatus.succeeded);
		   }
//		   System.out.println("linea 7");
		  }
//		  System.out.println("linea 8");
		  return null;  
		 }

	/**	 */
	@Override
	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}

}

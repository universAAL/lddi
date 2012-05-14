package org.universAAL.hw.exporter.activityhub;

import org.osgi.service.log.LogService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.samples.lighting.server.ContextEventPattern;
import org.universAAL.samples.lighting.server.unit_impl.MyLighting;

public class ActivityHubContextProvider {

    private ContextPublisher cp;
    private ActivityHubBusServer theServer;
	private LogService logger;

	public ActivityHubContextProvider(ModuleContext mc, ActivityHubBusServer busServer) {
		//ModuleContext needed here ??
		this.theServer = busServer;
		this.logger = busServer.getLogger();
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				ActivityHubServiceOntology.ACTIVITYHUB_SERVER_NAMESPACE
				+ "ActivityHubContextProvider");
		info.setType(ContextProviderType.controller);
		info.setProvidedEvents(providedEvents());
		cp = new DefaultContextPublisher(context, info);
		
		theServer.addListener(this);
		
		this.logger.log(LogService.LOG_INFO, "Activated ActivityHub ContextEvent Patterns");
	}

    private static ContextEventPattern[] providedEvents() {
    	//look at sample server...
    	
    }
}

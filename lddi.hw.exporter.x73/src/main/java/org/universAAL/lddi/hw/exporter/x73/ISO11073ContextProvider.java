package org.universAAL.lddi.hw.exporter.x73;

import org.osgi.service.log.LogService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextPublisher;

/**
 * Provides context event patterns for the uAAL context bus
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ISO11073ContextProvider {

    private ContextPublisher cp;
	private ISO11073DBusServer theServer;
	private LogService logger;

	public ISO11073ContextProvider(ModuleContext mc,
			ISO11073DBusServer x73Server) {
		this.theServer = x73Server;
		this.logger = x73Server.getLogger();
		
		
	}

}

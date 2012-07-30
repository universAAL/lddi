package org.universAAL.lddi.hw.exporter.x73;

import java.io.IOException;

import org.freedesktop.dbus.exceptions.DBusException;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class ISO11073DBusServer {

	private BundleContext context;
	private LogService logger;
	
	public ISO11073DBusServer(BundleContext context, LogService logger) throws IOException, DBusException {
		this.context = context;
		this.logger = logger;		
		
		init();
	}

	private void init() throws IOException, DBusException {
		
		// copy main content from dbus-test here!
		
		
	}
	
	
	public LogService getLogger() {
		return this.logger;
	}

}

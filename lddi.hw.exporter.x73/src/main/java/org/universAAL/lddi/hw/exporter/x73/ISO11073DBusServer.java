package org.universAAL.lddi.hw.exporter.x73;

import java.io.IOException;

import org.freedesktop.dbus.exceptions.DBusException;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import com.signove.health.*;

public class ISO11073DBusServer {

	private BundleContext context;
	private LogService logger;
	
	public ISO11073DBusServer(BundleContext context, LogService logger) throws IOException, DBusException {
		this.context = context;
		this.logger = logger;		

		init();
	}

	private void init() throws IOException, DBusException {
		logger.log(LogService.LOG_INFO, "ISO11073DBusServer init!");
		
		int data_types[] = {0x1004, 0x1007, 0x1029, 0x100f};

		System.out.println("Creating D-Bus connection");
		//logger .severe("Info Log");
		DBusConnection conn = null;
		try {
			conn = DBusConnection.getConnection(DBusConnection.SYSTEM);
			System.out.println("DBusConnection conn: " +conn);
		} catch (DBusException DBe) {
			System.out.println("Impossible doing D-Bus connection");
		}

		// Manager object
		manager remoteObject;
                remoteObject = (manager) conn.getRemoteObject("com.signove.health","/com/signove/health", manager.class);

		boolean condition = false;

		//Agent object
		String agent_pid = "/com/signove/health/agent/" + ((int) (1 + Math.random() * 2000000000));
		System.out.println("agent_pid: " +agent_pid);
		agent agt = new MyAgent(conn);
		conn.exportObject(agent_pid, agt);

		System.out.println("Configuring...");

		remoteObject.ConfigurePassive(agt, data_types);

		System.out.println("Waiting...");		
		
	}
	
	
	public LogService getLogger() {
		return this.logger;
	}

}

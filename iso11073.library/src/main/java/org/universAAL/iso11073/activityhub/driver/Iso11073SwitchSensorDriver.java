package org.universAAL.iso11073.activityhub.driver;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.universAAL.iso11073.activityhub.devicecategory.Iso11073SwitchSensor;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * This Driver class manages driver instances for SwitchSensor devices.
 * 
 * when an attached device service is unregistered:
 * drivers must take the appropriate action to release this device service
 * and peform any necessary cleanup, as described in their device category spec
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class Iso11073SwitchSensorDriver implements Driver {
	
	private BundleContext context;
	private LogService logger;
	private ServiceTracker tracker;
	private ActivityHubDriverClient client;

	private static final String MY_DRIVER_ID = "org.universAAL.iso11073.switchsensor.0.0.1";

	private ServiceRegistration regDriver;
	
	// Set of driver instances 
	private Set<Iso11073SwitchSensorInstance> connectedDriver;


	/**
	 * @param context
	 * @param logTracker
	 */
	public Iso11073SwitchSensorDriver(ActivityHubDriverClient client,
			BundleContext context) {
		this.client=client;
		this.context=context;
		this.logger=client.getLogger();
		
		this.connectedDriver = new HashSet<Iso11073SwitchSensorInstance>();
		
		this.registerDriver();
	}

	
	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#match(org.osgi.framework.ServiceReference)
	 */
	public int match(ServiceReference reference) throws Exception {
		// reference = device service
		int matchValue=Device.MATCH_NONE;
		String deviceCategory=(String)reference.getProperty(Constants.DEVICE_CATEGORY);
		// more possible properties to match: description, serial, id

		if ( deviceCategory.equals(Iso11073SwitchSensor.MY_DEVICE_CATEGORY) ) {
			matchValue = Iso11073SwitchSensor.MATCH_CLASS;
		}
		return matchValue; //must be > 0 to match
	}


	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#attach(org.osgi.framework.ServiceReference)
	 */
	public String attach(ServiceReference reference) throws Exception {

		// create "driving" instance
		Iso11073SwitchSensorInstance instance = new Iso11073SwitchSensorInstance(
				this.context, reference, client, this.logger);
		
		// init service tracker on device service for instance
		tracker = new ServiceTracker(this.context, reference, instance);
		tracker.open();
		
		synchronized(this.connectedDriver){
			if ( ! this.connectedDriver.add(instance) )
				this.logger.log(LogService.LOG_ERROR, "Duplicate Element in HashSet connectedDriver");
		}

		return null; // if attachment is correct
	}
	
	
	/**
	 * register this driver in OSGi registry
	 */
	private void registerDriver() {
		Properties propDriver=new Properties();
		propDriver.put(Constants.DRIVER_ID, MY_DRIVER_ID );
		this.regDriver=this.context.registerService(Driver.class.getName(), this, 
				propDriver);
		
		if ( this.regDriver != null )
			this.logger.log(LogService.LOG_INFO, "Iso11073SwitchSensorDriver registered!");
	}

	
	/**
	 * @return the connectedDriver
	 */
	public Set<Iso11073SwitchSensorInstance> getConnectedDriver() {
		return connectedDriver;
	}

}

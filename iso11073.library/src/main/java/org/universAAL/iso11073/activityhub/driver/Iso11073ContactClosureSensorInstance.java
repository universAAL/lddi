package org.universAAL.iso11073.activityhub.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.iso11073.activityhub.devicecategory.Iso11073ContactClosureSensor;
import org.universAAL.iso11073.activityhub.devicemodel.ContactClosureSensor;
import org.universAAL.iso11073.activityhub.devicemodel.ContactClosureSensorEvent;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * Working instance of the ActivityHub ContactClosure driver.
 * Tracks on the ContactClosureSensor device service passed in the attach method 
 * in Iso11073ContactClosureSensorDriver class.
 * This instance is passed to the consuming client (e.g. uAAL exporter bundle).
 * When the ContactClosureSensor device service disappears, this driver is removed
 * from the consuming client and from the device.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class Iso11073ContactClosureSensorInstance extends ActivityHubDriver 
	implements Iso11073ContactClosureSensor ,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

//	private ContactClosureSensorEvent lastSensorEvent;
	
	/**
	 * @param c OSGi BundleContext
	 * @param sr Service reference of ISO device
	 * @param client Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public Iso11073ContactClosureSensorInstance(BundleContext c,  
			ActivityHubDriverClient client, LogService log) {
		super(client);

		this.context=c;
		this.logger=log;
	}

	
	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver#getLastSensorEvent()
	 */
	@Override
	public int getLastSensorEvent() {
		return this.device.getSensorEventValue();
	}


	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	/**
	 * @param ActivityHubSensor service
	 */
	public Object addingService(ServiceReference reference) {
		
		ContactClosureSensor ccs = (ContactClosureSensor) this.context.getService(reference);
		
		// register driver in client driverList
		// MAIN FUNCTION HERE !!!
		this.setDevice(ccs);

		//return null; JavaDoc: @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
		return ccs;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked ActivityHub device service was modified. " +
				"Going to update the Iso11073ContactClosureSensorInstance");
		removedService(reference, service);
		addingService(reference);		
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		// removed device service
		this.context.ungetService(reference);
		this.detachDriver();
		this.removeDriver();
	}

	/**
	 * forward event to client
	 */
	public void incomingSensorEvent(int event) {

		// TODO send event to client
		this.logger.log(LogService.LOG_INFO, "Driver " + Iso11073ContactClosureSensor.MY_DEVICE_CATEGORY +
				" for device " + this.device.getDeviceId() + " received new event " + 
				ContactClosureSensorEvent.getContactClosureSensorEvent(event).toString());

		try {
			this.client.incomingSensorEvent(event);
		} catch (AssertionError ae) {
			this.logger.log(LogService.LOG_ERROR, "No suitable ContactClosureSensorEvent found " +
					"for value: " +	event);
			ae.printStackTrace();
		}
	}

}

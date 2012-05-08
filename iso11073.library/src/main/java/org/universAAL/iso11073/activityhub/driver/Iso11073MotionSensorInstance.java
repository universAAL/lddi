package org.universAAL.iso11073.activityhub.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.iso11073.activityhub.devicecategory.Iso11073MotionSensor;
import org.universAAL.iso11073.activityhub.devicemodel.MotionSensor;
import org.universAAL.iso11073.activityhub.devicemodel.MotionSensorEvent;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * Working instance of the ActivityHub MotionSensor driver.
 * Tracks on the MotionSensor device service passed in the attach method 
 * in Iso11073MotionSensorDriver class.
 * This instance is passed to the consuming client (e.g. uAAL exporter bundle).
 * When the MotionSensor device service disappears, this driver is removed
 * from the consuming client and from the device.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class Iso11073MotionSensorInstance extends ActivityHubDriver 
	implements Iso11073MotionSensor ,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	/**
	 * @param c OSGi BundleContext
	 * @param sr Service reference of ISO device
	 * @param client Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public Iso11073MotionSensorInstance(BundleContext c,
			ActivityHubDriverClient client, LogService log) {
		super(client);

		this.context=c;
		this.logger=log;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		
		MotionSensor ms = (MotionSensor) this.context.getService(reference);
		
		// register driver in client driverList
		// MAIN FUNCTION HERE !!!
		this.setDevice(ms);

		//return null; JavaDoc: @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
		return ms;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked ActivityHub device service was modified. " +
				"Going to update the Iso11073MotionSensorInstance");
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
		this.logger.log(LogService.LOG_INFO, "Driver " + Iso11073MotionSensor.MY_DEVICE_CATEGORY +
				" for device " + this.device.getDeviceId() + " received new event " + 
				MotionSensorEvent.getMotionSensorEvent(event).toString());

		try {
			this.client.incomingSensorEvent(event);
		} catch (AssertionError ae) {
			this.logger.log(LogService.LOG_ERROR, "No suitable MotionSensorEvent found " +
					"for value: " +	event);
			ae.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver#getLastSensorEvent()
	 */
	@Override
	public int getLastSensorEvent() {
		return this.device.getSensorEventValue();
	}

}

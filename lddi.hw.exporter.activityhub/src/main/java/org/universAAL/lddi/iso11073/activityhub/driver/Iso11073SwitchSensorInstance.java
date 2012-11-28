package org.universAAL.lddi.iso11073.activityhub.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.Iso11073SwitchSensor;
import org.universAAL.lddi.iso11073.activityhub.devicemodel.SwitchSensor;
import org.universAAL.lddi.iso11073.activityhub.devicemodel.SwitchSensorEvent;
import org.universAAL.lddi.iso11073.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.lddi.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * Working instance of the ActivityHub SwitchSensor driver.
 * Tracks on the SwitchSensor device service passed in the attach method 
 * in Iso11073SwitchSensorDriver class.
 * This instance is passed to the consuming client (e.g. uAAL exporter bundle).
 * When the SwitchSensor device service disappears, this driver is removed
 * from the consuming client and from the device.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class Iso11073SwitchSensorInstance extends ActivityHubDriver 
	implements Iso11073SwitchSensor ,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	/**
	 * @param c OSGi BundleContext
	 * @param sr Service reference of ISO device
	 * @param client Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public Iso11073SwitchSensorInstance(BundleContext c, 
			ActivityHubDriverClient client, LogService log) {
		super(client);

		this.context=c;
		this.logger=log;
	}


	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		
		SwitchSensor ss = (SwitchSensor) this.context.getService(reference);
		
		/** now couple my driver to the device */
		if ( this.setDevice(ss) )
			this.logger.log(LogService.LOG_INFO, "Successfully coupled " + Iso11073SwitchSensor.MY_DEVICE_CATEGORY 
					+ " driver to device " + this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + Iso11073SwitchSensor.MY_DEVICE_CATEGORY
					+ " driver to device " + this.device.getDeviceId() + ". No appropriate " +
					"ISO device created!");
			return null;
		}

		//return null; JavaDoc: @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
		return ss;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked ActivityHub device service was modified. " +
				"Going to update the Iso11073SwitchSensorInstance");
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


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver#getLastSensorEvent()
	 */
	@Override
	public int getLastSensorEvent() {
		return this.device.getSensorEventValue();
	}


	public void incomingSensorEvent(int event) {
		this.logger.log(LogService.LOG_INFO, "Driver " + Iso11073SwitchSensor.MY_DEVICE_CATEGORY +
				" for device " + this.device.getDeviceId() + " received new event " + 
				SwitchSensorEvent.getSwitchSensorEvent(event).toString());

		try {
			this.client.incomingSensorEvent(this.device.getDeviceId(), this.device.getDeviceCategory(), event);
		} catch (AssertionError ae) {
			this.logger.log(LogService.LOG_ERROR, "No suitable SwitchSensorEvent found " +
					"for value: " +	event);
			ae.printStackTrace();
		}		
	}

}

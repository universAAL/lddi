package org.universAAL.lddi.knx.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.lddi.knx.devicecategory.KnxDpt1;
import org.universAAL.lddi.knx.devicecategory.KnxDpt9;
import org.universAAL.lddi.knx.interfaces.KnxDriver;
import org.universAAL.lddi.knx.interfaces.KnxDriverClient;
import org.universAAL.lddi.knx.devicemodel.KnxDpt1Device;

/**
 * Working instance of the KnxDpt1 driver. Registers a service/device in OSGi registry.
 * Tracks on the KNX device service passed in the attach method in KnxDpt1Driver class. 
 * This instance is passed to the consuming client (e.g. uAAL exporter bundle).
 * When the KNX device service disappears, this driver is removed from the consuming 
 * client and from the device.
 *  
 * This driver handles knx 1-bit events (knx datapoint 1), which is on/off.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Instance extends KnxDriver implements KnxDpt1 ,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	/**
	 * @param c OSGi BundleContext
	 * @param sr Service reference of KNX device
	 * @param client Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public KnxDpt1Instance(BundleContext context, KnxDriverClient client,
			LogService logger) {
		super(client);
		
		this.context = context;
		this.logger = logger;
	}

	/**
	 * track on my device
	 * @param KnxDpt1 device service
	 * @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {

		KnxDpt1Device knxDev = (KnxDpt1Device) this.context.getService(reference);

		if ( knxDev == null)
			this.logger.log(LogService.LOG_ERROR, "knxDev is null for some reason!");
		
		/** now couple my driver to the device */
		if ( this.setDevice(knxDev) )
			this.logger.log(LogService.LOG_INFO, "Successfully coupled " + KnxDpt1.MY_DEVICE_CATEGORY 
					+ " driver to device " + this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + KnxDpt1.MY_DEVICE_CATEGORY
					+ " driver to device " + this.device.getDeviceId() + ". No appropriate " +
					"ISO device created!");
			return null;
		}
		
		// JavaDoc: @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
		return knxDev;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked knx device service was modified. " +
				"Going to update the KnxDpt1Instance");
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
	 * Calculate readable measurement value from given byte array according to KNX DPT 9.
	 * Call client.
	 * 
	 * @see org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory#newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		this.logger.log(LogService.LOG_INFO, "Driver " + KnxDpt1.MY_DEVICE_CATEGORY + " for device " + 
				this.device.getGroupAddress() + " with knx datapoint type " + this.device.getDatapointType() +
				" received new knx message " + Integer.toHexString(event[0]) );

		/**
		 * KNX datapoint type 1.*** is a 1-bit signal; therefore only on/off is forwarded to ISO devices!  
		 */
		if ( event[0] == DEFAULT_VALUE_OFF ) {
			this.client.incomingSensorEventDpt1( this.device.getGroupAddress(), 
					this.device.getDatapointTypeMainNumber(), this.device.getDatapointTypeSubNumber(),
					false);
		} else if ( event[0] == DEFAULT_VALUE_ON ) {
			this.client.incomingSensorEventDpt1( this.device.getGroupAddress(), 
					this.device.getDatapointTypeMainNumber(), this.device.getDatapointTypeSubNumber(),
					true);
		} else {
			this.logger.log(LogService.LOG_ERROR, "No matches on incoming Event " + Integer.toHexString(event[0]) +
					" from device " + this.device.getGroupAddress());
			return;
		}
		
	}

}

package org.universAAL.lddi.knx.driver;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.lddi.knx.devicecategory.KnxDpt1;
import org.universAAL.lddi.knx.devicecategory.KnxDpt9;
import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;
import org.universAAL.lddi.knx.devicedriver.KnxDriver;
import org.universAAL.lddi.knx.devicedriver.KnxDriverClient;
import org.universAAL.lddi.knx.devicemodel.KnxDpt9Device;
import org.universAAL.lddi.knx.utils.KnxEncoder;

/**
 * Working instance of the KnxDpt1 driver. Registers a service/device in OSGi registry.
 * Tracks on the KNX device service passed in the attach method in KnxDpt9Driver class. 
 * This instance is passed to the consuming client (e.g. uAAL exporter bundle).
 * When the KNX device service disappears, this driver is removed from the consuming 
 * client and from the device.
 *  
 * This driver handles knx float values (2 byte) i.e. for temperature (knx datapoint type 9).
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9Instance extends KnxDriver implements KnxDpt9 ,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	/**
	 * @param c OSGi BundleContext
	 * @param sr Service reference of KNX device
	 * @param client Link to consumer of this driver (e.g. uAAL exporter bundle)
	 */
	public KnxDpt9Instance(BundleContext context, KnxDriverClient client,
			LogService logger) {
		super(client);
		
		this.context = context;
		this.logger = logger;
	}

	/**
	 * track on my device
	 * @param KnxDpt9 device service
	 * @return The service object to be tracked for the ServiceReference object or null if the ServiceReference object should not be tracked.
	 */
	public Object addingService(ServiceReference reference) {

		KnxDpt9Device knxDev = (KnxDpt9Device) this.context.getService(reference);

		if ( knxDev == null)
			this.logger.log(LogService.LOG_ERROR, "knxDev is null for some reason!");
		
		/** now couple my driver to the device */
		if ( this.setDevice(knxDev) )
			this.logger.log(LogService.LOG_INFO, "Successfully coupled " + KnxDpt9.MY_DEVICE_CATEGORY 
					+ " driver to device " + this.device.getDeviceId());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + KnxDpt9.MY_DEVICE_CATEGORY
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
				"Going to update the KnxDpt9Instance");
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
	 * Calculate float value from knx message payload.
     * 				MSB			LSB
     * float value |-------- --------|
     * encoding 	MEEEEMMM MMMMMMMM
     * FloatValue = (0,01*M)*2(E)
     * E = [0 … 15]
     * M = [-2 048 … 2 047], two’s complement notation
	 */
	public float calculateFloatValue(byte[] payload) {
		// there are 3 bytes payload for a temperature event where the last 2 are important
		// the first seems always to be 80!?
		byte MSB = payload[1]; 
		byte LSB = payload[2];
		
		byte M_MSB = (byte) (MSB & 0x87);
		byte M_LSB = (byte) (LSB & 0xFF);
		
		byte E = (byte) ((MSB & 0x78) >> 3);

		int e = Integer.parseInt( Byte.toString(E) );
		
		short m = (short) (M_MSB << 8 | (M_LSB & 0xFF));
		
		float result = (float) ((0.01*m)*(Math.pow(2, e)));
		//System.out.println("*****************************float result: " + result);
		return result;
	}


	/**
	 * Calculate readable measurement value from given byte array according to KNX DPT 9.
	 * Call client.
	 * 
	 * @see org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory#newMessageFromKnxBus(byte[])
	 */
	public void newMessageFromKnxBus(byte[] event) {

		float value = calculateFloatValue(event);

		this.logger.log(LogService.LOG_INFO, "Driver " + KnxDpt9.MY_DEVICE_CATEGORY + " for device " + 
				this.device.getGroupAddress() + " with knx datapoint type " + this.device.getDatapointType() +
				" received new knx message " + value );

		this.client.incomingSensorEvent( this.device.getGroupAddress(), 
				this.device.getDatapointTypeMainNumber(), this.device.getDatapointTypeSubNumber(),
				value);
	}

}

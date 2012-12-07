package org.universAAL.lddi.knx.devicedriver;

import org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory;
import org.universAAL.lddi.knx.devicemodel.KnxDevice;

/**
 * This abstract class is designed to help developing a knx driver.
 * It stores information about the coupled device.
 * It provides an service tracker for the attached device service.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public abstract class KnxDriver {
	
	// the device I am driving
	protected KnxDevice device;

	/** upper layer instance */
	protected KnxDriverClient client; //->uAAL bus/exporter 

	public KnxDriver() {
	}
	
	public KnxDriver(KnxDriverClient client) {
		this.client = client;
	}
	
	/**
	 * store the device
	 * link this driver to the device
	 * @param the device to set
	 */
	public final boolean setDevice(KnxDevice device) {
		this.device = device;

		// add connected driver to driverList in client, if available
		if ( this.client != null )
			this.client.addDriver(this.device.getDeviceId(), this.device.getDeviceCategory(), this);
		
		return attachDriver();
	}
	
	/**
 	 * coupling this driver to device reference
 	 * method is abstract because of cast to device category IF
	 * @param id
	 */
	//protected abstract boolean attachDriver();
	
	
	/**
 	 * coupling this driver to device reference
	 * @param id
	 */
	protected boolean attachDriver() {
		if (this.device != null) {
			this.device.addDriver( (KnxBaseDeviceCategory) this);
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * decoupling this driver from device reference
	 */
	public final void detachDriver() {
		if (this.device != null)
			this.device.removeDriver();
	}
	
	/**
	 * Remove this driver from the driver list in knx network driver
	 * 
	 * @param device the device to remove
	 */
	public final void removeDriver() {
		// remove driver from driverList in my consuming client
		this.client.removeDriver(this.device.getDeviceId(), this);
	}
	
    /**
     * This method remove, if present, the "0x" prefix of the hexValue variable
     * @param hexValue string containing an hex value
     * @return the same string without prefix
     */
	public static String clearHexValue(String hexValue) {
		String correctHexValue;
		if(hexValue.startsWith("0x")){
			correctHexValue=hexValue.substring(2);
		}else
		{
			correctHexValue=hexValue;
		}
		return correctHexValue;
	}

}

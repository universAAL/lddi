/**
 * 
 */
package org.universAAL.knx.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.knx.utils.*;

/**
 * One KNX device represents one groupAddress (with further properties) from ETS4 XML export.
 * This device is registered in OSGi framework.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public abstract class KnxDevice implements Device{

	/** OSGi DAS properties */

	public String deviceCategory;
	/** intended for end users */
	public String deviceDescription;
	/** unique serial number for this device */
	private String deviceSerial;
	/** should be set; every time the same hardware is plugged in, the same PIDs are used */
	private String servicePid;

	private String deviceId = "-";
	private KnxGroupAddress knxDeviceProperties;
	
	private static String KNX_DEVICE_CATEGORY_PREFIX = "KnxDpt";
	
	// ref to OSGi attached driver
	private Object driver;
	
	private LogService logger;

	/**
	 * empty constructor for factory
	 */
	public KnxDevice() {
	}
	
	/**
	 * Fill empty device with parameters and set it alive
	 * 
	 * @param knxGroupAddress
	 * @param logger2
	 */
	public void setParams(KnxGroupAddress knxGroupAddress, LogService logger) {
		this.knxDeviceProperties = knxGroupAddress;
		this.logger = logger;

		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + this.knxDeviceProperties.getMainDpt();
		
//		this.deviceDescription;
//		this.deviceSerial;
//		this.servicePid;
		
		this.deviceId = this.knxDeviceProperties.getGroupAddress();
	}

	
	public void noDriverFound() {

		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for KNX device: " +
				knxDeviceProperties.getGroupAddress() );
		
	}

	public String getGroupAddress() {
		return this.knxDeviceProperties.getGroupAddress();
	}
	
	/**
	 * @return the deviceCategory
	 */
	public String getDeviceCategory() {
		return deviceCategory;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @return the deviceDescription
	 */
	public String getDeviceDescription() {
		return deviceDescription;
	}

	/**
	 * @return the deviceSerial
	 */
	public String getDeviceSerial() {
		return deviceSerial;
	}

	/**
	 * @return the servicePid
	 */
	public String getServicePid() {
		return servicePid;
	}


}

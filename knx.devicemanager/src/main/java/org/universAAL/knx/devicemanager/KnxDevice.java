/**
 * 
 */
package org.universAAL.knx.devicemanager;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;

/**
 * One KNX device represents one groupAddress (with further properties) from ETS4 XML export.
 * This device is registered in OSGi framework.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class KnxDevice implements Device{

	private String deviceId = "-";
	private String deviceCategory;
	private KnxGroupAddress knxDeviceProperties;
	
	private static String KNX_DEVICE_CATEGORY_PREFIX = "KnxDpt";
	
	private LogService logger;

	/**
	 * @param knxDeviceProperties
	 */
	public KnxDevice(KnxGroupAddress knxDeviceProperties,LogService logger) {
		this.knxDeviceProperties = knxDeviceProperties;
		this.logger = logger;
		
		String dpt = this.knxDeviceProperties.getDpt().trim();
		String dptMainNumber = dpt.substring(0, dpt.indexOf(".") );
		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + dptMainNumber;
		
		this.deviceId = this.knxDeviceProperties.getGroupAddress();
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


	public void noDriverFound() {

		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for KNX device: " +
				knxDeviceProperties.getGroupAddress() );
		
	}
	
}

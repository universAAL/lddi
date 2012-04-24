package org.universAAL.iso11073.activityhub;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;

/**
 * Activity hub sensor base class
 * 
 * @author Thomas Fuxreiter
 *
 */
public abstract class ActivityHubSensor implements Device {

	public String deviceCategory;
	private LogService logger;
	// some kind of deviceID !?
	private String deviceId = null;

	/**
	 * empty constructor for factory
	 */
	public ActivityHubSensor() {
	}

	/**
	 * Fill empty device with parameters and set it alive
	 * 
	 * @param knxGroupAddress
	 * @param logger2
	 */
	public void setParams(String deviceCategory, String deviceId, LogService logger) {
		this.deviceCategory = deviceCategory;
		this.deviceId = deviceId;
		this.logger = logger;

//		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + this.knxDeviceProperties.getMainDpt();
		
//		this.deviceDescription;
//		this.deviceSerial;
//		this.servicePid;
		
//		this.deviceId = this.knxDeviceProperties.getGroupAddress();
	}
	

	/**
	 * @return the deviceCategory
	 */
	public String getDeviceCategory() {
		return deviceCategory;
	}

	/**
	 * @param deviceCategory the deviceCategory to set
	 */
	public void setDeviceCategory(String deviceCategory) {
		this.deviceCategory = deviceCategory;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.device.Device#noDriverFound()
	 */
	public void noDriverFound() {

		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for ActivityHub device: " +
				this.deviceCategory + " - " + this.deviceId);		
	}
	
	
}

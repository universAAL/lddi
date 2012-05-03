package org.universAAL.iso11073.activityhub.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Activity hub sensor base class
 * 
 * @author Thomas Fuxreiter
 *
 */
public  
abstract 
class ActivityHubSensor implements Device {

	private ActivityHubDeviceCategory deviceCategory;
//	protected int sensorEvent;
	private ActivityHubLocation deviceLocation;
	// some kind of deviceID !?
	private String deviceId = null;
	private int incomingSensorEvent;
	
	private LogService logger;

	
//	/**
//	 * empty constructor for factory
//	 */
//	public ActivityHubSensor() {
//	}
	
	public ActivityHubSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		this.deviceCategory = deviceCategory;
		this.deviceLocation = deviceLocation;
		this.deviceId = deviceId;
		this.logger = logger;
	}
	
	
//	/**
//	 * Fill empty device with parameters and set it alive
//	 * 
//	 * @param knxGroupAddress
//	 * @param logger2
//	 */
//	public void setParams(String deviceCategory, String deviceId, LogService logger) {
//		this.deviceCategory = deviceCategory;
//		this.deviceId = deviceId;
//		this.logger = logger;
//
////		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + this.knxDeviceProperties.getMainDpt();
//		
////		this.deviceDescription;
////		this.deviceSerial;
////		this.servicePid;
//		
////		this.deviceId = this.knxDeviceProperties.getGroupAddress();
//	}
	

	/**
	 * @return the deviceCategory
	 */
	public ActivityHubDeviceCategory getDeviceCategory() {
		return deviceCategory;
	}

//	/**
//	 * @param deviceCategory the deviceCategory to set
//	 */
//	public void setDeviceCategory(ActivityHubDeviceCategory deviceCategory) {
//		this.deviceCategory = deviceCategory;
//	}

	/**
	 * @return the sensorEvent
	 */
	public abstract int getSensorEventValue();


	/**
	 * @param sensorEvent the sensorEvent to set
	 */
	public abstract void setSensorEvent(int sensorEvent);

	/**
	 * @return the deviceLocation
	 */
	public ActivityHubLocation getDeviceLocation() {
		return deviceLocation;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

//	/**
//	 * @param deviceId the deviceId to set
//	 */
//	public void setDeviceId(String deviceId) {
//		this.deviceId = deviceId;
//	}

	/* (non-Javadoc)
	 * @see org.osgi.service.device.Device#noDriverFound()
	 */
	public void noDriverFound() {

		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for ActivityHub device: " +
				this.deviceCategory + " - " + this.deviceId);		
	}
	
	
}

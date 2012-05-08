package org.universAAL.iso11073.activityhub.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubBaseDeviceCategory;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Activity hub sensor base class
 * 
 * 
 * @author Thomas Fuxreiter
 *
 */
public  
abstract 
class ActivityHubSensor implements Device {

	private LogService logger;
	private ActivityHubDeviceCategory deviceCategory;
	private ActivityHubLocation deviceLocation;
	// some kind of deviceID !?
	private String deviceId = null;

	protected Object lastSensorEvent;

	/** reference to my driver instance; can be just one! */
	protected ActivityHubBaseDeviceCategory driver;

	
	public ActivityHubSensor(ActivityHubDeviceCategory deviceCategory, 
			ActivityHubLocation deviceLocation, String deviceId, LogService logger) {
		this.deviceCategory = deviceCategory;
		this.deviceLocation = deviceLocation;
		this.deviceId = deviceId;
		this.logger = logger;
	}
	
	
	/** store a driver reference for this device */
	public void addDriver(ActivityHubBaseDeviceCategory driverInstance) {
		this.driver = driverInstance;
	}
	

	/** remove the driver reference of this device */
	public void removeDriver() {
		this.driver = null;
	}
	
	
	/** 
	 * set sensor event to on (e.g. switch-on)
	 * if input comes from an 1-bit sensor 
	 */
	public abstract void setSensorEventOn();

	/** 
	 * set sensor event to off (e.g. switch-off) 
	 * if input comes from an 1-bit sensor 
	 */
	public abstract void setSensorEventOff();
	
	/** 
	 * send sensor event to driver (upper layer) 
	 */
	protected void sendEvent(int event) {
		this.driver.incomingSensorEvent(event);
	}
	
	
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


//	/**
//	 * @param sensorEvent the sensorEvent to set
//	 */
//	public abstract void setSensorEvent(int sensorEvent);

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

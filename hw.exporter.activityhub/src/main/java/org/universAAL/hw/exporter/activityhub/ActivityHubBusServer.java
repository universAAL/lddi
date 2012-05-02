package org.universAAL.hw.exporter.activityhub;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.driver.ActivityHubDriverManager;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;

/**
 * Instanciates all ActivityHub drivers from ISO11073 library.
 * The drivers call back and register themselves in the driverList.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubBusServer implements ActivityHubDriverClient {

	private BundleContext context;
	private LogService logger;
	
	/**
	 * stores activityHubInstances for deviceCategories
	 * 
	 * key = deviceCategory
	 * value = ActivityHubDriver
	 */
	private Hashtable<ActivityHubDeviceCategory, Set<ActivityHubDriver>> driverListForCategory;

	/**
	 * stores the activityHubInstance (there should be just one!) for each deviceId
	 * 
	 * key = deviceId
	 * value = ActivityHubDriver
	 */
	private Hashtable<String, ActivityHubDriver> driverList;
	
	
	/**
	 * @param context
	 */
	public ActivityHubBusServer(BundleContext context, LogService logger) {
		this.context = context;
		this.logger = logger;		
		this.driverListForCategory = new Hashtable<ActivityHubDeviceCategory, 
			Set<ActivityHubDriver>>();
		this.driverList = new Hashtable<String, ActivityHubDriver>();

		// start all ActivityHub drivers
		ActivityHubDriverManager.startAllDrivers(this, this.context);
	
		this.logger.log(LogService.LOG_INFO, "I hope all ActivityHub drivers are" +
				" online now.............");
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#addDriver(java.lang.String, org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void addDriver(String deviceId, ActivityHubDeviceCategory deviceCategory,
			ActivityHubDriver activityHubDriver) {

		ActivityHubDriver driver = this.driverList.get(deviceId);

		synchronized(this.driverList)
		{
			ActivityHubDriver oldDriver = this.driverList.put(deviceId, activityHubDriver);
		}
		if ( driver == null ){
			this.logger.log(LogService.LOG_WARNING, "An existing ActivityHub driver " +
					"is now replaced by a new one for device " + deviceId
					+ " and category: " + deviceCategory);
		}
		
		// my unique device service id
//		String myId = activityHubDriver.getDevice().getDeviceCategory() + 
//			activityHubDriver.getDevice().getDeviceId();
		
		Set<ActivityHubDriver> driversListForCat = this.driverListForCategory.get(deviceCategory);
		if(driversListForCat == null){
			driversListForCat = new HashSet<ActivityHubDriver>();
				
			synchronized(this.driverListForCategory)
			{
				this.driverListForCategory.put(deviceCategory, driversListForCat);
			}
		}
		driversListForCat.add(activityHubDriver);
		
		this.logger.log(LogService.LOG_INFO, "new ActivityHub driver added for device " + deviceId
				+ " and category: " + deviceCategory);
		
	}

	
	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#incomingSensorEvent(java.lang.String, byte[])
	 */
	public void incomingSensorEvent(String deviceId, byte[] message) {
		this.logger.log(LogService.LOG_INFO, "received sensor event: " + message);
		
		// TODO create context event!
		
		
		
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#removeDriver(java.lang.String, org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void removeDriver(String deviceId,
			ActivityHubDriver activityHubDriver) {
		this.driverList.remove(deviceId);
		this.logger.log(LogService.LOG_INFO, "removed ActivityHub driver for " +
				"device " + deviceId);
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#getLogger()
	 */
	public LogService getLogger() {
		return this.logger;
	}


	/**
	 * @param deviceId
	 * @return
	 */
	public ActivityHubLocation getDeviceLocation(String deviceId) {

		ActivityHubDriver driver = this.driverList.get(deviceId);
		ActivityHubLocation loc = driver.getDevice().getDeviceLocation();
		if ( loc == null ) {
			this.logger.log(LogService.LOG_WARNING, "No location specified for " +
					"device: " + deviceId);
		}
		return loc;
	}


	/**
	 * @param deviceId
	 * @return
	 */
	public boolean validateDevice(String deviceId) {
		if( this.driverList.get(deviceId) == null ) return false;
		return true;
	}


	/**
	 * Find the driver for the requested device according to unique deviceId
	 * 
	 * @param deviceId
	 * @return integer value of last device event from device dependent event enumeration 
	 */
	public int getLastDeviceEvent(String deviceId) {
		//return last incoming device event from driver instance
		ActivityHubDriver driver = this.driverList.get(deviceId);
		if ( driver != null ) {
			return driver.getLastSensorEvent();
		} else {
			this.logger.log(LogService.LOG_WARNING, "No recent sensor event found for " +
					"device: " + deviceId);
			return -1;
		}
	}

}

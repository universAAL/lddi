package org.universAAL.hw.exporter.activityhub;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

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
	
    private ArrayList<ActivityHubContextProvider> listeners = new ArrayList<ActivityHubContextProvider>();

    
	/**
	 * constructor
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

//		ActivityHubDriver driver = this.driverList.get(deviceId);

		ActivityHubDriver oldDriver = null;
		synchronized(this.driverList)
		{
			oldDriver = this.driverList.put(deviceId, activityHubDriver);
			int i = activityHubDriver.getDevice().getDeviceCategory().getTypeCode();
		}
		if ( oldDriver != null ){
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

	
	/**
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#incomingSensorEvent(java.lang.String, byte[])
	 * 
	 * the correct sensor type must be identified according to the parameters
	 * @param deviceId (e.g. knx group address 1/2/3)
	 * @param device category (one category for each activityhub sensor type)
	 * @param event code (sensor type dependent!)
	 */
	public void incomingSensorEvent(String deviceId, ActivityHubDeviceCategory activityHubDeviceCategory, int event) {
		this.logger.log(LogService.LOG_INFO, "Client received sensor event: " + event);
		
//		ActivityHubDriver driver = this.driverList.get(deviceId);
		
		// TODO create context event! not here -> contextprovider class
		for (Iterator<ActivityHubContextProvider> i = listeners.iterator(); i.hasNext();)
			((ActivityHubContextProvider) i.next()).activityHubSensorStateChanged(deviceId, 
					activityHubDeviceCategory, event);
		
		// create semantic representation of event -> has to be done beforehand. 
		// create context event patterns in contextprovider
		
		// Use a factory for creation of ISO-SENSOR
		// switch on activityHubDeviceCategory
		// create URI with trailing deviceId

		// create appropriate event
		
		// create RDF-triple
		
	}

//	public void sendContextEvent() {
//	    for (Iterator<ActivityHubContextProvider> i = listeners.iterator(); i.hasNext();)
//			((ActivityHubContextProvider) i.next()).sendContextEvent();
//	    
//		//lampStateChanged(lampID,myLampDB[lampID].loc, false)
//	}
	

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


	/**
	 * store listener for context bus connection 
	 * @param activityHubContextProvider
	 */
	public void addListener(
			ActivityHubContextProvider activityHubContextProvider) {
		listeners.add(activityHubContextProvider);
	}

	/**
	 * @param activityHubContextProvider
	 */
	public void removeListener(
			ActivityHubContextProvider activityHubContextProvider) {
		listeners.remove(activityHubContextProvider);
	}


	/**
	 * copy deviceId(String) and ActivityHub device category(Integer) into sensorList parameter
	 * for all available ActivityHub sensors
	 * @param sensorList
	 */
	public void getActivityHubSensorList(Hashtable<String,Integer> sensorList) {
		synchronized (driverList) {
			synchronized (sensorList) {
				Iterator<Entry<String,ActivityHubDriver>> it = this.driverList.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String,ActivityHubDriver> entry = it.next();
					sensorList.put(new String(entry.getKey()),
							new Integer(entry.getValue().getDevice().getDeviceCategory().getTypeCode()) );
				}
			}
		}
	}

}

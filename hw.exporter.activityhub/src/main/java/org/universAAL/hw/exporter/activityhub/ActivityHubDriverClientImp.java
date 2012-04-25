package org.universAAL.hw.exporter.activityhub;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

import org.universAAL.iso11073.activityhub.driver.ActivityHubDriverManager;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * Instanciates all ActivityHub drivers from ISO11073 library
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubDriverClientImp implements ActivityHubDriverClient {

	private BundleContext context;
	private Hashtable<String, Set<ActivityHubDriver>> driverList;
	private LogService logger;
	
	
	/**
	 * @param context
	 */
	public ActivityHubDriverClientImp(BundleContext context, LogService logger) {
		this.context = context;
		this.logger = logger;		
		this.driverList=new Hashtable<String, Set<ActivityHubDriver>>();

		// start all ActivityHub drivers
		ActivityHubDriverManager.startAllDrivers(this, this.context);
	
		this.logger.log(LogService.LOG_INFO, "I hope all ActivityHub drivers are" +
				" online now.............");
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#addDriver(java.lang.String, org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void addDriver(String device, ActivityHubDriver driver) {

		Set<ActivityHubDriver> drivers=this.driverList.get(device);
		if(drivers==null){
			drivers=new HashSet<ActivityHubDriver>();
			
			synchronized(this.driverList)
			{
			this.driverList.put(device, drivers);
			}
		}
		drivers.add(driver);
		this.logger.log(LogService.LOG_INFO, "new ActivityHub driver added for device " + device);
		
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#removeDriver(java.lang.String, org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriver)
	 */
	public void removeDriver(String deviceId,
			ActivityHubDriver activityHubDriver) {
		Set<ActivityHubDriver> drivers=this.driverList.get(deviceId);
		drivers.remove(activityHubDriver);
		this.logger.log(LogService.LOG_INFO, "removed ActivityHub driver for device " + deviceId);
	}


	/* (non-Javadoc)
	 * @see org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient#getLogger()
	 */
	public LogService getLogger() {
		return this.logger;
	}

}

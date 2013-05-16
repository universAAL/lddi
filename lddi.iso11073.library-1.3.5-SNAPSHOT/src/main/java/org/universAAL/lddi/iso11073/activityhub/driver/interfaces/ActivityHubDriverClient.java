package org.universAAL.lddi.iso11073.activityhub.driver.interfaces;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;


/**
 * Applications using ActivityHubDriver should implement this IF to get event messages.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface ActivityHubDriverClient {

	public void addDriver(String deviceId, ActivityHubDeviceCategory activityHubDeviceCategory, 
			ActivityHubDriver activityHubDriver);
	public void removeDriver(String deviceId, ActivityHubDriver activityHubDriver);
	public LogService getLogger();
	
	/**
	 * get event message from underlying devices
	 */
	public void incomingSensorEvent(String deviceId, ActivityHubDeviceCategory activityHubDeviceCategory, int event);
}

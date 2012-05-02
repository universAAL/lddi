package org.universAAL.iso11073.activityhub.driver.interfaces;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;


/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface ActivityHubDriverClient {

	public void addDriver(String deviceId, ActivityHubDeviceCategory activityHubDeviceCategory, 
			ActivityHubDriver activityHubDriver);
	public void removeDriver(String deviceId, ActivityHubDriver activityHubDriver);
	public LogService getLogger();
	/**
	 * @param deviceId
	 * @param message
	 */
	public void incomingSensorEvent(String deviceId, byte[] message);
}

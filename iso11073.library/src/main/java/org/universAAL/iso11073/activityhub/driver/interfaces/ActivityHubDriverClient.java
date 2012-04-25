package org.universAAL.iso11073.activityhub.driver.interfaces;

import org.osgi.service.log.LogService;


/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface ActivityHubDriverClient {

	public void addDriver(String device,ActivityHubDriver driver);
	public void removeDriver(String deviceId, ActivityHubDriver activityHubDriver);
	public LogService getLogger();
}

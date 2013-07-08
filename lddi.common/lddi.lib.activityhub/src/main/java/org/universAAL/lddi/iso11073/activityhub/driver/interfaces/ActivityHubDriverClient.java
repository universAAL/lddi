/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
     See the NOTICE file distributed with this work for additional
     information regarding copyright ownership

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

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

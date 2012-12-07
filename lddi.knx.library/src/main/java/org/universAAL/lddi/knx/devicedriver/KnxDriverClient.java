package org.universAAL.lddi.knx.devicedriver;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;

/**
 * Applications using KNXDriver should implement this IF to get event messages.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface KnxDriverClient {
	
	/** couple KNX driver to upper layer */
	public void addDriver(String deviceId, KnxDeviceCategory deviceCategory,
			KnxDriver knxDriver);
	public void removeDriver(String deviceId, KnxDriver knxDriver);
	public LogService getLogger();
	
	/**
	 * get event message from underlying devices
	 */
	public void incomingSensorEvent(String deviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, float value);

}

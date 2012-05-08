package org.universAAL.knx.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.knx.devicecategory.KnxDpt1;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Device extends KnxDevice implements KnxDpt1 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt1Device() {
		super();
	}

	@Override
	public void newMessageFromHouse(String deviceAddress, byte event) {
		this.logger.log(LogService.LOG_INFO, "Device " + this.getDeviceId() + " got event: " + event);

		// TODO apply here encoding/decoding rules from KnxDpt1 IF ?? 
		this.driver.newMessageFromKnxBus(event);
	}

	/* (non-Javadoc)
	 * @see org.universAAL.knx.devicecategory.KnxDpt1#newMessageFromKnxBus(byte)
	 */
	public void newMessageFromKnxBus(byte event) {
		// not used in device; this method is called in driver!
	}



}

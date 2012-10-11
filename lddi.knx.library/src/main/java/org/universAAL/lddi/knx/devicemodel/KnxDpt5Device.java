package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt5;

/**
 * Concrete implementation of KNX devices for KNX data type 5.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt5Device extends KnxDevice implements KnxDpt5 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt5Device() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory#newMessageFromKnxBus(byte)
	 */
	public void newMessageFromKnxBus(byte event) {
		// not used in device; this method is called in driver!
	}
}

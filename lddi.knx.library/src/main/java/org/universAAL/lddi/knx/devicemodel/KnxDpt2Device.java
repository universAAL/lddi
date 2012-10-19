package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt2;

/**
 * Concrete implementation of KNX devices for KNX data type 2.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt2Device extends KnxDevice implements KnxDpt2 {

	public KnxDpt2Device() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory#newMessageFromKnxBus(byte)
	 */
	public void newMessageFromKnxBus(byte event) {
		// not used in device; this method is called in driver!
	}
 
}

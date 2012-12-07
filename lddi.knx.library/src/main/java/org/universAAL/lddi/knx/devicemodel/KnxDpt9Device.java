package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt9;

/**
 * Concrete implementation of KNX devices for KNX data type 9.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9Device extends KnxDevice implements KnxDpt9 {

    /**
     * empty constructor for factory
     */
    public KnxDpt9Device() {
    	super(MY_DEVICE_CATEGORY);
    }

    @Deprecated
    public void newMessageFromKnxBus(byte[] event) {
    	// not used in device; this method is called in driver!
    }

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicecategory.KnxDpt9#calculateFloatValue(byte[])
	 */
    @Deprecated
	public float calculateFloatValue(byte[] payload) {
    	// not used in device; this method is called in driver!
		return 0;
	}
}

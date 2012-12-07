package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt2;

/**
 * Concrete implementation of KNX devices for KNX data type 2.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt2Device extends KnxDevice implements KnxDpt2 {

    /**
     * empty constructor for factory
     */
    public KnxDpt2Device() {
    	super(MY_DEVICE_CATEGORY);
    }

    @Deprecated
    public void newMessageFromKnxBus(byte[] event) {
	// not used in device; this method is called in driver!
    }

}

package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt1;

/**
 * Concrete implementation of KNX devices for KNX data type 1.***.
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

    @Deprecated
    public void newMessageFromKnxBus(byte event) {
	// not used in device; this method is called in driver!
    }
}

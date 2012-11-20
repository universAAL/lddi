package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt4;

/**
 * Concrete implementation of KNX devices for KNX data type 4.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt4Device extends KnxDevice implements KnxDpt4 {

    /**
     * empty constructor for factory
     */
    public KnxDpt4Device() {
	super();
    }

    @Deprecated
    public void newMessageFromKnxBus(byte event) {
	// not used in device; this method is called in driver!
    }

    @Deprecated
    public String convertHexToString(CharacterSet characterSet) {
	// not used in device; this method is called in driver!
	return null;
    }
}

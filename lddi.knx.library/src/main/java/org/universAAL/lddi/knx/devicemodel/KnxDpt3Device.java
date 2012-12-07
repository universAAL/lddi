package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt3;

/**
 * Concrete implementation of KNX devices for KNX data type 3.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt3Device extends KnxDevice implements KnxDpt3 {

    /**
     * empty constructor for factory
     */
    public KnxDpt3Device() {
    	super(MY_DEVICE_CATEGORY);
    }

    @Deprecated
    public void newMessageFromKnxBus(byte[] event) {
	// not used in device; this method is called in driver!
    }
    
    @Deprecated
    public int calculateStepNumberOfInterval(byte stepcode) {
	// not used in device; this method is called in driver!
	return 0;
    }

}

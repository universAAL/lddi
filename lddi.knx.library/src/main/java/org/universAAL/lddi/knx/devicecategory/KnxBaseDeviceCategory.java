package org.universAAL.lddi.knx.devicecategory;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface KnxBaseDeviceCategory {
	/***
	 * The specific drivers have to implement this method to receive low level messages from the knx bus
	 * @param message the status/event byte of the knx telegram
	 */
	public void newMessageFromKnxBus( byte event );

}

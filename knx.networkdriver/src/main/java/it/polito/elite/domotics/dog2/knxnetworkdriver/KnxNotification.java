/**
 * 
 */
package it.polito.elite.domotics.dog2.knxnetworkdriver;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver;

/**
 * This class stores information about a Knx notification
 * @author Castellina Emiliano
 *
 */
public class KnxNotification extends KnxCommand {

	/**
	 * @param commandName
	 * @param groupAddress
	 * @param hexValue
	 */
	public KnxNotification(String commandName, String groupAddress,
			String hexValue) {
		super(commandName, groupAddress, hexValue);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param notificationName
	 * @param groupAddress2
	 */
	public KnxNotification(String notificationName, String groupAddress2) {
		super(notificationName, groupAddress2);
		// TODO Auto-generated constructor stub
	}
	
	

	public String getAddressHex(){
		return this.groupAddress+"#"+KnxDriver.clearHexValue(hexValue);
	}
	
	

}

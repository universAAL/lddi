package it.polito.elite.domotics.dog2.knxnetworkdriver;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver;


/***
 * It stores information about Knx commands 
 * @author Emiliano
 *
 */
public class KnxCommand {
	
	String commandName;
	String groupAddress;
	String hexValue;
	
	

	

	public KnxCommand(String commandName, String groupAddress, String hexValue) {
		this.commandName=commandName;
		this.groupAddress=groupAddress;
		this.hexValue=hexValue;
	}





	public KnxCommand(String notificationName, String groupAddress2) {
		this.commandName=notificationName;
		this.groupAddress=groupAddress2;
		this.hexValue="";
	}





	public String getName() {
		return commandName;
	}





	public String getGroupAddress() {
		return groupAddress;
	}





	public String getHexValue() {
		return hexValue;
	}




    /**
     * Check if a command or a notification is compatible with the passed device and hex value
     * @param device
     * @param status
     * @return
     */
	public boolean compatibleWith(String device, String status) {
		
		return this.groupAddress.equalsIgnoreCase(device) && status.equalsIgnoreCase(KnxDriver.clearHexValue(this.hexValue));
	}

	
	
	
}

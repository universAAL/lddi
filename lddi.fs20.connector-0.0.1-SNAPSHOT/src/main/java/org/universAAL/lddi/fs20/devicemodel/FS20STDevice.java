
package org.universAAL.lddi.fs20.devicemodel;

import java.io.IOException;

import org.universAAL.lddi.fs20.connect.FHZ1000PC;

/**
 * Representation of a FS20ST device (switch actuator)
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20STDevice extends FS20Device{
	
	/**
	 * Constructor for a FS20ST device with the used connection
	 * 
	 * @param connection = the connection to the FS20 network
	 */
	public FS20STDevice (FHZ1000PC connection){
		setConnection(connection);
	}

	/**
	 * Switch the FS20ST device off
	 * 
	 * @throws IOException = exception if something went wrong with the communication
	 */
	public void switchOff() throws IOException{
		write(getHouseCode(), getDeviceCode(), FHZ1000PC.COMMAND_OFF);
	}
	
	/**
	 * Switch the FS20ST device on
	 * 
	 * @throws IOException = exception if something went wrong with the communication
	 */
	public void switchOn() throws IOException{
		write(getHouseCode(), getDeviceCode(), FHZ1000PC.COMMAND_ON);
	}
	
}

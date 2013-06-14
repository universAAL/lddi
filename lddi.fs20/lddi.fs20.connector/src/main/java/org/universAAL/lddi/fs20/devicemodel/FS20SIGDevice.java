
package org.universAAL.lddi.fs20.devicemodel;

import java.io.IOException;

import org.universAAL.lddi.fs20.connect.FHZ1000PC;

/**
 * Representation of a FS20SIG device (acoustic signal actuator)
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20SIGDevice extends FS20Device{
	
	/**
	 * Constructor for a FS20RGBSA device with the used connection
	 * 
	 * @param connection = the connection to the FS20 network
	 */
	public FS20SIGDevice (FHZ1000PC connection){
		setConnection(connection);
	}

	/**
	 * Activates the FS20SIG device
	 * 
	 * @throws IOException = exception if something went wrong with the communication
	 */
	public void activateSignal() throws IOException{
		
		// Attention: the device has to be reset and switched off 
		// like it is done here
		
		write(getHouseCode(),getDeviceCode(), FHZ1000PC.COMMAND_ON);
		write(getHouseCode(),getDeviceCode(), (byte) 0x1c);
		write(getHouseCode(),getDeviceCode(), FHZ1000PC.COMMAND_OFF);
	}
	
}

package org.universAAL.lddi.fs20.devicemodel;

import org.universAAL.lddi.fs20.connect.FHZ1000PC;

/**
 * Representation of a FS20FMS device (Detects if a plugged in device is on or off)
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20FMSDevice extends FS20Device{
	
	/**
	 * Constructor for a FS20FMS device with the used connection
	 * 
	 * @param connection = the connection to the FS20 network
	 */
	public FS20FMSDevice (FHZ1000PC connection){
		setConnection(connection);
	}
}

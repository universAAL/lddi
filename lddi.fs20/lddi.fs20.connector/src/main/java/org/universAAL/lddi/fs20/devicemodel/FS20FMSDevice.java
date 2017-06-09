/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package org.universAAL.lddi.fs20.devicemodel;

import org.universAAL.lddi.fs20.connect.FHZ1000PC;

/**
 * Representation of a FS20FMS device (Detects if a plugged in device is on or
 * off)
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20FMSDevice extends FS20Device {

	/**
	 * Constructor for a FS20FMS device with the used connection
	 * 
	 * @param connection
	 *            = the connection to the FS20 network
	 */
	public FS20FMSDevice(FHZ1000PC connection) {
		setConnection(connection);
	}
}

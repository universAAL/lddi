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

import java.io.IOException;

import org.universAAL.lddi.fs20.connect.FHZ1000PC;

/**
 * Representation of an FS20RGB-SA display device
 * 
 * <b>Annotation:</b> 12 different views are equipped, more a possible
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20RGBSADevice extends FS20Device{
	
	private static final byte[] animations = {0x00,0x01,0x02,0x03,0x04,
												0x05,0x06,0x07,0x08,0x09,0x10,0x11};
	
	private String[] animationDescriptions = new String[12];
	
	/**
	 * Constructor for a FS20RGBSA device with the used connection
	 * 
	 * @param connection = the connection to the FS20 network
	 */
	public FS20RGBSADevice (FHZ1000PC connection){
		setConnection(connection);
	}

	/**
	 * Starts the animation 1 to 12
	 * 
	 * @param animationNumber = the given animation number
	 * @throws IOException = exception if something went wrong with the communication
	 */
	public void startAnimation(int animationNumber) throws IOException{
		write(getHouseCode(), getDeviceCode(), animations[animationNumber-1]);
	}
	
	public void setDescriptionToAnimation(int animationNumber, String description){
		animationDescriptions[animationNumber-1] = description;
	}

	

}

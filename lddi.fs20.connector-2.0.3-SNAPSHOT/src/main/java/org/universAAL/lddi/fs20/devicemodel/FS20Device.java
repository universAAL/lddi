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

import org.osgi.service.log.LogService;
import org.universAAL.lddi.fs20.connect.FHZ1000PC;


/**
 * One FS20 Device with all additional properties. This device is registered in
 * OSGi framework
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20Device {

	private String deviceURI = "-";
	private FS20DeviceProperties FS20DeviceProperties;
	
	private  FHZ1000PC connection;

	protected LogService logger;

	/**
	 * empty constructor for factory
	 */
	public FS20Device() {
	}
	
	protected void setConnection(FHZ1000PC connection){
		this.connection = connection;
	}
	private  FHZ1000PC getConnection(){
		return connection;
	}

	/**
	 * Fill empty device with parameters and set it alive
	 * 
	 * @param fs20DeviceProps
	 *            = contains all relevant properties
	 * @param network
	 *            = the conntected FS20 network
	 * @param logger
	 *            = logging service
	 */
	public void setParams(FS20DeviceProperties fs20DeviceProps,
			LogService logger) {
		this.FS20DeviceProperties = fs20DeviceProps;
		this.logger = logger;

		this.deviceURI = this.FS20DeviceProperties.getURI();


		this.logger.log(LogService.LOG_DEBUG, "Registered device " + deviceURI
				+ " in fs20.networkdriver.");
	}

	/**
	 * Fill empty device with parameters and set it alive
	 * 
	 * @param fs20DeviceProps
	 *            = contains all relevant properties
	 * @param network
	 *            = the conntected FS20 network
	 * @param logger
	 *            = logging service
	 */
	public void setParams(FS20DeviceProperties fs20DeviceProps) {
		this.FS20DeviceProperties = fs20DeviceProps;

		this.deviceURI = this.FS20DeviceProperties.getURI();

	}
	
	/**
	 * Get the device location
	 * @return returns the device location
	 */
	public String getDeviceLocation() {
		return this.FS20DeviceProperties.getLocation();
	}
	
	/**
	 * Get the device URI
	 * @return returns the device URI
	 */
	public String getDeviceURI() {
		return this.FS20DeviceProperties.getURI();
	}

	/**
	 * Get the device name
	 * @return returns the device name
	 */
	public String getDeviceName() {
		return this.FS20DeviceProperties.getName();
	}

	/**
	 * Get the HouseCode
	 * @return returns the houseCode
	 */
	public String getHouseCode() {
		return this.FS20DeviceProperties.getHousecode();
	}

	/**
	 * Get the DeviceCode
	 * @return returns the deviceCode
	 */
	public String getDeviceCode() {
		return this.FS20DeviceProperties.getDevicecode();
	}

	/**
	 * Get the device description
	 * @return returns the device description
	 */
	public String getDeviceDescription() {
		return this.FS20DeviceProperties.getDeviceDescription();
	}

	/**
	 * Get the function description
	 * @return returns the function description
	 */
	public String getFunctionDescription() {
		return this.FS20DeviceProperties.getFunctionDesciption();
	}
	
	/**
	 * Get the device type
	 * @return returns the device type
	 */
	public FS20DeviceTypes getDeviceType(){
		return this.FS20DeviceProperties.getDeviceType();
	}
	
	/**
	 * Sends a FS20 command to the FS20 bus
	 * 
	 * @param houseCode = the houseCode of the device
	 * @param deviceCode = the deviceCode of the device
	 * @param button = the control function
	 * @throws IOException = exception if something went wrong with the communication
	 */
	protected void write(String houseCode, String deviceCode, Byte button) throws IOException{
		FHZ1000PC con = getConnection();
		con.sendFS20Command(FHZ1000PC.StringFS20ToInt(houseCode), (byte) FHZ1000PC.StringFS20ToInt(deviceCode), button.byteValue());
		new Thread() {
			public void run() {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					System.err.println(e);
					e.printStackTrace();
				}
			}
		}.run();
	}

}

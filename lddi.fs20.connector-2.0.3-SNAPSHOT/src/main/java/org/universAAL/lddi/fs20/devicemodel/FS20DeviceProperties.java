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

/**
 * Object to describe all FS20 device properties
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20DeviceProperties {

	private String URI;
	private String name;
	private String housecode;
	private String devicecode;
	private FS20DeviceTypes type;
	private String value;
	private String description;
	private String functiondescription;
	private String location;

	/**
	 * Constructor of an incomplete described FS20 Device
	 * 
	 * @param URI
	 *            = the unique id
	 * @param name
	 *            = the name of the device, e.g. motiondetector livingroom
	 * @param housecode
	 *            = the housecode, e.g. 12345678
	 * @param devicecode
	 *            = the devicecode, e.g. 1234
	 * @param type
	 *            = the type of the device, e.g. motionsensor, display, FMS,
	 *            gong
	 */
	public FS20DeviceProperties(String URI, String name, String housecode,
			String devicecode, FS20DeviceTypes type) {
		super();
		this.URI = URI;
		this.name = name;
		this.housecode = housecode;
		this.devicecode = devicecode;
		this.type = type;
	}

	/**
	 * Constructor of a complete described FS20 Device
	 * 
	 * @param URI
	 *            = the unique id
	 * @param name
	 *            = the name of the device, e.g. motiondetector livingroom
	 * @param housecode
	 *            = the housecode, e.g. 12345678
	 * @param devicecode
	 *            = the devicecode, e.g. 1234
	 * @param type
	 *            = the type of the device, e.g. motionsensor, display, FMS,
	 *            gong
	 * @param value
	 *            = the last measurement value
	 * @param description
	 *            = a longer description of the device, e.g. where it is or
	 *            which pictures it can show
	 * @param functiondescription
	 *            = a description of it's function, e.g. measures current of
	 *            coffee machine
	 * @param location
	 *            = the location of the device, e.g. livingroom
	 */
	public FS20DeviceProperties(String URI, String name, String housecode,
			String devicecode, FS20DeviceTypes type, String value, String description,
			String functiondescription, String location) {
		this.URI = URI;
		this.name = name;
		this.housecode = housecode;
		this.devicecode = devicecode;
		this.type = type;
		this.value = value;
		this.description = description;
		this.functiondescription = functiondescription;
		this.location = location;
	}

	/**
	 * Returns the name of the FS20 device
	 * 
	 * @return name of the FS20 device
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the unique device ID of the FS20 device
	 * 
	 * @return the unique device ID
	 */
	public String getURI() {
		return URI;
	}

	/**
	 * Returns the housecode of the FS20 device
	 * 
	 * @return the housecode
	 */
	public String getHousecode() {
		return housecode;
	}

	/**
	 * Returns the devicecode of the FS20 device
	 * 
	 * @return the devicecode
	 */
	public String getDevicecode() {
		return devicecode;
	}

	/**
	 * Returns the device type of the FS20 device
	 * 
	 * @return the device type
	 */
	public FS20DeviceTypes getDeviceType() {
		return type;
	}

	/**
	 * Returns the last measured value of the FS20 device
	 * 
	 * @return the last measured value
	 */
	public String getMeasurementValue() {
		return value;
	}

	/**
	 * Returns a longer description of the FS20 device
	 * 
	 * @return description of the device
	 */
	public String getDeviceDescription() {
		return description;
	}

	/**
	 * Returns a longer description of the functionality of the FS20 device
	 * 
	 * @return description of the function
	 */
	public String getFunctionDesciption() {
		return functiondescription;
	}

	/**
	 * Returns the name of the location where the FS20 device is located
	 * 
	 * @return the location of the device
	 */
	public String getLocation() {
		return location;
	}
	


	/**
	 * Returns all properties as a string
	 * 
	 * @return A string of all properties
	 */
	public String toString() {
		return "Name:" + getName() + "; " + "ID:" + getURI() + "; "
				+ "Housecode:" + getHousecode() + "; " + "Devicecode:"
				+ getDevicecode() + "; " + "DeviceType:" + getDeviceType()
				+ "; " + "MeasuredValue:" + getMeasurementValue() + "; "
				+ "Description:" + getDeviceDescription() + "; "
				+ "FunctionDescription:" + getFunctionDesciption() + "; "
				+ "Location:" + getLocation();
	}
	

}

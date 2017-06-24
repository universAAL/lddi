/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at

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

package org.universAAL.lddi.knx.utils;

public class KnxGroupAddress {

	private String name;
	private String groupAddress;
	private String dpt;
	private String command = "N/A";
	private Boolean importGA = Boolean.FALSE;

	private String description;
	private String comment;

	// building part information belongs to one physical KNX device
	// not to a group device (KNX group address)
	// private String bpType; //BuildingPart Type
	// private String bpName; //BuildingPart Name
	// private String bpDescription;
	// location (room)

	public KnxGroupAddress(String dpt, String groupAddress, String name) {
		super();
		this.dpt = dpt;
		this.groupAddress = groupAddress;
		this.name = name;
	}

	/**
	 * @param name
	 * @param groupAddress
	 * @param dpt
	 * @param command
	 * @param importGA
	 * @param description
	 * @param bpType
	 * @param bpName
	 * @param bpDescription
	 */
	public KnxGroupAddress(String dpt, String groupAddress, String name, String description, String comment,
			String bpType, String bpName, String bpDescription) {
		this.name = name;
		this.groupAddress = groupAddress;
		this.dpt = dpt;
		this.description = description;
		this.comment = comment;
		// this.bpType = bpType;
		// this.bpName = bpName;
		// this.bpDescription = bpDescription;
	}

	/**
	 * @return description of KNX groupDevice
	 */
	public String getDescription() {
		return description;
	}

	// /**
	// * @return type of the BuildingPart where the KNX device is located
	// */
	// public String getBpType() {
	// return bpType;
	// }
	//
	// /**
	// * @return Name of the BuildingPart where the KNX device is located
	// */
	// public String getBpName() {
	// return bpName;
	// }
	//
	// /**
	// * @return description of the BuildingPart where the KNX device is located
	// */
	// public String getBpDescription() {
	// return bpDescription;
	// }

	/**
	 * @return name of the KNX groupDevice
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return group address as String in format "M/L/D"
	 */
	public String getGroupAddress() {
		return groupAddress;
	}

	/**
	 * @return datapoint type of the KNX groupDevice
	 */
	public String getDpt() {
		return dpt;
	}

	/**
	 * @return name, group address, datapoint type and of the KNX groupDevice
	 */
	public String toString() {
		return getName() + "; " + getGroupAddress() + "; " + getDpt() + "; " + getDescription() + "; " + getComment();
		// + "; " +
		// getBpType() + "; " +
		// getBpName() + "; " +
		// getBpDescription();
	}

	/**
	 * @return comment on KNX groupDevice
	 */
	private String getComment() {
		return this.comment;
	}

	/**
	 * @return main datapoint type number of the KNX groupDevice
	 */
	public String getDptMain() {
		String temp = this.getDpt().trim();
		return temp.substring(0, temp.indexOf("."));
	}

	/**
	 * @return sub datapoint type number of the KNX groupDevice
	 */
	public String getDptSub() {
		String temp = this.getDpt().trim();
		return temp.substring(temp.indexOf(".") + 1);
	}

	public Boolean getImportGA() {
		return importGA;
	}

	public String getCommand() {
		return command;
	}

}

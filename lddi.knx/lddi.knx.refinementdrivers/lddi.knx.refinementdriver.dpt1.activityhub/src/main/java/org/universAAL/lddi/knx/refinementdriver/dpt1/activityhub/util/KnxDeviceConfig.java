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

package org.universAAL.lddi.knx.refinementdriver.dpt1.activityhub.util;

import java.util.Dictionary;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDeviceConfig {
	private String configurationPid;
	private String knxGroupAddress;
	private Dictionary configurationProperties;
	
	
	/**
	 * @param knxGroupAddress
	 * @param configurationProperties
	 */
	public KnxDeviceConfig(String knxGroupAddress,
			Dictionary configurationProperties,
			String configurationPid) {
		this.knxGroupAddress = knxGroupAddress;
		this.configurationProperties = configurationProperties;
		this.configurationPid = configurationPid;
	}
	/**
	 * @return the knxGroupAddress
	 */
	public String getKnxGroupAddress() {
		return knxGroupAddress;
	}
	/**
	 * @param knxGroupAddress the knxGroupAddress to set
	 */
	public void setKnxGroupAddress(String knxGroupAddress) {
		this.knxGroupAddress = knxGroupAddress;
	}
	/**
	 * @return the configurationProperties
	 */
	public Dictionary getConfigurationProperties() {
		return configurationProperties;
	}
	/**
	 * @param configurationProperties the configurationProperties to set
	 */
	public void setConfigurationProperties(Dictionary configurationProperties) {
		this.configurationProperties = configurationProperties;
	}
	/**
	 * @return the configurationPid
	 */
	public String getConfigurationPid() {
		return configurationPid;
	}
	/**
	 * @param configurationPid the configurationPid to set
	 */
	public void setConfigurationPid(String configurationPid) {
		this.configurationPid = configurationPid;
	}
	

}

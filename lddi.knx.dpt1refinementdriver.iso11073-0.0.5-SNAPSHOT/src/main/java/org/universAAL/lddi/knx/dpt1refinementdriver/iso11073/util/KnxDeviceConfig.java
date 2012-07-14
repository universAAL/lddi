package org.universAAL.lddi.knx.dpt1refinementdriver.iso11073.util;

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

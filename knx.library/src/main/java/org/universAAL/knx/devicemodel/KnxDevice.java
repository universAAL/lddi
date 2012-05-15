package org.universAAL.knx.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.knx.devicecategory.KnxBaseDeviceCategory;
import org.universAAL.knx.networkdriver.KnxNetwork;
import org.universAAL.knx.utils.*;

/**
 * One KNX device represents one groupAddress (with additional properties) from ETS4 XML export.
 * This device is registered in OSGi framework.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public abstract class KnxDevice implements Device{

	/** OSGi DAS properties */

	public String deviceCategory;
	/** intended for end users */
	public String deviceDescription;
	/** unique serial number for this device */
	private String deviceSerial;
	/** should be set; every time the same hardware is plugged in, the same PIDs are used */
	private String servicePid;

	private String deviceId = "-";
	private KnxGroupAddress knxDeviceProperties;
	
	private static String KNX_DEVICE_CATEGORY_PREFIX = "KnxDpt";
	
	protected LogService logger;
	protected KnxNetwork network;
	
	/** reference to my driver instance; can be just one! */
	protected KnxBaseDeviceCategory driver;
	

	/**
	 * empty constructor for factory
	 */
	public KnxDevice() {
	}
	
	/**
	 * Fill empty device with parameters and set it alive
	 * 
	 * @param knxGroupAddress
	 * @param network 
	 * @param logger2
	 */
	public void setParams(KnxGroupAddress knxGroupAddress, KnxNetwork network, LogService logger) {
		this.knxDeviceProperties = knxGroupAddress;
		this.network = network;
		this.logger = logger; 

		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + this.knxDeviceProperties.getMainDpt();
		
//		this.deviceDescription;
//		this.deviceSerial;
//		this.servicePid;
		
		this.deviceId = this.knxDeviceProperties.getGroupAddress();
		
		// add device to deviceList in knx.networkdriver
		this.network.addDevice(this.deviceId, this);
		
		this.logger.log(LogService.LOG_INFO, "Registered device " + deviceId + " in knx.networkdriver.");
	}

	/** store a driver reference for this device */
	public void addDriver(KnxBaseDeviceCategory driverInstance) {
		this.driver = driverInstance;
	}
	

	/** remove the driver reference of this device */
	public void removeDriver() {
		this.driver = null;
	}

	
	/**
	 * The specific devices have to implement this method to receive low level messages from the network
	 * @param deviceAddress  address of the device or the group that fire the message
	 * @param message array of byte containing the information of the status or command
	 */
	public abstract void newMessageFromHouse(String deviceAddress, byte event);

	
	public void noDriverFound() {
		this.logger.log(LogService.LOG_WARNING, "No suitable drivers were found for KNX device: " +
				knxDeviceProperties.getGroupAddress() );
	}
	
	/**
	 * @return knGroupAddress as String "M/S/D"
	 */
	public String getGroupAddress() {
		return this.knxDeviceProperties.getGroupAddress();
	}
	
	/**
	 * @return knGroupAddress as String "M/S/D"
	 */
	public String getDatapointType() {
		return this.knxDeviceProperties.getDpt();
	}
	
	/**
	 * @return the deviceCategory
	 */
	public String getDeviceCategory() {
		return deviceCategory;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @return the deviceDescription
	 */
	public String getDeviceDescription() {
		return deviceDescription;
	}

	/**
	 * @return the deviceSerial
	 */
	public String getDeviceSerial() {
		return deviceSerial;
	}

	/**
	 * @return the servicePid
	 */
	public String getServicePid() {
		return servicePid;
	}


}

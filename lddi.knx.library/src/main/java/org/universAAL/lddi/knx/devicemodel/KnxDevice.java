package org.universAAL.lddi.knx.devicemodel;

import org.osgi.service.device.Device;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory;
import org.universAAL.lddi.knx.networkdriver.KnxNetwork;
import org.universAAL.lddi.knx.utils.*;

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
	/** including groupAddress and dpt */ 
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

		this.deviceCategory = KNX_DEVICE_CATEGORY_PREFIX + this.knxDeviceProperties.getDptMain();
		
//		this.deviceDescription;
//		this.deviceSerial;
//		this.servicePid;
		
		this.deviceId = this.knxDeviceProperties.getGroupAddress();
		
		// add device to deviceList in knx.networkdriver
		this.network.addDevice(this.deviceId, this);
		
		this.logger.log(LogService.LOG_DEBUG, "Registered device " + deviceId + " in knx.networkdriver.");
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
	 * @return knxGroupAddress as String "M/S/D"
	 */
	public String getGroupAddress() {
		return this.knxDeviceProperties.getGroupAddress();
	}
	
	/**
	 * @return knx datapoint type as String "M.mmm"
	 */
	public String getDatapointType() {
		return this.knxDeviceProperties.getDpt();
	}
	
	/**
	 * Returns 0 on error
	 * {@code for dpt "1.018" this method will return 18}
	 * @return knx datapoint main type
	 */
	public int getDatapointTypeMainNumber() {
		try {
			return Integer.parseInt(this.knxDeviceProperties.getDptMain());
		} catch (NumberFormatException e) {
			this.logger.log(LogService.LOG_ERROR, "Error on converting main datatype number of knx device " +
					this.deviceId);
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * Returns 0 on error
	 * {@code for dpt "1.018" this method will return 18}
	 * @return knx datapoint minor type
	 */
	public int getDatapointTypeSubNumber() {
		try {
			return Integer.parseInt(this.knxDeviceProperties.getDptSub());
		} catch (NumberFormatException e) {
			this.logger.log(LogService.LOG_ERROR, "Error on converting minor datatype number of knx device " +
					this.deviceId + " with dpt " + this.getDatapointType());
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @return name of the location, e.g. Main room, Living room
	 */
	public String getDeviceLocation() {
		return this.knxDeviceProperties.getBpName();
	}

	/**
	 * @return type of the location, e.g. Floor, Room, Corridor, Cabinet, Stairway or Building Part
	 */
	public String getDeviceLocationType() {
		return this.knxDeviceProperties.getBpType();
	}

	/**
	 * @return description of the location, e.g. Room, Corridor
	 */
	public String getDeviceLocationDescription() {
		return this.knxDeviceProperties.getBpDescription();
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

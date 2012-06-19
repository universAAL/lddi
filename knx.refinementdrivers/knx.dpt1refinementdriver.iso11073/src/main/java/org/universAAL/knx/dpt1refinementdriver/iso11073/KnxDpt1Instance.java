package org.universAAL.knx.dpt1refinementdriver.iso11073;

import java.util.Dictionary;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil;
import org.universAAL.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.iso11073.activityhub.devicemodel.ActivityHubFactory;
import org.universAAL.iso11073.activityhub.devicemodel.ActivityHubSensor;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;
import org.universAAL.knx.devicecategory.KnxDpt1;
import org.universAAL.knx.devicedriver.KnxDriver;
import org.universAAL.knx.devicemodel.KnxDpt1Device;

/**
 * Working instance of the KnxDpt1 driver. Registers a service/device.
 * Tracks on the KNX device service passed in the attach method in KnxDpt1RefinementDriver class. 
 * When the KNX device disappears, this service/device is unregistered.
 * 
 * This driver handles 1-bit events (knx datapoint 1), which is on/off.
 * It maps to the appropriate sensor-event of the created ISO11073 sensor. 
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Instance extends KnxDriver implements KnxDpt1
,ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;
//	private Dictionary knxIsoMappingProperties;
	
	// my mapping ISO device; exactly one
	private ActivityHubSensor activityHubSensor;
	
//	private Dictionary<String,String> knxIsoMappingProperties;
	
	// the ISO device I registered at OSGi registry
	private ServiceRegistration myIsoDeviceRegistration;
	
	private ActivityHubDeviceCategory isoDeviceCategory = null;
	
	// constructor called from the driver.attach method
	public KnxDpt1Instance(BundleContext c, LogService log, ActivityHubDeviceCategory ahDevCat) {
		this.context = c;
		this.logger = log;
		this.isoDeviceCategory = ahDevCat;
		
//		// test
//		this.logger.log(LogService.LOG_WARNING, "CHECK default values: False: " + 
//				String.format("%02X", DEFAULT_FALSE_VALUE) + 
//				" True: " + String.format("%02X", DEFAULT_TRUE_VALUE));
		
		//this.knxIsoMappingProperties = knxIsoMappingProperties;
	}


//	/** set my KNX-to-ISO device mapping properties */
//	public void setKnxIsoMappingProperties(Dictionary dictionary) {
//		this.knxIsoMappingProperties = dictionary;
////		this.knxIsoMappingProperties = new Properties();
//	}
	
	
	/**
	 * track on my device
	 * @param KnxDpt1Device
	 */
	public Object addingService(ServiceReference reference) {
		
		KnxDpt1Device knxDev = (KnxDpt1Device) this.context.getService(reference);

		
		/** now couple my driver to the device */
		if ( this.setDevice(knxDev) )
			this.logger.log(LogService.LOG_INFO, "Successfully coupled " + KnxDpt1.MY_DEVICE_CATEGORY 
					+ " driver to device " + this.device.getGroupAddress());
		else {
			this.logger.log(LogService.LOG_ERROR, "Error coupling " + KnxDpt1.MY_DEVICE_CATEGORY
					+ " driver to device " + this.device.getGroupAddress() + ". No appropriate " +
					"ISO device created!");
			return null;
		}
		
		// TODO: Regeln für die decodierung in device_category ??
		
		
		/** create appropriate ISO device */
		if ( this.isoDeviceCategory != null ) {

//			this.logger.log(LogService.LOG_INFO, "KNX-ISO mapping config: " + this.knxIsoMappingProperties);

//			// get knx-iso mapping properties for my device according to groupAddress
//			String isoDeviceType = (String) this.knxIsoMappingProperties.get("isoDeviceType");
//			ActivityHubDeviceCategory isoDeviceCategory = ActivityHubDeviceCategoryUtil.
//				toActivityHubDevice(isoDeviceType);
			
			if ( this.device.getGroupAddress() != null ) {
				// isoDeviceType configuration found
				
				this.logger.log(LogService.LOG_INFO, "KNX to ISO mapping parameter found for " +
						"device " + this.device.getGroupAddress() + " with KNX datapoint type " +
						this.device.getDatapointType() + " : " +isoDeviceCategory);

//				// check deviceLocation property
//				String loc = (String) this.knxIsoMappingProperties.get("deviceLocation");
//				ActivityHubLocation isoDeviceLocation = ActivityHubLocationUtil.
//					toActivityHubLocation(loc);
//				if (isoDeviceLocation == null) {
//					this.logger.log(LogService.LOG_WARNING, "Location for KNX device " +
//							this.device.getGroupAddress() + " not found!");
//				}
				
				//
				// TODO store location type and description from ETS export in KnxDevice
				//
				
				// create appropriate ActivityHub device
				this.activityHubSensor = ActivityHubFactory.createInstance(
						isoDeviceCategory,
//						isoDeviceLocation,
						null, // this might cause an error on uAAL middleware service calling
						this.device.getGroupAddress(),this.logger);
				if (this.activityHubSensor==null) {
					this.logger.log(LogService.LOG_ERROR, "Error on creating ActivityHubSensor " +
							" for device category: " + isoDeviceCategory + " with deviceId: " + 
							this.device.getGroupAddress());
					return null;
				}
				
//				// set instance alive
//				// use knx group address for now as device ID !
//				ahd.setParams(isoDeviceType,this.device.getGroupAddress(),this.logger);
				
				// register AH device in OSGi registry
				Properties propDeviceService=new Properties();

				// use properties value as device category
				propDeviceService.put(
						org.osgi.service.device.Constants.DEVICE_CATEGORY, isoDeviceCategory.toString());
				// more possible properties from OSGi: description, serial, id
				
				this.myIsoDeviceRegistration = this.context.registerService(
						org.osgi.service.device.Device.class.getName(), this.activityHubSensor, 
						propDeviceService);
				
				this.logger.log(LogService.LOG_INFO, "Registered ISO device " +
						this.activityHubSensor.getDeviceId() + " in OSGi registry under " +
						"device category: " + isoDeviceCategory);
				
			} else {
				String s1 = "KNX group address is null for device " + this.device.getGroupAddress();
				this.logger.log(LogService.LOG_ERROR, s1);
				throw new NullPointerException(s1);
			}
		} else {
			String s2 = "No configuration parameter found for Knx to ISO mapping for device " +
				this.device.getGroupAddress() + "; isoDeviceType=null";
			this.logger.log(LogService.LOG_ERROR, s2);
			throw new NullPointerException(s2);
		}
		return knxDev;
	}

	
	/**
	 * @see org.universAAL.knx.devicecategory.KnxDpt1#newMessageFromKnxBus(byte[])
	 * got new message from knx bus
	 * pass to ISO device
	 */
	public void newMessageFromKnxBus(byte event) {
		// try to display event byte readable. No good: Byte.toString(byte), Integer.toHexString(byte)
		this.logger.log(LogService.LOG_INFO, "Driver " + KnxDpt1.MY_DEVICE_CATEGORY + " for device " + 
				this.device.getGroupAddress() + " with knx datapoint type " + this.device.getDatapointType() +
				" received new knx message " + String.format("%02X", event));

		if (this.activityHubSensor != null){
		
//			// map on dpt!!
//			String dptString = "_" + this.device.getDatapointType().replace('.', '_');
//			this.logger.log(LogService.LOG_INFO, "Datapoint type String: " + dptString); 
//			
//			String constName = KnxDpt1.DEFAULT_VALUE_ON
//			
			if ( event == DEFAULT_VALUE_OFF ) {
				this.logger.log(LogService.LOG_INFO, "Event matches to DEFAULT_VALUE_OFF");
				this.activityHubSensor.setSensorEventOff();
			} else if ( event == DEFAULT_VALUE_ON ) {
				this.logger.log(LogService.LOG_INFO, "Event matches to DEFAULT_VALUE_ON");
				this.activityHubSensor.setSensorEventOn();
			} else {
				this.logger.log(LogService.LOG_ERROR, "No matches on incoming Event " + Integer.toHexString(event) +
						" from device " + this.device.getGroupAddress());
				return;
			}
		} else {
			this.logger.log(LogService.LOG_ERROR, "Driver " + KnxDpt1.MY_DEVICE_CATEGORY + " for device " + 
					this.device.getGroupAddress() + " lost its mapping ISO11073 sensor! " +
							"Cannot forward incoming message!");
		}
	}

	/**
	 * @param reference device service
	 */
	public void removedService(ServiceReference reference, Object service) {
		// removed device service
		this.context.ungetService(reference);
		this.detachDriver();
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked knx device service was modified. Going to update the KnxDpt1Instance");
		removedService(reference, service);
		addingService(reference);			
	}


	/**
	 * Managed associated ISO devices according to configuration change.
	 * Maybe destroy old ISO object and create new one.
	 * 
	 * @param properties 
	 * @return true if successful
	 */
	public boolean updateConfiguration(Dictionary properties) {
		// TODO Auto-generated method stub
		return true;
	}

}

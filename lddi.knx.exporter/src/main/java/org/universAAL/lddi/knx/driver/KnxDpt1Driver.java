package org.universAAL.lddi.knx.driver;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil;
import org.universAAL.lddi.knx.devicecategory.KnxDpt1;
import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;
import org.universAAL.lddi.knx.interfaces.KnxDriverClient;
import org.universAAL.lddi.knx.devicemodel.KnxDpt1Device;

/**
 * This Driver class manages driver instances for KNX DPT1 devices.
 * It is called on new device references coming from OSGi DeviceManager; matching on device category.
 * It instantiates drivers for every matching KNX device.
 * Attaches exactly one driver instance per deviceId.
 * Subsequent devices with the same deviceId are rejected!
 * 
 * When an attached device service is unregistered:
 * drivers must take the appropriate action to release this device service
 * and perform any necessary cleanup, as described in their device category spec.
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Driver implements Driver {

	private KnxDriverClient client;
	private BundleContext context;
	private LogService logger;
	private ServiceRegistration regDriver;

	private static final String MY_DRIVER_ID = "org.universAAL.knx.dpt1.0.0.1";
	private static final KnxDeviceCategory MY_KNX_DEVICE_CATEGORY = KnxDeviceCategory.KNX_DPT_1; 

	/**
	 * Management Map of instantiated driver instances.
	 * Key is groupAddress of the KNX device
	 * Value is the associated driver
	 */
	private final Map<String, KnxDpt1Instance> connectedDriverInstanceMap = 
		new ConcurrentHashMap<String, KnxDpt1Instance>();
	
	
	/**
	 * @param knxManager
	 * @param context
	 */
	public KnxDpt1Driver(KnxDriverClient client, BundleContext context) {
		this.client=client;
		this.context=context;
		this.logger=client.getLogger();
		
		this.registerDriver();
	}

	
	/** register this driver in OSGi */
	private void registerDriver() {
		Properties propDriver = new Properties();
		propDriver.put(Constants.DRIVER_ID, MY_DRIVER_ID);
		this.regDriver=this.context.registerService(Driver.class.getName(), this, 
				propDriver);
		
		if ( this.regDriver != null )
			this.logger.log(LogService.LOG_INFO, "Driver for KNX-DPT 1.001 registered!");
	}
	

	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#match(org.osgi.framework.ServiceReference)
	 */
	public int match(ServiceReference reference) throws Exception {
		// reference = device service
		int matchValue = Device.MATCH_NONE;
		KnxDeviceCategory deviceCategory = null;
		
		try {
			deviceCategory = KnxDeviceCategoryUtil.toKnxDevice(
					(String)reference.getProperty(Constants.DEVICE_CATEGORY)	);
		} catch (ClassCastException e) {
			this.logger.log(LogService.LOG_DEBUG,"Could not cast DEVICE_CATEGORY of requesting"
					+ " device service " + reference.getProperty(org.osgi.framework.Constants.SERVICE_ID)
					+ " to String. No match!");
			return matchValue;
		}
		
		
		// match check
		// more possible properties to match: description, serial, id
		if ( deviceCategory == MY_KNX_DEVICE_CATEGORY ) {
			matchValue = KnxDpt1.MATCH_CLASS;
		} else {
			this.logger.log(LogService.LOG_DEBUG, "Requesting device service " + deviceCategory +
					" doesn't match with driver. No match!");
		}
		
		return matchValue; //must be > 0 to match
	}

	
	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#attach(org.osgi.framework.ServiceReference)
	 */
	public String attach(ServiceReference reference) throws Exception {
		// get groupAddress
		KnxDpt1Device knxDev = (KnxDpt1Device) this.context.getService(reference);

		if  ( this.connectedDriverInstanceMap.containsKey(knxDev.getGroupAddress()) ) {
			this.logger.log(LogService.LOG_WARNING, "There is already a driver instance available for " +
					" the device " + knxDev.getGroupAddress());
			return "driver already exists for this device!";
		}
		
		// create "driving" instance
		KnxDpt1Instance instance = new KnxDpt1Instance(this.context, this.client, this.logger);

		// store instance
		this.connectedDriverInstanceMap.put(knxDev.getGroupAddress(), instance);

		// init service tracker; the driver instance itself tracks on the device reference!
		ServiceTracker tracker = new ServiceTracker(this.context, reference, instance);
		tracker.open();

		return null;
	}

	
	/**
	 * delete instance references 
	 * unregister my services ?
	 */
	public void stop() {
		
		// delete instance references !!
		for ( Iterator<KnxDpt1Instance> it = this.connectedDriverInstanceMap.values().iterator(); it.hasNext(); ) {
			it.next().detachDriver();
		}

		// the managed service and driver service is unregistered automatically by the OSGi framework!
	}
}

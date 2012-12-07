package org.universAAL.lddi.knx.exporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.devicecategory.KnxDeviceCategoryUtil.KnxDeviceCategory;
import org.universAAL.lddi.knx.devicedriver.KnxDriver;
import org.universAAL.lddi.knx.devicedriver.KnxDriverClient;
import org.universAAL.lddi.knx.driver.KnxDpt9Driver;
import org.universAAL.lddi.knx.exporter.util.LogTracker;

/**
 * Instantiates KNX drivers from KNX library.
 * The drivers call back and register themselves in the driverList.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxManager implements KnxDriverClient {

	private BundleContext context;
	private LogService logger;
	
    private ArrayList<KnxContextPublisher> listeners = new ArrayList<KnxContextPublisher>();

	/**
	 * stores the activityHubInstance (there should be just one!) for each deviceId.
	 * 
	 * key = deviceId
	 * value = ActivityHubDriver
	 */
	private Map<String, KnxDriver> driverList;
	
//	not needed yet!
//	/**
//	 * stores knxInstances for deviceCategories. 
//	 * I.e. there may be many driver instance for type KnxDpt1. 
//	 * 
//	 * key = KNX datapoint type (i.e. 9.001)
//	 * value = ActivityHubDriver
//	 */
//	private Hashtable<String, Set<KnxDriver>> driverListForCategory;

	
	/**
	 * Constructor
	 * @param context
	 * @param logTracker
	 */
	public KnxManager(BundleContext context, LogTracker logTracker) {
		this.context = context;
		this.logger = logTracker;
		
		this.driverList = new TreeMap<String, KnxDriver>();
		
		// start all KNX drivers
		new KnxDpt9Driver(this, this.context);
	}

	/**
	 * Just passing the incoming sensor value to uAAL-MW related class (-> context provider).
	 * No storage of event here!
	 * 
	 * @param deviceGroupAddress (e.g. knx group address 1/2/3)
	 * @param datapointType (i.e. 9.001)
	 * @param value (i.e. temperature value)
	 * 
	 * @see org.universAAL.lddi.knx.devicedriver.KnxDriverClient#incomingSensorEvent(java.lang.String, org.universAAL.lddi.knx.devicecategory.KnxBaseDeviceCategory, int)
	 */
	public void incomingSensorEvent(String deviceGroupAddress, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, float value) {
		
		this.logger.log(LogService.LOG_INFO, "Client received sensor event: " + value);
		
		for (Iterator<KnxContextPublisher> i = listeners.iterator(); i.hasNext();)
			((KnxContextPublisher) i.next()).publishKnxEvent(deviceGroupAddress, 
					datapointTypeMainNubmer, datapointTypeSubNubmer, value);

	}

	/** {@inheritDoc} */
	public void addDriver(String deviceId, KnxDeviceCategory knxDeviceCategory, KnxDriver knxDriver) {

		KnxDriver oldDriver = null;
		synchronized(this.driverList)
		{
			oldDriver = this.driverList.put(deviceId, knxDriver);
//			knxDriver.getDevice().getDeviceCategory().getTypeCode();
		}
		if ( oldDriver != null ){
			this.logger.log(LogService.LOG_WARNING, "An existing KNX driver " +
					"is now replaced by a new one for device " + deviceId
					+ " and category: " + knxDeviceCategory);
		}
		
		this.logger.log(LogService.LOG_INFO, "new KNX driver added for device " + deviceId
				+ " and category: " + knxDeviceCategory);
	}
	
	/** {@inheritDoc} */
	public void removeDriver(String deviceId, KnxDriver knxDriver) {
		this.driverList.remove(deviceId);
		this.logger.log(LogService.LOG_INFO, "removed KNX driver for " +
				"device " + deviceId);
	}

	/**
	 * store listener for context bus connection.
	 * @param knxContextPublisher
	 */
	public void addListener(KnxContextPublisher knxContextPublisher) {
		listeners.add(knxContextPublisher);
	}

	/**
	 * remove listener for context bus connection.
	 * @param knxContextPublisher
	 */
	public void removeListener(KnxContextPublisher knxContextPublisher) {
		listeners.remove(knxContextPublisher);
	}
	
	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicedriver.KnxDriverClient#getLogger()
	 */
	public LogService getLogger() {
		return this.logger;
	}

}

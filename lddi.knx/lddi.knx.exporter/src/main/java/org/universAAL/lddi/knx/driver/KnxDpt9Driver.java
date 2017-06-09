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
import org.universAAL.lddi.knx.groupdevicecategory.IKnxDpt9;
import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil;
import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;
import org.universAAL.lddi.knx.groupdevicemodel.KnxDpt9GroupDevice;
import org.universAAL.lddi.knx.interfaces.IKnxDriverClient;

/**
 * This Driver class manages driver instances for KNX DPT9 devices. It is called
 * on new device references coming from OSGi DeviceManager; matching on device
 * category. It instantiates drivers for every matching KNX groupDevice.
 * Attaches exactly one driver instance per deviceId. Subsequent devices with
 * the same deviceId are rejected!
 * 
 * When an attached device service is unregistered: drivers must take the
 * appropriate action to release this device service and perform any necessary
 * cleanup, as described in their groupDevice category spec.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9Driver implements Driver {

	public IKnxDriverClient client;
	public BundleContext context;
	public LogService logger;
	private ServiceRegistration regDriver;

	private static final String MY_DRIVER_ID = "org.universAAL.knx.dpt9.0.0.1";
	private static final KnxGroupDeviceCategory MY_KNX_DEVICE_CATEGORY = KnxGroupDeviceCategory.KNX_DPT_9;
	// "IKnxDpt9";

	/**
	 * Management Map of instantiated driver instances. Key is groupAddress of
	 * the KNX groupDevice Value is the associated driver
	 */
	public final Map<String, KnxDpt9Instance> connectedDriverInstanceMap = new ConcurrentHashMap<String, KnxDpt9Instance>();

	/**
	 * @param knxManager
	 * @param context
	 */
	public KnxDpt9Driver(IKnxDriverClient client, BundleContext context) {
		this.client = client;
		this.context = context;
		this.logger = client.getLogger();

		this.registerDriver();
	}

	/** register this driver in OSGi */
	private void registerDriver() {
		Properties propDriver = new Properties();
		propDriver.put(Constants.DRIVER_ID, MY_DRIVER_ID);
		this.regDriver = this.context.registerService(Driver.class.getName(), this, propDriver);

		if (this.regDriver != null)
			this.logger.log(LogService.LOG_INFO, "Driver for KNX-DPT 9.001 registered!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.device.Driver#attach(org.osgi.framework.
	 * ServiceReference)
	 */
	public String attach(ServiceReference reference) throws Exception {
		// get groupAddress
		KnxDpt9GroupDevice knxDev = (KnxDpt9GroupDevice) this.context.getService(reference);

		if (this.connectedDriverInstanceMap.containsKey(knxDev.getGroupAddress())) {
			this.logger.log(LogService.LOG_WARNING, "There is already a driver instance available for "
					+ " the groupDevice " + knxDev.getGroupAddress());
			return "driver already exists for this groupDevice!";
		}

		// create "driving" instance
		KnxDpt9Instance instance = new KnxDpt9Instance(this);

		// store instance
		this.connectedDriverInstanceMap.put(knxDev.getGroupAddress(), instance);

		// init service tracker; the driver instance itself tracks on the
		// groupDevice reference!
		ServiceTracker tracker = new ServiceTracker(this.context, reference, instance);
		tracker.open();

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.service.device.Driver#match(org.osgi.framework.ServiceReference)
	 */
	public int match(ServiceReference reference) throws Exception {
		// reference = groupDevice service
		int matchValue = Device.MATCH_NONE;
		KnxGroupDeviceCategory groupDeviceCategory = null;

		try {
			groupDeviceCategory = KnxGroupDeviceCategoryUtil
					.getCategory((String) reference.getProperty(Constants.DEVICE_CATEGORY));
		} catch (ClassCastException e) {
			this.logger.log(LogService.LOG_DEBUG, "Could not cast DEVICE_CATEGORY of requesting" + " device service "
					+ reference.getProperty(org.osgi.framework.Constants.SERVICE_ID) + " to String. No match!");
			return matchValue;
		}

		// match check
		// more possible properties to match: description, serial, id
		if (groupDeviceCategory == MY_KNX_DEVICE_CATEGORY) {
			matchValue = IKnxDpt9.MATCH_CLASS;
		} else {
			this.logger.log(LogService.LOG_DEBUG,
					"Requesting device service " + groupDeviceCategory + " doesn't match with driver. No match!");
		}

		return matchValue; // must be > 0 to match
	}

	/**
	 * delete instance references unregister my services ?
	 */
	public void stop() {

		// delete instance references !!
		for (Iterator<KnxDpt9Instance> it = this.connectedDriverInstanceMap.values().iterator(); it.hasNext();) {
			it.next().detachDriver();
		}

		// the managed service and driver service is unregistered automatically
		// by the OSGi framework!
	}
}

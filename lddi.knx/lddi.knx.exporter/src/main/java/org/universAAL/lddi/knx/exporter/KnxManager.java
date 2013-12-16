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

package org.universAAL.lddi.knx.exporter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.groupdevicecategory.KnxGroupDeviceCategoryUtil.KnxGroupDeviceCategory;
import org.universAAL.lddi.knx.interfaces.KnxDriver;
import org.universAAL.lddi.knx.interfaces.IKnxDriverClient;
import org.universAAL.lddi.knx.driver.KnxDpt1Driver;
import org.universAAL.lddi.knx.driver.KnxDpt1Instance;
import org.universAAL.lddi.knx.driver.KnxDpt3Driver;
import org.universAAL.lddi.knx.driver.KnxDpt5Driver;
import org.universAAL.lddi.knx.driver.KnxDpt5Instance;
import org.universAAL.lddi.knx.driver.KnxDpt9Driver;
import org.universAAL.lddi.knx.driver.KnxDpt9Instance;
import org.universAAL.lddi.knx.exporter.util.LogTracker;

/**
 * Instantiates KNX drivers from KNX library.
 * The drivers call back and register themselves in the driverList.
 * Just passing the incoming sensor value to uAAL-MW related class (-> context publisher)
 * and vice versa, from uAAL service provider to KNX driver.
 * No storage of events in this class!
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxManager implements IKnxDriverClient {

	private BundleContext context;
	private LogService logger;
	
    private ArrayList<KnxContextPublisher> contextListeners = new ArrayList<KnxContextPublisher>();
    private ArrayList<KnxServiceCallee> serviceListeners = new ArrayList<KnxServiceCallee>();

	/**
	 * stores the knxInstance (there should be just one!) for each groupDeviceId.
	 * 
	 * key = groupDeviceId
	 * value = KnxDriver
	 */
	private Map<String, KnxDriver> driverList;

	/**
	 * @return the driverList
	 */
	public Map<String, KnxDriver> getDriverList() {
		return driverList;
	}
	
//	not needed yet!
//	/**
//	 * stores knxInstances for deviceCategories. 
//	 * I.e. there may be many driver instance for type IKnxDpt1. 
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
		new KnxDpt1Driver(this, this.context);
		new KnxDpt3Driver(this, this.context);
		new KnxDpt5Driver(this, this.context);
		new KnxDpt9Driver(this, this.context);
	}


	/**
	 * {@inheritDoc}
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, boolean value) {
		
		this.logger.log(LogService.LOG_INFO, "Client received sensor event: " + value);
		
		for (Iterator<KnxContextPublisher> i = contextListeners.iterator(); i.hasNext();)
			((KnxContextPublisher) i.next()).publishKnxEvent(groupDeviceId, 
					datapointTypeMainNubmer, datapointTypeSubNubmer, value);
		
		if (contextListeners.isEmpty())
			this.logger.log(LogService.LOG_WARNING, "No context providers available for KNX groupDevice " + groupDeviceId);

	}
	/**
	 * {@inheritDoc} 
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void sendSensorEvent(String groupDeviceId, boolean value) {
	
		KnxDriver knxdriver = null;
		synchronized(this.driverList)
		{
			knxdriver = this.driverList.get(groupDeviceId);
		}
		if ( knxdriver == null ){
			this.logger.log(LogService.LOG_WARNING, "No KnxDriver available for " +
					groupDeviceId + ". Cannot forward event " + value + " to KNX bus!");
		} else {
			// call sendMessage on driver
			KnxDpt1Instance driverInstance;
			try {
				driverInstance = (KnxDpt1Instance) knxdriver;
			} catch (ClassCastException e) {
				this.logger.log(LogService.LOG_ERROR, "Datatype DPT1 (boolean) doesn't suit for groupDevice " +
						groupDeviceId + "! Cannot forward event " + value + " to KNX bus!");
				return;
			}
			
			// forward message to driver
			if (driverInstance != null)
				driverInstance.sendMessageToKnxBus(value);
		}
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, String code) {
		
		this.logger.log(LogService.LOG_DEBUG, "Client received sensor event: " + code);
		
		for (Iterator<KnxContextPublisher> i = contextListeners.iterator(); i.hasNext();)
			((KnxContextPublisher) i.next()).publishKnxEvent(groupDeviceId, 
					datapointTypeMainNubmer, datapointTypeSubNubmer, code);

		if (contextListeners.isEmpty())
			this.logger.log(LogService.LOG_WARNING, "No context providers available for KNX groupDevice " + groupDeviceId);

	}
	/**
	 * {@inheritDoc} 
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void sendSensorEvent(String groupDeviceId, String code) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/**
	 * {@inheritDoc}
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void incomingSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, float value) {
		
		this.logger.log(LogService.LOG_DEBUG, "Client received sensor event: " + value);
		
		for (Iterator<KnxContextPublisher> i = contextListeners.iterator(); i.hasNext();)
			((KnxContextPublisher) i.next()).publishKnxEvent(groupDeviceId, 
					datapointTypeMainNubmer, datapointTypeSubNubmer, value);

		if (contextListeners.isEmpty())
			this.logger.log(LogService.LOG_WARNING, "No context providers available for KNX groupDevice " + groupDeviceId);

	}
	/**
	 * {@inheritDoc} 
	 * @see org.universAAL.lddi.knx.IKnxDriverClient.KnxDriverClient
	 */
	public void sendSensorEvent(String groupDeviceId, int datapointTypeMainNubmer, 
			int datapointTypeSubNubmer, float value) {
		
		KnxDriver knxdriver = null;
		synchronized(this.driverList)
		{
			knxdriver = this.driverList.get(groupDeviceId);
		}
		if ( knxdriver == null ){
			this.logger.log(LogService.LOG_WARNING, "No KnxDriver available for " +
					groupDeviceId + ". Cannot forward event " + value + " to KNX bus!");
		} else {
			// switch on main datapoint type
			switch (knxdriver.groupDevice.getDatapointTypeMainNumber()) {
			case 5:
				try {
					// forward message to driver
					((KnxDpt5Instance) knxdriver).sendMessageToKnxBus(value);
				
				} catch (ClassCastException e) {
					this.logger.log(LogService.LOG_ERROR, "Datatype DPT5 (8-bit unsigned int) " +
							"doesn't suit for groupDevice " +
							groupDeviceId + "! Cannot forward event " + value + " to KNX bus!");
				}
				return;
			case 9:
				try {
					// forward message to driver
					((KnxDpt9Instance) knxdriver).sendMessageToKnxBus(value);
				} catch (ClassCastException e) {
					this.logger.log(LogService.LOG_ERROR, "Datatype DPT9 (2-byte float value) " +
							"doesn't suit for groupDevice " +
							groupDeviceId + "! Cannot forward event " + value + " to KNX bus!");
				}
				return;
			default:
				this.logger.log(LogService.LOG_ERROR, "Neither datapoint type 5 (8-bit unsigned int) " +
						"nor dpt 9 (2-byte float value) found for groupDevice " +
						groupDeviceId + "! Cannot forward event " + value + " to KNX bus!");
				return;
			}
		}
	}
	
	
	
	
	/** {@inheritDoc} */
	public void addDriver(String groupDeviceId, KnxGroupDeviceCategory knxGroupDeviceCategory, KnxDriver knxDriver) {

		KnxDriver oldDriver = null;
		synchronized(this.driverList)
		{
			//System.out.println("###########add driver to driver list " + groupDeviceId);
			oldDriver = this.driverList.put(groupDeviceId, knxDriver);
//			knxDriver.getDevice().getDeviceCategory().getTypeCode();
		}
		if ( oldDriver != null ){
			this.logger.log(LogService.LOG_WARNING, "An existing KNX driver " +
					"is now replaced by a new one for groupDevice " + groupDeviceId
					+ " and category: " + knxGroupDeviceCategory);
		}
		
		this.logger.log(LogService.LOG_INFO, "new KNX driver added for groupDevice " + groupDeviceId
				+ " and category: " + knxGroupDeviceCategory);
	}
	
	/** {@inheritDoc} */
	public void removeDriver(String groupDeviceId, KnxDriver knxDriver) {
		this.driverList.remove(groupDeviceId);
		this.logger.log(LogService.LOG_INFO, "removed KNX driver for " +
				"groupDevice " + groupDeviceId);
	}

	/**
	 * store listener for context bus connection.
	 * @param knxContextPublisher
	 */
	public void addContextListener(KnxContextPublisher knxContextPublisher) {
		contextListeners.add(knxContextPublisher);
	}

	/**
	 * remove listener for context bus connection.
	 * @param knxContextPublisher
	 */
	public void removeContextListener(KnxContextPublisher knxContextPublisher) {
		contextListeners.remove(knxContextPublisher);
	}
	
	
	/**
	 * store listener for service bus connection.
	 * @param knxServiceCallee
	 */
	public void addServiceProvider(KnxServiceCallee knxServiceCallee) {
		serviceListeners.add(knxServiceCallee);
	}

	/**
	 * remove listener for service bus connection.
	 * @param knxServiceCallee
	 */
	public void removeServiceProvider(KnxServiceCallee knxServiceCallee) {
		serviceListeners.remove(knxServiceCallee);
	}
	

	/* (non-Javadoc)
	 * @see org.universAAL.lddi.knx.devicedriver.KnxDriverClient#getLogger()
	 */
	public LogService getLogger() {
		return this.logger;
	}
}

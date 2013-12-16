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
import java.util.Map.Entry;

import org.universAAL.lddi.knx.exporter.KnxToDeviceOntologyMappingFactory.DeviceOntologyType;
import org.universAAL.lddi.knx.interfaces.KnxDriver;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.ontology.device.LightController;
import org.universAAL.ontology.device.ValueDevice;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxServiceCallee extends ServiceCallee {

	static final String DEVICE_URI_PREFIX = 
		KnxServiceCalleeProvidedService.KNX_SERVER_NAMESPACE + "controlledDevice";
	
	private ArrayList<ValueDevice> controlledDevices = new ArrayList<ValueDevice>(); // will be filled in the constructor
	
	private static final ServiceResponse invalidInput
	= new ServiceResponse(CallStatus.serviceSpecificFailure);
	static {
		invalidInput.addOutput(
				new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invalid input!"));
	}
	
	/* constantly sends events to the KNX bus for communication testing */
	private MyThread testThread;
	private static KnxManager knxManager;

	private Map<String,KnxDriver> drivers;
	
	/**
	 * @param mc
	 * @param knxManager
	 */
	public KnxServiceCallee(ModuleContext mc, KnxManager knxManager) {

		/** register my services on uAAL service bus */
		super(mc, KnxServiceCalleeProvidedService.profiles);

		this.knxManager = knxManager;

//		devices are not ready at constructor time! init later on service call!	
//		// init controlledDevices
//		drivers = knxManager.getDriverList();
//		System.out.println("##############################driverlist.size: " + drivers.size());
//		for (Entry<String, KnxDriver> e : drivers.entrySet()) {
//			String deviceName = e.getKey();
//			KnxDriver driver = e.getValue();
//			
//			ValueDevice vd = KnxToDeviceOntologyMappingFactory.getDeviceOntologyInstanceForKnxDpt(
//					driver.groupDevice.getDatapointTypeMainNumber(),
//					driver.groupDevice.getDatapointTypeSubNumber(),
//					deviceName, DeviceOntologyType.Controller);
//			System.out.println(deviceName + 
//					driver.groupDevice.getDatapointTypeMainNumber() +
//					driver.groupDevice.getDatapointTypeSubNumber() + vd.getClassURI());
//			controlledDevices.add(vd);
//		}
				
//		for (int i=0; i<4; i++) {
//			LightSource light = new LightSource(LAMP_URI_PREFIX + i);
//			light.setBrightness(0);
//			controlledLamps.add(light);
//			}
		
		
		// start simulator
		// this.testThread = new MyThread();
		// testThread.start();
	}

	
	
	/* (non-Javadoc)
	 * @see org.universAAL.middleware.service.ServiceCallee#communicationChannelBroken()
	 */
	@Override
	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.universAAL.middleware.service.ServiceCallee#handleCall(org.universAAL.middleware.service.ServiceCall)
	 */
	@Override
	public ServiceResponse handleCall(ServiceCall call) {
		//System.out.println("handle Call");
		String operation = call.getProcessURI();
		if (operation.startsWith(KnxServiceCalleeProvidedService.SERVICE_GET_CONTROLLED_DEVICES)) {
			//System.out.println("getControlled devices");
			return getControlledDevices();
		}
		Object input = call.getInputValue(KnxServiceCalleeProvidedService.INPUT_DEVICE_URI);
		if (operation.startsWith(KnxServiceCalleeProvidedService.SERVICE_SWITCH_OFF))
			return switchController(input.toString(), false);
		if (operation.startsWith(KnxServiceCalleeProvidedService.SERVICE_SWITCH_ON))
			return switchController(input.toString(), true);
		return null;
	}
	
	private ServiceResponse getControlledDevices() {
		
		// init controlledDevices
		drivers = knxManager.getDriverList();
		//System.out.println("##############################driverlist.size: " + drivers.size());
		for (Entry<String, KnxDriver> e : drivers.entrySet()) {
			String deviceName = e.getKey();
			KnxDriver driver = e.getValue();
//			System.out.println("######" + deviceName + 
//					driver.groupDevice.getDatapointTypeMainNumber() +
//					driver.groupDevice.getDatapointTypeSubNumber());
			ValueDevice vd = KnxToDeviceOntologyMappingFactory.getDeviceOntologyInstanceForKnxDpt(
					driver.groupDevice.getDatapointTypeMainNumber(),
					driver.groupDevice.getDatapointTypeSubNumber(),
					deviceName, DeviceOntologyType.Controller);
			if (vd == null) {
				//System.out.println("#########No mapping ontology class found");
			} else {
				//System.out.println("#####" + vd.getClassURI());
				controlledDevices.add(vd);
			}
		}
	
		
		
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		sr.addOutput(new ProcessOutput(KnxServiceCalleeProvidedService.OUTPUT_CONTROLLED_DEVICES,
				controlledDevices.isEmpty() ? null : controlledDevices ));
			//System.out.println("controlledDevices.size: " + controlledDevices.size());
		return sr;
	}
	
	private ServiceResponse switchController(String deviceURI, boolean value) {
		try {
			String deviceName = deviceURI.substring(DEVICE_URI_PREFIX.length());
			
			knxManager.sendSensorEvent(deviceName, value);
			
			return new ServiceResponse(CallStatus.succeeded);
		} catch (Exception e) {
			return invalidInput;
		}
	}
	
	
	
	
	
	
	
	/**
	 * Runnable helper class for simulating incoming service requests.
	 * Constantly looping.
	 * 
	 * @author Thomas Fuxreiter (foex@gmx.at)
	 */
	static class MyThread extends Thread {
	    private volatile boolean active = true;
		public MyThread() {	}
		public void run() {
			while (active) {
					try {
						Thread.sleep(10000);
						knxManager.sendSensorEvent("0/0/4", true);
						Thread.sleep(10000);
						knxManager.sendSensorEvent("0/0/4", false);
						
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/0", 5, 1, Float.parseFloat("35"));
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/0", 5, 1, Float.parseFloat("20"));
						
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/1", 5, 1, Float.parseFloat("35"));
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/1", 5, 1, Float.parseFloat("20"));
						
					} catch (InterruptedException e) {
				          System.out.println("Thread interrupted " + e.getMessage());
					}
				
			}
		}
		public void stopThread() {
			active = false;
		}
	}
	
	public void stop() {
		if (testThread != null)
			testThread.stopThread();
	}


	
	
	
}

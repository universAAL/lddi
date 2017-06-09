/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
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

package org.universAAL.lddi.fs20.connect;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.fs20.devicemodel.FS20Device;
import org.universAAL.lddi.fs20.util.LogTracker;

/**
 * This class registers a new FS20 event listener. If a new event is detected it
 * searches the matching registered service and changes the value of the service
 * which is recognized by the fs20.exporter bundle via ServiceEvent.MODIFIED and
 * will converted to an UAAL context event
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20Listener {

	static FHZ1000PC port;
	private static Integer received_houseCode;
	private static Byte received_deviceCode;
	private static byte received_function;

	private String houseCodeAsString;
	private String deviceCodeAsString;
	private String functionAsString;

	private static LogTracker logger;
	private static BundleContext context;

	private HashMap<String, ServiceRegistration> registrations = new HashMap<String, ServiceRegistration>();

	private Dictionary<String, String> dic = new Hashtable<String, String>();

	public FS20Listener(BundleContext context) {
		this.context = context;
	}

	/**
	 * Changes the value of a registered service which conducts to an UAAL
	 * context event
	 * 
	 * @param sr
	 *            = the ServiceRegistration of the matches service
	 */
	private void sendContextEvent(ServiceRegistration sr) {
		if (sr != null) {
			FS20Device device = (FS20Device) context.getService(sr.getReference());

			switch (device.getDeviceType()) {
			case FS20FMS:
				if (functionAsString.equals("1111")) {
					dic.put("value", "0");
					sr.setProperties(dic);
				} else if (functionAsString.equals("1212")) {
					dic.put("value", "1");
					sr.setProperties(dic);
				}
				break;
			case FS20PIRx:
				if (functionAsString.equals("1111")) {
					dic.put("value", "0");
					sr.setProperties(dic);
				} else if (functionAsString.equals("1212")) {
					dic.put("value", "1");
					sr.setProperties(dic);
				}
				break;
			}
		}
	}

	/**
	 * Searches the matching ServiceRegistration to the given housecode and
	 * devicecode
	 * 
	 * @param housecode
	 *            = the given HouseCode of the FS20 device
	 * @param devicecode
	 *            = the given DeviceCode of the FS20 device
	 * @return the matching ServiceRegistration, or null
	 */
	private ServiceRegistration getMatchingDevice(String housecode, String devicecode) {
		FS20Device device;

		for (Iterator<ServiceRegistration> it = registrations.values().iterator(); it.hasNext();) {
			ServiceRegistration sr = it.next();
			device = (FS20Device) context.getService(sr.getReference());
			if ((housecode.equals(device.getHouseCode())) && (devicecode.equals(device.getDeviceCode()))) {
				return sr;
			}
		}
		return null;
	}

	/**
	 * Called by the Activator and sets all ServiceRegistrations
	 * 
	 * @param reg
	 *            = all registered FS20 devices as ServiceRegistrations
	 */
	public void setDevices(HashMap<String, ServiceRegistration> reg) {
		registrations = reg;
	}

	/**
	 * Initializes a new FS20EventListener
	 * 
	 * @param fhz
	 *            = the FS20 gateway connection
	 * @param log
	 *            = the logger
	 */
	public void Init(FHZ1000PC fhz, LogTracker log) {
		try {

			logger = log;

			fhz.registerEventListener(new FS20EventListener() {
				public void fs20Event(FS20Event event) {

					received_houseCode = event.getHouseCode();
					received_deviceCode = event.getButton();
					received_function = event.getFunction();

					houseCodeAsString = binFS20ByteToString(received_houseCode.intValue(), 16);
					deviceCodeAsString = binFS20ByteToString(received_deviceCode.intValue(), 8);
					functionAsString = binFS20ByteToString(new Byte(received_function).intValue(), 8);

					sendContextEvent(getMatchingDevice(houseCodeAsString, deviceCodeAsString));

					logger.log(LogService.LOG_INFO,
							"New FS20 event detectet: " + "HouseCode="
									+ binFS20ByteToString(received_houseCode.intValue(), 16) + " DeviceCode="
									+ binFS20ByteToString(received_deviceCode.intValue(), 8) + " Function="
									+ binFS20ByteToString(new Byte(received_function).intValue(), 8));
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts the houseCode, deviceCode and functionCode to a String object
	 * 
	 * @param value
	 *            = the integer value of a received code
	 * @param bits
	 *            = number of bits
	 * @return returns the received code as a String
	 */
	public static String binFS20ByteToString(int value, int bits) {
		String result = "";
		for (int i = 0; i < bits; i += 2) {
			int bitPair = (value >> (bits - 2 - i)) & 0x03;
			result += (char) (bitPair + '1');
		}
		return result;
	}

}
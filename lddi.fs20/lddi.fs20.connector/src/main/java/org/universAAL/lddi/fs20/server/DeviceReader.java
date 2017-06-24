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

package org.universAAL.lddi.fs20.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.universAAL.lddi.fs20.connect.FHZ1000PC;
import org.universAAL.lddi.fs20.devicemodel.FS20DeviceProperties;
import org.universAAL.lddi.fs20.devicemodel.FS20DeviceTypes;
import org.universAAL.lddi.fs20.devicemodel.FS20RGBSADevice;
import org.universAAL.lddi.fs20.devicemodel.FS20FMSDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20PIRxDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20SIGDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20STDevice;
import org.universAAL.ontology.activityhub.UsageSensor;
import org.universAAL.ontology.av.device.LoudSpeaker;
import org.universAAL.ontology.device.LightActuator;
import org.universAAL.ontology.device.MotionSensor;
import org.universAAL.ontology.device.SwitchActuator;

/**
 * Load all devices out of property files stored in the folder
 * rundir/confadmin/fs20 and register them to OSGi
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class DeviceReader {

	private static HashMap<String, ServiceRegistration> registrations = new HashMap<String, ServiceRegistration>();

	/**
	 * Load all devices out of property files stored in the folder
	 * rundir/confadmin/fs20
	 *
	 * @param context
	 *            = bundle context for registering the devices as services
	 * @param connection
	 *            = the connection to the FS20 network
	 * @return returns all ServiceRegistrations
	 */
	public static HashMap<String, ServiceRegistration> readDevices(BundleContext context, FHZ1000PC connection) {

		try {

			File home = new File("../confadmin/fs20"); // "../confadmin/fs20"

			File[] files = home.listFiles();

			InputStream in;

			for (int i = 0; i < files.length; i++) {
				File tempFile = files[i];

				in = new FileInputStream(tempFile.getPath());
				Properties tempProps = new Properties();
				tempProps.load(in);
				in.close();

				if (tempProps.getProperty("type").equals(FS20DeviceTypes.FS20PIRx.name())) { // "FS20PIRx"

					FS20PIRxDevice device = new FS20PIRxDevice(connection);
					FS20DeviceProperties props = new FS20DeviceProperties(
							MotionSensor.MY_URI + "#" + tempProps.getProperty("housecode")
									+ tempProps.getProperty("devicecode"),
							tempProps.getProperty("name"), tempProps.getProperty("housecode"),
							tempProps.getProperty("devicecode"), FS20DeviceTypes.FS20PIRx);

					device.setParams(props);

					registrations.put(device.getHouseCode() + device.getDeviceCode(),
							context.registerService(FS20PIRxDevice.class.getName(), device, null));
				} else if (tempProps.getProperty("type").equals(FS20DeviceTypes.FS20ST.name())) {

					FS20STDevice device = new FS20STDevice(connection);
					FS20DeviceProperties props = new FS20DeviceProperties(
							SwitchActuator.MY_URI + "#" + tempProps.getProperty("housecode")
									+ tempProps.getProperty("devicecode"),
							tempProps.getProperty("name"), tempProps.getProperty("housecode"),
							tempProps.getProperty("devicecode"), FS20DeviceTypes.FS20ST);

					device.setParams(props);

					registrations.put(device.getHouseCode() + device.getDeviceCode(),
							context.registerService(FS20STDevice.class.getName(), device, null));
				} else if (tempProps.getProperty("type").equals(FS20DeviceTypes.FS20RGBSA.name())) {

					FS20RGBSADevice device = new FS20RGBSADevice(connection);
					FS20DeviceProperties props = new FS20DeviceProperties(
							LightActuator.MY_URI + "#" + tempProps.getProperty("housecode")
									+ tempProps.getProperty("devicecode"),
							tempProps.getProperty("name"), tempProps.getProperty("housecode"),
							tempProps.getProperty("devicecode"), FS20DeviceTypes.FS20RGBSA);

					device.setParams(props);

					device.setDescriptionToAnimation(1, tempProps.getProperty("description1"));
					device.setDescriptionToAnimation(2, tempProps.getProperty("description2"));
					device.setDescriptionToAnimation(3, tempProps.getProperty("description3"));
					device.setDescriptionToAnimation(4, tempProps.getProperty("description4"));
					device.setDescriptionToAnimation(5, tempProps.getProperty("description5"));
					device.setDescriptionToAnimation(6, tempProps.getProperty("description6"));
					device.setDescriptionToAnimation(7, tempProps.getProperty("description7"));
					device.setDescriptionToAnimation(8, tempProps.getProperty("description8"));
					device.setDescriptionToAnimation(9, tempProps.getProperty("description9"));
					device.setDescriptionToAnimation(10, tempProps.getProperty("description10"));
					device.setDescriptionToAnimation(11, tempProps.getProperty("description11"));
					device.setDescriptionToAnimation(12, tempProps.getProperty("description12"));

					registrations.put(device.getHouseCode() + device.getDeviceCode(),
							context.registerService(FS20RGBSADevice.class.getName(), device, null));
				} else if (tempProps.getProperty("type").equals(FS20DeviceTypes.FS20SIG.name())) {

					FS20SIGDevice device = new FS20SIGDevice(connection);
					FS20DeviceProperties props = new FS20DeviceProperties(
							LoudSpeaker.MY_URI + "#" + tempProps.getProperty("housecode")
									+ tempProps.getProperty("devicecode"),
							tempProps.getProperty("name"), tempProps.getProperty("housecode"),
							tempProps.getProperty("devicecode"), FS20DeviceTypes.FS20SIG);

					device.setParams(props);

					registrations.put(device.getHouseCode() + device.getDeviceCode(),
							context.registerService(FS20SIGDevice.class.getName(), device, null));
				} else if (tempProps.getProperty("type").equals(FS20DeviceTypes.FS20FMS.name())) {

					FS20FMSDevice device = new FS20FMSDevice(connection);
					FS20DeviceProperties props = new FS20DeviceProperties(
							UsageSensor.MY_URI + "#" + tempProps.getProperty("housecode")
									+ tempProps.getProperty("devicecode"),
							tempProps.getProperty("name"), tempProps.getProperty("housecode"),
							tempProps.getProperty("devicecode"), FS20DeviceTypes.FS20FMS);

					device.setParams(props);

					registrations.put(device.getHouseCode() + device.getDeviceCode(),
							context.registerService(FS20FMSDevice.class.getName(), device, null));
				}
			}
			return registrations;
		}

		catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}

}

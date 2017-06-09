/*
        Copyright 2007-2014 CNR-ISTI, http://isti.cnr.it
        Institute of Information Science and Technologies
        of the Italian National Research Council

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
package org.universAAL.lddi.zigbee.commissioning;

import it.cnr.isti.thread.Stoppable;
import it.cnr.isti.thread.ThreadUtils;
import it.cnr.isti.zigbee.api.ZigBeeBasedriverException;
import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.cluster.glue.Cluster;
import it.cnr.isti.zigbee.zcl.library.api.core.Attribute;
import it.cnr.isti.zigbee.zcl.library.api.core.ReportListener;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationListener;
import it.cnr.isti.zigbee.zcl.library.impl.attribute.Attributes;
import it.cnr.isti.zigbee.zcl.library.impl.core.AttributeImpl;
import it.cnr.isti.zigbee.zcl.library.impl.core.SubscriptionImpl;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.IASWDCluster;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.ias_wd.SquawkPayloadImpl;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.ias_wd.StartWarningPayloadImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.lddi.zigbee.commissioning.clusters.impl.IASZoneAALImpl;
import org.universAAL.lddi.zigbee.commissioning.clusters.impl.IASZoneClusterAAL;
import org.universAAL.lddi.zigbee.commissioning.clusters.impl.OccupacySensingAALImpl;
import org.universAAL.lddi.zigbee.commissioning.clusters.impl.OccupacySensingClusterAAL;
import org.universAAL.lddi.zigbee.commissioning.devices.api.IAS_ZoneAAL;
import org.universAAL.lddi.zigbee.commissioning.devices.api.OccupancySensorAAL;
import org.universAAL.lddi.zigbee.commissioning.devices.impl.IAS_ZoneDeviceAAL;
import org.universAAL.lddi.zigbee.commissioning.devices.impl.OccupancySensorDeviceAAL;

public class Activator implements BundleActivator, Stoppable, ManagedService {

	private final int IAS_ZONE_CLUSTER_ID = 1280;
	private final int IAS_ACE_CLUSTER_ID = 1281;
	private final short OCCUPANCY_SENSING_CLUSTER_ID = 1030;
	private final int IAS_WD_CLUSTER_ID = 1282;

	private final int IAS_ZONE_DEVICE_ID = 1026;
	private final int IAS_WD_DEVICE_ID = 1027;
	private final int IAS_CIE_DEVICE_ID = 1024;
	private final int IAS_ACE_DEVICE_ID = 1025;
	private final int OCCUPANCY_SENSOR_ID = 263;

	private final short IAS_WD_STOP = 0;
	private final short IAS_WD_BURGLAR = 1;
	private final short IAS_WD_FIRE = 2;
	private final short IAS_WD_EMERGENCY = 3;

	private static BundleContext bc;
	private boolean run = true;

	private static Logger logger;

	private Map<String, Device> ancillary_control_equipment_devices = new HashMap<String, Device>();
	private Map<String, Device> cie_devices = new HashMap<String, Device>();

	private Map<String, Device> ias_warning_devices = new HashMap<String, Device>();
	// private List<IASWarningSensorImpl> ias_warning_devices_sensors = new
	// ArrayList<IASWarningSensorImpl>();
	private List<ServiceRegistration> ias_warning_devices_services = new ArrayList<ServiceRegistration>();

	private Map<String, Device> occupancy_sensor_devices = new HashMap<String, Device>();
	private List<OccupancySensorDeviceAAL> occupancy_sensor_devices_sensors = new ArrayList<OccupancySensorDeviceAAL>();
	private List<ServiceRegistration> occupancy_sensor_devices_services = new ArrayList<ServiceRegistration>();

	private Map<String, Device> ias_zone_devices = new HashMap<String, Device>();
	private List<IAS_ZoneDeviceAAL> ias_zone_sensors = new ArrayList<IAS_ZoneDeviceAAL>();
	private List<ServiceRegistration> ias_zone_sensors_services = new ArrayList<ServiceRegistration>();

	private ZigBeeDevice[] deviceServices = new ZigBeeDevice[1024];
	private ZigBeeDevice coordinator;

	private ServiceRegistration managedService;

	private Thread demo, reset;
	private boolean testReset;

	private volatile long VISIBILITY_TIMEOUT = 120000, SCAN_TIMEOUT = 15000;
	private volatile int PIROccupiedToUnoccupiedDelay = -1, PIRUnoccupiedToOccupiedDelay = -1,
			PIRUnoccupiedToOccupiedThreshold = -1;
	private volatile boolean alarm, squawk;

	private volatile FileWriter fstream;
	private volatile BufferedWriter out;

	public void start(BundleContext bundleContext) throws Exception {

		bc = bundleContext;
		Dictionary dic = new Hashtable();
		dic.put("service.pid", "lddi.zigbee.commissioning.configuration");

		managedService = bc.registerService(ManagedService.class.getName(), this, dic);

		ServiceReference sr = bc.getServiceReference(LoggerFactory.class.getName());
		if (sr != null) {
			try {
				logger = (Logger) bc.getService(sr);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else
			logger = LoggerFactory.getLogger(Activator.class);
	}

	public void run() {

		logger.info("{} STARTED Successfully", Thread.currentThread().getName());

		while (this.run) {
			try {
				if (bc != null) {
					ServiceReference[] srs = bc.getServiceReferences(ZigBeeDevice.class.getName(), null);
					if (srs != null) {
						for (int i = 0; i < srs.length; i++) {
							try {
								deviceServices[i] = (ZigBeeDevice) bc.getService(srs[i]);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
						}
					}

					if (fstream == null)
						fstream = new FileWriter("DEMO.txt");
					if (out == null)
						out = new BufferedWriter(fstream);

					findDemoDevices();
					bindDevices();
					setParametersAndEnableReportingPIR();
					addIASZoneListener();

					if (testReset) {
						Iterator<Entry<String, Device>> it_occ = occupancy_sensor_devices.entrySet().iterator();
						while (it_occ.hasNext()) {
							Entry<String, Device> current = it_occ.next();

							if ((current.getValue().getLastTimeSeen() + VISIBILITY_TIMEOUT) < System
									.currentTimeMillis()) {
								resetDevice(current.getValue());
							}
						}

						it_occ = ias_zone_devices.entrySet().iterator();
						while (it_occ.hasNext()) {
							Entry<String, Device> current = it_occ.next();

							if ((current.getValue().getLastTimeSeen() + VISIBILITY_TIMEOUT) < System
									.currentTimeMillis()) {
								resetDevice(current.getValue());
							}
						}
					}

					ThreadUtils.waitingUntil(System.currentTimeMillis() + SCAN_TIMEOUT);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		logger.info("{} TERMINATED Successfully", Thread.currentThread().getName());
	}

	public void updated(Dictionary newConfig) throws ConfigurationException {

		if (newConfig != null) {

			this.SCAN_TIMEOUT = Long.parseLong(newConfig.get("SCAN_TIMEOUT").toString().trim()) * 1000;

			this.PIROccupiedToUnoccupiedDelay = Integer
					.parseInt(newConfig.get("PIROccupiedToUnoccupiedDelay").toString().trim());
			this.PIRUnoccupiedToOccupiedDelay = Integer
					.parseInt(newConfig.get("PIRUnoccupiedToOccupiedDelay").toString().trim());
			this.PIRUnoccupiedToOccupiedThreshold = Integer
					.parseInt(newConfig.get("PIRUnoccupiedToOccupiedThreshold").toString().trim());

			if (newConfig.get("alarm").toString().trim().equalsIgnoreCase("true"))
				this.alarm = true;
			else
				this.alarm = false;
			if (newConfig.get("squawk").toString().trim().equalsIgnoreCase("true"))
				this.squawk = true;
			else
				this.squawk = false;

			if (newConfig.get("reset").toString().trim().equalsIgnoreCase("true"))
				this.testReset = true;
			else
				this.testReset = false;

			this.VISIBILITY_TIMEOUT = Long.parseLong(newConfig.get("VISIBILITY_TIMEOUT").toString().trim()) * 1000;

			if (demo == null) {
				demo = new Thread(this, "DEMO AAL");
				demo.start();
			}
		}
	}

	private void findDemoDevices() {

		try {
			for (int j = 0; j < deviceServices.length; j++) {
				ZigBeeDevice current_device = deviceServices[j];
				if (current_device != null) {

					if (coordinator == null && current_device.getPhysicalNode().getNetworkAddress() == 0) {
						coordinator = current_device;
						// writeLog("coordinator found!", new Device(0,
						// current_device, System.currentTimeMillis()));
					}

					switch (current_device.getDeviceId()) {

					case OCCUPANCY_SENSOR_ID:
						if (occupancy_sensor_devices.get(current_device.getUniqueIdenfier()) == null) { // never
																										// found
																										// before
							Device d = new Device(current_device, System.currentTimeMillis());

							d.getPIRattributes().add(d.new Att(false, PIROccupiedToUnoccupiedDelay));
							d.getPIRattributes().add(d.new Att(false, PIRUnoccupiedToOccupiedDelay));
							d.getPIRattributes().add(d.new Att(false, PIRUnoccupiedToOccupiedThreshold));

							occupancy_sensor_devices.put(current_device.getUniqueIdenfier(), d);
							logger.debug("Found a OCCUPANCY_SENSOR_DEVICE: "
									+ d.getDevice().getPhysicalNode().getIEEEAddress());

							writeLog("found a OCCUPANCY_SENSOR_DEVICE", d);
						}
						break;

					case IAS_WD_DEVICE_ID:
						if (ias_warning_devices.get(current_device.getUniqueIdenfier()) == null) { // never
																									// found
																									// before
							Device d = new Device(current_device, System.currentTimeMillis());
							ias_warning_devices.put(current_device.getUniqueIdenfier(), d);
							logger.debug(
									"Found a IAS_WARNING_DEVICE: " + d.getDevice().getPhysicalNode().getIEEEAddress());

							writeLog("found a IAS_WARNING_DEVICE", d);

							/*
							 * try{ IASWarningSensorImpl warningDevice = new
							 * IASWarningSensorImpl(current_device.
							 * getUniqueIdenfier());
							 * ias_warning_devices_sensors.add(warningDevice);
							 * ias_warning_devices_services.add(bc.
							 * registerService(IASWarningSensor.class.getName(),
							 * warningDevice, new Properties())); }
							 * catch(Exception ex){ ex.printStackTrace();
							 * writeLog(ex.toString(), d); }
							 */
						}
						break;

					case IAS_ACE_DEVICE_ID:
						if (ancillary_control_equipment_devices.get(current_device.getUniqueIdenfier()) == null) { // never
																													// found
																													// before
							Device d = new Device(current_device, System.currentTimeMillis());
							ancillary_control_equipment_devices.put(current_device.getUniqueIdenfier(), d);
							logger.debug("Found a IAS_ANCILLARY_CONTROL_EQUIPMENT_DEVICE: "
									+ d.getDevice().getPhysicalNode().getIEEEAddress());

							writeLog("found a IAS_ANCILLARY_CONTROL_EQUIPMENT_DEVICE", d);
						}
						break;

					case IAS_CIE_DEVICE_ID:
						if (cie_devices.get(current_device.getUniqueIdenfier()) == null) { // never
																							// found
																							// before
							Device d = new Device(current_device, System.currentTimeMillis());
							cie_devices.put(current_device.getUniqueIdenfier(), d);
							logger.debug("Found a IAS_CONTROL_INDICATING_EQUIPMENT_DEVICE: "
									+ d.getDevice().getPhysicalNode().getIEEEAddress());

							writeLog("found a IAS_CONTROL_INDICATING_EQUIPMENT_DEVICE", d);
						}
						break;

					case IAS_ZONE_DEVICE_ID:
						if (ias_zone_devices.get(current_device.getUniqueIdenfier()) == null) { // never
																								// found
																								// before
							Device d = new Device(current_device, System.currentTimeMillis());
							ias_zone_devices.put(current_device.getUniqueIdenfier(), d);
							logger.debug(
									"Found a IAS_ZONE_DEVICE: " + d.getDevice().getPhysicalNode().getIEEEAddress());

							writeLog("found a IAS_ZONE_DEVICE", d);
						}
						break;

					default:
						break;
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void resetDevice(final Device d) {

		try {
			reset = new Thread(new Runnable() {

				public void run() {

					boolean reset = false, resetReporting = false, resetBind = false;

					while (!reset) {
						switch (d.getDevice().getDeviceId()) {
						case OCCUPANCY_SENSOR_ID:

							writeLog("too prolonged inactivity - removing device...", d, false);
							if (d.isReportingConfiguredPIR())
								resetReporting = d.getSubscriptionPIR().getSubscription()
										.removeReportListner(d.getSubscriptionPIR().getReportListener());
							else
								resetReporting = true;

							if (d.isBinded())
								try {
									resetBind = d.getDevice().unbind(OCCUPANCY_SENSING_CLUSTER_ID);
								} catch (ZigBeeBasedriverException e) {
									e.printStackTrace();
								}
							else
								resetBind = true;

							if (resetReporting && resetBind) {
								reset = true;
								occupancy_sensor_devices.remove(d.getDevice().getUniqueIdenfier());
								writeLog("*removed* a OCCUPANCY_SENSOR_DEVICE for prolonged inactivity (LastTimeSeen: "
										+ getTime(d.getLastTimeSeen()) + ")", d);
							} else
								try {
									reset = true;
									occupancy_sensor_devices.remove(d.getDevice().getUniqueIdenfier());
									writeLog("device not responding, removing forced.", d, false);

									Thread.sleep(SCAN_TIMEOUT);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}

							break;

						case IAS_ZONE_DEVICE_ID:

							writeLog("too prolonged inactivity - removing device...", d, false);
							if (d.isListenerIASzone())
								resetReporting = ((IASZoneClusterAAL) d.getSubscriptionIAS().getCluster())
										.removeZoneStatusChangeNotificationListener(
												d.getSubscriptionIAS().getListener());
							// ((IASZoneClusterAAL)d.getSubscriptionIAS().getCluster()).removeZoneStatusChangeNotificationListener(d.getSubscriptionIAS().getListener());
							else
								resetReporting = true;

							if (d.isBinded())
								try {
									resetBind = d.getDevice().unbind(IAS_ZONE_CLUSTER_ID);
								} catch (ZigBeeBasedriverException e) {
									e.printStackTrace();
								}
							else
								resetBind = true;

							if (resetReporting && resetBind) {
								reset = true;
								ias_zone_devices.remove(d.getDevice().getUniqueIdenfier());
								writeLog("*removed* a IAS_ZONE_DEVICE for prolonged inactivity (LastTimeSeen: "
										+ getTime(d.getLastTimeSeen()) + ")", d);
							} else
								try {
									reset = true;
									ias_zone_devices.remove(d.getDevice().getUniqueIdenfier());
									writeLog("device not responding, removing forced.", d, false);

									Thread.sleep(SCAN_TIMEOUT);
								} catch (InterruptedException e1) {
									e1.printStackTrace();
								}

							break;

						default:
							break;
						}
					}
				}
			});

			reset.start();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private boolean setParametersAndEnableReportingPIR() throws ZigBeeClusterException {

		Iterator<Entry<String, Device>> it = occupancy_sensor_devices.entrySet().iterator();
		while (it.hasNext()) {
			final Entry<String, Device> current = it.next();
			ZigBeeDevice device = current.getValue().getDevice();

			OccupacySensingAALImpl occupSensing;
			if (PIROccupiedToUnoccupiedDelay <= PIRUnoccupiedToOccupiedDelay)
				occupSensing = new OccupacySensingAALImpl(device, Long.parseLong(PIROccupiedToUnoccupiedDelay + ""));
			else
				occupSensing = new OccupacySensingAALImpl(device, Long.parseLong(PIRUnoccupiedToOccupiedDelay + ""));

			if (current.getValue().getPIRattributes().get(0).getValue() != -1
					&& !current.getValue().getPIRattributes().get(0).isSet())
				try {
					occupSensing.getPIROccupiedToUnoccupiedDelay().setValue(PIROccupiedToUnoccupiedDelay);
					current.getValue().getPIRattributes().get(0).setSet(true);

					writeLog("set AttributePIROccupiedToUnoccupiedDelay - value " + PIROccupiedToUnoccupiedDelay,
							current.getValue());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			if (current.getValue().getPIRattributes().get(1).getValue() != -1
					&& !current.getValue().getPIRattributes().get(1).isSet())
				try {
					occupSensing.getPIRUnoccupiedToOccupiedDelay().setValue(PIRUnoccupiedToOccupiedDelay);
					current.getValue().getPIRattributes().get(1).setSet(true);

					writeLog("set AttributePIRUnoccupiedToOccupiedDelay - value " + PIRUnoccupiedToOccupiedDelay,
							current.getValue());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			if (current.getValue().getPIRattributes().get(2).getValue() != -1
					&& !current.getValue().getPIRattributes().get(2).isSet())
				try {
					occupSensing.getPIRUnoccupiedToOccupiedThreshold().setValue(PIRUnoccupiedToOccupiedThreshold);
					current.getValue().getPIRattributes().get(2).setSet(true);

					writeLog(
							"set AttributePIRUnoccupiedToOccupiedThreshold - value " + PIRUnoccupiedToOccupiedThreshold,
							current.getValue());
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			if (/* current.getValue().isBinded() && */!current.getValue().isReportingConfiguredPIR()) {

				Subscription sub = configureReporting(current.getValue(),
						new AttributeImpl(current.getValue().getDevice(),
								new OccupacySensingClusterAAL(current.getValue().getDevice()), Attributes.OCCUPANCY));

				if (sub != null) {
					current.getValue().setReportingConfiguredPIR(true);
					current.getValue().setSubscriptionPIR(sub);

					try {
						OccupancySensorDeviceAAL pirSensor = new OccupancySensorDeviceAAL(getBundleContext(), device,
								occupSensing);
						this.occupancy_sensor_devices_sensors.add(pirSensor);
						this.occupancy_sensor_devices_services.add(
								bc.registerService(OccupancySensorAAL.class.getName(), pirSensor, new Properties()));
					} catch (Exception ex) {
						ex.printStackTrace();
						writeLog(ex.toString(), current.getValue());
					}

					writeLog("*** reporting has been configured for OCCUPANCY attribute", current.getValue());

					return true;
				}
			}
		}

		return false;
	}

	private void addIASZoneListener() throws ZigBeeClusterException {

		Iterator<Entry<String, Device>> it = ias_zone_devices.entrySet().iterator();
		while (it.hasNext()) {

			final Entry<String, Device> current = it.next();
			final ZigBeeDevice device = current.getValue().getDevice();

			if (current.getValue().isBinded() && current.getValue().isBackbinded()
					&& !current.getValue().isListenerIASzone()) {

				IASZoneAALImpl zone = new IASZoneAALImpl(device);

				ZoneStatusChangeNotificationListener listener = new ZoneStatusChangeNotificationListener() {

					public void zoneStatusChangeNotification(short zoneStatus) {
						try {
							writeLog("\t" + zoneStatus, current.getValue());

							for (IAS_ZoneDeviceAAL sensor : ias_zone_sensors) {
								if (zoneStatus == 25648) {
									writeLog("closed", current.getValue());

									// sensor.getIASZone().notifyStatusChange(false);
								} else if (zoneStatus == 25649) {
									writeLog("opened", current.getValue());

									// sensor.notifyStatusChange(true);

									if (alarm)
										alarm(IAS_WD_EMERGENCY, true, 5);
									if (squawk)
										squawk(new Short("0"), new Short("2"), true);
								} else if (zoneStatus == 25616) {
									writeLog("standing", current.getValue());

									// sensor.notifyStatusChange(false);
								} else if (zoneStatus == 25617) {
									writeLog("falling", current.getValue());

									// sensor.notifyStatusChange(true);

									if (alarm)
										alarm(IAS_WD_EMERGENCY, true, 5);
									if (squawk)
										squawk(new Short("0"), new Short("2"), true);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};

				if (zone.addZoneStatusChangeNotificationListener(listener)) {

					current.getValue().setListenerIASzone(true);
					current.getValue().setSubscriptionIAS(new Listener(zone, listener));

					try {
						IAS_ZoneDeviceAAL zoneSensor = new IAS_ZoneDeviceAAL(getBundleContext(), device, zone);
						this.ias_zone_sensors.add(zoneSensor);
						this.ias_zone_sensors_services
								.add(bc.registerService(IAS_ZoneAAL.class.getName(), zoneSensor, new Properties()));
					} catch (Exception ex) {
						ex.printStackTrace();
						writeLog(ex.toString(), current.getValue());
					}

					writeLog("*** IASZone cluster listener has been registered", current.getValue());
				}
			}
		}
	}

	private void alarm(short warningMode, boolean strobe, int secondsWarningDuration) {

		try {
			Iterator<Entry<String, Device>> it = ias_warning_devices.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Device> current = it.next();
				ZigBeeDevice device = current.getValue().getDevice();

				IASWDCluster c = new IASWDCluster(device);
				if (strobe)
					c.startWarning(new StartWarningPayloadImpl(warningMode, new Short("0"), secondsWarningDuration));
				else
					c.startWarning(new StartWarningPayloadImpl(warningMode, new Short("1"), secondsWarningDuration));

				writeLog("alarm " + warningMode + " for " + secondsWarningDuration + " seconds", current.getValue());
				// for(IASWarningSensorImpl ias_warning_devices_sensor:
				// ias_warning_devices_sensors)
				// ias_warning_devices_sensor.notifyStatusChange();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void squawk(short squawkMode, short squawkLevel, boolean strobe) {

		// squawkMode
		// 1: armed
		// 2: disarmed

		// squawkLevel: low, medium high, very high level sound
		if ((squawkMode == 0 || squawkMode == 1) && (squawkLevel >= 0 && squawkLevel <= 3))
			try {
				Iterator<Entry<String, Device>> it = ias_warning_devices.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Device> current = it.next();
					ZigBeeDevice device = current.getValue().getDevice();

					IASWDCluster c = new IASWDCluster(device);
					if (strobe)
						c.squawk(new SquawkPayloadImpl(squawkMode, squawkLevel, new Short("1")));
					else
						c.squawk(new SquawkPayloadImpl(squawkMode, squawkLevel, new Short("0")));

					writeLog("squawk " + squawkMode + " at level " + squawkLevel, current.getValue());
					// for(IASWarningSensorImpl ias_warning_devices_sensor:
					// ias_warning_devices_sensors)
					// ias_warning_devices_sensor.notifyStatusChange();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		squawk(new Short("2"), new Short("0"), false); // disabling squawk
	}

	private void bindDevices() throws ZigBeeBasedriverException {

		Iterator<Entry<String, Device>> it_cie = cie_devices.entrySet().iterator();
		while (it_cie.hasNext()) {
			Entry<String, Device> current1 = it_cie.next();
			ZigBeeDevice cie_device = current1.getValue().getDevice();

			Iterator<Entry<String, Device>> it_ace = ancillary_control_equipment_devices.entrySet().iterator();
			while (it_ace.hasNext()) {
				Entry<String, Device> current2 = it_ace.next();
				ZigBeeDevice ancillary_control_equipment_device = current2.getValue().getDevice();
				// CIE -> ACE (1280,1281)
				bindTo(cie_device, ancillary_control_equipment_device, IAS_ZONE_CLUSTER_ID);
				bindTo(ancillary_control_equipment_device, cie_device, IAS_ZONE_CLUSTER_ID);

				bindTo(cie_device, ancillary_control_equipment_device, IAS_ACE_CLUSTER_ID);
				bindTo(ancillary_control_equipment_device, cie_device, IAS_ACE_CLUSTER_ID);
			}

			// ias zone -> cie (1280)
			Iterator<Entry<String, Device>> it = ias_zone_devices.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Device> current = it.next();
				if (current != null) {
					if (!current.getValue().isBackbinded()) {
						current.getValue()
								.setBackbinded(bindTo(current.getValue().getDevice(), cie_device, IAS_ZONE_CLUSTER_ID));

						if (current.getValue().isBackbinded())
							writeLog("binded to " + cie_device.getUniqueIdenfier() + " on cluster IAS_ZONE",
									current.getValue());

					}
					if (!current.getValue().isBinded()) {
						current.getValue()
								.setBinded(bindTo(cie_device, current.getValue().getDevice(), IAS_ZONE_CLUSTER_ID));

						if (current.getValue().isBinded())
							writeLog("bind from " + cie_device.getUniqueIdenfier() + " on cluster IAS_ZONE",
									current.getValue());
					}
				}
			}

			// CIE -> WD (1280,1282)
			it = ias_warning_devices.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Device> current = it.next();
				if (current != null) {
					bindTo(cie_device, current.getValue().getDevice(), IAS_ZONE_CLUSTER_ID);
					bindTo(current.getValue().getDevice(), cie_device, IAS_ZONE_CLUSTER_ID);

					bindTo(cie_device, current.getValue().getDevice(), IAS_WD_CLUSTER_ID);
					bindTo(current.getValue().getDevice(), cie_device, IAS_WD_CLUSTER_ID);
				}
			}
		}

		/*
		 * Iterator<Entry<String, Device>> it =
		 * occupancy_sensor_devices.entrySet().iterator(); while(it.hasNext()){
		 * Entry<String, Device> current = it.next(); //if(coordinator != null)
		 * if(!current.getValue().isBinded()){
		 * current.getValue().setBinded(current.getValue().getDevice().bind(
		 * OCCUPANCY_SENSING_CLUSTER_ID));
		 * 
		 * if(current.getValue().isBinded())
		 * writeLog("binded on cluster OCCUPANCY_SENSING", current.getValue());
		 * } }
		 */
	}

	private void writeLog(String s, Device d) {

		writeLog(s, d, true);
	}

	private void writeLog(String s, Device d, boolean update) {

		try {
			System.out
					.println("DEMO AAL - " + getTime() + " - device " + d.getDevice().getUniqueIdenfier() + " - " + s);
			out.write(getTime() + " - device " + d.getDevice().getUniqueIdenfier() + " - " + s);
			out.newLine();
			out.flush();

			if (update)
				d.setLastTimeSeen(System.currentTimeMillis());
		} catch (Exception ex) {
		}
	}

	private Subscription configureReporting(final Device d, AttributeImpl att) throws ZigBeeClusterException {

		final ZigBeeDevice device = d.getDevice();
		OccupacySensingClusterAAL c = new OccupacySensingClusterAAL(device);

		SubscriptionImpl sub = new SubscriptionImpl(device, c, att);
		try {
			ReportListener rl = new ReportListener() {

				public void receivedReport(Dictionary<Attribute, Object> reports) {

					Enumeration<Attribute> attributes = reports.keys();
					while (attributes.hasMoreElements()) {
						Attribute a = (Attribute) attributes.nextElement();
						Object v = reports.get(a);

						writeLog("\t" + ((Integer) v).intValue(), d);

						for (OccupancySensorDeviceAAL occupancy_sensor_devices_sensor : occupancy_sensor_devices_sensors) {

							if (((Integer) v).intValue() == 0) {
								writeLog("\tsent 'no presence' message.", d);
								// occupancy_sensor_devices_sensor.getOccupacySensing().
								// get notifyStatusChange(false);
							}
							if (((Integer) v).intValue() == 1) {
								writeLog("\tsent 'presence detected' message.", d);
								// occupancy_sensor_devices_sensor.notifyStatusChange(true);

								if (alarm)
									alarm(IAS_WD_EMERGENCY, true, 5);
								if (squawk)
									squawk(new Short("0"), new Short("2"), true);
							}
						}
					}
				}
			};

			if (sub.addReportListner(rl))
				return new Subscription(rl, sub);
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String getTime() {

		long time = System.currentTimeMillis();
		long seconds = (time / 1000) % 60;
		long minutes = (time / (1000 * 60)) % 60;
		long hours = (time / (1000 * 60 * 60)) % 24;

		String second, minute, hour;
		if (seconds < 10)
			second = "0" + seconds;
		else
			second = seconds + "";
		if (minutes < 10)
			minute = "0" + minutes;
		else
			minute = minutes + "";
		if (hours < 10)
			hour = "0" + hours;
		else
			hour = hours + "";

		return hour + ":" + minute + ":" + second;
	}

	private String getTime(long millis) {

		long time = millis;
		long seconds = (time / 1000) % 60;
		long minutes = (time / (1000 * 60)) % 60;
		long hours = (time / (1000 * 60 * 60)) % 24;

		String second, minute, hour;
		if (seconds < 10)
			second = "0" + seconds;
		else
			second = seconds + "";
		if (minutes < 10)
			minute = "0" + minutes;
		else
			minute = minutes + "";
		if (hours < 10)
			hour = "0" + hours;
		else
			hour = hours + "";

		return "(GMT) " + hour + ":" + minute + ":" + second;
	}

	private boolean bindTo(ZigBeeDevice src, ZigBeeDevice dst, int clusterID) {

		try {
			if (src.bindTo(dst, clusterID)) {
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public void end() {

		try {
			run = false;
			if (demo != null && demo.isAlive())
				demo.interrupt();
			if (reset != null && reset.isAlive())
				reset.interrupt();

			out.close();
			fstream.close();

			managedService.unregister();

			for (ServiceRegistration ias_zone_sensors_service : this.ias_zone_sensors_services)
				ias_zone_sensors_service.unregister();

			for (ServiceRegistration occupancy_sensor_devices_service : this.occupancy_sensor_devices_services)
				occupancy_sensor_devices_service.unregister();

			for (ServiceRegistration ias_warning_devices_service : this.ias_warning_devices_services)
				ias_warning_devices_service.unregister();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop(BundleContext arg0) throws Exception {
		end();
		bc = null;
		deviceServices = null;
	}

	public static BundleContext getBundleContext() {
		return bc;
	}

	private class Subscription {
		private ReportListener reportListener;
		private SubscriptionImpl subscription;

		public Subscription(ReportListener reportListener, SubscriptionImpl subscription) {
			this.reportListener = reportListener;
			this.subscription = subscription;
		}

		public ReportListener getReportListener() {
			return reportListener;
		}

		public void setReportListener(ReportListener reportListener) {
			this.reportListener = reportListener;
		}

		public SubscriptionImpl getSubscription() {
			return subscription;
		}

		public void setSubscription(SubscriptionImpl subscription) {
			this.subscription = subscription;
		}
	}

	private class Listener {
		private Cluster cluster;
		private ZoneStatusChangeNotificationListener listener;

		public Listener(Cluster cluster, ZoneStatusChangeNotificationListener listener) {
			this.cluster = cluster;
			this.listener = listener;
		}

		public Cluster getCluster() {
			return cluster;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public ZoneStatusChangeNotificationListener getListener() {
			return listener;
		}

		public void setListener(ZoneStatusChangeNotificationListener listener) {
			this.listener = listener;
		}
	}

	public class Device {

		public class Att {
			private boolean set;
			private int value;

			private Att(boolean set, int value) {
				this.set = set;
				this.value = value;
			}

			public int getValue() {
				return value;
			}

			public void setValue(int value) {
				this.value = value;
			}

			public boolean isSet() {
				return set;
			}

			public void setSet(boolean set) {
				this.set = set;
			}
		}

		private List<Att> PIRattributes;

		private boolean reportingConfiguredPIR;
		private Subscription subscriptionPIR;

		private boolean listenerIASzone;
		private Listener subscriptionIAS;

		private boolean binded;
		private boolean backbinded;
		private ZigBeeDevice device;
		private long lastTimeSeen;

		public Device(ZigBeeDevice device, long lastTimeSeen) {
			this.setPIRattributes(new ArrayList<Activator.Device.Att>());

			this.reportingConfiguredPIR = false;
			this.subscriptionPIR = null;

			this.listenerIASzone = false;
			this.subscriptionIAS = null;

			this.device = device;
			this.binded = false;
			this.backbinded = false;
			this.lastTimeSeen = lastTimeSeen;
		}

		public Subscription getSubscriptionPIR() {
			return subscriptionPIR;
		}

		public void setSubscriptionPIR(Subscription subscriptionPIR) {
			this.subscriptionPIR = subscriptionPIR;
		}

		public Listener getSubscriptionIAS() {
			return subscriptionIAS;
		}

		public void setSubscriptionIAS(Listener subscriptionIAS) {
			this.subscriptionIAS = subscriptionIAS;
		}

		public boolean isReportingConfiguredPIR() {
			return reportingConfiguredPIR;
		}

		public void setReportingConfiguredPIR(boolean reportingConfiguredPIR) {
			this.reportingConfiguredPIR = reportingConfiguredPIR;
		}

		public boolean isListenerIASzone() {
			return listenerIASzone;
		}

		public void setListenerIASzone(boolean listenerIASzone) {
			this.listenerIASzone = listenerIASzone;
		}

		public long getLastTimeSeen() {
			return lastTimeSeen;
		}

		public void setLastTimeSeen(long lastTimeSeen) {
			this.lastTimeSeen = lastTimeSeen;
		}

		public ZigBeeDevice getDevice() {
			return device;
		}

		public void setDevice(ZigBeeDevice device) {
			this.device = device;
		}

		public boolean isBinded() {
			return binded;
		}

		public void setBinded(boolean binded) {
			this.binded = binded;
		}

		public boolean isBackbinded() {
			return backbinded;
		}

		public void setBackbinded(boolean backbinded) {
			this.backbinded = backbinded;
		}

		public List<Att> getPIRattributes() {
			return PIRattributes;
		}

		public void setPIRattributes(List<Att> attributes) {
			this.PIRattributes = attributes;
		}
	}
}
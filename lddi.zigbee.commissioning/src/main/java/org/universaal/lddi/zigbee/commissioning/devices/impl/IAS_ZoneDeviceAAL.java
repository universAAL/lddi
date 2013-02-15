package org.universaal.lddi.zigbee.commissioning.devices.impl;

import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.device.api.security_safety.IAS_Zone;
import it.cnr.isti.zigbee.ha.driver.core.HADeviceBase;
import it.cnr.isti.zigbee.ha.driver.core.HAProfile;
import it.cnr.isti.zigbee.ha.driver.core.ZigBeeHAException;
import it.cnr.isti.zigbee.ha.driver.core.reflection.AbstractDeviceDescription;
import it.cnr.isti.zigbee.ha.driver.core.reflection.DeviceDescription;

import org.osgi.framework.BundleContext;
import org.universaal.lddi.zigbee.commissioning.clusters.api.IASZoneAAL;
import org.universaal.lddi.zigbee.commissioning.devices.api.IAS_ZoneAAL;

public class IAS_ZoneDeviceAAL extends HADeviceBase implements IAS_ZoneAAL {

	private IASZoneAAL iasZoneCluster;

	public IAS_ZoneDeviceAAL(BundleContext ctx, ZigBeeDevice zbDevice) throws ZigBeeHAException {

		super(ctx, zbDevice);

		iasZoneCluster = (IASZoneAAL) addCluster(HAProfile.IAS_ZONE);
	}

	public IAS_ZoneDeviceAAL(BundleContext ctx, ZigBeeDevice zbDevice, IASZoneAAL cluster) throws ZigBeeHAException {

		super(ctx, zbDevice);

		iasZoneCluster = cluster;
	}

	public IASZoneAAL getIASZone() {

		return iasZoneCluster;
	}

	@Override
	public String getName() {

		return IAS_Zone.NAME;
	}

	@Override
	public DeviceDescription getDescription() {

		return DEVICE_DESCRIPTOR;
	}

	final static DeviceDescription DEVICE_DESCRIPTOR =  new AbstractDeviceDescription(){

		public int[] getCustomClusters() {
			return IAS_Zone.CUSTOM;
		}

		public int[] getMandatoryCluster() {
			return IAS_Zone.MANDATORY;
		}

		public int[] getOptionalCluster() {
			return IAS_Zone.OPTIONAL;
		}

		public int[] getStandardClusters() {
			return IAS_Zone.STANDARD;
		}
	};
}
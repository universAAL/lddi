package org.universaal.lddi.zigbee.commissioning.devices.impl;

import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.cluster.glue.general.Groups;
import it.cnr.isti.zigbee.ha.device.api.lighting.OccupancySensor;
import it.cnr.isti.zigbee.ha.driver.core.HADeviceBase;
import it.cnr.isti.zigbee.ha.driver.core.HAProfile;
import it.cnr.isti.zigbee.ha.driver.core.ZigBeeHAException;
import it.cnr.isti.zigbee.ha.driver.core.reflection.AbstractDeviceDescription;
import it.cnr.isti.zigbee.ha.driver.core.reflection.DeviceDescription;

import org.osgi.framework.BundleContext;
import org.universaal.lddi.zigbee.commissioning.clusters.api.OccupacySensingAAL;
import org.universaal.lddi.zigbee.commissioning.devices.api.OccupancySensorAAL;

public class OccupancySensorDeviceAAL extends HADeviceBase implements OccupancySensorAAL {

	private OccupacySensingAAL occupancySensing;
	private Groups groups;

	public  OccupancySensorDeviceAAL(BundleContext ctx, ZigBeeDevice zbDevice) throws ZigBeeHAException {

		super(ctx,zbDevice);

		occupancySensing = (OccupacySensingAAL) addCluster(HAProfile.OCCUPANCY_SENSING);
		groups = (Groups) addCluster(HAProfile.GROUPS);
	}

	public  OccupancySensorDeviceAAL(BundleContext ctx, ZigBeeDevice zbDevice, OccupacySensingAAL cluster) throws ZigBeeHAException {

		super(ctx,zbDevice);

		occupancySensing = cluster;
		groups = (Groups) addCluster(HAProfile.GROUPS);
	}

	public OccupacySensingAAL getOccupacySensing() {
		return occupancySensing;
	}

	@Override
	public String getName() {
		return OccupancySensor.NAME;
	}

	final static DeviceDescription DEVICE_DESCRIPTOR =  new AbstractDeviceDescription(){

		public int[] getCustomClusters() {
			return OccupancySensor.CUSTOM;
		}

		public int[] getMandatoryCluster() {
			return OccupancySensor.MANDATORY;
		}

		public int[] getOptionalCluster() {
			return OccupancySensor.OPTIONAL;
		}

		public int[] getStandardClusters() {
			return OccupancySensor.STANDARD;
		}
	};

	@Override
	public DeviceDescription getDescription() {
		return DEVICE_DESCRIPTOR;
	}

	public Groups getGroups() {
		return groups;
	}
}
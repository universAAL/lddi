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
package org.universAAL.lddi.zigbee.commissioning.devices.impl;

import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.device.api.security_safety.IAS_Zone;
import it.cnr.isti.zigbee.ha.driver.core.HADeviceBase;
import it.cnr.isti.zigbee.ha.driver.core.HAProfile;
import it.cnr.isti.zigbee.ha.driver.core.ZigBeeHAException;
import it.cnr.isti.zigbee.ha.driver.core.reflection.AbstractDeviceDescription;
import it.cnr.isti.zigbee.ha.driver.core.reflection.DeviceDescription;

import org.osgi.framework.BundleContext;
import org.universAAL.lddi.zigbee.commissioning.clusters.api.IASZoneBridge;
import org.universAAL.lddi.zigbee.commissioning.devices.api.IAS_ZoneBridge;

public class IAS_ZoneDevice extends HADeviceBase implements IAS_ZoneBridge {

	private IASZoneBridge iasZoneCluster;

	public IAS_ZoneDevice(BundleContext ctx, ZigBeeDevice zbDevice) throws ZigBeeHAException {

		super(ctx, zbDevice);

		iasZoneCluster = (IASZoneBridge) addCluster(HAProfile.IAS_ZONE);
	}

	public IAS_ZoneDevice(BundleContext ctx, ZigBeeDevice zbDevice, IASZoneBridge cluster) throws ZigBeeHAException {

		super(ctx, zbDevice);

		iasZoneCluster = cluster;
	}

	public IASZoneBridge getIASZone() {

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

	final static DeviceDescription DEVICE_DESCRIPTOR = new AbstractDeviceDescription() {

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
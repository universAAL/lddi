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
package org.universAAL.lddi.zigbee.commissioning.devices.api;

import org.universAAL.lddi.zigbee.commissioning.clusters.api.OccupacySensingAAL;

import it.cnr.isti.zigbee.ha.cluster.glue.general.Groups;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.OccupacySensing;
import it.cnr.isti.zigbee.ha.driver.ArraysUtil;
import it.cnr.isti.zigbee.ha.driver.core.HADevice;
import it.cnr.isti.zigbee.ha.driver.core.HAProfile;

public interface OccupancySensorAAL extends HADevice {

	public static final int DEVICE_ID = 0x0107;
	public static final String NAME = "Occupancy Sensor 'stabilized'";

	public static final int[] MANDATORY = ArraysUtil.append(HADevice.MANDATORY,
			new int[] { HAProfile.OCCUPANCY_SENSING });
	public static final int[] OPTIONAL = ArraysUtil.append(HADevice.OPTIONAL, new int[] { HAProfile.GROUPS });
	public static final int[] STANDARD = ArraysUtil.append(MANDATORY, OPTIONAL);
	public static final int[] CUSTOM = {};

	/**
	 * Access method for the <b>Mandatory</b> cluster: {@link OccupacySensing}
	 * 
	 * @return the {@link OccupacySensing} cluster implemented by the device
	 */
	public OccupacySensingAAL getOccupacySensing();

	public Groups getGroups();
}

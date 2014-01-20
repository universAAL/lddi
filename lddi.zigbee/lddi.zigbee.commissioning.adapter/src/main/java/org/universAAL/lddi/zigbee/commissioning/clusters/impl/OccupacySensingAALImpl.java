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
package org.universAAL.lddi.zigbee.commissioning.clusters.impl;

import org.universAAL.lddi.zigbee.commissioning.clusters.api.OccupacySensingAAL;

import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.Activator;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.OccupancyListener;
import it.cnr.isti.zigbee.zcl.library.api.core.Attribute;
import it.cnr.isti.zigbee.zcl.library.api.core.Subscription;

public class OccupacySensingAALImpl implements OccupacySensingAAL {

	private OccupacySensingClusterAAL occupacySensingCluster;

	private Attribute occupancy;
	private Attribute occupancySensorType;
	private Attribute pirOccupiedToUnoccupiedDelay;
	private Attribute pirUnoccupiedToOccupiedDelay;
	private Attribute pirUnoccupiedToOccupiedThreshold;
	private Attribute ultraSonicOccupiedToUnoccupiedDelay;
	private Attribute ultraSonicUnoccupiedToOccupiedDelay;
	private Attribute ultrasonicUnoccupiedToOccupiedThreshold;

	private OccupancyBridgeListenersAAL eventBridge;

	public OccupacySensingAALImpl(ZigBeeDevice zbDevice){		

		occupacySensingCluster = new OccupacySensingClusterAAL(zbDevice);

		occupancy = occupacySensingCluster.getAttributeOccupancy();
		occupancySensorType = occupacySensingCluster.getAttributeOccupancySensorType();
		pirOccupiedToUnoccupiedDelay = occupacySensingCluster.getAttributePIROccupiedToUnoccupiedDelay();
		pirUnoccupiedToOccupiedDelay = occupacySensingCluster.getAttributePIRUnoccupiedToOccupiedDelay();
		ultraSonicOccupiedToUnoccupiedDelay = occupacySensingCluster.getAttributeUltraSonicOccupiedToUnoccupiedDelay();
		ultraSonicUnoccupiedToOccupiedDelay = occupacySensingCluster.getAttributeUltraSonicUnoccupiedToOccupiedDelay();
		pirUnoccupiedToOccupiedThreshold = occupacySensingCluster.getAttributePIRUnoccupiedToOccupiedThreshold();
		ultrasonicUnoccupiedToOccupiedThreshold = occupacySensingCluster.getAttributeUltrasonicUnoccupiedToOccupiedThreshold();

		eventBridge = new OccupancyBridgeListenersAAL(Activator.getConfiguration(), occupancy, this, 2);		
	}

	public OccupacySensingAALImpl(ZigBeeDevice zbDevice, long minTimeBeforeNotifyStatusChange){		

		occupacySensingCluster = new OccupacySensingClusterAAL(zbDevice);

		occupancy = occupacySensingCluster.getAttributeOccupancy();
		occupancySensorType = occupacySensingCluster.getAttributeOccupancySensorType();
		pirOccupiedToUnoccupiedDelay = occupacySensingCluster.getAttributePIROccupiedToUnoccupiedDelay();
		pirUnoccupiedToOccupiedDelay = occupacySensingCluster.getAttributePIRUnoccupiedToOccupiedDelay();
		ultraSonicOccupiedToUnoccupiedDelay = occupacySensingCluster.getAttributeUltraSonicOccupiedToUnoccupiedDelay();
		ultraSonicUnoccupiedToOccupiedDelay = occupacySensingCluster.getAttributeUltraSonicUnoccupiedToOccupiedDelay();
		pirUnoccupiedToOccupiedThreshold = occupacySensingCluster.getAttributePIRUnoccupiedToOccupiedThreshold();
		ultrasonicUnoccupiedToOccupiedThreshold = occupacySensingCluster.getAttributeUltrasonicUnoccupiedToOccupiedThreshold();

		eventBridge = new OccupancyBridgeListenersAAL(Activator.getConfiguration(), occupancy, this, minTimeBeforeNotifyStatusChange);		
	}

	public Attribute getOccupancy() {
		return occupancy;
	}

	public Attribute getOccupancySensorType() {
		return occupancySensorType;
	}

	public Attribute getPIROccupiedToUnoccupiedDelay() {
		return pirUnoccupiedToOccupiedDelay;
	}

	public Attribute getPIRUnoccupiedToOccupiedDelay() {
		return pirOccupiedToUnoccupiedDelay;
	}

	public Attribute getUltraSonicOccupiedToUnoccupiedDelay() {
		return ultraSonicOccupiedToUnoccupiedDelay;
	}

	public Attribute getUltraSonicUnoccupiedToOccupiedDelay() {
		return ultraSonicUnoccupiedToOccupiedDelay;
	}

	public Subscription[] getActiveSubscriptions() {
		return occupacySensingCluster.getActiveSubscriptions();
	}

	public int getId() {
		return occupacySensingCluster.getId();
	}

	public String getName() {
		return occupacySensingCluster.getName();
	}

	public boolean subscribe_feedback(OccupancyListener listener) {
		return eventBridge.subscribe(listener);
	}

	public boolean unsubscribe_feedback(OccupancyListener listener) {
		return eventBridge.unsubscribe(listener);
	}

	public void subscribe(OccupancyListener listener) {
		eventBridge.subscribe(listener);
	}

	public void unsubscribe(OccupancyListener listener) {
		eventBridge.unsubscribe(listener);
	}

	public Attribute getAttribute(int id) {		
		Attribute[] attributes = occupacySensingCluster.getAvailableAttributes();
		for (int i = 0; i < attributes.length; i++) {
			if( attributes[i].getId() == id ) 
				return attributes[i];
		}
		return null;
	}

	public Attribute[] getAttributes() {
		return occupacySensingCluster.getAvailableAttributes();
	}

	public Attribute getPIRUnoccupiedToOccupiedThreshold() {
		return pirUnoccupiedToOccupiedThreshold;
	}

	public Attribute getUltraSonicUnoccupiedToOccupiedThreshold() {
		return ultrasonicUnoccupiedToOccupiedThreshold;
	}	
}
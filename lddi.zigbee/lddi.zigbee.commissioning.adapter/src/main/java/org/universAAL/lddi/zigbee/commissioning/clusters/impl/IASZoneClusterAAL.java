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

import it.cnr.isti.zigbee.api.Cluster;
import it.cnr.isti.zigbee.api.ClusterFilter;
import it.cnr.isti.zigbee.api.ClusterListner;
import it.cnr.isti.zigbee.api.ZigBeeBasedriverException;
import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationListener;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationResponse;
import it.cnr.isti.zigbee.zcl.library.impl.core.ResponseImpl;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.IASZoneCluster;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.ias_zone.IAS_ZoneZoneStatusChangeNotificationFilter;
import it.cnr.isti.zigbee.zcl.library.impl.security_safety.ias_zone.ZoneStatusChangeNotificationResponseImpl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IASZoneClusterAAL extends IASZoneCluster {

	private final ArrayList<ZoneStatusChangeNotificationListener> listeners = new ArrayList<ZoneStatusChangeNotificationListener>();
	private final Logger log = LoggerFactory.getLogger(IASZoneClusterAAL.class);
	private ZoneStatusChangeNotificationListenerNotifierAAL bridge;

	private short previousStatus;

	public IASZoneClusterAAL(ZigBeeDevice zbDevice) {
		super(zbDevice);
		bridge = new ZoneStatusChangeNotificationListenerNotifierAAL();

		previousStatus = -1;
	}
	public boolean addZoneStatusChangeNotificationListener(ZoneStatusChangeNotificationListener listener) {
		synchronized (listeners) {
			if ( listeners.size() == 0 ){
				try {
					getZigBeeDevice().bind(ID);
				} catch (ZigBeeBasedriverException e) {
					log.error("Unable to bind to device for IASZone reporting", e);
					return false;
				}
				if ( getZigBeeDevice().addClusterListener(bridge) == false ) {
					log.error("Unable to register the cluster listener for IASZone reporting");
					return false;
				}
			}
			listeners.add(listener);
			return true;		
		}
	}

	public boolean removeZoneStatusChangeNotificationListener(ZoneStatusChangeNotificationListener listener) {
		synchronized (listeners) {
			boolean removed = listeners.remove(listener); 
			if ( listeners.size() == 0 && removed ){
				try {
					getZigBeeDevice().unbind(ID);
				} catch (ZigBeeBasedriverException e) {
					log.error("Unable to unbind to device for IASZone reporting", e);
					return false;
				}
				if ( getZigBeeDevice().removeClusterListener(bridge) == false ) {
					log.error("Unable to unregister the cluster listener for IASZone reporting");
					return false;
				}
			}
			return removed;		
		}
	}

	private class ZoneStatusChangeNotificationListenerNotifierAAL implements ClusterListner{

		public void setClusterFilter(ClusterFilter filter) {
		}

		public ClusterFilter getClusterFilter() {
			return IAS_ZoneZoneStatusChangeNotificationFilter.FILTER;
		}

		public void handleCluster(ZigBeeDevice device, Cluster c) {
			try {
				ResponseImpl response = new ResponseImpl(c, ID);
				ZoneStatusChangeNotificationResponse zscnr = new ZoneStatusChangeNotificationResponseImpl(response);
				ArrayList<ZoneStatusChangeNotificationListener> localCopy;
				synchronized (listeners) {
					localCopy = new ArrayList<ZoneStatusChangeNotificationListener>(listeners);					
				}
				log.debug("Notifying {} ZoneStatusChangeNotificationListener", localCopy.size());
				for (ZoneStatusChangeNotificationListener alarmListner : localCopy) {
					try{
						if(zscnr.getZoneStatus() != previousStatus){ // notifying only changes
							log.debug("Notifying {}:{}", alarmListner.getClass().getName(), alarmListner);
							alarmListner.zoneStatusChangeNotification(zscnr.getZoneStatus());	
						}
					}
					catch(Exception e){
						log.error("Error while notifying {}:{} caused by {}",new Object[]{
								alarmListner.getClass().getName(), alarmListner, e.getStackTrace() 
						});
					}
				}
				previousStatus = zscnr.getZoneStatus();
			} 
			catch (ZigBeeClusterException e) {
				e.printStackTrace();
			}
		}
	}
}
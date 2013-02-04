/*
   Copyright 2008-2010 CNR-ISTI, http://isti.cnr.it
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

package lddi.zigbee.commissioning.clusters.impl;

import it.cnr.isti.zigbee.ha.cluster.glue.Cluster;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.OccupancyEvent;
import it.cnr.isti.zigbee.ha.cluster.glue.measureament_sensing.event.OccupancyListener;
import it.cnr.isti.zigbee.ha.driver.core.ReportingConfiguration;
import it.cnr.isti.zigbee.zcl.library.api.core.Attribute;
import it.cnr.isti.zigbee.zcl.library.api.core.ReportListener;
import it.cnr.isti.zigbee.zcl.library.api.core.Subscription;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Default implementation of the delegator class that handles the eventing of the {@link OccupancyListener} 
 * 
 * @author <a href="mailto:stefano.lenzi@isti.cnr.it">Stefano "Kismet" Lenzi</a>
 * @author <a href="mailto:francesco.furfari@isti.cnr.it">Francesco Furfari</a>
 * @version $LastChangedRevision: 584 $ ($LastChangedDate: 2012-11-29 11:18:55 +0100 (Thu, 29 Nov 2012) $)
 * @since 0.6.0
 *
 */
public class OccupancyBridgeListenersAAL implements ReportListener {

	private class OccupancyEventImpl implements OccupancyEvent{

		private final Cluster source;
		private final int event;

		public OccupancyEventImpl(Cluster cluster, Integer value) {
			source = cluster;
			event = value; 
		}

		public int getEvent() {
			return event;
		}

		public Cluster getSource() {
			return source;
		}
	}

	private final Attribute bridged;
	private final ArrayList<OccupancyListener> listeners = new ArrayList<OccupancyListener>();
	private final Cluster cluster;
	private final ReportingConfiguration configuration;

	private int previousEvent;
	private long lastReadingTime, minTimeBeforeNotifyStatusChange;

	public OccupancyBridgeListenersAAL(final ReportingConfiguration conf, final Attribute attribute, final Cluster c) {
		bridged = attribute;
		cluster = c;
		configuration = conf;

		previousEvent = -1;
		lastReadingTime = 0;
		this.minTimeBeforeNotifyStatusChange = 2*1000;
	}

	public OccupancyBridgeListenersAAL(final ReportingConfiguration conf, final Attribute attribute, final Cluster c, long minTimeBeforeNotifyStatusChange) {
		bridged = attribute;
		cluster = c;
		configuration = conf;

		previousEvent = -1;
		lastReadingTime = 0;
		this.minTimeBeforeNotifyStatusChange = minTimeBeforeNotifyStatusChange*1000;
	}

	public void receivedReport(Dictionary<Attribute, Object> reports) {

		if (reports.get(bridged) == null) {
			return;
		}
		synchronized (listeners) {
			for (OccupancyListener listener : listeners) {
				if(((Integer) reports.get(bridged)).intValue() != previousEvent) // notifying only changes
					if(lastReadingTime == 0 // first time
					|| ((lastReadingTime+minTimeBeforeNotifyStatusChange) >= System.currentTimeMillis()) // minimum time expired
							){
						listener.changedOccupancy(new OccupancyEventImpl(cluster, (Integer) reports.get(bridged)));						
					}
			}
		}
		previousEvent = (Integer) reports.get(bridged);
		lastReadingTime = System.currentTimeMillis();
	}

	public List<OccupancyListener> getListeners(){
		return listeners;
	}

	public boolean subscribe(OccupancyListener listener) {

		synchronized (listeners) {
			if ( listeners.size() == 0 ) {
				Subscription subscription = bridged.getSubscription();
				if ( configuration.getReportingOverwrite() || subscription.isActive() == false ) {
					subscription.setMaximumReportingInterval(configuration.getReportingMaximum());
					subscription.setMinimumReportingInterval(configuration.getReportingMinimum());
					subscription.updateConfiguration();
				}
				if ( subscription.addReportListner(this) == false ) {
					return false;
				}
			}
			return listeners.add(listener);
		}
	}

	public boolean unsubscribe(OccupancyListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
			if ( listeners.size() == 0 ) {
				Subscription subscription = bridged.getSubscription();
				if ( subscription.getReportListenersCount() == 1 ){
					subscription.clear();
				}
			}
		}
		return true;
	}	
}
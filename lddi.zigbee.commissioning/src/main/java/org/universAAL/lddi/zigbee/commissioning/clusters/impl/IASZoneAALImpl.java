package org.universAAL.lddi.zigbee.commissioning.clusters.impl;

import org.universAAL.lddi.zigbee.commissioning.clusters.api.IASZoneAAL;

import it.cnr.isti.zigbee.api.ZigBeeDevice;
import it.cnr.isti.zigbee.ha.driver.core.ZigBeeHAException;
import it.cnr.isti.zigbee.zcl.library.api.core.Attribute;
import it.cnr.isti.zigbee.zcl.library.api.core.Response;
import it.cnr.isti.zigbee.zcl.library.api.core.Subscription;
import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;
import it.cnr.isti.zigbee.zcl.library.api.global.DefaultResponse;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneEnrollRequestPayload;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneEnrollResponse;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationListener;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationPayload;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationResponse;

public class IASZoneAALImpl implements IASZoneAAL {

	private final IASZoneClusterAAL cluster;

	private final Attribute zoneState;
	private final Attribute zoneType;
	private final Attribute zoneStatus;
	private final Attribute iasCIEaddress;

	public IASZoneAALImpl(ZigBeeDevice zbDevice){

		cluster = new IASZoneClusterAAL(zbDevice);
		zoneState = cluster.getAttributeZoneState();
		zoneType = cluster.getAttributeZoneType();
		zoneStatus = cluster.getAttributeZoneStatus();
		iasCIEaddress = cluster.getAttributeIASCIEAddress();
	}

	public int getId() {
		return cluster.getId();
	}

	public String getName() {
		return cluster.getName();
	}

	public Subscription[] getActiveSubscriptions() {
		return cluster.getActiveSubscriptions();
	}

	public Attribute[] getAttributes() {
		return cluster.getAvailableAttributes();
	}

	public Attribute getAttribute(int id) {

		Attribute[] attributes = cluster.getAvailableAttributes();
		for (int i = 0; i < attributes.length; i++) {
			if( attributes[i].getId() == id ) 
				return attributes[i];
		}
		return null;
	}

	public Attribute getZoneState() {

		return zoneState;
	}

	public Attribute getZoneType() {

		return zoneType;
	}

	public Attribute getZoneStatus() {

		return zoneStatus;
	}

	public Attribute getIASCIEAddress() {

		return iasCIEaddress;
	}

	public ZoneEnrollResponse zoneEnrollRequest(ZoneEnrollRequestPayload payload) throws ZigBeeHAException {

		try {
			ZoneEnrollResponse response = (ZoneEnrollResponse) cluster.zoneEnrollRequest(payload);
			return response;
		} 
		catch (ZigBeeClusterException e) {
			throw new ZigBeeHAException(e);
		}
	}

	public Response zoneStatusChangeNotification(ZoneStatusChangeNotificationPayload payload) throws ZigBeeHAException {
		try {
			Response response = cluster.zoneStatusChangeNotification(payload);
			if (response.getZCLHeader().getCommandId() != ZoneStatusChangeNotificationResponse.ID)
				throw new ZigBeeHAException( ((DefaultResponse) response).getStatus().toString());

			return response;
		} catch (ZigBeeClusterException e) {
			throw new ZigBeeHAException(e);
		}
	}

	public boolean addZoneStatusChangeNotificationListener(ZoneStatusChangeNotificationListener listener) {
		return cluster.addZoneStatusChangeNotificationListener(listener);
	}

	public boolean removeZoneStatusChangeNotificationListener(ZoneStatusChangeNotificationListener listener) {
		return cluster.removeZoneStatusChangeNotificationListener(listener);
	}
}
/*
 Copyright 2008-2014 ITACA-TSB, http://www.tsb.upv.es
 Instituto Tecnologico de Aplicaciones de Comunicacion 
 Avanzadas - Grupo Tecnologias para la Salud y el 
 Bienestar (TSB)

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

package org.universAAL.hw.exporter.zigbee.ha.devices;

import it.cnr.isti.zigbee.zcl.library.api.core.ZigBeeClusterException;
import it.cnr.isti.zigbee.zcl.library.api.security_safety.ias_zone.ZoneStatusChangeNotificationListener;

import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.lddi.zigbee.commissioning.devices.api.IAS_ZoneAAL;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.ContactSensor;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.phThing.DeviceService;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class IASZoneCallee extends ExporterSensorCallee implements
	ZoneStatusChangeNotificationListener {
    static {
	NAMESPACE = "http://ontology.universAAL.org/ZBIASZoneService.owl#";
    }

    private IAS_ZoneAAL zbDevice;
    private ContactSensor ontologyDevice;
    private DefaultContextPublisher cp;

    /**
     * Constructor to be used in the exporter, which sets up all the exporting
     * process.
     * 
     * @param context
     *            The OSGi context
     * @param serv
     *            The OSGi service backing the interaction with the device in
     *            the abstraction layer
     */
    public IASZoneCallee(ModuleContext context, IAS_ZoneAAL serv) {
	super(context, new ServiceProfile[]{});
	LogUtils.logDebug(Activator.moduleContext, IASZoneCallee.class,
		"IASZoneCallee",
		new String[] { "Ready to subscribe" }, null);
	zbDevice = serv;

	// Info Setup
	// TODO replace the deprecated
	String deviceSuffix = zbDevice.getZBDevice().getUniqueIdenfier()
		.replace("\"", "");
	String deviceURI = NAMESPACE + "sensor" + deviceSuffix;
	ontologyDevice = new ContactSensor(deviceURI);
	ontologyDevice.setValue(StatusValue.NoCondition);
	// TODO Skip location and attachment for now
	// String locationSuffix = Activator.getProperties().getProperty(
	// deviceSuffix);
	// if (locationSuffix != null
	// && !locationSuffix.equals(Activator.UNINITIALIZED_SUFFIX)) {
	// ontologyDevice
	// .setLocation(new Room(
	// Constants.uAAL_MIDDLEWARE_LOCAL_ID_PREFIX
	// + locationSuffix));
	// } else {
	// Properties prop = Activator.getProperties();
	// prop.setProperty(deviceSuffix, Activator.UNINITIALIZED_SUFFIX);
	// Activator.setProperties(prop);
	// }

	//Service reg
	newProfiles = getServiceProfiles(NAMESPACE, DeviceService.MY_URI,
		ontologyDevice);
	this.addNewServiceProfiles(newProfiles);

	// Context reg
	ContextProvider info = new ContextProvider(NAMESPACE
		+ "zbIASZoneContextProvider");
	info.setType(ContextProviderType.gauge);
	ContextEventPattern cep=new ContextEventPattern();
	cep.addRestriction(MergedRestriction
		    .getFixedValueRestriction(
			    ContextEvent.PROP_RDF_SUBJECT,
			    ontologyDevice));
	cep.addRestriction(MergedRestriction
		    .getFixedValueRestriction(
			    ContextEvent.PROP_RDF_PREDICATE,
			    ContactSensor.PROP_HAS_VALUE));
	info.setProvidedEvents(new ContextEventPattern[]{cep});
	cp = new DefaultContextPublisher(context, info);

	// ZB device subscription
	if (zbDevice.getIASZone().addZoneStatusChangeNotificationListener(this)) {
	    LogUtils.logDebug(Activator.moduleContext, IASZoneCallee.class,
		    "IASZoneCallee", new String[] { "Subscribed" }, null);
	} else {
	    LogUtils.logDebug(Activator.moduleContext, IASZoneCallee.class,
		    "IASZoneCallee", new String[] { "Failed to Subscribe!!!" },
		    null);
	}
    }

    @Override
    protected ServiceResponse getValue() {
	LogUtils.logDebug(Activator.moduleContext, IASZoneCallee.class,
		"getPresence",
		new String[] { "The service called was 'get the status'" },
		null);
	ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
	Boolean finalValue = new Boolean(false);
	try {
	    finalValue = (Boolean) zbDevice.getIASZone().getZoneStatus()
		    .getValue();
	} catch (ZigBeeClusterException e) {
	    LogUtils.logError(
		    Activator.moduleContext,
		    IASZoneCallee.class,
		    "getPresence",
		    new String[] { "Error getting the value: ZB error" },
		    e);
	    ServiceResponse response = new ServiceResponse(
		    CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "ZB error!"));
	    return response;
	} catch (ClassCastException e) {
	    LogUtils.logError(
		    Activator.moduleContext,
		    IASZoneCallee.class,
		    "getPresence",
		    new String[] { "Error getting the value: Unexpected value" },
		    e);
	    ServiceResponse response = new ServiceResponse(
		    CallStatus.serviceSpecificFailure);
	    response.addOutput(new ProcessOutput(
		    ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
		    "Unexpected value!"));
	    return response;
	}
	sr.addOutput(new ProcessOutput(NAMESPACE + OUT_GET_VALUE,
		finalValue));
	return sr;
    }

    public void zoneStatusChangeNotification(short arg0) {
	LogUtils.logDebug(Activator.moduleContext,
		PresenceDetectorCallee.class, "zoneStatusChangeNotification",
		new String[] { "Changed-Event received: "+ arg0 }, null);
	ontologyDevice.setValue(arg0 > 0 ? StatusValue.Activated
		: StatusValue.NotActivated);
	cp.publish(new ContextEvent(ontologyDevice,
		ContactSensor.PROP_HAS_VALUE));
    }
}

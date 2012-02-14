/*
 Copyright 2008-2011 ITACA-TSB, http://www.tsb.upv.es
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

package org.universAAL.hw.exporter.zigbee.ha.services;

import java.util.Hashtable;

import org.universAAL.middleware.owl.Enumeration;
import org.universAAL.middleware.owl.Restriction;
import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.lighting.ElectricLight;
import org.universAAL.ontology.lighting.LightSource;

/**
 * Ontological service that controls a specific exported HW device. Methods
 * included in this class are the mandatory ones for representing an ontological
 * service in Java classes for uAAL.
 * 
 * @author alfiva
 */
public class OnOffLightService extends DeviceService {
    public static final String LIGHTING_SERVER_NAMESPACE = "http://ontology.igd.fhg.de/ZBLightingServer.owl#";
    public static final String MY_URI = LIGHTING_SERVER_NAMESPACE
	    + "ZBLightingService";

    public static final String SERVICE_TURN_OFF = LIGHTING_SERVER_NAMESPACE
	    + "turnOff";
    public static final String SERVICE_TURN_ON = LIGHTING_SERVER_NAMESPACE
	    + "turnOn";
    public static final String SERVICE_GET_ON_OFF = LIGHTING_SERVER_NAMESPACE
	    + "getOnOff";

    public static final String INPUT_LAMP = LIGHTING_SERVER_NAMESPACE
	    + "inputDimmerLamp";

    public static final String OUTPUT_LAMP_BRIGHTNESS = LIGHTING_SERVER_NAMESPACE
	    + "brightness";

    public static final ServiceProfile[] profiles = new ServiceProfile[3];
    private static Hashtable serverRestrictions = new Hashtable();
    static {
	register(OnOffLightService.class);
	addRestriction(Restriction.getAllValuesRestriction(PROP_CONTROLS,
		LightSource.MY_URI), new String[] { PROP_CONTROLS },
		serverRestrictions);
	addRestriction(Restriction.getFixedValueRestriction(
		LightSource.PROP_HAS_TYPE, ElectricLight.lightBulb),
		new String[] { DeviceService.PROP_CONTROLS,
			LightSource.PROP_HAS_TYPE }, serverRestrictions);
	addRestriction(Restriction.getAllValuesRestrictionWithCardinality(
		LightSource.PROP_SOURCE_BRIGHTNESS, new Enumeration(
			new Integer[] { new Integer(0), new Integer(100) }), 1,
		1), new String[] { DeviceService.PROP_CONTROLS,
		LightSource.PROP_SOURCE_BRIGHTNESS }, serverRestrictions);

	PropertyPath brightnessPath = new PropertyPath(null, true,
		new String[] { DeviceService.PROP_CONTROLS,
			LightSource.PROP_SOURCE_BRIGHTNESS });

	OnOffLightService getOnOff = new OnOffLightService(SERVICE_GET_ON_OFF);
	profiles[0] = getOnOff.getProfile();
	ProcessOutput output = new ProcessOutput(OUTPUT_LAMP_BRIGHTNESS);
	output.setCardinality(1, 1);
	profiles[0].addOutput(output);
	profiles[0].addSimpleOutputBinding(output, brightnessPath.getThePath());

	OnOffLightService turnOff = new OnOffLightService(SERVICE_TURN_OFF);
	profiles[1] = turnOff.getProfile();
	profiles[1]
		.addChangeEffect(brightnessPath.getThePath(), new Integer(0));

	OnOffLightService turnOn = new OnOffLightService(SERVICE_TURN_ON);
	profiles[2] = turnOn.getProfile();
	profiles[2].addChangeEffect(brightnessPath.getThePath(), new Integer(
		100));
    }

    private OnOffLightService(String uri) {
	super(uri);
    }
}

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

package org.universAAL.hw.exporter.zigbee.ha.services;

//import java.util.Hashtable;

import org.universAAL.middleware.rdf.PropertyPath;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.home.PresenceDetector;
import org.universAAL.ontology.phThing.DeviceService;

/**
 * Ontological service that controls a specific exported HW device. Methods
 * included in this class are the mandatory ones for representing an ontological
 * service in Java classes for uAAL.
 * 
 * @author alfiva
 */
public class PresenceDetectorService extends DeviceService {
    public static final String PRESENCE_SERVER_NAMESPACE = "http://ontology.igd.fhg.de/ZBPresenceServer.owl#";
    public static final String MY_URI = PRESENCE_SERVER_NAMESPACE
	    + "ZBPresenceService";

    public static final String SERVICE_GET_PRESENCE = PRESENCE_SERVER_NAMESPACE
	    + "getPresence";

    public static final String OUTPUT_PRESENCE = PRESENCE_SERVER_NAMESPACE
	    + "presence";

    public static final ServiceProfile[] profiles = new ServiceProfile[1];
//    private static Hashtable serverRestrictions = new Hashtable();
    static {
	/* Temporarily out, with ne data representation...
	addRestriction(Restriction.getAllValuesRestriction(PROP_CONTROLS,
		PresenceDetector.MY_URI), new String[] { PROP_CONTROLS },
		serverRestrictions);
	*/
	PropertyPath presencePath = new PropertyPath(null, true, new String[] {
		DeviceService.PROP_CONTROLS,
		PresenceDetector.PROP_MEASURED_VALUE });

	PresenceDetectorService getPresence = new PresenceDetectorService(
		SERVICE_GET_PRESENCE);
	profiles[0] = getPresence.getProfile();
	ProcessOutput output = new ProcessOutput(OUTPUT_PRESENCE);
	output.setCardinality(1, 1);
	profiles[0].addOutput(output);
	profiles[0].addSimpleOutputBinding(output, presencePath.getThePath());
    }

    protected PresenceDetectorService(String uri) {
	super(uri);
    }
}
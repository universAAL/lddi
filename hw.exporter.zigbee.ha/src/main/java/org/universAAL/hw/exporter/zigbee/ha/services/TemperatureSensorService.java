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
import org.universAAL.ontology.phThing.DeviceService;
import org.universAAL.ontology.weather.TempSensor;

/**
 * Ontological service that controls a specific exported HW device. Methods
 * included in this class are the mandatory ones for representing an ontological
 * service in Java classes for uAAL.
 * 
 * @author alfiva
 */
public class TemperatureSensorService extends DeviceService {
    public static final String SERVER_NAMESPACE = "http://ontology.igd.fhg.de/ZBTemperatureServer.owl#";
    public static final String MY_URI = SERVER_NAMESPACE
	    + "ZBTemepratureService";

    public static final String SERVICE_GET_VALUE = SERVER_NAMESPACE
	    + "getValue";

    public static final String OUTPUT_VALUE = SERVER_NAMESPACE + "value";

    public static final ServiceProfile[] profiles = new ServiceProfile[1];
//    private static Hashtable serverRestrictions = new Hashtable();
    static {
	/* Temporarily out, with new data representation...
	addRestriction(Restriction.getAllValuesRestriction(PROP_CONTROLS,
		TempSensor.MY_URI), new String[] { PROP_CONTROLS },
		serverRestrictions);
	*/
	PropertyPath path = new PropertyPath(null, true, new String[] {
		DeviceService.PROP_CONTROLS, TempSensor.PROP_MEASURED_VALUE });

	TemperatureSensorService getValue = new TemperatureSensorService(
		SERVICE_GET_VALUE);
	profiles[0] = getValue.getProfile();
	ProcessOutput output = new ProcessOutput(OUTPUT_VALUE);
	output.setCardinality(1, 1);
	profiles[0].addOutput(output);
	profiles[0].addSimpleOutputBinding(output, path.getThePath());

    }

    protected TemperatureSensorService(String uri) {
	super(uri);
    }

}

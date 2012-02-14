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

import org.universAAL.middleware.owl.DataRepOntology;
import org.universAAL.middleware.owl.OntClassInfoSetup;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.phThing.DeviceService;

public class HAServicesOntology extends Ontology {

    /**
     * Factory for serialization.
     */
    private static HAServiceFactory factory = new HAServiceFactory();

    /**
     * Ontology domain namespace.
     */
    public static final String NAMESPACE = "http://ontology.universAAL.org/ContextHistory.owl#";

    /**
     * Main constructor.
     * 
     * @param ontURI
     *            Domain namespace
     */
    public HAServicesOntology(String ontURI) {
	super(ontURI);
    }

    /**
     * Constructor that automatically sets namespace.
     */
    public HAServicesOntology() {
	super(NAMESPACE);
    }

    @Override
    public void create() {
	Resource r = getInfo();
	r.setResourceComment("The ontology defining the ZB HA Services exported");
	r.setResourceLabel("ZigBee HA");
	addImport(DataRepOntology.NAMESPACE);

	OntClassInfoSetup oci;

	// Load DimemrLightServices
	oci = createNewOntClassInfo(DimmerLightService.MY_URI, factory, 0);
	oci.setResourceComment("The class of services managing dimmable lights through ZigBee technology");
	oci.setResourceLabel("Dimmer Light Services");
	oci.addSuperClass(DeviceService.MY_URI);

	// Load OnOffLightServices
	oci = createNewOntClassInfo(OnOffLightService.MY_URI, factory, 1);
	oci.setResourceComment("The class of services managing on/off lights through ZigBee technology");
	oci.setResourceLabel("Dimmer Light Services");
	oci.addSuperClass(DeviceService.MY_URI);

	// Load PresenceDetectorServices
	oci = createNewOntClassInfo(PresenceDetectorService.MY_URI, factory, 2);
	oci.setResourceComment("The class of services managing presence detectors through ZigBee technology");
	oci.setResourceLabel("Dimmer Light Services");
	oci.addSuperClass(DeviceService.MY_URI);

	// Load TempSensorServices
	oci = createNewOntClassInfo(TemperatureSensorService.MY_URI, factory, 3);
	oci.setResourceComment("The class of services managing temperature sensor through ZigBee technology");
	oci.setResourceLabel("Dimmer Light Services");
	oci.addSuperClass(DeviceService.MY_URI);
    }

}

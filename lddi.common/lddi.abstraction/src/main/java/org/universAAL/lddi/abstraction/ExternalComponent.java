/*
	Copyright 2013-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research

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
package org.universAAL.lddi.abstraction;

import java.util.Enumeration;
import java.util.Hashtable;

import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.ontology.lddi.config.datapoints.ExternalDataConverter;
import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.phThing.PhysicalThing;

/**
 * <p>
 * An external component is a hard- or software component that is outside the
 * universAAL network and can be reached via a {@link CommunicationGateway
 * gateway}.
 * </p>
 *
 * <p>
 * Instances of this class should be created only by instances of
 * {@link CommunicationGateway}. The gateway that have created an instance can
 * be fetched by calling {@link #getGateway()}, which is important for the
 * instances of {@link ComponentIntegrator} when they are notified by gateways
 * about the existence of external components.
 * </p>
 *
 * <p>
 * To support gateways in mapping the ontological way of "addressing" in
 * universAAL to the kind of addressing in the external network, this class
 * provides the methods {@link #addPropMapping(String, ExternalDatapoint)} and
 * {@link #getDatapoint(String)} in terms of mapping ontological properties to
 * read- and writable datapoints. Gateways are expected to use this class as
 * means for grouping external datapoints that correspond to a distinct
 * component of a distinct type. Then, in each external component, all
 * properties mapped to the selected group of external datapoints must be
 * defined for that type in the used ontology. This type must be set by gateways
 * and can be fetched by calling {@link #getTypeURI()}. Thinking in terms of
 * devices, this means that all properties must belong to a single device whose
 * type is returned by {@link #getTypeURI()}.
 * </p>
 *
 */
public class ExternalComponent {

	// private static int noOfComponents = 0;

	private CommunicationGateway gw;
	private ManagedIndividual ontResource;
	private ExternalDataConverter converter;
	private Hashtable<String, ExternalDatapoint> propMappings = new Hashtable<String, ExternalDatapoint>();

	/**
	 * The constructor has been made 'protected' in order to force communication
	 * gateways to create own subclasses so that each communication gateway can
	 * create only instances defined by itself.
	 */
	public ExternalComponent(CommunicationGateway gw, ManagedIndividual description, ExternalDataConverter converter) {
		if (gw == null  ||  description == null  ||  converter == null)
			throw new NullPointerException("ExternalComponent constructor: parameter null!");

		this.gw = gw;
		this.converter = converter;
//		String componentURI = gw.getComponentURIprefix()
//				+ typeURI.substring(typeURI.lastIndexOf('#')+1)
//				+ (noOfComponents++);
		// ontResource = ManagedIndividual.getInstance(typeURI, componentURI);
		ontResource = description;
	}

	public void addPropMapping(String propURI, ExternalDatapoint datapoint) {
		if (propURI == null || datapoint == null)
			return;

		propMappings.put(propURI, datapoint);
	}
	
	Object changeProperty(String propURI, Object value) {
		Object oldVal = ontResource.getProperty(propURI);
		ontResource.changeProperty(propURI, converter.importValue(value, getTypeURI(), propURI));
		return oldVal;
	}
	
	public Enumeration<ExternalDatapoint> enumerateDatapoints() {
		return propMappings.elements();
	}
	
	public Enumeration<String> enumerateProperties() {
		return propMappings.keys();
	}

	public String getComponentURI() {
		return ontResource.getURI();
	}

	public ExternalDatapoint getDatapoint(String propURI) {
		return propMappings.get(propURI);
	}

	public CommunicationGateway getGateway() {
		return gw;
	}
	
	public Location getLocation() {
		if (ontResource instanceof PhysicalThing)
			return ((PhysicalThing) ontResource).getLocation();
		return null;
	}
	
	public ManagedIndividual getOntResource() {
		return ontResource;
	}
	
	public Object getPropertyValue(String propURI) {
		if (propURI == null)
			return null;
		ExternalDatapoint edp = propMappings.get(propURI);
		if (edp == null)
			return null;
		Object value = converter.importValue(gw.readValue(edp), getTypeURI(), propURI);
		ontResource.changeProperty(propURI, value);
		return value;
	}

	public String getTypeURI() {
		return ontResource.getClassURI();
	}
	
	public boolean setLocation(String locURI) {
		if (locURI != null  &&  ontResource instanceof PhysicalThing) {
			((PhysicalThing) ontResource).setLocation(new Location(locURI));
			return true;
		}
		return false;
	}
	
	public void setPropertyValue(String propURI, Object value) {
		if (propURI == null)
			return;
		ExternalDatapoint edp = propMappings.get(propURI);
		if (edp == null)
			ontResource.changeProperty(propURI, null);
		else {
			Object exValue = converter.exportValue(getTypeURI(), propURI, value);
			gw.writeValue(edp, exValue);
			Object check = gw.readValue(edp);
			if (value == null) {
				if (check != null)
					return;
			} else if (!exValue.equals(check))
				return;
			ontResource.changeProperty(propURI, value);
		}
	}
}

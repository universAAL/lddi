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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.ontology.lddi.config.datapoints.ExternalTypeSystem;
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
public final class ExternalComponent {

	private CommunicationGateway gw;
	private ManagedIndividual ontResource;
	ExternalDataConverter converter;
	private Hashtable<String, ExternalDatapoint> propMappings = new Hashtable<String, ExternalDatapoint>();

	/**
	 * The constructor has been made 'protected' in order to force communication
	 * gateways to create own subclasses so that each communication gateway can
	 * create only instances defined by itself.
	 */
	public ExternalComponent(CommunicationGateway gw, ManagedIndividual description, String externaltypeSystem) {
		if (gw == null  ||  description == null  ||  externaltypeSystem == null)
			throw new NullPointerException("ExternalComponent constructor: parameter null!");

		this.gw = gw;
		ontResource = description;
		converter = CommunicationGateway.edConverters.get(externaltypeSystem);
	}
	
	public ExternalTypeSystem getExternalTypeSystem() {
		for (String uri : CommunicationGateway.edConverters.keySet())
			if (CommunicationGateway.edConverters.get(uri) == converter)
				return ExternalTypeSystem.getLocallyRegisteredInstanceByURI(uri);
		return null;
	}

	public void addPropMapping(String propURI, ExternalDatapoint datapoint) {
		if (propURI == null || datapoint == null)
			return;

		propMappings.put(propURI, datapoint);
	}
	
	Object changeProperty(String propURI, Object value) {
		Object oldVal = ontResource.getProperty(propURI);
		return ontResource.changeProperty(propURI, converter.importValue(value, getTypeURI(), propURI))?
				oldVal  :  Float.NaN;
	}
	
	public String currentValueAsString(String propURI) {
		return converter.toString(getTypeURI(), propURI, ontResource.getProperty(propURI));
	}
	
	Collection<ExternalDatapoint> datapoints() {
		return propMappings.values();
	}
	
	Hashtable<Object, URL> enumerateAltValues(ExternalDatapoint dp) {
		try {
			return (dp == null  ||  dp.getComponent() != this)?  null
					: converter.getAlternativeValues(ontResource.getClassURI(), dp.getProperty());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
	
	public String getExternalValue(String propURI, String pullAddress) {
		if (propURI == null  ||  pullAddress == null)
			return null;
		
		return converter.toString(getTypeURI(), propURI, converter.importValue(gw.getValue(pullAddress), getTypeURI(), propURI));
	}
	
	public CommunicationGateway getGateway() {
		return gw;
	}
	
	Object getInitialValue(ExternalDatapoint dp) {
		return (dp == null  ||  dp.getComponent() != this)?  null
				: converter.getInitialValue(ontResource.getClassURI(), dp.getProperty());
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
			return ontResource.getProperty(propURI);
		Object value = converter.importValue(gw.readValue(edp), getTypeURI(), propURI);
		ontResource.changeProperty(propURI, value);
		return value;
	}
	
	public String getTypeURI() {
		return ontResource.getClassURI();
	}

	public String internalValueAsString(String propURI, Object value) {
		return converter.toString(getTypeURI(), propURI, value);
	}
	
	public Object internalValueOf(String propURI, String valStr) {
		return converter.valueOf(valStr, getTypeURI(), propURI);
	}
	
	boolean isPercentage(ExternalDatapoint dp) {
		return (dp == null  ||  dp.getComponent() != this)?  false
				: converter.isPercentage(ontResource.getClassURI(), dp.getProperty());
	}
	
	public void setDatapointValue(String propURI, String setAddress, String value) {
		if (propURI != null  &&  setAddress != null) {
			Object o = converter.exportValue(getTypeURI(), propURI,
					converter.valueOf(value, getTypeURI(), propURI));
			if (o != null)
				gw.setValue(setAddress, o);
		}
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

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

import java.util.Hashtable;

/**
 * <p>An external component is a hard- or software component that is outside the universAAL network
 * and can be reached via a {@link CommunicationGateway gateway}.</p>
 * 
 * <p>Instances of this class should be created only by instances of {@link CommunicationGateway}.
 * The gateway that have created an instance can be fetched by calling {@link #getGateway()}, which
 * is important for the instances of {@link ComponentIntegrator} when they are notified by gateways
 * about the existence of external components.</p>
 * 
 * <p>To support gateways in mapping the ontological way of "addressing" in universAAL to the kind of addressing
 * in the external network, this class provides the methods {@link #addPropMapping(String, ExternalDataPoint)}
 * and {@link #getDataPoint(String)} in terms of mapping ontological properties to read- and writable data
 * points. Gateways are expected to use this class as means for grouping external data points that correspond
 * to a distinct component of a distinct type. Then, in each external component, all properties mapped to
 * the selected group of external data points must be defined for that type in the used ontology. This type must
 * be set by gateways and can be fetched by calling {@link #getTypeURI()}. Thinking in terms of devices, this
 * means that all properties must belong to a single device whose type is returned by {@link #getTypeURI()}.</p>
 *
 */
public class ExternalComponent {

	private CommunicationGateway gw;
	private String typeURI = null;
	private Hashtable<String,ExternalDataPoint> propMappings = null;

	protected ExternalComponent(CommunicationGateway gw, String typeURI) {
		if (gw == null  ||  typeURI == null)
			throw new NullPointerException("ExternalComponent constructor: parameter null!");
		
		this.gw = gw;
		this.typeURI = typeURI;
	}
	
	public void addPropMapping(String propURI, ExternalDataPoint dataPoint) {
		if (propURI == null  ||  dataPoint == null)
			return;
		
		if (propMappings == null)
			propMappings = new Hashtable<String, ExternalDataPoint>();
		
		propMappings.put(propURI, dataPoint);
	}
	
	public ExternalDataPoint getDataPoint(String propURI) {
		return (propMappings==null)? null : propMappings.get(propURI);
	}
	
	public CommunicationGateway getGateway() {
		return gw;
	}
	
	public String getTypeURI() {
		return typeURI;
	}
}

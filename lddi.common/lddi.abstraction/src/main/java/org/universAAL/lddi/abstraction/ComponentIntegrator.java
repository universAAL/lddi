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
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.SharedObjectListener;
import org.universAAL.middleware.owl.ManagedIndividual;

/**
 * As a "specialist" for certain component types, it cooperates with all
 * {@link CommunicationGateway communication gateways} that have access to
 * external components of those types in order to integrate such external
 * components in the universAAL network by (1) publishing related events to the
 * universAAL context bus and / or (2) registering related service profiles to
 * the universAAL service bus.
 *
 * For a better understanding, please refer to both the package documentation
 * and the documentation of the methods further below.
 */
public abstract class ComponentIntegrator implements SharedObjectListener {

	/**
	 * A constant string that can be used by component integrators to map the
	 * ontological representation of an external component to its corresponding
	 * original {@link ExternalComponent}.
	 */
	public static final String PROP_CORRESPONDING_EXTERNAL_COMPONENT = "universAAL:lddi.abstraction/ComponentIntegrator#correspondingExternalComponent";
	
//	/**
//	 * Mapping the URI of components to their ontological representation.
//	 * "disconnected" in the name should remind us that (1) setting properties of these ontological resources
//	 * are not reflected in the external component, and (2) getting property values of these resources has the
//	 * risk of not being reading the latest value as changes to the external components may not have found the
//	 * chance to be reflected in these "copies".
//	 */
//	private Hashtable<String, ManagedIndividual> disconnectedOntResources = new Hashtable<String, ManagedIndividual>();
	
	/**
	 * Mapping the URI of components to the original representation of the related external components.
	 * "connected" in the name should remind that (1) getting property values of these representations will lead
	 * to actually accessing the external component and reading the related current value, and (2) setting
	 * properties of these representations will cause to a change of the value of the related properties of
	 * the actual external component.
	 */
	private Hashtable<String, ExternalComponent> connectedComponents = new Hashtable<String, ExternalComponent>();
	
	private HashSet<String> myTypes = new HashSet<String>();
	
	protected void init(ModuleContext mc, Object[] containerSpecificFetchParams, String[] myTypes) {
		for (String type : myTypes)
			this.myTypes.add(type);
		
		Object[] registeredGateways = mc.getContainer().fetchSharedObject(mc, containerSpecificFetchParams, this);
		if (registeredGateways != null )
			for (Object o : registeredGateways) {
				if (o instanceof CommunicationGateway)
					for (String type: myTypes)
						((CommunicationGateway) o).register(type, this);
			}
	}
	
	/**
	 * Used by {@link CommunicationGateway communication gateways} to share with
	 * this integrator the external components reachable through that gateway
	 * after this integrator
	 * {@link CommunicationGateway#register(String, ComponentIntegrator)
	 * subscribes} to them for external components of certain types. Therefore,
	 * the array of components provided here as parameter is expected to contain
	 * only external components already subscribed for. Additionally, the
	 * components in the array must already contain the mapping of ontological
	 * properties to external datapoints so that this integrator can make use of
	 * them when utilizing some of the other methods of the gateway.
	 */
	public void componentsAdded(ExternalComponent[] components) {
		if (components == null  ||  components.length == 0)
			return;
			
		for (ExternalComponent ec : components)
			if (myTypes.contains(ec.getTypeURI()))
				connectedComponents.put(ec.getComponentURI(), ec);
	}
	
	public void componentsRemoved(ExternalComponent[] components) {
		if (components == null  ||  components.length == 0)
			return;
			
		for (ExternalComponent ec : components)
			connectedComponents.remove(ec.getComponentURI());
	}
	
	public void componentsReplaced(ExternalComponent[] components) {
		if (components == null  ||  components.length == 0)
			return;
		
		// remove only ecternal components comming fromthe same communication gateway
		CommunicationGateway cgw = components[0].getGateway();
		for (Iterator<Entry<String, ExternalComponent>> i=connectedComponents.entrySet().iterator(); i.hasNext();)
			if (i.next().getValue().getGateway() == cgw)
				i.remove();
			
		for (ExternalComponent ec : components)
			if (myTypes.contains(ec.getTypeURI()))
				connectedComponents.put(ec.getComponentURI(), ec);
	}
	
	public abstract void componentsUpdated(ExternalComponent[] components);
	
	protected final ManagedIndividual getOntResourceByURI(String uriString) {
		return connectedComponents.get(uriString).getOntResource();
	}
	
	protected final void getAllOntResources(List<ManagedIndividual> targetList) {
		for (Enumeration<ExternalComponent> e = connectedComponents.elements(); e.hasMoreElements();)
			targetList.add(e.nextElement().getOntResource());
	}
	
	protected final void getOntResourcesByType(String type, List<ManagedIndividual> targetList) {
		for (Enumeration<ExternalComponent> e = connectedComponents.elements(); e.hasMoreElements();) {
			ExternalComponent ec = e.nextElement();
			if (ec.getTypeURI().equals(type))
				targetList.add(ec.getOntResource());
		}
	}

	/**
	 * Used by {@link CommunicationGateway communication gateways} to notify the
	 * integrator about the change of the value of a datapoint that is within
	 * the scope of a previous subscription of this integrator to the notifying
	 * gateway, no matter if the subscription was done by calling
	 * {@link CommunicationGateway#startEventing(ComponentIntegrator, ExternalDatapoint, byte)}
	 * or any of the wildcarding versions of it.
	 *
	 * @param datapoint
	 *            the datapoint whose value has changed; the integrator can use
	 *            {@link ExternalDatapoint#getComponent()} and
	 *            {@link ExternalDatapoint#getProperty()} for getting the
	 *            ontological info needed for further processing the event
	 *            (mostly for publishing a context event onto the context bus).
	 * @param value
	 *            The new value of the given datapoint.
	 */
	public abstract void processEvent(ExternalDatapoint datapoint, Object value);
	
	private Hashtable<Object, CommunicationGateway> discoveredGateways = new Hashtable<Object, CommunicationGateway>();

	public void sharedObjectAdded(Object sharedObj, Object removeHook) {
		if (sharedObj instanceof CommunicationGateway) {
			discoveredGateways.put(removeHook, (CommunicationGateway) sharedObj);
			for (String type: myTypes)
				((CommunicationGateway) sharedObj).register(type, this);
		}
	}

	public void sharedObjectRemoved(Object removeHook) {
		CommunicationGateway cgw = discoveredGateways.remove(removeHook);
		if (cgw != null)
			for (Iterator<Entry<String, ExternalComponent>> i=connectedComponents.entrySet().iterator(); i.hasNext();)
				if (i.next().getValue().getGateway() == cgw)
					i.remove();
	}
	
	protected final Object getExternalValue(String componentURI, String propertyURI) {
		if (componentURI == null  ||  propertyURI == null)
			return null;
		
		ExternalComponent ec = connectedComponents.get(componentURI);
		return (ec == null)? null : ec.getPropertyValue(propertyURI);
	}
	
	protected final Object fetchProperty(ManagedIndividual ontResource, String propURI) {
		if (ontResource == null  ||  propURI == null)
			return null;
		
		ExternalComponent ec = connectedComponents.get(ontResource.getURI());
		return (ec == null)? null : ec.getPropertyValue(propURI);
	}
	
	protected final void setExternalValue(String componentURI, String propertyURI, Object value) {
		if (componentURI == null  ||  propertyURI == null)
			return;
		
		ExternalComponent ec = connectedComponents.get(componentURI);
		if (ec != null)
			ec.setPropertyValue(propertyURI, value);
	}
	
	protected final void updateProperty(ManagedIndividual ontResource, String propertyURI, Object value) {
		if (ontResource == null  ||  propertyURI == null)
			return;
		
		ExternalComponent ec = connectedComponents.get(ontResource.getURI());
		if (ec != null)
			ec.setPropertyValue(propertyURI, value);
	}

}
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
import java.util.Set;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.SharedObjectListener;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.OntClassInfo;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.util.ResourceUtil;
import org.universAAL.ontology.location.Location;

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

	private class Subscription {
		private String typeURI, propURI;
		private int hashCode;
		
		private Subscription(String typeURI, String propURI) {
			this.typeURI = typeURI;
			this.propURI = propURI;
			
			typeURI += propURI;
			hashCode = typeURI.hashCode();
			
			for (Enumeration<CommunicationGateway> e=discoveredGateways.elements(); e.hasMoreElements();)
				subscribe(e.nextElement());
		}
		
		private void subscribe(CommunicationGateway cgw) {
			cgw.startEventing(ComponentIntegrator.this, typeURI, propURI);
		}
		
		private void subscribe(ExternalComponent ec) {
			if (typeURI.equals(ec.getTypeURI()))
				ec.getGateway().startEventing(ComponentIntegrator.this, ec.getDatapoint(propURI));
		}
		
		@Override
		public int hashCode() {
			return hashCode; 
		}
		
		@Override
		public boolean equals(Object other) {
			return other instanceof Subscription
					&&  hashCode == ((Subscription) other).hashCode
					&&  typeURI.equals(((Subscription) other).typeURI)
					&&  propURI.equals(((Subscription) other).propURI);
		}
	}
	
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
	protected Hashtable<String, ExternalComponent> connectedComponents = new Hashtable<String, ExternalComponent>();
	
	protected Hashtable<Object, CommunicationGateway> discoveredGateways = new Hashtable<Object, CommunicationGateway>();
	
	protected Hashtable<String, Hashtable<String, Subscription>> subscriptions = new Hashtable<String, Hashtable<String, Subscription>>();
	
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
	 void componentsAdded(List<ExternalComponent> components) {
		if (components == null  ||  components.isEmpty())
			return;
			
		for (ExternalComponent ec : components) {
			Hashtable<String, Subscription> subs = subscriptions.get(ec.getTypeURI());
			if (subs != null) {
				connectedComponents.put(ec.getComponentURI(), ec);
				for (Subscription s : subs.values())
					s.subscribe(ec);
			}
		}
	}
	
	void componentsRemoved(ExternalComponent[] components) {
		if (components == null  ||  components.length == 0)
			return;
			
		for (ExternalComponent ec : components)
			connectedComponents.remove(ec.getComponentURI());
	}
	
	void componentsUpdated(ExternalComponent[] components) {
		// To-Do
	}
	
	protected final Object fetchProperty(ManagedIndividual ontResource, String propURI) {
		if (ontResource == null  ||  propURI == null)
			return null;
		
		ExternalComponent ec = connectedComponents.get(ontResource.getURI());
		return (ec == null)? null : ec.getPropertyValue(propURI);
	}
	
	protected final Iterator<ManagedIndividual> getAllOntResources() {
		HashSet<ManagedIndividual> targetList = new HashSet<ManagedIndividual>();
		for (Enumeration<ExternalComponent> e = connectedComponents.elements(); e.hasMoreElements();)
			targetList.add(e.nextElement().getOntResource());
		return targetList.iterator();
	}
	
	protected final Object getExternalValue(String componentURI, String propertyURI) {
		if (componentURI == null  ||  propertyURI == null)
			return null;
		
		ExternalComponent ec = connectedComponents.get(componentURI);
		return (ec == null)? null : ec.getPropertyValue(propertyURI);
	}
	
	protected final ManagedIndividual getOntResourceByURI(String uriString) {
		return connectedComponents.get(uriString).getOntResource();
	}
	
	protected final Iterator<ManagedIndividual> getOntResourcesByLocation(Location loc) {
		HashSet<ManagedIndividual> targetList = new HashSet<ManagedIndividual>();
		for (Enumeration<ExternalComponent> e = connectedComponents.elements(); e.hasMoreElements();) {
			ExternalComponent ec = e.nextElement();
			if (loc.greaterEqual(ec.getLocation()))
				targetList.add(ec.getOntResource());
		}
		return targetList.iterator();
	}
	
	protected final Iterator<ManagedIndividual> getOntResourcesByType(String type) {
		HashSet<ManagedIndividual> targetList = new HashSet<ManagedIndividual>();
		for (Enumeration<ExternalComponent> e = connectedComponents.elements(); e.hasMoreElements();) {
			ExternalComponent ec = e.nextElement();
			if (ec.getTypeURI().equals(type))
				targetList.add(ec.getOntResource());
		}
		return targetList.iterator();
	}
	
	private ModuleContext owner = null;
	
	public final void init(ModuleContext mc, String[] myTypes) {
		owner = mc;
		HashSet<String> allTypes = new HashSet<String>();
		for (String type : myTypes) {
			allTypes.addAll(OntologyManagement.getInstance().getNamedSubClasses(type, true, false));
			OntClassInfo oci = OntologyManagement.getInstance().getOntClassInfo(type);
			if (oci == null  ||  !oci.isAbstract())
				allTypes.add(type);
		}
		
		for (String type : allTypes)
			subscriptions.put(type, new Hashtable<String, Subscription>());
		
		Object[] registeredGateways = mc.getContainer().fetchSharedObject(mc, Activator.cgwSharingParams, this);
		if (registeredGateways != null )
			for (Object o : registeredGateways) {
				if (o instanceof CommunicationGateway) {
					discoveredGateways.put(Activator.getSharedObjectRemoveHook(o), (CommunicationGateway) o);
					for (String type: allTypes)
						((CommunicationGateway) o).register(type, this);
				}
			}
	}
	
	protected ContextPublisher myContextPublisher = null;

	/**
	 * Note-1: The implementation is supposed to publish a context event onto the universAAL context bus.
	 * Note-2: the new value is already set for the given <code>ontResource</code> and <code>propURI</code>
	 *         &rarr; if the implementation needs to process the new value, it must fetch it by
	 *         <code>ontResource.getProperty(propURI)</code>.
	 * @param ontResource
	 * @param propURI
	 * @param oldValue
	 */
	protected void propertyChanged(ManagedIndividual ontResource, String propURI, Object oldValue, boolean isReflected, long actualOccurrenceTime, long meanOccurrenceTime) {
		if (myContextPublisher != null) {
			ContextEvent ce = new ContextEvent(ontResource, propURI, isReflected, actualOccurrenceTime, meanOccurrenceTime);
			myContextPublisher.publish(ce);
		} else if (owner != null) {
			StringBuffer sb = new StringBuffer(512);
			sb.append("Ignoring the change of ");
			ResourceUtil.addResource2SB(ontResource, sb);
			sb.append("->").append(propURI);
			
			LogUtils.logInfo(owner, getClass(), "propertyChanged", sb.toString());
		}
		
	}

	protected final void setExternalValue(String componentURI, String propertyURI, Object value) {
		if (componentURI == null  ||  propertyURI == null)
			return;
		
		ExternalComponent ec = connectedComponents.get(componentURI);
		if (ec != null)
			ec.setPropertyValue(propertyURI, value);
	}
	
	public final void sharedObjectAdded(Object sharedObj, Object removeHook) {
		if (sharedObj instanceof CommunicationGateway) {
			discoveredGateways.put(removeHook, (CommunicationGateway) sharedObj);
			for (String type: subscriptions.keySet()) {
				((CommunicationGateway) sharedObj).register(type, this);
				for (Subscription s : subscriptions.get(type).values())
					s.subscribe((CommunicationGateway) sharedObj);
			}
		}
	}
	
	public final void sharedObjectRemoved(Object removeHook) {
		CommunicationGateway cgw = discoveredGateways.remove(removeHook);
		if (cgw != null)
			for (Iterator<Entry<String, ExternalComponent>> i=connectedComponents.entrySet().iterator(); i.hasNext();)
				if (i.next().getValue().getGateway() == cgw)
					i.remove();
	}
	
	protected final synchronized void subscribe(String typeURI, String propURI) {
		if (typeURI != null  &&  propURI != null) {
			Set<String> allTypes = OntologyManagement.getInstance().getNamedSubClasses(typeURI, true, false);
			OntClassInfo oci = OntologyManagement.getInstance().getOntClassInfo(typeURI);
			if (oci == null  ||  !oci.isAbstract())
				allTypes.add(typeURI);
			
			for (String type : allTypes) {
				Hashtable<String, Subscription> subs = subscriptions.get(type);
				if (subs != null) {
					Subscription s = subs.get(propURI);
					if (s == null) {
						s = new Subscription(type, propURI);
						subs.put(propURI, s);
					}
				}
			}
		}
	}
	
	protected final void updateProperty(ManagedIndividual ontResource, String propertyURI, Object value) {
		if (ontResource == null  ||  propertyURI == null)
			return;
		
		ExternalComponent ec = connectedComponents.get(ontResource.getURI());
		if (ec != null)
			ec.setPropertyValue(propertyURI, value);
	}
	
	protected void propertyDeleted(ManagedIndividual mi, String propURI, boolean isReflected, long actualOccurrenceTime, long meanOccurrentTime) {
		StringBuffer sb = new StringBuffer(512);
		sb.append("Ignoring the deletion of ");
		ResourceUtil.addResource2SB(mi, sb);
		sb.append("->").append(propURI);
		
		if (owner != null)
			LogUtils.logInfo(owner, getClass(), "propertyDeleted", sb.toString());
	}

	protected void propertyStoppedToChange(ManagedIndividual mi, String propURI, boolean isReflected, long actualOccurrenceTime, long meanOccurrentTime) {
		StringBuffer sb = new StringBuffer(512);
		sb.append("Ignoring the change stop of ");
		ResourceUtil.addResource2SB(mi, sb);
		sb.append("->").append(propURI);
		
		if (owner != null)
			LogUtils.logInfo(owner, getClass(), "propertyStoppedToChange", sb.toString());
	}
}
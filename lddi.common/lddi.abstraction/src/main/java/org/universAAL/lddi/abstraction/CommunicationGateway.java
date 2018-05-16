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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.universAAL.lddi.abstraction.config.data.CGwDataConfiguration;
import org.universAAL.lddi.abstraction.config.data.ont.CGwDataConfigOntology;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.datarep.SharedResources;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.Ontology;
import org.universAAL.middleware.owl.OntologyManagement;

/**
 * A gateway providing a bridge to a network of external components making it
 * for {@link ComponentIntegrator component integrators} possible to gain access
 * to related {@link ExternalDatapoint external datapoints}.
 *
 * For a better understanding, please refer to both the package documentation
 * and the documentation of the methods further below.
 */
public abstract class CommunicationGateway {
	
	public static final String CGW_CONF_APP_ID = "lddi:abstract:CommunicationGateway";
	public static final String CGW_CONF_APP_PART_DATA_ID = "dataConfParams"; 
	public static final String CGW_CONF_APP_PART_PROTOCOL_ID = "protocolConfParams"; 
	
	public static ConfigurationParameter newCGwConfParam(final String id, final String appPartID, final String description, final MergedRestriction type, final Object defaultVal) {
		return new ConfigurationParameter() {

			public Scope getScope() {
				return Scope.applicationPartScope(id, CGW_CONF_APP_ID, appPartID);
			}

			public String getDescription(Locale loc) {
				return description;
			}

			public MergedRestriction getType() {
				return type;
			}

			public Object getDefaultValue() {
				return defaultVal;
			}
		};
	}
	
	private String componentURIprefix;
	private Ontology cgwDataConfigOnt = new CGwDataConfigOntology();
	// map typeURI to list of components of that type
	private Hashtable<String, ArrayList<ExternalComponent>> discoveredComponents = new Hashtable<String, ArrayList<ExternalComponent>>();
	// remember which integrators are interested in which types of components
	private Hashtable<String, ArrayList<ComponentIntegrator>> registeredIntegrators = new Hashtable<String, ArrayList<ComponentIntegrator>>();
	/**
	 * @see #addDiscoverer(ExternalComponentDiscoverer)
	 */
	private HashSet<ExternalComponentDiscoverer> discoverers = new HashSet<ExternalComponentDiscoverer>(3);
	private Hashtable<ExternalDatapoint, HashSet<ComponentIntegrator>> subscriptions = new Hashtable<ExternalDatapoint, HashSet<ComponentIntegrator>>();
	
	protected CommunicationGateway(ModuleContext mc, Object[] containerSpecificSharingParams, boolean useStandardConfFiles) {
		mc.getContainer().shareObject(mc, this, containerSpecificSharingParams);
		
		String uSpaceURI = SharedResources.getMiddlewareProp(SharedResources.SPACE_URI);
		if (uSpaceURI.endsWith("#"))
			componentURIprefix = uSpaceURI + getClass().getSimpleName();
		else if (uSpaceURI.endsWith("/"))
			componentURIprefix = uSpaceURI + getClass().getSimpleName() + "#";
		else
			componentURIprefix = uSpaceURI + "/" + getClass().getSimpleName() + "#";
		
		OntologyManagement om = OntologyManagement.getInstance();
		om.register(mc, cgwDataConfigOnt);
				
		if (useStandardConfFiles) {
			addDiscoverer(new CGwDataConfiguration(mc, this));
		}
	}
	
	/**
	 * Only subclasses can introduce discoverers, which just serves as a sort of "registered certificate".
	 * Calling "component discovery methods" ({@link #addComponents(List, ExternalComponentDiscoverer) addComponents},
	 * {@link #removeComponents(List, ExternalComponentDiscoverer) removeComponents}, {@link #replaceComponents(List,
	 * ExternalComponentDiscoverer) replaceComponents} and {@link #updateComponents(List, ExternalComponentDiscoverer)
	 * updateComponents}) is only possible, if such a previously "registered certificate" is passed to those methods.
	 * 
	 * @param d
	 */
	protected final void addDiscoverer(ExternalComponentDiscoverer d) {
		if (d != null)
			discoverers.add(d);
	}
	
	public void addComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}
	
	public void removeComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}
	
	public void replaceComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}
	
	public void updateComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}
	
	public final String getComponentURIprefix() {
		return componentURIprefix;
	}

	/**
	 * To be used by {@link ComponentIntegrator component integrators} to
	 * register to this gateway for external components of the given type. The
	 * implementation must (1) add the given integrator to the list of
	 * integrators interested in the given type of components, and (2) call
	 * {@link ComponentIntegrator#processComponents( ExternalComponent[]) the
	 * related notification method} of the integrator with the list of
	 * components of the given type, both for those already known at the time of
	 * registration and at any time in future when new components of the same
	 * type are added to the external network.
	 */
	public final void register(String componentTypeURI, ComponentIntegrator integrator) {
		synchronized (discoverers) {
			ArrayList<ComponentIntegrator> integrators = registeredIntegrators.get(componentTypeURI);
			if (integrator == null) {
				integrators = new ArrayList<ComponentIntegrator>();
				registeredIntegrators.put(componentTypeURI, integrators);
			}
			integrators.add(integrator);
			
			ArrayList<ExternalComponent> components = discoveredComponents.get(componentTypeURI);
			integrator.componentsAdded(components.toArray(new ExternalComponent[components.size()]));
		}
	}

	/**
	 * <p>
	 * Serves as means for subscribing for events related to the changes of the
	 * value of a given property of a given external component by specifying the
	 * related external datapoint. Note that this implies that the external
	 * components made known by gateways to integrators must include the
	 * property mapping to datapoints.
	 * </p>
	 *
	 * <p>
	 * To be used by {@link ComponentIntegrator component integrators} to inform
	 * this gateway about the interest to be notified as soon as the value of
	 * the given external datapoint changes. The implementation must (1) add the
	 * given integrator to the list of integrators interested in the same kind
	 * of events, and (2) call
	 * {@link ComponentIntegrator#processEvent(ExternalDatapoint, Object) the
	 * related notification method} of the integrator both with the current
	 * value at the time of registration and at any time in future when the
	 * value changes.
	 * </p>
	 *
	 * @param integrator
	 *            the integrator registing for notification.
	 * @param datapoint
	 *            the datapoint whose changes of values must fire events.
	 * @param intervalSeconds
	 *            needed when the external communication protocol doesn't
	 *            support "real-time" eventing and hence the gateway has to
	 *            implement the eventing mechanism by pulling the value every n
	 *            seconds and check if it has changed or not. In that case, this
	 *            parameter indicates the related preference of the integrator.
	 * @return An ID for this subscription so that integrators can unsubscribe
	 *         later if need be.
	 */
	public void startEventing(ComponentIntegrator integrator, ExternalDatapoint datapoint,
			byte intervalSeconds) {
		
	}

	/**
	 * Serves as means for subscribing for events related to the changes of the
	 * value of any property of the given external component. Compared to
	 * {@link #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it
	 * wildcards all datapoints within the scope of the given external
	 * component.
	 *
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte)
	 */
	public void startEventing(ComponentIntegrator integrator, ExternalComponent component,
			byte intervalSeconds) {
		
	}

	/**
	 * If the given property URI is not null, serves as means for subscribing
	 * for events related to the changes of the value of the given property of
	 * any external component of the given type; otherwise, with propURI ==
	 * null, it can be used to subscribe for the changes of the value of any
	 * property of any external component of the given type. Compared to
	 * {@link #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it
	 * wildcards all datapoints within the scope of all external components of
	 * the given type, either by selecting those that correspond to a given
	 * property or not.
	 *
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte)
	 */
	public void startEventing(ComponentIntegrator integrator, String componentTypeURI, String propURI,
			byte intervalSeconds) {
//		String subscribeTopic;
//		MQTTComponent[] arr = setOfExternalComponent
//				.get(componentTypeURI);
//		if (propURI == null) {
//			for (MQTTComponent externalComponent : arr) {
//				for (ExternalDatapoint externalDataPoint : externalComponent
//						.getAllDataPoint()) {
//					MQTTDataPoint mqttDataPoint = (MQTTDataPoint) externalDataPoint;
//				    subscribeTopic=mqttDataPoint.getSubscriberTopic();
//				Activator.client.subscribe(mqttDataPoint.getSubscriberTopic());// to do subscribe
//				subscribtionSet.put(subscribeTopic,setOfComponentIntegrato.get(componentTypeURI) );
//				setOfDataPoint.put(subscribeTopic, mqttDataPoint);
//				}
//
//			}
//		} else
//			for (ExternalComponent externalComponent : arr) {
//				 subscribeTopic=((MQTTDataPoint)externalComponent.getDatapoint(propURI)).getSubscriberTopic();
//				Activator.client.subscribe(subscribeTopic); // To do subscribe
//				subscribtionSet.put(subscribeTopic,setOfComponentIntegrato.get(componentTypeURI) );
//				setOfDataPoint.put(subscribeTopic, ((MQTTDataPoint)externalComponent.getDatapoint(propURI)));
//
//			}
	}

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * unsubscribe a subscription done previously.
	 *
	 * @param eventingID
	 *            the ID returned previously when calling
	 *            {@link #startEventing( ComponentIntegrator, ExternalDatapoint, byte)}
	 *            or any of the wildcarding versions of it.
	 */
	public abstract void stopEventing(ComponentIntegrator integrator, ExternalDatapoint datapoint);

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * get the value of a given property of a given external component by
	 * specifying the related external datapoint. Note that this implies that
	 * the external components made known by gateways to integrators must
	 * include the property mapping to datapoints.
	 */
	public abstract Object readValue(ExternalDatapoint datapoint);

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * change the value of a given property of a given external component by
	 * specifying the related external datapoint and the new value. Note that
	 * this implies that the external components made known by gateways to
	 * integrators must include the property mapping to datapoints.
	 */
	public abstract void writeValue(ExternalDatapoint datapoint, Object value);

}
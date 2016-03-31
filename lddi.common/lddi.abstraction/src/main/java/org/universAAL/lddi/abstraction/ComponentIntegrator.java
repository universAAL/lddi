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

/**
 * As a "specialist" for certain component types, it cooperates with all
 * {@link CommunicationGateway communication gateways} that have access to
 * external components of those types in order to integrate such external
 * components in the universAAL network by (1) publishing related events
 * to the universAAL context bus and / or (2) registering related service
 * profiles to the universAAL service bus.
 * 
 * For a better understanding, please refer to both the package documentation and the
 * documentation of the methods further below.
 */
public interface ComponentIntegrator {

	/**
	 * A constant string that can be used by component integrators to map the ontological representation of
	 * an external component to its corresponding original {@link ExternalComponent}.
	 */
	public static final String PROP_CORRESPONDING_COMPONENT = "uAAL:lddi.abstraction/ComponentIntegrator#correspondingComponent";

	/**
	 * Used by {@link CommunicationGateway communication gateways} to share
	 * with this integrator the external components reachable through that
	 * gateway after this integrator {@link CommunicationGateway#register(String,
	 * ComponentIntegrator) subscribes} to them for external components of
	 * certain types. Therefore, the array of components provided here as
	 * parameter is expected to contain only external components already
	 * subscribed for. Additionally, the components in the array must already
	 * contain the mapping of ontological properties to external datapoints
	 * so that this integrator can make use of them when utilizing some of the
	 * other methods of the gateway.
	 */
	public void processComponents(ExternalComponent[] components);

	/**
	 * Used by {@link CommunicationGateway communication gateways} to notify
	 * the integrator about the change of the value of a datapoint that is 
	 * within the scope of a previous subscription of this integrator to the
	 * notifying gateway, no matter if the subscription was done by calling
	 * {@link CommunicationGateway#startEventing(ComponentIntegrator,
	 * ExternalDatapoint, byte)} or any of the wildcarding versions of it.
	 * 
	 * @param datapoint the datapoint whose value has changed; the integrator
	 * can use {@link ExternalDatapoint#getComponent()} and {@link
	 * ExternalDatapoint#getProperty()} for getting the ontological info
	 * needed for further processing the event (mostly for publishing a
	 * context event onto the context bus).
	 * @param value The new value of the given datapoint.
	 */
	public void processEvent(ExternalDatapoint datapoint, Object value);

}
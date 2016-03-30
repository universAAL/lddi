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

/**
 * <p>This package assumes that there are external (meaning not readily connected to universAAL)
 * networking-enabled HW/SW components to which we may need access in a universAAL network.
 * As an LLDI-based approach, this package refines the LDDI abstraction layer the following way
 * based on the assumption that all component type specific models will be provided in term of
 * ontologies on which the access and integration layers will rely:</p>
 * 
 * <p><img src="./doc-files/layers.png" width="600"></p>
 * 
 * <p>As can be seen in the above figure, the main elements in this framework are {@link
 * org.universAAL.lddi.abstraction.CommunicationGateway communication gateways}, {@link
 * org.universAAL.lddi.abstraction.ComponentIntegrator component integrators}, {@link
 * org.universAAL.lddi.abstraction.ExternalComponent external components}, and component
 * type specific ontologies (there are also {@link org.universAAL.lddi.abstraction.ExternalDatapoint
 * external datapoints} as an additional concept that is not included in this package info).</p> 
 * 
 * <p>As indicated by the figure above, this package has a universAAL-specific view; in particular,
 * the abstraction layer of LDDI exists in this package virtually in terms of a set of relevant
 * ontologies that can be chosen to serve as the contract between the communication gateways and
 * the component integrators with regard to whatever is component type specific.</p>
 * 
 * <p>As opposed to component integrators, the extent of dependency of communication gateways to
 * universAAL can be limited to the usage of {@link org.universAAL.middleware.container.Container
 * universAAL containers}. This dependency is needed so that the implemented gateway can be shared
 * within the container using  {@link org.universAAL.middleware.container.Container#shareObject(
 * org.universAAL.middleware.container.ModuleContext, Object, Object[]) the container's shareObject
 * method}. On the other side, component integrators must fetch gateways already shared within the
 * container using {@link org.universAAL.middleware.container.Container#fetchSharedObject(
 * org.universAAL.middleware.container.ModuleContext, Object[], 
 * org.universAAL.middleware.container.SharedObjectListener)} so that in addition they are notified
 * whenever new communication gateways are shared within the container. Component integrators are
 * then expected to register the found gateways using {@link
 * org.universAAL.lddi.abstraction.CommunicationGateway#register(String, ComponentIntegrator) the
 * related method of the gateway}.</p>
 * 
 * <p>Important note: the above implies that with this approach, the interplay of gateways and
 * integrators will work only when they are deployed within the same universAAL container in order
 * to be able to effectively integrate external components into a given universAAL network.</p>
 */
package org.universAAL.lddi.abstraction;

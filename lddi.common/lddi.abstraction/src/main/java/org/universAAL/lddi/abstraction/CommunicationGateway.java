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
 * A gateway providing a bridge to a network of external components making it for
 * {@link ComponentIntegrator component integrators} possible to gain access to
 * related {@link ExternalDatapoint external datapoints}.
 * 
 * For a better understanding, please refer to both the package documentation and the
 * documentation of the methods further below.
 */
public abstract class CommunicationGateway {

	/**
	 * To be used by {@link ComponentIntegrator component integrators} to register to
	 * this gateway for external components of the given type. The implementation must
	 * (1) add the given integrator to the list of integrators interested in the given
	 * type of components, and (2) call {@link ComponentIntegrator#processComponents(
	 * ExternalComponent[]) the related notification method} of the integrator with the
	 * list of components of the given type, both for those already known at the time
	 * of registration and at any time in future when new components of the same type
	 * are added to the external network.
	 */
	public abstract void register(String componentTypeURI, ComponentIntegrator integrator);

	/**
	 * <p>Serves as means for subscribing for events related to the changes of the value
	 * of a given property of a given external component by specifying the related
	 * external datapoint. Note that this implies that the external components made
	 * known by gateways to integrators must include the property mapping to datapoints.</p>
	 * 
	 * <p>To be used by {@link ComponentIntegrator component integrators} to inform this
	 * gateway about the interest to be notified as soon as the value of the given
	 * external datapoint changes. The implementation must (1) add the given integrator
	 * to the list of integrators interested in the same kind of events, and (2) call
	 * {@link ComponentIntegrator#processEvent(ExternalDatapoint, Object) the related
	 * notification method} of the integrator both with the current value at the time
	 * of registration and at any time in future when the value changes.</p>
	 * 
	 * @param integrator the integrator registing for notification.
	 * @param datapoint the datapoint whose changes of values must fire events.
	 * @param intervalSeconds needed when the external communication protocol doesn't
	 * support "real-time" eventing and hence the gateway has to implement the eventing
	 * mechanism by pulling the value every n seconds and check if it has changed or not.
	 * In that case, this parameter indicates the related preference of the integrator.
	 * @return An ID for this subscription so that integrators can unsubscribe later if
	 * need be.
	 */
	public abstract int startEventing(ComponentIntegrator integrator, ExternalDatapoint datapoint, byte intervalSeconds);

	/**
	 * Serves as means for subscribing for events related to the changes of the value
	 * of any property of the given external component. Compared to {@link
	 * #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it wildcards all
	 * datapoints within the scope of the given external component.
	 * 
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte) 
	 */
	public abstract int startEventing(ComponentIntegrator integrator, ExternalComponent component, byte intervalSeconds);

	/**
	 * If the given property URI is not null, serves as means for subscribing
	 * for events related to the changes of the value of the given property of
	 * any external component of the given type; otherwise, with propURI == null,
	 * it can be used to subscribe for the changes of the value of any property
	 * of any external component of the given type. Compared to {@link
	 * #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it wildcards
	 * all datapoints within the scope of all external components of the given
	 * type, either by selecting those that correspond to a given property or not.
	 * 
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte) 
	 */
	public abstract int startEventing(ComponentIntegrator integrator, String componentTypeURI, String propURI, byte intervalSeconds);

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to unsubscribe
	 * a subscription done previously.
	 * @param eventingID the ID returned previously when calling {@link #startEventing(
	 * ComponentIntegrator, ExternalDatapoint, byte)} or any of the wildcarding versions
	 * of it.
	 */
	public abstract void stopEventing(int eventingID);

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to get the
	 * value of a given property of a given external component by specifying the related
	 * external datapoint. Note that this implies that the external components made
	 * known by gateways to integrators must include the property mapping to datapoints.
	 */
	public abstract Object readValue(ExternalDatapoint datapoint);

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to change the
	 * value of a given property of a given external component by specifying the related
	 * external datapoint and the new value. Note that this implies that the external
	 * components made known by gateways to integrators must include the property mapping
	 * to datapoints.
	 */
	public abstract void writeValue(ExternalDatapoint datapoint, Object value);

}
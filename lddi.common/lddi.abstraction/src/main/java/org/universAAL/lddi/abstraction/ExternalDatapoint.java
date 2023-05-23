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
 * <p>
 * An ExternalDatapoint is a readable and / or writable datapoint outside the
 * universAAL network. It is assumed that ontological properties of instances of
 * {@link ExternalComponent} can be mapped to such datapoints.
 * {@link CommunicationGateway Communication gateways} may pack all the info (no
 * matter if a simple address or a complex structure) needed for mapping
 * ontological properties of reachable external components in objects that
 * implement this interface and store / fetch it for their list of components
 * using {@link ExternalComponent#addPropMapping(String, ExternalDatapoint)} /
 * {@link ExternalComponent#getDatapoint(String)}.
 * </p>
 *
 * <p>
 * It is important that gateways map each ontological property of a distinct
 * external component to a single instance of this class. Therefore, in cases
 * that different addresses have to be used for reading and writing, gateways
 * will have to encapsulate this complexity properly within objects that
 * implement this interface. A way to do so, is to define the specific classes
 * for implementing this interface the following way:
 * </p>
 *
 * <p>
 * <img src="./doc-files/dpHierarchy.png" width="600" height="450" alt="a
 * possible hierarchy of classes implementing ExternalDatapoint">
 * </p>
 *
 * <p>
 * Another important point is related to the info needed for realizing the read
 * and write actions; in the above text, this was most of the times reduced only
 * to the addressing aspect, but often also type info as well as hints for
 * converting the external values to values compatible with the used ontological
 * model have to be included. On the other hand, even the "addressing" aspect
 * may not be resolved by just of an integer value. As a simple example, a
 * modbus gateway needs in addition to an address also the size of each read or
 * write access in bits / bytes / words.
 * </p>
 *
 */
public interface ExternalDatapoint {

	/**
	 * Returns the {@link ExternalComponent external component} to which this
	 * datapoint belongs.
	 */
	public ExternalComponent getComponent();

	/**
	 * Returns the URI of the ontological property that maps to this datapoint.
	 */
	public String getProperty();
	
	public String getPullAddress();
	
	public String getPushAddress();
	
	public String getSetAddress();
	
	public int getAutoPullWaitSeconds();
	
	public int getPushDeadSeconds();
	
	public boolean needsAutoReset();
	
	public Object getAutoResetValue();
	
	public int getAutoResetWaitSeconds();
	
	public ExternalDatapoint getInversion(); 
}

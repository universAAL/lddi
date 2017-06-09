/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
package org.universAAL.lddi.manager.publisher;

/**
 * Interface that should be necessary implemented by the HDP manager in order to
 * attend "connection", "disconnection" and "received data" agents (Continua
 * sources) events
 * 
 * @author Angel Martinez (amartinez@tsbtecnologias.es)
 * @author Luis Gigante (lgigante@tsbtecnologias.es) *
 * @version 0 June, 2012
 * 
 */

// Imports

// Class
public interface hdpManagerListener {

	/**
	 * HDP data channel ready (created or reconnected). x073 agent state machine
	 * at connected status.
	 * 
	 * @param Input
	 *            arguments are not needed
	 * @return Any data is returned by this method (void)
	 * 
	 */
	public void onChannelConnected();

	/**
	 * HDP data channel deleted (data path will not be valid anymore). x073
	 * agent state machine at disconnected status.
	 * 
	 * @param Input
	 *            arguments are not needed
	 * @return Any data is returned by this method (void)
	 * 
	 */
	public void onChannelDisconnected();

	/**
	 * Input HDP data frame available at the right file descriptor. x073 agent
	 * state machine at connected status. *
	 * 
	 * @param Input
	 *            arguments are not needed
	 * @return Any data is returned by this method (void)
	 * 
	 */
	public void onDataReceived();

	/**
	 * Output of printf functions in native library (C code). Callback method to
	 * show C messages in Java environments
	 * 
	 * @param Message
	 *            to be shown (string text)
	 * @return Any data is returned by this method (void)
	 * 
	 */
	public void onMessage(String str);

}

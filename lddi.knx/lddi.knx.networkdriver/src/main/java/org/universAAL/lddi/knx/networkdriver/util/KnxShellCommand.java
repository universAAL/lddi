/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at

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

package org.universAAL.lddi.knx.networkdriver.util;

import org.apache.felix.service.command.Descriptor;
import org.universAAL.lddi.knx.networkdriver.KnxNetworkDriverImp;
import org.universAAL.lddi.knx.utils.KnxCommand;

/**
 * Provide Gogo shell commands.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxShellCommand {

	private KnxNetworkDriverImp nwDriver;

	public KnxShellCommand(KnxNetworkDriverImp networkDriver) {
		this.nwDriver = networkDriver;
	}

	/**
	 * Provide Gogo shell command: 'knxcommand'
	 *
	 * @param knx
	 *            group address
	 * @param command
	 * @param command
	 *            type
	 */
	@Descriptor("send knx command to knx bus")
	public void knxcommand(@Descriptor("group address (1/2/3)") String ga,
			@Descriptor("on/off command (0 or 1)") String command,
			@Descriptor("command type: read or write  (r or w)") String commandType) {

		KnxCommand type = KnxCommand.VALUE_WRITE;
		if (commandType.equals("r"))
			type = KnxCommand.VALUE_READ;

		if (command.equals("1"))
			nwDriver.sendCommand(ga, true, type);
		else
			nwDriver.sendCommand(ga, false, type);

	}
}

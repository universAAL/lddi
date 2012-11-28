package org.universAAL.lddi.knx.networkdriver.util;

import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxNetworkDriverImp;

import org.apache.felix.service.command.Descriptor;

/**
 * Provide Gogo shell commands.
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxShellCommand {

	private KnxNetworkDriverImp nwDriver;

	public KnxShellCommand(KnxNetworkDriverImp networkDriver) {
		this.nwDriver = networkDriver;
	}

	/**
	 * Provide Gogo shell command: 'knxcommand'
	 * @param knx group address
	 * @param command
	 */
    @Descriptor("send knx command to knx bus")
    public void knxcommand(
    		@Descriptor("group address (1/2/3)") String ga,
    		@Descriptor("command (81)") String command) {
    	
    	nwDriver.sendCommand(ga, command);

    }
}

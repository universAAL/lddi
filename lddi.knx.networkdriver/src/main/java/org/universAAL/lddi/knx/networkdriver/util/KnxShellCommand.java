package org.universAAL.lddi.knx.networkdriver.util;

import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxNetworkDriverImp;

import org.apache.felix.service.command.Descriptor;
import org.universAAL.lddi.knx.utils.KnxCommand;

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
	 * @param command type
	 */
    @Descriptor("send knx command to knx bus")
    public void knxcommand(
    		@Descriptor("group address (1/2/3)") String ga,
    		@Descriptor("on/off command (0 or 1)") String command,
    		@Descriptor("command type: read or write  (r or w)") String commandType) {
    	
    	KnxCommand type = KnxCommand.VALUE_WRITE;
    	if (commandType.equals("r")) type = KnxCommand.VALUE_READ;
    	
    	if (command.equals("1")) nwDriver.sendCommand(ga, true, type);
    	else nwDriver.sendCommand(ga, false, type);

    }
}

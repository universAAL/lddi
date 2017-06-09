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
package org.universAAL.lddi.lib.ieeex73std.x73.p20601.fsm;

import org.universAAL.lddi.lib.ieeex73std.events.EventIEEEManager;
import org.universAAL.lddi.lib.ieeex73std.manager.Manager;
import org.universAAL.lddi.lib.ieeex73std.manager.Timeout;
import org.universAAL.lddi.lib.ieeex73std.utils.Logging;

public class StateMachine20601 {

	public static final boolean CHANNELTRANSPORTSTATE_DISCONNECTED = false;
	public static final boolean CHANNELTRANSPORTSTATE_CONNECTED = true;
	public static final String CHANNELTRANSPORT_DISCONNECTED_STRING = "DISCONNECTED";
	public static final String CHANNELTRANSPORT_CONNECTED_STRING = "CONNECTED";

	public static final int CHANNELSTATE_UNASSOCIATED = 1;
	public static final int CHANNELSTATE_ASSOCIATING = 2;
	public static final int CHANNELSTATE_ASSOCIATED_OPERATING = 3;
	public static final int CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG = 4;
	public static final int CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG = 5;
	public static final int CHANNELSTATE_DISASSOCIATING = 6;

	private boolean transportstate;
	private int channelstate;
	private Manager manager;
	public EventIEEEManager evtmanager;
	public Timeout timeout;

	/**
	 * The manager starts switched off
	 */
	public StateMachine20601(Manager manager, EventIEEEManager evtmgr) {

		transportstate = CHANNELTRANSPORTSTATE_DISCONNECTED;
		channelstate = CHANNELSTATE_UNASSOCIATED;
		evtmanager = evtmgr;
		this.manager = manager;
	}

	/**
	 * Getters and setters
	 * 
	 * @return
	 */
	public boolean getTransportState() {
		return transportstate;
	}

	public int getChannelState() {
		return channelstate;
	}

	public String getStringTransportState() {
		if (transportstate)
			return CHANNELTRANSPORT_CONNECTED_STRING;
		return CHANNELTRANSPORT_DISCONNECTED_STRING;
	}

	public String getStringChannelState() {
		switch (channelstate) {
		case CHANNELSTATE_UNASSOCIATED:
			return "UNASSOCIATED";
		case CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
			return "ASSOCIATED - CONFIGURING - CHECKING CONFIGURATION";
		case CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
			return "ASSOCIATED - CONFIGURING - WAITING FOR CONFIGURATION";
		case CHANNELSTATE_ASSOCIATED_OPERATING:
			return "ASSOCIATED - OPERATING";
		case CHANNELSTATE_DISASSOCIATING:
			return "DISASSOCIATING";
		case CHANNELSTATE_ASSOCIATING:
			return "ASSOCIATING";
		default:
			Logging.logError("The Channel State is not correct");
			return "Fail";
		}
	}

	public boolean setChannelState(int newstate) {
		boolean ok = false;
		synchronized (this) {
			if (!transportstate) {
				channelstate = CHANNELSTATE_UNASSOCIATED;
				Logging.logError("Channel State cannot be changed because transport is off." + " Remains unassociated");
			}
			switch (newstate) {
			case CHANNELSTATE_UNASSOCIATED:
				ok = transitiontoUnassociated();
				break;
			case CHANNELSTATE_ASSOCIATING:
				ok = transitiontoAssociating();
				break;
			case CHANNELSTATE_ASSOCIATED_OPERATING:
				ok = transitiontoOperating();
				break;
			case CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
				ok = transitiontoCheckingConfig();
				break;
			case CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				ok = transitiontoWaitingConfig();
				break;
			case CHANNELSTATE_DISASSOCIATING:
				ok = transitiontoDisassociating();
				break;
			default:
				Logging.logError("This value can't be assigned as a ChannelState. Value passed: " + newstate);
				break;
			}

		}
		return ok;
	}

	/**
	 * Activation of the transport protocol due to a incoming connection
	 */
	public boolean transportActivate() {
		boolean ok = false;
		synchronized (this) {
			if (!transportstate) {
				transportstate = CHANNELTRANSPORTSTATE_CONNECTED;
				channelstate = CHANNELSTATE_UNASSOCIATED;
				ok = true;
			}
		}
		return ok;
	}

	/** Disconnection from the agent */
	public boolean transportDeactivate() {
		boolean ok = false;
		synchronized (this) {
			if (transportstate) {
				transportstate = CHANNELTRANSPORTSTATE_DISCONNECTED;
				channelstate = CHANNELSTATE_UNASSOCIATED;
				ok = true;
			}
		}
		return ok;
	}

	/**
	 * The this turns back to its initial state. Unassociating from the agent.
	 */

	public boolean transitiontoUnassociated() { // 0
		boolean ok = false;
		if (isValidTransition(channelstate, CHANNELSTATE_UNASSOCIATED)) {

			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_UNASSOCIATED;
					ok = true;
				}
			}
			// return ok;
		}
		return ok;
	}

	/**
	 * Once the transport is activated, the process of associating may start.
	 * First of all, it has to receive a association request from the agent.
	 * 
	 */

	public boolean transitiontoAssociating() { // 1
		boolean ok = false;
		if (isValidTransition(channelstate, CHANNELSTATE_ASSOCIATING)) {
			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_ASSOCIATING;
					ok = true;
				}
			}
		}
		return ok;
	}

	/**
	 * Finally, after checking that the configuration is valid, the data channel
	 * can be stablished
	 * 
	 * Also, the process of receiving and checking configurations could be
	 * avoided if the agent information has been stored in a previous connection
	 * in a cache.
	 */

	public boolean transitiontoOperating() { // 2
		boolean ok = false;

		if (isValidTransition(channelstate, CHANNELSTATE_ASSOCIATED_OPERATING)) {

			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_ASSOCIATED_OPERATING;
					ok = true;
				}
			}
		}
		return ok;
	}

	/**
	 * Once the manager receives a configuration from the agent, it will be
	 * analysed for knowing if it can be handled.
	 */

	public boolean transitiontoCheckingConfig() { // 3
		boolean ok = false;
		timeout.cancel();
		if (isValidTransition(channelstate, CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG)) {
			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG;
					ok = true;
				}
			}
		}
		return ok;
	}

	/**
	 * This state is reached when the association request is accepted by the
	 * manager, but the agent information is not stored in cache. So, it has to
	 * obtain a configuration from it in order to know what king of device is
	 * and what information the agent provides.
	 * 
	 * Another option is, while configurating, the received configuration is not
	 * accepted, so the manager will wait for another one.
	 */
	public boolean transitiontoWaitingConfig() { // 4
		boolean ok = false;
		if (isValidTransition(channelstate, CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG)) {
			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG;
					ok = true;

					// initiate a timer for 10 seconds if we don't get a Config,
					// turn to Uanssociated.
					timeout = new Timeout(evtmanager);
					timeout.waitforConfig();

				}
			}
		}
		return ok;
	}

	/**
	 * The data communication has finished successfully, so the agent and the
	 * manager close the association until the next interaction.
	 * 
	 */

	public boolean transitiontoDisassociating() { // 5
		boolean ok = false;

		if (isValidTransition(channelstate, CHANNELSTATE_DISASSOCIATING)) {
			synchronized (this) {
				if (transportstate) {
					channelstate = CHANNELSTATE_DISASSOCIATING;
					ok = true;
				}
			}
		}
		return ok;
	}

	/**
	 * Check if the transition is valid or not!
	 */

	public boolean isValidTransition(int oldstate, int newstate) {
		// TODO Implementar este metodo en las transiciones de arriba

		boolean valid = false;

		switch (oldstate) {
		case CHANNELSTATE_UNASSOCIATED: /** 0 */
			if (newstate == CHANNELSTATE_ASSOCIATING) // 1
				valid = true;
			break;

		case CHANNELSTATE_ASSOCIATING: /** 1 */

			if (newstate == CHANNELSTATE_ASSOCIATED_OPERATING) // 2
				valid = true;
			if (newstate == CHANNELSTATE_UNASSOCIATED) // 0
				valid = true;
			if (newstate == CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG) // 4
				valid = true;
			break;

		case CHANNELSTATE_ASSOCIATED_OPERATING: /** 2 */
			if (newstate == CHANNELSTATE_DISASSOCIATING) // 5
				valid = true;
			if (newstate == CHANNELSTATE_UNASSOCIATED) // 5
				valid = true;
			break;

		case CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG: /** 4 */
			if (newstate == CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG) // 3
				valid = true;
			if (newstate == CHANNELSTATE_UNASSOCIATED) // 0
				valid = true;
			break;

		case CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG: /** 3 */
			if (newstate == CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG) // 4
				valid = true;
			if (newstate == CHANNELSTATE_ASSOCIATED_OPERATING) // 2
				valid = true;
			if (newstate == CHANNELSTATE_UNASSOCIATED) // 0
				valid = true;
			break;

		case CHANNELSTATE_DISASSOCIATING: /** 5 */
			if (newstate == CHANNELSTATE_UNASSOCIATED) // 0
				valid = true;
			break;
		default:
			break;
		}
		return valid;
	}

	/**
	 * Comment the next two methods once they are not needed.
	 */
	public void testFSM() {
		for (int i = 0; i < 6; i++) {
			this.resetthis(i);

			System.out.println("START WITH: " + this.getStringChannelState());
			this.setChannelState(i);
			// System.out.println(this.getChannelState() + " - "+
			// this.getStringChannelState());
			for (int j = 0; j < 6; j++) {
				if (j != i) {
					this.setChannelState(j);
					System.out.println("From " + i + " to " + j + ": the state now is " + this.getChannelState() + " - "
							+ this.getStringChannelState());
					this.resetthis(i);
				}
			}
			System.out.println("----------------------");
		}
	}

	public void resetthis(int i) {
		channelstate = i;
	}

	public void startTimeout() {

		// Timeout to = new Timeout(3000, "PMStore Clear", evtmanager);
	}

}

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

package org.universAAL.lddi.knx.networkdriver;

import java.net.*;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.utils.KnxCommand;
import org.universAAL.lddi.knx.utils.KnxEncoder;

/**
 * Envelopes KNX commands to UDP Packets and sends them on a UDP Multicast Channel.
 * Uses KNXEncoder to operate translation from high-level commands to low-level KNX commands.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxWriter {

	protected KnxNetworkDriverImp core;
	private String lastDeviceAddress;
	private boolean lastDeviceStatus;
	private KnxCommand lastCommandType;
	private boolean repeatBit = false;
	private volatile byte[] lastPacketSent;

	public KnxWriter(KnxNetworkDriverImp core) {
		this.core = core;
	}

	/**
	 * @return the lastPacketSent
	 */
	public byte[] getLastPacketSent() {
		return lastPacketSent;
	}

	/**
	 * Convert byte array to hex encoded string without delimiters.
	 */
	public String convertToHex(byte[] b) {
		StringBuilder byteString = new StringBuilder();

		for (int i = 0; i < b.length; i++) {

			String hexNumber = "0" + Integer.toHexString(0xff & b[i]);

			byteString.append(hexNumber);

		}
		return byteString.toString();
	}

	/**
	 * Wrapper for write method. Without command type.
	 */
	public void write(String deviceAddress, boolean deviceStatus) {
		write(deviceAddress, deviceStatus, KnxCommand.VALUE_WRITE);
	}

	/**
	 * Send KNX command to UPD multichannel. Store current sent telegram.
	 * 
	 * @param deviceAddress
	 *            knx group address (1/2/3)
	 * @param deviceStatus
	 *            knx command
	 * @param commandType
	 */
	public void write(String deviceAddress, boolean deviceStatus,
			KnxCommand commandType) {
		if (this.lastDeviceAddress.equals(deviceAddress)
				&& this.lastDeviceStatus == deviceStatus
				&& this.lastCommandType == commandType) {
			// same command as last time; set Repeat-Bit
			this.repeatBit = false;
		}

		try {
			// Connection
			DatagramSocket sender = new DatagramSocket();

			// FOCUS: command to host
			// InetAddress addr = InetAddress.getByName(core.getHouseIp());

			// FOCUS: command to multicast
			InetAddress addr = InetAddress.getByName(core.getMulticastIp());

			// Translating commands from String to byte[]
			byte[] telegram = KnxEncoder.encode(repeatBit, deviceAddress,
					deviceStatus, commandType);

			// Generating UDP packet
			DatagramPacket packet = new DatagramPacket(telegram,
					telegram.length, addr, core.getMulticastUdpPort());

			// Sending the packet
			core.getLogger().log(
					LogService.LOG_INFO,
					"Sending command to KNX: "
							+ KnxEncoder.convertToReadableHex(telegram));
			sender.send(packet);
			// store last sent command
			this.lastPacketSent = KnxEncoder.removeTrailingZeros(telegram);
			sender.close();
			this.lastDeviceAddress = deviceAddress;
			this.lastDeviceStatus = deviceStatus;
			this.lastCommandType = commandType;

		} catch (Exception e) {
			core.getLogger().log(LogService.LOG_ERROR,
					"Unable to write to the server KNX... " + e);
		} finally {
			this.repeatBit = false;
		}

	}

	/**
	 * Send KNX command 00 to group Address. Is seems that all devices belonging
	 * to this group address answer with their status. So, multiple answer
	 * telegrams are possible.
	 * 
	 * @param knx address; either group address (1/2/3) or device address (1.2.3)
	 */
	public void requestDeviceStatus(String deviceId) {
		try {
			// Connection
			DatagramSocket sender = new DatagramSocket();

			// FOCUS: command to host
			// InetAddress addr = InetAddress.getByName(core.getHouseIp());

			// FOCUS: command to multicast
			InetAddress addr = InetAddress.getByName(core.getMulticastIp());

			// Translating commands from String to byte[]
			// adding 00 for status and READ as message type
			byte[] telegram = KnxEncoder.encode(deviceId, false,
					KnxCommand.VALUE_READ);

			// Generating UDP packet
			DatagramPacket packet = new DatagramPacket(telegram,
					telegram.length, addr, core.getMulticastUdpPort());

			// Sending the packet
			core.getLogger().log(LogService.LOG_INFO,
					"Requesting status from KNX device " + deviceId);
			sender.send(packet);
			core.getLogger().log(
					LogService.LOG_DEBUG,
					"Sending KNX command "
							+ KnxEncoder.convertToReadableHex(telegram));

			this.lastPacketSent = KnxEncoder.removeTrailingZeros(telegram);
			// core.getLogger().log(LogService.LOG_DEBUG,
			// "lastPacketSent " +
			// KnxEncoder.getHexString(this.lastPacketSent));
			// packet = new DatagramPacket(telegram, telegram.length, addr,
			// 51000);

			sender.close();

		} catch (Exception e) {
			core.getLogger().log(LogService.LOG_ERROR,
					"Unable to write to KNX bus " + e);
		}
	}
}

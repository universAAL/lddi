package it.polito.elite.domotics.dog2.knxnetworkdriver;

import java.net.*;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.utils.KnxEncoder;
import org.universAAL.lddi.knx.utils.KnxEncoder.KnxMessageType;

/**
 * Provides the writing to the knx gateway. Uses the encoder to operate
 * translation from high-level commands to low-level command sent to the
 * gateway.
 * 
 * @author Enrico Allione (enrico.allione@gmail.com)
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxWriter {

	protected KnxNetworkDriverImp core;
	private String lastDeviceAddress;
	private String lastDeviceStatus;
	private KnxMessageType lastMessageType;
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

	// public static String byteArrayToHexString(byte in[]) {
	//
	// int i = 0;
	//
	// if (in == null || in.length <= 0)
	//
	// return null;
	//
	// String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
	// "A", "B", "C", "D", "E", "F" };
	//
	// StringBuffer out = new StringBuffer(in.length * 3);
	//
	// // TODO 20 bytes hardcoded!
	// while (i < in.length && i < 20) {
	// byte ch = 0x00;
	// byte ch2 = 0x00;
	// ch = (byte) (in[i] & 0xF0); // Strip offhigh nibble
	//
	// ch = (byte) (ch >>> 4);
	// // shift the bits down
	//
	// ch = (byte) (ch & 0x0F);
	// // must do this is high order bit is on!
	//
	// // out.append(pseudo[ (int) ch]); // convert thenibble to a String
	// // Character
	//
	// ch2 = (byte) (in[i] & 0x0F); // Strip offlow nibble
	//
	// out.append(pseudo[(int) ch] + pseudo[(int) ch2] + " "); // convert
	// // thenibble
	// // to a
	// // String
	// // Character
	//
	// i++;
	//
	// }
	//
	// String rslt = new String(out);
	//
	// return rslt;
	//
	// }

	/**
	 * Wrapper for write method. Without message type.
	 */
	public void write(String deviceAddress, String deviceStatus) {
		write(deviceAddress, deviceStatus, KnxMessageType.WRITE);
	}

	/**
	 * Send KNX command to UPD multichannel. Store current sent telegram.
	 * 
	 * @param deviceAddress
	 *            knx group address (1/2/3)
	 * @param deviceStatus
	 *            knx command
	 * @param messageType
	 */
	public void write(String deviceAddress, String deviceStatus,
			KnxMessageType messageType) {
		if (this.lastDeviceAddress == deviceAddress
				&& this.lastDeviceStatus == deviceStatus
				&& this.lastMessageType == messageType) {
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
					deviceStatus, messageType);

			// Generating UDP packet
			DatagramPacket packet = new DatagramPacket(telegram,
					telegram.length, addr, core.getHousePort());

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
			this.lastMessageType = messageType;

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
			byte[] telegram = KnxEncoder.encode(deviceId, "00",
					KnxMessageType.READ);

			// Generating UDP packet
			DatagramPacket packet = new DatagramPacket(telegram,
					telegram.length, addr, core.getHousePort());

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
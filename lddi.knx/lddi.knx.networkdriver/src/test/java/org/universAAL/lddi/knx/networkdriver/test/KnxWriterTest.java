package org.universAAL.lddi.knx.networkdriver.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

import org.junit.Test;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.networkdriver.Activator;
import org.universAAL.lddi.knx.networkdriver.KnxNetworkDriverImp;
import org.universAAL.lddi.knx.networkdriver.KnxWriter;
import org.universAAL.lddi.knx.networkdriver.util.LogTracker;
import org.universAAL.lddi.knx.utils.KnxCommand;
import org.universAAL.lddi.knx.utils.KnxEncoder;

/**
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxWriterTest {

	private static InetAddress GROUP_ADDRESS;
	private static int GROUP_PORT = 3671;
	private static byte[] sourceByte = new byte[] { 0, 0 };
	private static int socketTimeout = 3000; // 3s for sending
	private MulticastSocket mcSocket;

	public KnxWriterTest() {

		try {
			GROUP_ADDRESS = InetAddress.getByName("224.0.23.12");

			mcSocket = new MulticastSocket();
			mcSocket.joinGroup(GROUP_ADDRESS);
			mcSocket.setSoTimeout(socketTimeout);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void sendKnxPacket() {
		// byte[] dataByte = new byte[] {(byte) 0xbc, 0x0d, 0, 0, 0, 2, 1, 0,
		// (byte) 0x81};

		// KnxWriter writer = new KnxWriter(new
		// KnxNetworkDriverImp(Activator.context, null));
		// new LogTracker(null).log(LogTracker.LOG_INFO, "sendKnxPacket to 0/0/1
		// ");

		System.out.println("sendKnxPacket to 0/0/2 switch OFF");
		write("0/0/2", new byte[] { (byte) 0x80 }, KnxCommand.VALUE_WRITE);
		wait1s();
		System.out.println("sendKnxPacket to 0/0/2 switch ON");
		write("0/0/2", new byte[] { (byte) 0x81 }, KnxCommand.VALUE_WRITE);
		wait1s();
		System.out.println("sendKnxPacket to 0/0/3 switch OFF");
		write("0/0/3", new byte[] { (byte) 0x80 }, KnxCommand.VALUE_WRITE);
		wait1s();
		System.out.println("sendKnxPacket to 0/0/3 switch ON");
		write("0/0/3", new byte[] { (byte) 0x81 }, KnxCommand.VALUE_WRITE);
	}

	/**
	 * Send KNX command to UPD multicast channel. Store current sent telegram.
	 *
	 * @param deviceAddress
	 *            knx group address (1/2/3)
	 * @param dataByte
	 *            knx command
	 * @param commandType
	 */
	public void write(String deviceAddress, byte[] dataByte, KnxCommand commandType) {
		// if (this.lastDeviceAddress != null
		// && this.lastDeviceAddress.equals(deviceAddress)
		// && Arrays.equals(this.lastDataByte, dataByte)
		// && this.lastCommandType == commandType) {
		// // same command as last time; set Repeat-Bit
		// this.repeatBit = true;
		// }

		byte[] telegram = KnxEncoder.encode(false, sourceByte, deviceAddress, dataByte, commandType, true);

		try {
			// Generating UDP packet
			DatagramPacket packet = new DatagramPacket(telegram, telegram.length, GROUP_ADDRESS, GROUP_PORT);

			System.out.println("Sending command to KNX: " + KnxEncoder.convertToReadableHex(telegram));

			// Sending the packet
			if (mcSocket.isBound()) {
				// using multicast socket
				System.out.println("Sending on multicast channel!");
				mcSocket.send(packet);
			} else {
				System.out.println("Not sent on any channel!");
				throw new Exception("Unable to write to KNX bus! Socket not bound to destination address!");
			}

			// store last sent command
			// this.lastPacketSent = KnxEncoder.removeTrailingZeros(telegram);
			// this.lastDeviceAddress = deviceAddress;
			// this.lastDataByte = dataByte;
			// this.lastCommandType = commandType;

		} catch (IOException e) {
			System.out.println("Unable to write to KNX bus! " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			// this.repeatBit = false;
		}
	}

	public void testKNXCommunication() {
		// KnxNetworkDriverImp netDrv = new
		// KnxNetworkDriverImp(Activator.context, null);

		Activator.networkDriver.sendCommand("0/0/2", true, KnxCommand.VALUE_WRITE);
		wait1s();
		Activator.networkDriver.requestState("0/0/2");
		wait1s();
		Activator.networkDriver.sendCommand("0/0/2", false, KnxCommand.VALUE_WRITE);
		wait1s();
		Activator.networkDriver.requestState("0/0/2");
		wait1s();

		// requests to knx groupDevice address directly gives no response!!
		// Activator.networkDriver.requestState("1.1.5");
	}

	void wait1s() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

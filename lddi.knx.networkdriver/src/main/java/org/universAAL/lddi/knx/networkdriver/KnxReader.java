package org.universAAL.lddi.knx.networkdriver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.utils.KnxEncoder;
import org.universAAL.lddi.knx.utils.KnxTelegram;

/**
 * Listens on IP network for Multicast Packets. Uses KNXEncoder to
 * operate translation from low level data (knx) to high level data (sent to
 * uAAL).
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxReader implements Runnable {

	protected KnxNetworkDriverImp core;

	static private int socketTimeout = 0; // infinite timeout on receive()

	private MulticastSocket mcReceiver;

	public KnxReader(KnxNetworkDriverImp core) {
		this.core = core;
	}

	public void stopReader() {
		if (this.mcReceiver != null)
			this.mcReceiver.close();
	}

	public void run() {
		try {
			this.mcReceiver = new MulticastSocket(core.getMulticastUdpPort());
			InetAddress group = InetAddress.getByName(core.getMulticastIp());
			this.mcReceiver.joinGroup(group);

			this.mcReceiver.setSoTimeout(socketTimeout);

			core.getLogger().log(
					LogService.LOG_INFO,
					"Server KNX listening on port " + core.getMulticastUdpPort()
							+ " (joined " + core.getMulticastIp() + ")");

			while (!Thread.currentThread().isInterrupted()) {

				byte buffer[] = new byte[mcReceiver.getReceiveBufferSize()];
				DatagramPacket udpPacket = new DatagramPacket(buffer,
						buffer.length);

				// blocking here...
				this.mcReceiver.receive(udpPacket);
				// The datagram packet contains also the sender's IP address,
				// and the port number
				// on the sender's machine. This method blocks until a datagram
				// is received.

				byte[] temp = udpPacket.getData();

				// Outsourced to KnxEncoder !
				// //remove trailing 0 (buffer is 8192 bytes long!)
				// int i = temp.length - 1;
				// while(temp[i] == 0)
				// --i;
				// // now temp[i] is the last non-zero byte
				// byte[] dataPacket = new byte[i+1];
				// System.arraycopy(temp, 0, dataPacket, 0, i+1);

				byte[] dataPacket = KnxEncoder.removeTrailingZeros(temp);

				core.getLogger().log(
						LogService.LOG_DEBUG,
						"Incoming command from KNX: "
								+ udpPacket.getAddress().toString() + " "
								+ KnxEncoder.convertToReadableHex(dataPacket));

				// if this packet was just sent by KnxWriter discard it
				if (Arrays.equals(dataPacket, core.network.getLastSentPacket())) {
					core.getLogger().log(
							LogService.LOG_DEBUG,
							"Discard incoming UDP Multicast Packet "
									+ "which was just sent by KnxWriter "
									+ KnxEncoder
											.convertToReadableHex(dataPacket));
					continue;
				}
				// core.getLogger().log(LogService.LOG_DEBUG,"incoming byte[] "
				// + new String(dataPacket));
				
				byte [] lastPacket = core.network.getLastSentPacket();
				if (lastPacket != null) {
				core.getLogger().log(
						LogService.LOG_DEBUG,
						"last sent command: "
								+ KnxEncoder.convertToReadableHex(lastPacket));
				}

				// 17 data bytes minimum!
				// remove the header from the data packet (8 bytes)
				int l = (dataPacket.length) - 8; // 8 bytes header
				byte[] knxPacket = new byte[l];
				System.arraycopy(temp, 8, knxPacket, 0, l);

				KnxTelegram telegram = KnxEncoder.decode(knxPacket);
				if (telegram == null) {
					core.getLogger().log(
							LogService.LOG_WARNING,
							"Incoming command from KNX bus is not valid "
									+ "and will not be processed! --- "
									+ udpPacket.getAddress().toString()
									+ " BYTEs "
									+ KnxEncoder
											.convertToReadableHex(dataPacket));
					continue;
				}
				
				String groupAddress = KnxEncoder
						.convertGroupAddressToReadable(telegram.getDestByte());

				core.getLogger().log(
						LogService.LOG_INFO,
						"KNX telegram received (" + knxPacket.length
								+ " bytes - data length:"
								+ telegram.getDataLength() + "): "
								+ telegram.toString());
				core.getLogger().log(
						LogService.LOG_DEBUG,
						"Source: " + groupAddress + "; TELEGRAM: "
								+ KnxEncoder.convertToReadableHex(knxPacket));

				if (telegram.isTelegramIsDatapointType1()) {
					this.core.newMessageFromHouse(groupAddress, new byte[]{telegram.getDpt1DataByte()} );
				}else{
					this.core.newMessageFromHouse(groupAddress, telegram.getDataByte());
				}

			}
		} catch (SocketException se) {
			core.getLogger().log(LogService.LOG_INFO,
					"UDP Multicast Socket closed! Stop listening!");
		} catch (Exception e) {
			core.getLogger().log(LogService.LOG_ERROR, e.getMessage());
			e.printStackTrace();
		}
	}
}

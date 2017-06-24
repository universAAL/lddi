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

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.utils.KnxEncoder;
import org.universAAL.lddi.knx.utils.KnxTelegram;

import tuwien.auto.calimero.CloseEvent;
import tuwien.auto.calimero.FrameEvent;
import tuwien.auto.calimero.KNXAddress;
import tuwien.auto.calimero.cemi.CEMILData;
import tuwien.auto.calimero.exception.KNXException;
import tuwien.auto.calimero.exception.KNXRemoteException;
import tuwien.auto.calimero.exception.KNXTimeoutException;
import tuwien.auto.calimero.link.KNXLinkClosedException;
import tuwien.auto.calimero.link.KNXNetworkLinkIP;
import tuwien.auto.calimero.link.event.NetworkLinkListener;
import tuwien.auto.calimero.link.medium.TPSettings;
import tuwien.auto.calimero.process.ProcessCommunicator;
import tuwien.auto.calimero.process.ProcessCommunicatorImpl;

/**
 * Listens on IP network for KNX Packets. According to configuration this class
 * listens either on a UDP multicast channel, or established a direct tunneling
 * connection to the KNX/IP gateway.
 *
 * Uses KNXEncoder to operate translation from low level data (knx) to high
 * level data (sent to uAAL).
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxReader implements Runnable {

	private boolean shutdown = false;

	protected KnxNetworkDriverImp core;

	static private int socketTimeout = 0; // infinite timeout on receive()

	private MulticastSocket mcReceiver;

	static InetAddress GROUP_ADDRESS;
	static int GROUP_PORT;

	private KNXNetworkLinkIP tunnel;
	private ProcessCommunicator pc;

	public KnxReader(KnxNetworkDriverImp core) {
		this.core = core;
		try {
			GROUP_ADDRESS = InetAddress.getByName(core.getMulticastIp());
			GROUP_PORT = core.getMulticastUdpPort();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		while (!shutdown) {
			if (core.isMulticast()) {
				// USING UDP MULTICAST!
				core.getLogger().log(LogService.LOG_INFO,
						"KNX network driver listens on UDP MULTICAST CHANNEL for KNX telegrams!");
				try {
					// create socket on KNX port
					this.mcReceiver = new MulticastSocket(core.getMulticastUdpPort());
					InetAddress group = InetAddress.getByName(core.getMulticastIp());
					this.mcReceiver.joinGroup(group);
					this.mcReceiver.setSoTimeout(socketTimeout);

					core.getLogger().log(LogService.LOG_INFO, "Server KNX listening on port "
							+ core.getMulticastUdpPort() + " (joined " + core.getMulticastIp() + ")");

					while (!Thread.currentThread().isInterrupted()) {

						byte buffer[] = new byte[mcReceiver.getReceiveBufferSize()];
						DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);

						// blocking here...
						this.mcReceiver.receive(udpPacket);
						// The datagram packet contains also the mcSocket's IP
						// address, and the port number on the mcSocket's
						// machine.
						// This method blocks until a datagram is received.

						byte[] temp = udpPacket.getData();

						byte[] dataPacket = KnxEncoder.removeTrailingZeros(temp);

						core.getLogger().log(LogService.LOG_DEBUG,
								"Incoming command from KNX: " + udpPacket.getAddress().toString() + " "
										+ KnxEncoder.convertToReadableHex(dataPacket));

						// if this packet was just sent by KnxWriter discard it
						if (Arrays.equals(dataPacket, core.network.getLastSentPacket())) {
							core.getLogger().log(LogService.LOG_DEBUG,
									"Discard incoming UDP Multicast Packet " + "which was just sent by KnxWriter "
											+ KnxEncoder.convertToReadableHex(dataPacket));
							continue;
						}

						byte[] lastPacket = core.network.getLastSentPacket();
						if (lastPacket != null) {
							core.getLogger().log(LogService.LOG_DEBUG,
									"last sent command: " + KnxEncoder.convertToReadableHex(lastPacket));
						}

						// 17 data bytes minimum!
						// remove the header from the data packet (8 bytes)
						int l = (dataPacket.length) - 8; // 8 bytes header
						byte[] knxPacket = new byte[l];
						System.arraycopy(temp, 8, knxPacket, 0, l);

						KnxTelegram telegram = KnxEncoder.decode(knxPacket);
						if (telegram == null) {
							core.getLogger().log(LogService.LOG_WARNING,
									"Incoming command from KNX bus is not valid " + "and will not be processed! --- "
											+ udpPacket.getAddress().toString() + " BYTEs "
											+ KnxEncoder.convertToReadableHex(dataPacket));
							continue;
						}

						String groupAddress = KnxEncoder.convertGroupAddressToReadable(telegram.getDestByte());

						core.getLogger().log(LogService.LOG_INFO, "KNX telegram received (" + knxPacket.length
								+ " bytes - data length:" + telegram.getDataLength() + "): " + telegram.toString());
						core.getLogger().log(LogService.LOG_DEBUG, "Source: " + groupAddress + "; TELEGRAM: "
								+ KnxEncoder.convertToReadableHex(knxPacket));

						// if (telegram.isTelegramIsDatapointType1()) {
						// this.core.newMessageFromHouse(groupAddress,
						// new byte[] { telegram.getDpt1DataByte() });
						// } else {
						this.core.newMessageFromHouse(groupAddress, telegram.getDataByte());
						// }

					}
				} catch (SocketException se) {
					core.getLogger().log(LogService.LOG_INFO, "UDP Multicast Socket closed! Stop listening!");
				} catch (Exception e) {
					core.getLogger().log(LogService.LOG_ERROR, e.getMessage());
					e.printStackTrace();
				} finally {
					shutdown = true;
				}

			} else {
				// USING DIRECT TUNNELING!
				core.getLogger().log(LogService.LOG_INFO, "KNX network driver uses DIRECT TUNNELING to KNX gateway!");

				InetSocketAddress remoteEndPoint = new InetSocketAddress(core.getKnxGatewayIp(),
						core.getKnxGatewayPort());
				InetSocketAddress localEndPoint = new InetSocketAddress(core.getMyIp(), core.getMyPort());

				core.getLogger().log(LogService.LOG_INFO,
						"Creating IP tunnel from " + localEndPoint + " to " + remoteEndPoint);

				try {
					tunnel = new KNXNetworkLinkIP(KNXNetworkLinkIP.TUNNEL, localEndPoint, remoteEndPoint, false,
							new TPSettings(false));
					pc = new ProcessCommunicatorImpl(tunnel);

					tunnel.addLinkListener(new NetworkLinkListener() {
						public void confirmation(final FrameEvent frameEvent) {
							core.getLogger().log(LogService.LOG_INFO, "Confirmation " + frameEvent);
						}

						public void indication(final FrameEvent frameEvent) {
							core.getLogger().log(LogService.LOG_DEBUG, "Indication "
									+ KnxEncoder.convertToReadableHex(frameEvent.getFrame().toByteArray()));

							// byte[] frameBytes = frameEvent.getFrameBytes();
							// // is always null!!
							// if (frameBytes != null) {
							// core.getLogger().log(LogService.LOG_ERROR,
							// "Received KNX frame: " + frameBytes);
							// core
							// .getLogger()
							// .log(
							// LogService.LOG_ERROR,
							// "Received KNX frame: "
							// + KnxEncoder
							// .convertToReadableHex(frameBytes));
							// }

							final CEMILData frame = (CEMILData) frameEvent.getFrame();
							KNXAddress destAddress = frame.getDestination();
							final byte[] apdu = frame.getPayload();

							core.getLogger().log(LogService.LOG_INFO, "Received KNX frame with destination address "
									+ destAddress + " and payload " + KnxEncoder.convertToReadableHex(apdu));

							// prepare data bytes
							byte[] databyte = null;
							if (apdu.length == 2) // datapoint type = 1
								databyte = new byte[] { (byte) (apdu[1] & 0x3F) };
							else {
								databyte = new byte[apdu.length - 2];
								System.arraycopy(apdu, 2, databyte, 0, apdu.length - 2);
							}

							// core.getLogger().log(LogService.LOG_ERROR,
							// "DATABYTE: "
							// + KnxEncoder.convertToReadableHex(databyte));

							// pass on to core driver
							core.newMessageFromHouse(destAddress.toString(), databyte);

						}

						public void linkClosed(final CloseEvent closeEvent) {
							core.getLogger().log(LogService.LOG_WARNING, "Closed event " + closeEvent.getReason());

							// TODO: What to do when the tunnel is closed by the
							// gateway?
						}
					});

				} catch (KNXLinkClosedException e) {
					core.getLogger().log(LogService.LOG_ERROR,
							"KnxReader: KNX NETWORK LINK was CLOSED! " + e.getMessage());
				} catch (KNXRemoteException e) {
					core.getLogger().log(LogService.LOG_ERROR,
							"KnxReader: KNX NETWORK LINK problem: " + e.getMessage());
				} catch (KNXTimeoutException e) {
					core.getLogger().log(LogService.LOG_ERROR,
							"KnxReader: KNX NETWORK LINK problem: " + e.getMessage());
				} catch (KNXException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					shutdown = true;
				}
			}
		}
	}

	public void stopReader() {
		if (this.mcReceiver != null)
			this.mcReceiver.close();
		if (this.tunnel != null)
			tunnel.close();
	}

}

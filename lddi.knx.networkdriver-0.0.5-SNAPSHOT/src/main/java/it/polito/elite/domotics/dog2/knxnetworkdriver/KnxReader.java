package it.polito.elite.domotics.dog2.knxnetworkdriver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.utils.KnxEncoder;
import org.universAAL.lddi.knx.utils.KnxTelegram;

/** Provides readings from the knx gateway by the LAN.
 * Uses the encoder to operate translation from low level data
 * (knx) to high level data (sent to uAAL).
 * @author Enrico Allione (enrico.allione@gmail.com)
 * @author Thomas Fuxreiter (foex@gmx.at)
 */

public class KnxReader 
//extends Thread
implements Runnable
{

	protected KnxNetworkDriverImp core;
	protected KnxEncoder encoder;
	
	static private int socketTimeout = 0;	// infinite timeout on receive()
	static private int telegramLenght = 17;	// KNX core telegram length

//	private boolean running;
	private MulticastSocket mcReceiver;
	
	public KnxReader(KnxNetworkDriverImp core) {
		this.core = core;
//		this.running=true;
	}

//	public void stopReader(){
//		this.running=false;
//	}
	
	public void stopReader(){
		if (this.mcReceiver != null) 
			this.mcReceiver.close();
	}
	
//	public void run()  {
//		while(running){
//			listen();
//			Thread.yield();
//		}
//	}

//	private void listen(){
	public void run(){
		int k = 0;
//		boolean flag = true;

		try {
			this.mcReceiver = new MulticastSocket(core.getMyUdpPort());
			InetAddress group = InetAddress.getByName(core.getMulticastIp());
			this.mcReceiver.joinGroup(group);

			this.mcReceiver.setSoTimeout(socketTimeout);

			core.getLogger().log(LogService.LOG_INFO,"Server KNX listening on port " + 
					core.getMyUdpPort() + " (joined " + core.getMulticastIp() + ")");

//			while (flag) {
			while (!Thread.currentThread().isInterrupted()) {
				
				byte buffer[] = new byte[mcReceiver.getReceiveBufferSize()];
				//buffer is 8192 bytes !!
				DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
				
				//blocking here...
				this.mcReceiver.receive(udpPacket);
				//The datagram packet contains also the sender's IP address, and the port number
				//on the sender's machine. This method blocks until a datagram is received.

				byte[]temp = udpPacket.getData();

				core.getLogger().log(LogService.LOG_DEBUG,"\n---COMMAND FROM KNX --- " + 
						udpPacket.getAddress().toString()+" BYTEs "+KnxWriter.byteArrayToHexString(temp));

				//remove trailing 0 (buffer is 8192 bytes long!)
				int i = temp.length - 1;
				while(temp[i] == 0)
				    --i;
				// now temp[i] is the last non-zero byte
				byte[] dataPacket = new byte[i+1];
				System.arraycopy(temp, 0, dataPacket, 0, i+1);
				
				//remove also the UDP header
				int l = (i+1) - 9;	//9 bytes UDP header
				byte[] knxPacket = new byte[l];
				System.arraycopy(temp, 9, knxPacket, 0, l);
				
				KnxTelegram telegram = KnxEncoder.decode(knxPacket);
				if (telegram == null) {
					core.getLogger().log(LogService.LOG_WARNING,"\n--- Incoming COMMAND FROM KNX bus is not valid " +
							"and will not be processed! --- " + 
							udpPacket.getAddress().toString()+" BYTEs "+KnxWriter.byteArrayToHexString(temp));
					continue;
				}
				String groupAddress = KnxEncoder.getGroupAddress(telegram.getDestByte());

				core.getLogger().log(LogService.LOG_INFO,"KNX telegram received (" + knxPacket.length +
						" bytes): " + telegram.toString());
				core.getLogger().log(LogService.LOG_DEBUG,"Source: " + groupAddress + 
						"; TELEGRAM: " + KnxWriter.byteArrayToHexString(temp));

				this.core.newMessageFromHouse(groupAddress, telegram.getValueByte());

				
				
				
				
				byte[] deviceByte = new byte[2];
				deviceByte[0] = temp[10];		
				deviceByte[1] = temp[11];

				byte[] groupByte = new byte[2];
				groupByte[0] = temp[12];		
				groupByte[1] = temp[13];
				
				//difference between the expected size of the telegram and the real one
				int oversize = temp.length - KnxReader.telegramLenght;
				oversize=oversize<0?0:oversize; //avoid array index exception
				
				
				byte[] statusByte = new byte[oversize+2];
				
				for(int j=0; j<statusByte.length; j++){
					//statusByte[0] = temp[14]; //old version
					statusByte[j]=temp[temp.length-2-oversize+j];
				}
				
				//TODO change to handle data value such as temperature
//				String groupAddress = KnxEncoder.getGroupAddress(groupByte);
				//String knxStatus = KnxEncoder.getStatus(statusByte);
				
				
				 //byteString=new StringBuilder();
				
				

				k += 1;

			
			}
		}
		catch (SocketException se){
			core.getLogger().log(LogService.LOG_INFO,"UDP Multicast Socket closed! Stop listening!");
		}
		catch (Exception e){
			core.getLogger().log(LogService.LOG_ERROR,e.getMessage());
			 e.printStackTrace();
		}
	}



}
package it.polito.elite.domotics.dog2.knxnetworkdriver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

import org.osgi.service.log.LogService;

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

				
				//remove trailing 0 (buffer is 8192 bytes long!)
				byte[]temp = udpPacket.getData();
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
				
				
				core.getLogger().log(LogService.LOG_INFO,"KNX telegram received (" + knxPacket.length +
						" bytes): " + KnxEncoder.decode(dataPacket));
				
				
				
				
				
				
				byte[] deviceByte = new byte[2];
				
				//difference between the expected size of the telegram and the real one
				int oversize = temp.length - KnxReader.telegramLenght;
				oversize=oversize<0?0:oversize; //avoid array index exception
				
				
				byte[] statusByte = new byte[oversize+2];
				deviceByte[0] = temp[10];		
				deviceByte[1] = temp[11];
				for(int j=0; j<statusByte.length; j++){
					//statusByte[0] = temp[14]; //old version
					statusByte[j]=temp[temp.length-2-oversize+j];
				}
				//TODO change to handle data value such as temperature
				String groupAddress = KnxEncoder.getGroupAddress(deviceByte);
				//String knxStatus = KnxEncoder.getStatus(statusByte);
				
				core.getLogger().log(LogService.LOG_DEBUG,"\n---------COMMAND FROM HOUSE TO DOG--------- " + udpPacket.getAddress().toString()+"BYTE "+KnxWriter.byteArrayToHexString(temp));
				
				 //byteString=new StringBuilder();
				
				

				k += 1;
				core.getLogger().log(LogService.LOG_DEBUG,"Source: " + groupAddress + "; TELEGRAM: " +KnxWriter.byteArrayToHexString(temp));

				this.core.newMessageFromHouse(groupAddress, statusByte);
			
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
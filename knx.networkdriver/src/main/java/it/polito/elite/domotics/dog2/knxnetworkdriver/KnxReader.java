/*                               
    _/_/_/                        
   _/    _/    _/_/      _/_/_/   
  _/    _/  _/    _/  _/    _/    
 _/    _/  _/    _/  _/    _/    Domotic OSGi Gateway
_/_/_/      _/_/      _/_/_/      
                         _/       
                    _/_/

WEBSITE: http://domoticdog.sourceforge.net
LICENSE: see the file License.txt

*/
package it.polito.elite.domotics.dog2.knxnetworkdriver;



import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.osgi.service.log.LogService;



/** Provides readings from the house by the LAN. Uses the encoder to operate translation from low level data (from the house)
 * 		to high level data (sent to MessageDispatcher).
 * @author Enrico Allione (enrico.allione@gmail.com)
 *
 */

public class KnxReader extends Thread {

	protected KnxNetworkDriverImp core;
	protected KnxEncoder encoder;
	
	static private int socketTimeout = 0;	// response timeout
	static private int telegramLenght = 15;	// EIB core telegram length

	private boolean running;

	public KnxReader(KnxNetworkDriverImp core) {
		this.core = core;
		this.running=true;
	}

	public void stopReader(){
		this.running=false;
	}
	
	public void run()  {
		while(running){
		listen();
		Thread.yield();
		}
	}

	private void listen(){
		int k = 0;
		boolean flag = true;

		try {
			MulticastSocket mcReceiver = new MulticastSocket(core.getMyUdpPort());
			InetAddress group = InetAddress.getByName(core.getMulticastIp());
			mcReceiver.joinGroup(group);

			mcReceiver.setSoTimeout(socketTimeout);
			
			

			core.getLogger().log(LogService.LOG_INFO,"Server KNX listening on port " + core.getMyUdpPort() + 
					" (joined " + core.getMulticastIp() + ", " + k + " received from beginning)...");

			while (flag) {
				byte buffer[] = new byte[mcReceiver.getReceiveBufferSize()];
				DatagramPacket udpPacket = new DatagramPacket(buffer, buffer.length);
				mcReceiver.receive(udpPacket);
//				udpPacket.

				byte[]temp = udpPacket.getData();
				core.getLogger().log(LogService.LOG_INFO,"KNX telegram: " + KnxEncoder.decode(temp));
						
				byte[] deviceByte = new byte[2];
				
				//difference between the aspected size of the telegram and the real one
				int oversize=temp.length-KnxReader.telegramLenght;
				oversize=oversize<0?0:oversize; //avoid array index exception
				
				
				byte[] statusByte = new byte[oversize+2];
				deviceByte[0] = temp[10];		
				deviceByte[1] = temp[11];
				for(int i=0; i<statusByte.length; i++){
					//statusByte[0] = temp[14]; //old version
					statusByte[i]=temp[temp.length-2-oversize+i];
				}
				//TODO change to handle data value such as temperature
				String knxDevice = KnxEncoder.getGroupAddress(deviceByte);
				//String knxStatus = KnxEncoder.getStatus(statusByte);
				
				core.getLogger().log(LogService.LOG_DEBUG,"\n---------COMMAND FROM HOUSE TO DOG--------- " + udpPacket.getAddress().toString()+"BYTE "+KnxWriter.byteArrayToHexString(temp));
				
				 //byteString=new StringBuilder();
				
				

				k += 1;
				core.getLogger().log(LogService.LOG_DEBUG,"Source: " + knxDevice + "; TELEGRAM: " +KnxWriter.byteArrayToHexString(temp));

				this.core.newMessageFromHouse(knxDevice, statusByte);
			
			}
		}
		catch (Exception e){
			core.getLogger().log(LogService.LOG_ERROR,e.getMessage());
			// e.printStackTrace();
		}
	}



}
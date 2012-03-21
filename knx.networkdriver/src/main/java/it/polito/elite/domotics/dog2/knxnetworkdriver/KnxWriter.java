package it.polito.elite.domotics.dog2.knxnetworkdriver;

import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxEncoder.KnxMessageType;

import java.net.*;

import org.osgi.service.log.LogService;


/** Provides the writing to the knx gateway.
 * Uses the encoder to operate translation from high-level commands
 * (from ?) to low-level command (sent to the gateway).
 * 
 * @author Enrico Allione (enrico.allione@gmail.com)
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxWriter {

	protected KnxNetworkDriverImp core;
	
	
	public KnxWriter(KnxNetworkDriverImp core) {
		this.core = core;
	}
	
	
	
	public String toHexString(byte[] temp ){
		StringBuilder byteString=new StringBuilder();
		
		for(int i=0;i<temp.length;i++)
		{
		    
			String hexNumber = "0" + Integer.toHexString(0xff & temp[i]);

		byteString.append(hexNumber);
				
			
		}
		return byteString.toString();
	}
	
	public static String byteArrayToHexString(byte in[]) {

	   
	    int i = 0; 

	    if (in == null || in.length <= 0)

	        return null;

	        

	    String pseudo[] = {"0", "1", "2",
	"3", "4", "5", "6", "7", "8",
	"9", "A", "B", "C", "D", "E",
	"F"};

	    StringBuffer out = new StringBuffer(in.length * 3);

	    //TODO 20 bytes hardcoded! 
	    while (i < in.length && i<20) {
	        byte ch = 0x00;
	 	    byte ch2 =0x00;
	        ch = (byte) (in[i] & 0xF0); // Strip offhigh nibble

	        ch = (byte) (ch >>> 4);
	     // shift the bits down

	        ch = (byte) (ch & 0x0F);    
	// must do this is high order bit is on!

	       // out.append(pseudo[ (int) ch]); // convert thenibble to a String Character

	        ch2 = (byte) (in[i] & 0x0F); // Strip offlow nibble 

	        out.append(pseudo[ (int) ch]+pseudo[ (int) ch2]+" "); // convert thenibble to a String Character
           
	        i++;

	    }

	    String rslt = new String(out);

	    return rslt;

	} 
	
	public void write(String deviceAddress, String deviceStatus){
		write(deviceAddress, deviceStatus,KnxMessageType.WRITE);
	}
	
	public void write(String deviceAddress, String deviceStatus, KnxMessageType messageType){
		try {		
				// Connection
       DatagramSocket sender = new DatagramSocket();
        InetAddress addr = InetAddress.getByName(core.getHouseIp());	// FOCUS: command to host
       //InetAddress addr = InetAddress.getByName(core.getMulticastIp());	// FOCUS: command to multicast

       		// Translating commands from String to byte[]
       byte[] telegram = KnxEncoder.encode(deviceAddress, deviceStatus,messageType);
       
       
       
       	// Generating UDP packet
       DatagramPacket packet = new DatagramPacket(telegram, telegram.length, addr, core.getHousePort());
       
       // Sending the packet
       core.getLogger().log(LogService.LOG_DEBUG,"\n---------COMMAND FROM EMI TO HOUSE---------"+byteArrayToHexString(telegram));
       sender.send(packet);
       sender.close();

	}
	catch (Exception e){
		core.getLogger().log(LogService.LOG_ERROR,"Unable to write to the server KNX... " + e);
	}
		
		
	}
	
	/** Writes a KNX telegram into an UDP packet, using KnxEncoder methods to create the telegram
	 * @param deviceAddress: id of the device
	 * @param status: status of the device
	 */
		
	
	public void read (String deviceId){
	
		core.getLogger().log(LogService.LOG_ERROR,"\n---------REQUEST READ ---------");
		
		
		try {		
  				// Connection
	       DatagramSocket sender = new DatagramSocket();
           InetAddress addr = InetAddress.getByName(core.getHouseIp());	// FOCUS: command to host
	       //InetAddress addr = InetAddress.getByName(core.getMulticastIp());	// FOCUS: command to multicast

	       		// Translating commands from String to byte[]
	       byte[] telegram = KnxEncoder.encode(deviceId, "00",KnxMessageType.READ);
	       
	       	// Generating UDP packet
	       DatagramPacket packet = new DatagramPacket(telegram, telegram.length, addr, core.getHousePort());
	       
	       // Sending the packet
	       core.getLogger().log(LogService.LOG_DEBUG,"\n---------READING STATE FROM EMI TO HOUSE---------"+byteArrayToHexString(telegram));
	       
	    
	       sender.send(packet);
          // packet = new DatagramPacket(telegram, telegram.length, addr, 51000);
	       
	       
	      
	       sender.close();

		}
		catch (Exception e){
			core.getLogger().log(LogService.LOG_ERROR,"Unable to write to the server KNX... " + e);
		}
	}
	
	
	
}
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

import java.util.Properties;

import org.osgi.service.log.LogService;



/** Provides bottom-up (house to DOG) and top-down (DOG to house) translation of commands.
 * @author Enrico Allione (enrico.allione@gmail.com)
 *
 */
public class KnxEncoder {
	
	public static enum KnxMessageType{READ,WRITE,SCENARIO}; 
	
	/**
	 * @param deviceAddress address of the device
	 * @param highStatus status to be set to the device
	 * @return
	 */
	public static byte[] encode(String deviceAddress, String command, KnxMessageType messageType){

		// Costant fields
		String header = "1006000f0201";	// Konnex Header
		
		String type="";
		String stuff = ""; // Stuff (a least one time it became d101..!?!		
		String lowStatus ="";
		
		switch (messageType) {
		case READ:
			type="290c";
			stuff="d100";
			lowStatus="00";
			break;
		case WRITE:
			type="29bc";
			stuff="d100";
			lowStatus=command;

			break;
		case SCENARIO:
			type="29bc";
			stuff="b100";
			lowStatus=command;
			break;

		default:
			break;
		}
		
		// Response "bc"; Otherwise "0c" for reading (?)
		String sourceAddress = "1.0.252"; // Source physical address
		

		String source = KnxEncoder.createAddress(sourceAddress);
		
	   // Destination
		String destination = new String();
		
		if (deviceAddress.contains("/")) 
			destination = KnxEncoder.createGroupAddress(deviceAddress);	// group address
		else if (deviceAddress.contains(".")) 
			destination = KnxEncoder.createAddress(deviceAddress);			// physical addres

		String stringTelegram =  header + type + source + destination + stuff + lowStatus;

		byte messagge[] = KnxEncoder.toBytes(stringTelegram);
		
		return  messagge;
	}
	

	/**
	 * @param message: telegram from the house
	 * @return 
	 */
	public static String decode(byte message[]){
		/* Riceve the bytecode and  decode it in a knxMessage */
		
		String readMessage = new String();	
		readMessage = KnxEncoder.getInfoFromMessage(message);	
		return readMessage;
	}
	

	/**
	 * @param address: address of the device as x.y.z
	 * @return the address as hex
	 */
	private static String createAddress(String address){
		/* This method is called for the device physical address
		 * 	Input: string "x.y.z" physical address
		 * 	Output: string "xy" physical address in hexadecimal format
		 * 		x is 0-15 on 4bits;	y is 0-15 on 4bits;	z is 0-255 on 8bits;
		 */ 
		String source = "0000";
		String vectorAddress[] = address.split("[.]"); 
		int xInt = Integer.parseInt(vectorAddress[0]);
		int yInt = Integer.parseInt(vectorAddress[1]);
		int highInt = 0;
		int zInt = Integer.parseInt(vectorAddress[2]);
		
		xInt = xInt & 0xf; //  4 lower bits : f => 0000 1111
		yInt = yInt & 0xf; //  4 lower bits : f => 0000 1111
		xInt <<= 4; // left shift  4 
		highInt = xInt + yInt;		
		   		
		// highInt: higher 8 bits
		// zInt:  lower 8 bits
		
		// highAddress: from highInt into 2 hexa digits (highHex)
		String highHex = Integer.toHexString(highInt);
		if (highInt < 16) highHex = "0" + highHex;
		
		// DEVICE: from zInt into 2 hexa digits (lowHex)
		String lowHex = Integer.toHexString(zInt);
		if (zInt < 16) lowHex = "0" + lowHex;
		   		
		source = highHex + lowHex;   
		
		return source;
	}

	/**
	 * @param address: address of group of device as x.y.z
	 * @return the address as hex
	 */
	private static String createGroupAddress(String address){
		/* This method is called for the device group address
		 * 	Input: stringa "x/y/z" group address
		 * 	Output: stringa "xy" hexadecimal group address
		 * 		x is 0-15 on 5 bits; y is 0-7 on 3 bits; z is 0-255 on 8 bits;
		 */ 
		String dest = "0000";
		String vectorAddress[] = address.split("[/]"); 
		int xInt = Integer.parseInt(vectorAddress[0]);
		int yInt = Integer.parseInt(vectorAddress[1]);
		int highInt = 0;
		int zInt = Integer.parseInt(vectorAddress[2]);
				
		xInt = xInt & 0x1f;	// lower 5 bits  (1f => 0001 1111)
		yInt = yInt & 0x7; 	// lower 3 bit ( 7 => 0000 0111)
		xInt <<= 3; // left shift of  3 bits
		highInt = xInt + yInt;

		// highInt:higher 8 bits
		// zInt: lower 8 bits
	
		// highAddress: from highInt into 2 hexa digits (highHex)
		String highHex = Integer.toHexString(highInt);
		if (highInt < 16) highHex = "0" + highHex;
		
		// DEVICE: from zInt into 2 hexa digits (lowHex)
		String lowHex = Integer.toHexString(zInt);
		if (zInt < 16) lowHex = "0" + lowHex;
		   		
		dest = highHex + lowHex;   
	
		return dest;
	}

	/**
	 * @param test: a message string
	 * @return the message as hex
	 */
	private static byte [] toBytes (String test){
		
	   byte bTest[] = new byte[test.length() / 2];
	   for (int i = 0; i < bTest.length; i++) {
		   bTest[i] = (byte) Integer.parseInt(test.substring(2*i, 2*i+2), 16);
	   }
	   return bTest;
	}

	/**
	 * @param mex message from the house in bytes
	 * @return message as String
	 */
	private static String getInfoFromMessage(byte message[]){
		
		/*	mex[]:
		 * 		0-6) header;
		 * 		7) 0c = reading; bc = command
		 * 		8-9) source;	// device which provides its state
		 * 		10-11) destination;	// can be both group (managed) or single (unmanaged) 
		 * 		12-13) stuff = d100;
		 * 		14) command or state;
		 */
		String telegram = new String();
		
		byte sourceByte[] = new byte[2];
		byte destByte[] = new byte[2];
		byte valueByte[] = new byte[1];
		byte typeByte[] = new byte[2];
		
		sourceByte[0] = message[10];		sourceByte[1] = message[11];
		destByte[0] = message[12];		destByte[1] = message[13];
		valueByte[0] = message[16];
		
		String source = KnxEncoder.getAddress(sourceByte);
		// Decoding  group Address, but can be singol address
		String destination = KnxEncoder.getGroupAddress(destByte); 
		
		String value = KnxEncoder.getStatus(valueByte);
		String type = KnxEncoder.getType(typeByte);
		
		telegram = source + "#" + destination + "#" + value + "#" + type;
		
		return telegram;

	}
	
	/**
	 * From address in byte to address in x.y.z format
	 * @param buffer single device address in bytes
	 * @return address as String
	 */
	private static String getAddress(byte buffer[]){
		String address = new String();
		
		// buffer[0]: higher bits
		// buffer[1]: lower bits
		
		int highAddress = buffer[0];
		int lowAddress = ((int)buffer[1]) & 0xff; // prendi gli 8 bit + alti:  => 1111 1111
		
		int area = highAddress & 0xf0; 	// prendi i 4 bit + alti:  => 1111 0000
		area = area >>4; // shifta di 4 bit a dx
		int linea = highAddress & 0xf; 	// prendi i 4 bit + bassi: => 0000 1111

		address = area + "." + linea + "." + lowAddress;
		return address;
	}

	
	/**
	 * @param buffer group device address in bytes
	 * @return group device address as String
	 */
	public static String getGroupAddress(byte buffer[]){
		String groupAddress = new String();
		// prende groupAddress in byte e restituisce indirizzo visuale "x/y/z"

		// prende address in byte e restituisce indirizzo visuale "x.y.z"
		// buffer[0]: parte alta
		// buffer[1]: parte bassa
		
		int highAddress = buffer[0];
		int lowAddress = buffer[1];	// prende il segno sopra 127
		
		int main = highAddress & 0xf0; 	// prendi i 5 bit + alti:  => 1111 0000
		main = main >>4; // shifta di 3 bit a dx
		int middle = highAddress & 0xf; // prendi i 3 bit + bassi: => 0000 1111
		
		groupAddress = main + "/" + middle + "/" + lowAddress;
	
		return groupAddress;
	}

	
	/**
	 * @param buffer status in bytes
	 * @return status as String
	 */
	public static String getStatus(byte buffer[]){
		// Di per se � un byte, ma se si scopre che centrano anche quelli prima serve un array
		String status = new String();//Integer.toHexString(buffer[0]);
		// Quindi buffer[0] contiene l'int che rappresenta lo stato 
		//		(40=off; 41=on) in hexa; 	(64=off; 65=on) in decimale ???????????????? sembra 80 e 81
		//		(80=off; 81=on) in hexa; 	(128=off; 129=on) in decimal
		/*if (buffer[0]==-128) status = "Off";
		else if (buffer[0]==-127) status = "On";
		else {
			// Per visualizzare stato HEXA a video
			status = Integer.toHexString(buffer[0]);
			if (status.length()==1) {
				status = "0" + status;
			   }
		   else if(status.length()==8){
			   status = (String)status.subSequence(6,8);
		   }
		}
			*/
		byte one=1;
		byte stateByte=buffer[0];
		int lastBit=stateByte & one;
		if(lastBit==1)
			status="On";
		else
			status="Off";
		// prende byte e restituisce lo stato: occhio a ffffff se > 15
		return status;
	}
	
	/**
	 * @param buffer message type in bytes
	 * @return message type as String
	 */
	private static String getType(byte buffer[]){
		// Di per se � un byte, ma se si scopre che centrano anche quelli prima serve un array
		String status = new String();
		switch (buffer[0]){
			case 'b' : status = "WRITE"; 
			break;
			case 'c': status = "READ"; 
			break;
			//default: status = "UNKNOWN";
			default: {
				// Per visualizzare stato HEXA non codificato (son 2byte almeno)
				String appoggioStatus = new String();
				for (int k=0; k<buffer.length; k++){
					appoggioStatus = Integer.toHexString(buffer[k]);
					if (appoggioStatus.length()==1) {
						status = status + "0" + appoggioStatus;
					   	//appo = "<" + appo + ">"; // per stampare a video visuale campi
					   }
				   else if(appoggioStatus.length()==8){
					   status = status + (String)appoggioStatus.subSequence(6,8);
				   }
				}
			}
		}
		// prende byte e restituisce lo stato: occhio a ffffff se > 15
		return status;
	}

	
	/**
	 * @param telegram the konnex telegram
	 * @return telegram suitable to be shown on screen
	 */
	public static String displayTelegram(String telegram){

		String header = telegram.substring(0, 14);
		String cs = telegram.substring(14,16);
		String source = telegram.substring(16, 20);
		String dest = telegram.substring(20,24);
		String stuff = telegram.substring(24,28);
		String data = telegram.substring(28,30);
		String sepa = "  ";
		
		return header + sepa + cs + sepa + source + sepa + dest + sepa + stuff + sepa + data ;
	}
}
package org.universAAL.lddi.knx.utils;



/** Provides bottom-up (knx to uAAL) and top-down (uAAL to knx) translation of commands.
 * @author Enrico Allione (enrico.allione@gmail.com)
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxEncoder {
	
	public static enum KnxMessageType{READ,WRITE,SCENARIO}; 
	
	/**
	 * 
	 * @param deviceAddress address of the device
	 * @param highStatus status to be set to the device
	 * @return
	 */
	public static byte[] encode(boolean repeatBit, String deviceAddress, String command, KnxMessageType messageType){

		// Is repeat Bit really important? (bit number 5 in first byte)
		// When sending a packet the first time it should be 1;
		// 0 when the packet is repeated (http://de.wikipedia.org/wiki/Europ%C3%A4ischer_Installationsbus)
		
		
		// TODO Whole method must be rewritten!!!
		
		// Costant fields
		String header = "e000000000010080";	// Konnex Header
		
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
		String destination = "";
		
		if (deviceAddress.contains("/")) 
			destination = KnxEncoder.createGroupAddress(deviceAddress);	// group address
		else if (deviceAddress.contains(".")) 
			destination = KnxEncoder.createAddress(deviceAddress);			// physical addres

		String stringTelegram =  header + type + source + destination + stuff + lowStatus;

		byte messagge[] = KnxEncoder.toBytes(stringTelegram);
		
		return  messagge;
	}
	
	public static byte[] encode(String deviceAddress, String command, KnxMessageType messageType){
		return encode(false, deviceAddress, command, messageType);
	}

	/**
	 * @param message: udpTelegram from knx
	 * @return message as String; null if telegram is not valid
	 */
	public static KnxTelegram decode(byte knxMessage[]){
		/* Receive the bytecode and  decode it in a knxMessage */
		
//		String readMessage = new String();	
//		readMessage = KnxEncoder.getInfoFromMessage(message);	
//		return readMessage;
		
		/*	KNX packet structure: http://de.wikipedia.org/wiki/Europ%C3%A4ischer_Installationsbus
		 *  
		 * 	knxmessage[]:
		 * 		0) control
		 * 		1-2) source;	// device which provides its state
		 * 		3-4) destination;	// can be both group (managed) or single (unmanaged) 
		 * 		5) DRL (Destination-adress-flag, Routing-counter, LENGTH(data))
		 * 		6) TPCI/APCI
		 * 		7) Data/ACPI (command or state; there are 15 different command types)
		 * 		following bytes) data
		 */
		
		// check if telegram valid
		if (knxMessage.length < 8)
			return null;
		
		KnxTelegram telegram = new KnxTelegram();
		telegram.setSourceByte(new byte[] {knxMessage[1], knxMessage[2]});
		telegram.setDestByte(new byte[] {knxMessage[3], knxMessage[4]});
		telegram.setDrlByte(knxMessage[5]);
		telegram.setValueByte(knxMessage[7]);
		return telegram;
	}
	

	/**
	 * Not reviewed yet!
	 * 
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
	 * Not reviewed yet!
	 * 
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
	 * Not reviewed yet!
	 * 
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
	 * @param drlByte
	 * @return drl bits (4 bits)
	 */
	static int getDataLength(byte drlByte) {
		
		return ((int)drlByte) & 0xf; //mask 4 right bits
		
	}

	
	/**
	 * @param valueByte
	 * @return string representation of valueByte
	 */
	static String getDataValue(byte valueByte) {
		  int i = valueByte & 0xFF;
		  return Integer.toHexString(i);
	}

	/**
	 * Convert address from bytes to address in x.y.z format
	 * @param buffer single device address in bytes
	 * @return device address as String
	 */
	static String getAddress(byte buffer[]){

		// buffer[0]: higher bits
		// buffer[1]: lower bits

		// layout knx device address: AAAA LLLL  DDDD DDDD
		// A=area
		// L=line
		// D=device
		
		int highAddress = buffer[0];

		//device address 8bit
		int lowAddress = ((int)buffer[1]) & 0xff; // prendi gli 8 bit + alti:  => 1111 1111

		//area code 4msbits
		int area = highAddress & 0xf0; 	// prendi i 4 bit + alti:  => 1111 0000
		area = area >>4; // shifta di 4 bit a dx

	    //line code 4lsbits
	    int linea = highAddress & 0xf; 	// prendi i 4 bit + bassi: => 0000 1111

	    return area + "." + linea + "." + lowAddress;
	}

	
	/**
	 * Convert address from bytes to address in x/y/z format
	 * @param buffer group device address in bytes
	 * @return group address as String
	 */
	public static String getGroupAddress(byte buffer[]){

		// buffer[0]: higher bits
		// buffer[1]: lower bits

		// layout knx group address: MMMM MIII  SSSS SSSS
		// M=main group
		// I=middle group
		// S=sub group
		
		int highAddress = buffer[0];
		int lowAddress = buffer[1];
		
		//main group 5 msbits
		int main = highAddress & 0xf8; 	// prendi i 5 bit + alti:  => 1111 1000
		main = main >> 3; // shifta di 3 bit a dx
		
		//middle group 3 lsbits
		int middle = highAddress & 0x7; // prendi i 3 bit + bassi: => 0000 0111
		
		return main + "/" + middle + "/" + lowAddress;
	}

//	The mapping to existing devices (devicetypes) is not possible at this stage. It is done in KnxNetworkDriverImp
//	Therefore we cannot transfer valueByte value to a command here!
//	
//	/**
//	 * octet number 7 in KNX prot
//	 * 
//	 * @param valueByte status in bytes
//	 * @return status as String
//	 */
//	public static String getStatus(byte valueByte){
//		// Di per se � un byte, ma se si scopre che centrano anche quelli prima serve un array
//		String status = new String();//Integer.toHexString(buffer[0]);
//		// Quindi buffer[0] contiene l'int che rappresenta lo stato 
//		//		(40=off; 41=on) in hexa; 	(64=off; 65=on) in decimale ???????????????? sembra 80 e 81
//		//		(80=off; 81=on) in hexa; 	(128=off; 129=on) in decimal
//		/*if (buffer[0]==-128) status = "Off";
//		else if (buffer[0]==-127) status = "On";
//		else {
//			// Per visualizzare stato HEXA a video
//			status = Integer.toHexString(buffer[0]);
//			if (status.length()==1) {
//				status = "0" + status;
//			   }
//		   else if(status.length()==8){
//			   status = (String)status.subSequence(6,8);
//		   }
//		}
//			*/
//		
//		// include knx datapointtypes; this decoding should be done in the drivers; remove this hack
//		byte one=1;
////		byte stateByte=valueByte[0];
//		int lastBit=valueByte & one;
//		if(lastBit==1)
//			status="On";
//		else
//			status="Off";
//		// prende byte e restituisce lo stato: occhio a ffffff se > 15
//		return status;
//	}
	
	/**
	 * @param buffer message type in bytes
	 * @return message type as String
	 */
	static String getType(byte buffer[]){
		// Di per se � un byte, ma se si scopre che centrano anche quelli prima serve un array
		StringBuffer status = new StringBuffer();
		switch (buffer[0]){
			case 'b' : return "WRITE"; 
			
			case 'c': return "READ"; 
			
			//default: status = "UNKNOWN";
			default: {
				// Per visualizzare stato HEXA non codificato (son 2byte almeno)
//				String appoggioStatus = "";
				for (int k=0; k<buffer.length; k++){
					String appoggioStatus = Integer.toHexString(buffer[k]);
					if (appoggioStatus.length()==1) {
						status.append("0" + appoggioStatus);
					   	//appo = "<" + appo + ">"; // per stampare a video visuale campi
					   }
				   else if(appoggioStatus.length()==8){
					   status.append(appoggioStatus.subSequence(6,8));
				   }
				}
			}
		}
		// prende byte e restituisce lo stato: occhio a ffffff se > 15
		return status.toString();
	}

	
	/**
	 * @param the konnex telegram
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
	
    /**
     * This method remove, if present, the "0x" prefix of the hexValue variable
     * @param hexValue string containing an hex value
     * @return the same string without prefix
     */
	public static String clearHexValue(String hexValue) {
		String correctHexValue;
		if(hexValue.startsWith("0x")){
			correctHexValue=hexValue.substring(2);
		}else
		{
			correctHexValue=hexValue;
		}
		return correctHexValue;
	}
}
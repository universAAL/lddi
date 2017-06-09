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

package org.universAAL.lddi.knx.utils;

/**
 * Provides bottom-up (knx to uAAL) and top-down (uAAL to knx) translation of
 * commands.
 * 
 * @author Enrico Allione (enrico.allione@gmail.com)
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxEncoder {

	public static enum KnxMessageType {
		READ, WRITE, SCENARIO
	};

	/**
	 * this nibble defines the number of data bytes in the telegram. at least 1
	 * (e.g. for DPT1) max 15 (0xF)
	 */
	private static String headerDataLength = "1";

	// UDP Multicast Header !!!
	private static String udpMcHeaderFront = "06100530001";
	private static String udpMcHeaderRear = "2900";

	// UDP Header
	/**
	 * next 3 bits after 44 are counter
	 */
	private static String udpHeader = "061004200016044101001100";
	// private static String udpHeaderRear = "2900";

	/**
	 * Encode a knx telegram for sending to a knx bus. For all datapoint types!
	 * 
	 * @param repeatBit
	 *            has this telegram been sent already?
	 * @param sourceByte
	 *            sending KNX address
	 * @param targetAddress
	 *            knx address - could be device or group address
	 * @param dataByte
	 *            knx command
	 * @param messageType
	 *            (read, write, scenario)
	 * @return knx telegram for sending to knx bus
	 */
	public static byte[] encode(boolean repeatBit, byte[] sourceByte, String targetAddress, byte[] dataByte,
			KnxCommand commandType, boolean multicast) {

		// String header = "0610053000122900";
		// String header = "061004200016044406001100"; // header copied from ETS
		// wireshark sniff

		KnxTelegram telegram = new KnxTelegram();

		telegram.createDefaultControlByte();

		if (repeatBit)
			telegram.setRepeatBit();

		telegram.setSourceByte(sourceByte);

		if (targetAddress.contains("/")) {
			// group address
			telegram.setDestByte(KnxEncoder.convertToByteArray(KnxEncoder.convertGroupAddressToHex(targetAddress)));
		} else if (targetAddress.contains(".")) {
			// physical address
			telegram.setDestByte(KnxEncoder.convertToByteArray(KnxEncoder.convertDeviceAddressToHex(targetAddress)));
		}

		telegram.setDataBytes(dataByte);

		headerDataLength = Integer.toHexString(dataByte.length);

		telegram.setKnxCommandType(commandType);

		byte B_header[];
		if (multicast)
			B_header = KnxEncoder.convertToByteArray(udpMcHeaderFront + headerDataLength + udpMcHeaderRear);
		else
			B_header = KnxEncoder.convertToByteArray(udpHeader);

		byte B_telegram[] = telegram.getByteArray();
		byte message[] = new byte[B_header.length + B_telegram.length];
		System.arraycopy(B_header, 0, message, 0, B_header.length);
		System.arraycopy(B_telegram, 0, message, B_header.length, B_telegram.length);
		return message;
	}

	// /**
	// * Wrapper for other encode method. Just adding false as repeatBit.
	// *
	// * @see public static byte[] encode(boolean repeatBit, String knxAddress,
	// * String command, KnxMessageType messageType)
	// */
	// public static byte[] encode(String deviceAddress, boolean command,
	// KnxCommand commandType) {
	// return encode(false, deviceAddress, command, commandType);
	// }

	// /**
	// * Encode a knx telegram for sending to a knx bus. Only for DPT1 (on/off)!
	// *
	// * @param repeatBit
	// * ; has this telegram been sent already?
	// * @param targetAddress
	// * knx address - could be device or group address
	// * @param value
	// * knx status
	// * @param messageType
	// * (read, write, scenario)
	// * @return knx telegram for sending to knx bus
	// */
	// public static byte[] encode(boolean repeatBit, String targetAddress,
	// boolean value, KnxCommand commandType) {
	//
	// String header = "0610053000112900";
	//
	// KnxTelegram telegram = new KnxTelegram();
	//
	// telegram.createDefaultControlByte();
	//
	// if (repeatBit) telegram.setRepeatBit();
	//
	// // Sending commands from ETS sets source address to 15/15/1. So do we.
	// telegram.setSourceByte(new byte[] { (byte) 0xFF, (byte) 0x01 });
	//
	// if (targetAddress.contains("/")) {
	// // group address
	// telegram.setDestByte(KnxEncoder.convertToByteArray(KnxEncoder
	// .convertGroupAddressToHex(targetAddress)));
	// } else if (targetAddress.contains(".")) {
	// // physical address
	// telegram.setDestByte(KnxEncoder.convertToByteArray(KnxEncoder
	// .convertDeviceAddressToHex(targetAddress)));
	// }
	//
	// telegram.setDataLength(1);
	//
	// telegram.setDpt1Data(value);
	//
	// telegram.setKnxCommandType(commandType);
	//
	//
	//// String stringTelegram = header + controlbyte + sourceAddress
	//// + destAddress + drl + pci + data;
	//// KnxEncoder.convertToByteArray(stringTelegram);
	////
	// byte B_header[] = KnxEncoder.convertToByteArray(header);
	// byte B_telegram[] = telegram.getByteArray();
	// byte message[] = new byte[B_header.length + B_telegram.length];
	// System.arraycopy(B_header, 0, message, 0, B_header.length);
	// System.arraycopy(B_telegram, 0, message, B_header.length,
	// B_telegram.length);
	// return message;
	// }

	/**
	 * Decode knx byte array and create KnxTelegram object.
	 * 
	 * @param knx
	 *            telegram
	 * @return KnxTelegram object; null if telegram is not valid
	 */
	public static KnxTelegram decode(byte knxMessage[]) {
		/* Receive the bytecode and decode it in a knxMessage */

		// String readMessage = new String();
		// readMessage = KnxEncoder.getInfoFromMessage(message);
		// return readMessage;

		/*
		 * KNX packet structure:
		 * http://de.wikipedia.org/wiki/Europ%C3%A4ischer_Installationsbus
		 * 
		 * knxmessage[]: 0) control 1-2) source; // device which provides its
		 * state 3-4) destination; // can be both group (managed) or single
		 * (unmanaged) 5) DRL (Destination-adress-flag, Routing-counter,
		 * LENGTH(data)) 6) TPCI/APCI 7) Data/ACPI (command or state; there are
		 * 15 different command types) following bytes) data
		 */

		// check if telegram valid (minimum 9 bytes)
		if (knxMessage.length < 9)
			return null;

		KnxTelegram telegram = new KnxTelegram(knxMessage);
		// telegram.setSourceByte(new byte[] { knxMessage[1], knxMessage[2] });
		// telegram.setDestByte(new byte[] { knxMessage[3], knxMessage[4] });
		// telegram.setDrlByte(knxMessage[5]);

		// try {
		// int dataLength = KnxEncoder.extractPayloadLength(knxMessage[5]);
		// byte[] data = new byte[dataLength];
		// System.arraycopy(knxMessage, 7, data, 0, dataLength);
		// telegram.setDataByte(data);
		// } catch (IndexOutOfBoundsException ex) {
		// return null;
		// }

		// If the length of the byte array doesn't match with data length field
		// discard this telegram!
		if (!telegram.telegramDatalengthIsCorrect)
			return null;

		return telegram;
	}

	/**
	 * Remove trailing Zero bytes.
	 * 
	 * @param original
	 * @return cropped byte array
	 */
	public static byte[] removeTrailingZeros(byte[] original) {
		// remove trailing 0
		int i = original.length - 1;
		while (original[i] == 0)
			--i;
		// now original[i] is the last non-zero byte
		byte[] cropped = new byte[i + 1];
		System.arraycopy(original, 0, cropped, 0, i + 1);

		return cropped;
	}

	/**
	 * Translates readable device addresses to knx hex code. x is 0-15 on 4bits;
	 * y is 0-15 on 4bits; z is 0-255 on 8bits;
	 * 
	 * @param address
	 *            of the device as x.y.z
	 * @return the address as hex String (2 bytes)
	 */
	public static String convertDeviceAddressToHex(String address) {

		String source = "0000";
		String vectorAddress[] = address.split("[.]");
		int xInt = Integer.parseInt(vectorAddress[0]);
		int yInt = Integer.parseInt(vectorAddress[1]);
		int highInt = 0;
		int zInt = Integer.parseInt(vectorAddress[2]);

		xInt = xInt & 0xf; // 4 lower bits : f => 0000 1111
		yInt = yInt & 0xf; // 4 lower bits : f => 0000 1111
		xInt <<= 4; // left shift 4
		highInt = xInt + yInt;

		// highInt: higher 8 bits
		// zInt: lower 8 bits

		// highAddress: from highInt into 2 hexa digits (highHex)
		String highHex = Integer.toHexString(highInt);
		if (highInt < 16)
			highHex = "0" + highHex;

		// DEVICE: from zInt into 2 hexa digits (lowHex)
		String lowHex = Integer.toHexString(zInt);
		if (zInt < 16)
			lowHex = "0" + lowHex;

		source = highHex + lowHex;

		return source;
	}

	/**
	 * Converts a readable knx group address to hex encoded string. x is 0-15 on
	 * 5 bits; y is 0-7 on 3 bits; z is 0-255 on 8 bits;
	 * 
	 * @param readable
	 *            string "x/y/z" group address
	 * @return hex-encoded string "xy" hexadecimal group address
	 */
	public static String convertGroupAddressToHex(String address) {
		String dest = "0000";
		String vectorAddress[] = address.split("[/]");
		int main = Integer.parseInt(vectorAddress[0]);
		int middle = Integer.parseInt(vectorAddress[1]);
		int highInt = 0;
		int sub = Integer.parseInt(vectorAddress[2]);

		main = main & 0x1f; // lower 5 bits (1f => 0001 1111)
		main <<= 3; // left shift of 3 bits

		middle = middle & 0x7; // lower 3 bit (7 => 0000 0111)
		highInt = main + middle;

		// highHex:higher 8 bits
		// lowHex: lower 8 bits

		// highAddress: from highInt into 2 hexa digits (highHex)
		String highHex = Integer.toHexString(highInt);
		if (highInt < 16)
			highHex = "0" + highHex;

		// from sub into 2 hexa digits (lowHex)
		String lowHex = Integer.toHexString(sub);
		if (sub < 16)
			lowHex = "0" + lowHex;

		dest = highHex + lowHex;

		return dest;
	}

	/**
	 * Converts a hex-encoded knx message to byte array.
	 * 
	 * @param a
	 *            hex-encoded message string
	 * @return the message as byte array
	 */
	public static byte[] convertToByteArray(String hexString) {

		byte bTest[] = new byte[hexString.length() / 2];
		for (int i = 0; i < bTest.length; i++) {
			bTest[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
		}
		return bTest;
	}

	/**
	 * Convert byte array to readable hex-encoded string.
	 * 
	 * @param
	 * @return hex representation of the byte array as a String with semicolons
	 *         as delimiter
	 */
	public static String convertToReadableHex(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			if (i > 0)
				sb.append(':');
			sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	/**
	 * Convert byte array to hex encoded string without delimiters.
	 */
	public static String convertToHex(byte[] b) {
		StringBuilder byteString = new StringBuilder();

		for (int i = 0; i < b.length; i++) {

			String hexNumber = "0" + Integer.toHexString(0xff & b[i]);

			byteString.append(hexNumber);

		}
		return byteString.toString();
	}

	/**
	 * Extract bits representing knx message data length (payload).
	 * 
	 * @param drlByte
	 *            of knx telegram
	 * @return length of payload
	 */
	static int extractPayloadLength(byte drlByte) {

		return ((int) drlByte) & 0xf; // mask 4 right bits

	}

	/**
	 * Convert address from knx encoded bytes to device address in x.y.z format.
	 * 
	 * @param buffer
	 *            single device address in bytes
	 * @return device address as String
	 */
	static String convertDeviceAddressToReadable(byte buffer[]) {

		// buffer[0]: higher bits
		// buffer[1]: lower bits

		// layout knx device address: AAAA LLLL DDDD DDDD
		// A=area
		// L=line
		// D=device

		int highAddress = buffer[0];

		// device address 8bit
		int lowAddress = ((int) buffer[1]) & 0xff; // prendi gli 8 bit + alti:
		// => 1111 1111

		// area code 4msbits
		int area = highAddress & 0xf0; // prendi i 4 bit + alti: => 1111 0000
		area = area >> 4; // shifta di 4 bit a dx

		// line code 4lsbits
		int linea = highAddress & 0xf; // prendi i 4 bit + bassi: => 0000 1111

		return area + "." + linea + "." + lowAddress;
	}

	/**
	 * Convert knx group address from knx encoded bytes to readable x/y/z
	 * format.
	 * 
	 * @param buffer
	 *            group device address in bytes
	 * @return group address as String
	 */
	public static String convertGroupAddressToReadable(byte buffer[]) {

		// buffer[0]: higher bits
		// buffer[1]: lower bits

		// layout knx group address: MMMM MIII SSSS SSSS
		// M=main group
		// I=middle group
		// S=sub group

		int highAddress = buffer[0];
		int lowAddress = buffer[1];

		// main group 5 msbits
		int main = highAddress & 0xf8; // prendi i 5 bit + alti: => 1111 1000
		main = main >> 3; // shifta di 3 bit a dx

		// middle group 3 lsbits
		int middle = highAddress & 0x7; // prendi i 3 bit + bassi: => 0000 0111

		return main + "/" + middle + "/" + lowAddress;
	}

	// The mapping to existing devices (devicetypes) is not possible at this
	// stage. It is done in KnxNetworkDriverImp
	// Therefore we cannot transfer valueByte value to a command here!
	//
	// /**
	// * octet number 7 in KNX prot
	// *
	// * @param valueByte status in bytes
	// * @return status as String
	// */
	// public static String getStatus(byte valueByte){
	// // Di per se � un byte, ma se si scopre che centrano anche quelli prima
	// serve un array
	// String status = new String();//Integer.toHexString(buffer[0]);
	// // Quindi buffer[0] contiene l'int che rappresenta lo stato
	// // (40=off; 41=on) in hexa; (64=off; 65=on) in decimale ????????????????
	// sembra 80 e 81
	// // (80=off; 81=on) in hexa; (128=off; 129=on) in decimal
	// /*if (buffer[0]==-128) status = "Off";
	// else if (buffer[0]==-127) status = "On";
	// else {
	// // Per visualizzare stato HEXA a video
	// status = Integer.toHexString(buffer[0]);
	// if (status.length()==1) {
	// status = "0" + status;
	// }
	// else if(status.length()==8){
	// status = (String)status.subSequence(6,8);
	// }
	// }
	// */
	//
	// // include knx datapointtypes; this decoding should be done in the
	// drivers; remove this hack
	// byte one=1;
	// // byte stateByte=valueByte[0];
	// int lastBit=valueByte & one;
	// if(lastBit==1)
	// status="On";
	// else
	// status="Off";
	// // prende byte e restituisce lo stato: occhio a ffffff se > 15
	// return status;
	// }

	// /**
	// * @param buffer
	// * message type in bytes
	// * @return message type as String
	// */
	// static String getType(byte buffer[]) {
	// // Di per se � un byte, ma se si scopre che centrano anche quelli
	// // prima serve un array
	// StringBuffer status = new StringBuffer();
	// switch (buffer[0]) {
	// case 'b':
	// return "WRITE";
	//
	// case 'c':
	// return "READ";
	//
	// // default: status = "UNKNOWN";
	// default: {
	// // Per visualizzare stato HEXA non codificato (son 2byte almeno)
	// // String appoggioStatus = "";
	// for (int k = 0; k < buffer.length; k++) {
	// String appoggioStatus = Integer.toHexString(buffer[k]);
	// if (appoggioStatus.length() == 1) {
	// status.append("0" + appoggioStatus);
	// // appo = "<" + appo + ">"; // per stampare a video visuale
	// // campi
	// } else if (appoggioStatus.length() == 8) {
	// status.append(appoggioStatus.subSequence(6, 8));
	// }
	// }
	// }
	// }
	// // prende byte e restituisce lo stato: occhio a ffffff se > 15
	// return status.toString();
	// }

	// /**
	// * @param the konnex telegram
	// * @return telegram suitable to be shown on screen
	// */
	// public static String displayTelegram(String telegram){
	//
	// String header = telegram.substring(0, 14);
	// String cs = telegram.substring(14,16);
	// String source = telegram.substring(16, 20);
	// String dest = telegram.substring(20,24);
	// String stuff = telegram.substring(24,28);
	// String data = telegram.substring(28,30);
	// String sepa = " ";
	//
	// return header + sepa + cs + sepa + source + sepa + dest + sepa + stuff +
	// sepa + data ;
	// }

	/**
	 * This method remove, if present, the "0x" prefix of the hexValue variable.
	 * 
	 * @param hexValue
	 *            string containing an hex value
	 * @return the same string without prefix
	 */
	public static String clearHexValue(String hexValue) {
		String correctHexValue;
		if (hexValue.startsWith("0x")) {
			correctHexValue = hexValue.substring(2);
		} else {
			correctHexValue = hexValue;
		}
		return correctHexValue;
	}
}
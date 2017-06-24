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
 * Structure of a KNX telegram. Minimum 9 bytes.
 *
 * 0 1 2 3 4 5 6 7 8 9 - N-1 Controlbyte IP-Fill-Byte Sourceaddress Destaddress
 * DRL TPCI/APCI APCI/Data Data
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxTelegram {

	// Bytes
	private byte controlByte;
	private byte ipFillByte = (byte) 0xd0; // only in knx packets over IP! d0 in
											// Weinzierl docu; e0 in ETS sent
											// telegrams
	private byte sourceByte[] = new byte[2];
	private byte destByte[] = new byte[2];
	private byte drlByte; // DRL-Byte (Destination-address-flag 7. bit,
							// Routing-counter6-4. bit, Length3-0. bit)
	private byte tpciApciByte; // is overlapping with the next byte (apci)
	private byte apciDataByte; // is overlapping with the data byte (e.g. for
								// 1-bit datatypes -> 80, 81)
	private byte extraDataByte[];

	// Information in control byte
	private boolean repeat; // is the packet repeated (sent again) or not
	private KnxPacketPriority priority;

	// Information in DRL byte
	/**
	 * Bit 7 of DRL byte. Tests on the IP network showed that this bit is always
	 * 0 although it should be 1 (target is group address) Maybe the KNX/IP
	 * Router removes it!
	 */
	private boolean targetIsGroupAddress;
	/**
	 * Bit 6-4 of DRL byte. Tests on the IP network showed that these bits are
	 * always 0. Maybe the KNX/IP Router removes it!
	 */
	private int routingCounter;

	private int dataLength;

	// Information in TPCI/APCI bytes
	KnxCommand knxCommand;
	/**
	 * Only for DTP1 the data (0 or 1) is directly within the 8 byte BBXXXXXD
	 * (bit 0). For other DPTs this bit is not used. Instead the data is
	 * appended with extra bytes.
	 */
	// boolean telegramIsDatapointType1;
	// boolean dpt1DataBool;
	// byte dpt1DataByte;
	boolean telegramDatalengthIsCorrect;

	/**
	 * Constructor
	 *
	 * @param knxMessage
	 */
	public KnxTelegram(byte[] knxMessage) {
		this.controlByte = knxMessage[0];
		this.ipFillByte = knxMessage[1]; // should be always the same (0xd0)
		this.sourceByte[0] = knxMessage[2];
		this.sourceByte[1] = knxMessage[3];
		this.destByte[0] = knxMessage[4];
		this.destByte[1] = knxMessage[5];
		this.drlByte = knxMessage[6];
		this.tpciApciByte = knxMessage[7];
		this.apciDataByte = knxMessage[8];

		// extract detail info

		// repeat 5.bit from control byte
		if (((this.controlByte & 0x20) >> 5) == 0)
			this.repeat = true;
		else
			this.repeat = false;

		// priority bits 2 + 3 from control byte
		this.priority = KnxPacketPriority.get((int) ((this.controlByte & 0x0C) >> 2));

		// data length: bits 0-3 of DRL byte
		this.dataLength = (int) this.drlByte & 0x0F;

		// check if number of data bytes matches with data length field
		if (knxMessage.length - 9 == this.dataLength - 1)
			this.telegramDatalengthIsCorrect = true;

		// if there are no extra data bytes the DPT is 1
		// if (this.dataLength == 1) {
		// this.telegramIsDatapointType1 = true;
		// this.dpt1DataByte = (byte) (this.apciDataByte & 0x3F);
		// this.dpt1DataBool = ( this.dpt1DataByte == 1)? true : false;
		// System.out.println("***********telegramIsDatapointType1: " +
		// this.telegramIsDatapointType1);
		// }

		// store extra data bytes to array
		if (this.dataLength > 1 && this.telegramDatalengthIsCorrect) {
			// fill databyte
			this.extraDataByte = new byte[this.dataLength - 1];
			try {
				System.arraycopy(knxMessage, 9, this.extraDataByte, 0, this.dataLength - 1);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}

		// command type: bits 0+1 from TPCI byte and 7+6 from APCI byte
		this.knxCommand = KnxCommand.get((int) ((this.tpciApciByte & 0x03) << 2) + ((this.apciDataByte & 0xC0) >> 6));

	}

	/**
	 * Constructor Create an empty object and set fields separately.
	 */
	public KnxTelegram() {
	}

	/**
	 * @return knx telegram in readable format.
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(KnxEncoder.convertDeviceAddressToReadable(this.sourceByte));
		builder.append('#');
		builder.append(KnxEncoder.convertGroupAddressToReadable(this.destByte));
		builder.append('#');
		// if (this.telegramIsDatapointType1) {
		// builder.append("DPT1-Value:" + this.dpt1DataBool);
		// } else {
		builder.append(String.format("%02X", this.apciDataByte));
		if (this.extraDataByte != null) {
			builder.append(":" + KnxEncoder.convertToReadableHex(this.extraDataByte));
		}
		// }
		builder.append('#');
		// builder.append(KnxEncoder.extractPayloadLength(this.drlByte));
		// builder.append('#');
		builder.append(this.knxCommand);

		return new String(builder);
	}

	/**
	 * Create a default control byte 1 0 1 1 1 1 0 0 (default priority and no
	 * repeat bit)
	 */
	public void createDefaultControlByte() {
		this.controlByte = (byte) 0xbc;
	}

	/**
	 * Set Repeat Bit in KNX telegram (5. bit in control byte)
	 */
	public void setRepeatBit() {
		this.controlByte = (byte) (this.controlByte | (byte) 0x20);
	}

	/**
	 * Set KNX priority.
	 */
	public void setPriority(KnxPacketPriority prio) {
		this.controlByte = (byte) (this.controlByte | (prio.getTypeCode() << 2));
	}

	/**
	 * Set the data length in the DRL byte (bits 0-3)
	 */
	public void setDataLength(int length) {
		this.dataLength = length;
		this.drlByte = (byte) (this.drlByte | (length & 0x0F));
	}

	/**
	 * first byte of data byte array is apci/data byte. last 5 bits of the
	 * apci/data byte are reserved for data! other data bytes go to
	 * extraDataByte[]
	 */
	public void setDataBytes(byte[] data) {
		this.setDataLength(data.length);
		this.apciDataByte = (byte) ((this.apciDataByte & 0xC0) | (data[0] & 0x1F));
		// this.extraDataByte = data;
		if (this.dataLength > 1) {
			this.extraDataByte = new byte[this.dataLength - 1];
			System.arraycopy(data, 1, this.extraDataByte, 0, this.dataLength - 1);
		}
	}

	/**
	 * @return apci/data byte and extra data bytes
	 */
	public byte[] getDataByte() {
		byte[] dataBytes = new byte[this.dataLength];
		dataBytes[0] = (byte) (this.apciDataByte & 0x1F);

		if (this.dataLength > 1 && this.extraDataByte != null)
			System.arraycopy(this.extraDataByte, 0, dataBytes, 1, this.dataLength - 1);

		return dataBytes;
	}

	// /**
	// * Set the data bit for DPT1 telegrams. Bit 0 in APCI byte.
	// */
	// public void setDpt1Data(boolean command) {
	// if (command)
	// this.apciDataByte = (byte) (this.apciDataByte | 1 );
	// else
	// this.apciDataByte = (byte) (this.apciDataByte | 0 );
	// }

	/**
	 * Set KNX command type.
	 */
	public void setKnxCommandType(KnxCommand valueWrite) {
		this.apciDataByte = (byte) (this.apciDataByte | (valueWrite.getTypeCode() << 6));
		if (valueWrite == KnxCommand.MEMORY_WRITE)
			this.tpciApciByte = (byte) (this.tpciApciByte | (valueWrite.getTypeCode() >> 2));
	}

	/**
	 * @return byte array of this telegram.
	 */
	public byte[] getByteArray() {
		byte[] ba = new byte[this.dataLength + 8];
		ba[0] = this.controlByte;
		ba[1] = this.ipFillByte;
		ba[2] = this.sourceByte[0];
		ba[3] = this.sourceByte[1];
		ba[4] = this.destByte[0];
		ba[5] = this.destByte[1];
		ba[6] = this.drlByte;
		ba[7] = this.tpciApciByte;
		ba[8] = this.apciDataByte;
		if (this.dataLength > 1)
			System.arraycopy(this.extraDataByte, 0, ba, 9, this.dataLength - 1);
		return ba;
	}

	// /**
	// * @return the telegramIsDatapointType1
	// */
	// public boolean isTelegramIsDatapointType1() {
	// return telegramIsDatapointType1;
	// }

	// /**
	// * @return the dpt1DataByte
	// */
	// public byte getDpt1DataByte() {
	// return dpt1DataByte;
	// }

	/**
	 * @return the sourceByte
	 */
	public byte[] getSourceByte() {
		return this.sourceByte.clone();
	}

	/**
	 * @param sourceByte
	 *            the sourceByte to set
	 */
	public void setSourceByte(byte[] sourceByte) {
		this.sourceByte = sourceByte;
	}

	/**
	 * @return the destByte
	 */
	public byte[] getDestByte() {
		return this.destByte.clone();
	}

	/**
	 * @param destByte
	 *            the destByte to set
	 */
	public void setDestByte(byte[] destByte) {
		this.destByte = destByte;
	}

	/**
	 * @return the priority
	 */
	public KnxPacketPriority getPriority() {
		return priority;
	}

	// /**
	// * @param valueByte the valueByte to set
	// */
	// public void setDataByte(byte[] extraDataByte) {
	//// this.dataByte = new byte[extraDataByte.length];
	//// System.arraycopy(extraDataByte, 0, this.dataByte, 0,
	// extraDataByte.length);
	// this.dataByte = extraDataByte;
	// }

	/**
	 * @return the drlByte
	 */
	public byte getDrlByte() {
		return drlByte;
	}

	// /**
	// * @param drlByte the drlByte to set
	// */
	// public void setDrlByte(byte drlByte) {
	// this.drlByte = drlByte;
	// }

	// /**
	// * @return the typeByte
	// */
	// public byte[] getTypeByte() {
	// return this.typeByte.clone();
	// }

	// /**
	// * @param typeByte the typeByte to set
	// */
	// public void setTypeByte(byte[] typeByte) {
	// this.typeByte = typeByte;
	// }

	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return this.dataLength;
	}

	// /**
	// * @param dataLength the dataLength to set
	// */
	// public void setDataLength(int dataLength) {
	// this.dataLength = dataLength;
	// }
}

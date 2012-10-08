package org.universAAL.lddi.knx.utils;


/**
 * Structure of a KNX telegram
 * Octet 
 * 0 				1 		2 		3 		4 		5 		6 			7 					8 ... N-1 	N<=22
 * Controlbyte 		Sourceaddress 	Destaddress 	DRL 	TPCI 	APCI 	Data / APCI 	Data 		Checksum
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxTelegram {

	byte controlByte;
	byte sourceByte[] = new byte[2];
	byte destByte[] = new byte[2];
	byte drlByte; //DRL-Byte (Destination-address-flag, Routing-counter, Length)
	byte pciByte;
	byte dataByte[];
	byte checksumByte;

	// legacy fields
//	byte valueByte;
	byte typeByte[] = new byte[2];
	int dataLength;
	
	/**
	 * @param controlByte (must be 1 byte)
	 * @param sourceByte (must be 2 bytes)
	 * @param destByte (must be 2 bytes)
	 * @param drlByte (must be 1 byte)
	 * @param pciByte (must be 1 byte)
	 * @param dataByte (array of any length)
	 */
	public KnxTelegram(byte controlByte, byte[] sourceByte, byte[] destByte, byte drlByte, 
			byte pciByte, byte[] dataByte) {
		this.controlByte = controlByte;
		this.sourceByte = sourceByte;
		this.destByte = destByte;
		this.drlByte = drlByte;
		this.pciByte = pciByte;
		
//		this.dataByte = new byte[dataByte.length];
		this.dataByte = dataByte;//.clone();
		
	}
	
	/**
	 * 
	 */
	public KnxTelegram() {
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(KnxEncoder.getAddress(sourceByte));
		builder.append('#');
		builder.append(KnxEncoder.getGroupAddress(destByte));
		builder.append('#');
		builder.append(KnxEncoder.getDataValue(dataByte));
		builder.append('#');
		builder.append(KnxEncoder.getDataLength(drlByte));
		// type?
//		builder.append('#');
//		builder.append(KnxEncoder.getType(typeByte));
		
		return new String(builder);
	}
	/**
	 * @return the sourceByte
	 */
	public byte[] getSourceByte() {
		return this.sourceByte.clone();
	}

	/**
	 * @param sourceByte the sourceByte to set
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
	 * @param destByte the destByte to set
	 */
	public void setDestByte(byte[] destByte) {
		this.destByte = destByte;
	}

	/**
	 * @return the valueByte
	 */
	public byte[] getDataByte() {
		return dataByte;
	}
	
	/**
	 * @param valueByte the valueByte to set
	 */
	public void setDataByte(byte[] dataByte) {
		this.dataByte = dataByte;
	}
	
	/**
	 * @return the drlByte
	 */
	public byte getDrlByte() {
		return drlByte;
	}
	
	/**
	 * @param drlByte the drlByte to set
	 */
	public void setDrlByte(byte drlByte) {
		this.drlByte = drlByte;
	}
	
	/**
	 * @return the typeByte
	 */
	public byte[] getTypeByte() {
		return this.typeByte.clone();
	}
	
	/**
	 * @param typeByte the typeByte to set
	 */
	public void setTypeByte(byte[] typeByte) {
		this.typeByte = typeByte;
	}
	
	/**
	 * @return the dataLength
	 */
	public int getDataLength() {
		return dataLength;
	}
	
	/**
	 * @param dataLength the dataLength to set
	 */
	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

}

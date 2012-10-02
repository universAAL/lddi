package org.universAAL.lddi.knx.utils;


/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxTelegram {

	byte sourceByte[] = new byte[2];
	byte destByte[] = new byte[2];
	
	byte valueByte;
	byte drlByte; //DRL-Byte (Destination-address-flag, Routing-counter, Length)
	byte typeByte[] = new byte[2];
	int dataLength;
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(KnxEncoder.getAddress(sourceByte));
		builder.append('#');
		builder.append(KnxEncoder.getGroupAddress(destByte));
		builder.append('#');
		builder.append(KnxEncoder.getDataValue(valueByte));
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
		return sourceByte;
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
		return destByte;
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
	public byte getValueByte() {
		return valueByte;
	}
	/**
	 * @param valueByte the valueByte to set
	 */
	public void setValueByte(byte valueByte) {
		this.valueByte = valueByte;
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
		return typeByte;
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

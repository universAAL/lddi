package org.universAAL.lddi.knx.devicemodel;

import org.universAAL.lddi.knx.devicecategory.KnxDpt9;

/**
 * Concrete implementation of KNX devices for KNX data type 9.***.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9Device extends KnxDevice implements KnxDpt9 {

    /**
     * empty constructor for factory
     */
    public KnxDpt9Device() {
	super();
    }

    @Deprecated
    public void newMessageFromKnxBus(byte[] event) {
	// not used in device; this method is called in driver!
    }

	/**
	 * Calculate float value from knx message payload.
     * 				MSB			LSB
     * float value |-------- --------|
     * encoding 	MEEEEMMM MMMMMMMM
     * FloatValue = (0,01*M)*2(E)
     * E = [0 … 15]
     * M = [-2 048 … 2 047], two’s complement notation
	 */
	public float calculateFloatValue(byte[] payload) {
		byte MSB = payload[0]; 
		byte LSB = payload[1];
		
		byte M_MSB = (byte) (MSB & 0x87);
		byte M_LSB = (byte) (LSB & 0xFF);
		
		byte E = (byte) ((MSB & 0x78) >> 3);

		int e = Integer.parseInt( Byte.toString(E) );
		
		short m = (short) (M_MSB << 8 | (M_LSB & 0xFF));
		
		return (float) ((0.01*m)*(Math.pow(2, e)));
	}
   
}

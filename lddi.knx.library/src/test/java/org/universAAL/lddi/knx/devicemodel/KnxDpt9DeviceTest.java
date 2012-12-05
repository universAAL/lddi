package org.universAAL.lddi.knx.devicemodel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9DeviceTest {

	@Test
	public void calculateFloatValueTest() {
		KnxDpt9Device d = new KnxDpt9Device();
		float fl = d.calculateFloatValue( new byte[] {(byte) 0x0C,(byte) 0xA6} );
		assertTrue("KnxDpt9Device.calculateFloatValue( new byte[] {(byte) 0x0C,(byte) 0xA6} ) " +
				"returns " + fl + " but should return 23.8", 
				Float.toString(fl).equalsIgnoreCase("23.8"));
	}
}

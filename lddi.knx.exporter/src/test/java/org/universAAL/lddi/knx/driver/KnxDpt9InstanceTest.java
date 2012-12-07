package org.universAAL.lddi.knx.driver;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt9InstanceTest {

	@Test
	public void calculateFloatValueTest() {
		KnxDpt9Instance d = new KnxDpt9Instance(null, null, null);
		float fl = d.calculateFloatValue( new byte[] {(byte) 0x80,(byte) 0x0C,(byte) 0xA6} );
		assertTrue("KnxDpt9Device.calculateFloatValue( new byte[] {(byte) 0x0C,(byte) 0xA6} ) " +
				"returns " + fl + " but should return 23.8", 
				Float.toString(fl).equalsIgnoreCase("23.8"));
	}
}

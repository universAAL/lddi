package org.universAAL.lddi.knx.utils;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import org.universAAL.lddi.knx.utils.KnxEncoder.KnxMessageType;


/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxEncoderTest {

	@Test
	public void createGroupAddressTest() {
		assertTrue("KnxEncoder.createGroupAddress('1/2/3') returns " + KnxEncoder.createGroupAddress("1/2/3")
				+ "; but should return 0a03",
				KnxEncoder.createGroupAddress("1/2/3").equalsIgnoreCase("0a03"));
		assertTrue(KnxEncoder.createGroupAddress("0/0/2").equalsIgnoreCase("0002"));
		assertTrue(KnxEncoder.createGroupAddress("1/1/1").equalsIgnoreCase("0901"));
		assertTrue(KnxEncoder.createGroupAddress("29/7/234").equalsIgnoreCase("efea"));
	}
	
	@Test
	public void getDataLengthTest() {
		byte drl = 1;
		assertTrue("KnxEncoder.getDataLength(0x01) returns " + KnxEncoder.getDataLength(drl) +
				"; but should return 1", KnxEncoder.getDataLength(drl) == 1);
		drl = (byte) 0xf;
		assertTrue("KnxEncoder.getDataLength((byte) 0xff) returns " + KnxEncoder.getDataLength(drl) +
				"; but should return 15", KnxEncoder.getDataLength(drl) == 15);
	}
	
	@Test
	public void encodeTest() {
		String hexString = "0610053000112900bce000000901010081";
		byte[] message = new byte[] {(byte) 0xe0, 0, 0, 9, 1, 1, 0, (byte) 0x81 };
		
		assertTrue("KnxEncoder.encode(false, '1/1/1', '81', KnxMessageType.WRITE) returns " + 
				KnxEncoder.getHexString(KnxEncoder.encode(false, "1/1/1", "81", KnxMessageType.WRITE)) +
				"; but should return " + hexString,//KnxEncoder.getHexString(message), 
				Arrays.equals(KnxEncoder.encode(false, "1/1/1", "81", KnxMessageType.WRITE), KnxEncoder.toBytes(hexString))
						);
	}
	
	@Test
	public void createAddressTest() {
		assertTrue("KnxEncoder.createAddress('1.2.3') returns " + KnxEncoder.createAddress("1.2.3") +
				"; but should return 1203", 
				KnxEncoder.createAddress("1.2.3").equalsIgnoreCase("1203"));

		assertTrue("KnxEncoder.createAddress('15.14.234') returns " + KnxEncoder.createAddress("15.14.234") +
				"; but should return feea", 
				KnxEncoder.createAddress("15.14.234").equalsIgnoreCase("feea"));

	}
	
	
//	 JUnit 4 annotations:
//		    @Test kennzeichnet Methoden als ausführbare Testfälle.
//		    @Before und @After markieren Setup- bzw. Teardown-Aufgaben, die für jeden Testfall wiederholt werden sollen.
//		    @BeforeClass und @AfterClass markieren Setup- bzw. Teardown-Aufgaben, die nur einmal pro Testklasse ausgeführt werden sollen.
//		    @Ignore kennzeichnet temporär nicht auszuführende Testfälle.

}

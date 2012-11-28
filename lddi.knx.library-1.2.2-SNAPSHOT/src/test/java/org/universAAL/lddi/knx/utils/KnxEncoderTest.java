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
		assertTrue("KnxEncoder.createGroupAddress('1/2/3') returns " + KnxEncoder.convertGroupAddressToHex("1/2/3")
				+ "; but should return 0a03",
				KnxEncoder.convertGroupAddressToHex("1/2/3").equalsIgnoreCase("0a03"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("0/0/2").equalsIgnoreCase("0002"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("1/1/1").equalsIgnoreCase("0901"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("29/7/234").equalsIgnoreCase("efea"));
	}
	
	@Test
	public void getDataLengthTest() {
		byte drl = 1;
		assertTrue("KnxEncoder.getDataLength(0x01) returns " + KnxEncoder.extractPayloadLength(drl) +
				"; but should return 1", KnxEncoder.extractPayloadLength(drl) == 1);
		drl = (byte) 0xf;
		assertTrue("KnxEncoder.getDataLength((byte) 0xff) returns " + KnxEncoder.extractPayloadLength(drl) +
				"; but should return 15", KnxEncoder.extractPayloadLength(drl) == 15);
		
		byte[] message = new byte[] {(byte) 0xe0, 0, 0, 9, 1, 3, 0, (byte) 0x81, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xe0, 0, 0, 9, 1, 1, 0, (byte) 0x81, (byte) 0x12, (byte) 0x13 }).getDataLength() returns "
						+ KnxEncoder.decode(message).getDataLength()
						+ "; but should be 1", KnxEncoder.decode(message)
						.getDataLength() == 3);
	}
	
	@Test
	public void encodeTest() {
		String hexString = "0610053000112900bce000000901010081";
		
		assertTrue("KnxEncoder.encode(false, '1/1/1', '81', KnxMessageType.WRITE) returns " + 
				KnxEncoder.convertToReadableHex(KnxEncoder.encode(false, "1/1/1", "81", KnxMessageType.WRITE)) +
				"; but should return " + hexString,//KnxEncoder.getHexString(message), 
				Arrays.equals(KnxEncoder.encode(false, "1/1/1", "81", KnxMessageType.WRITE), KnxEncoder.convertToByteArray(hexString))
						);
	}
	
	@Test
	public void createAddressTest() {
		assertTrue("KnxEncoder.createAddress('1.2.3') returns " + KnxEncoder.convertDeviceAddressToHex("1.2.3") +
				"; but should return 1203", 
				KnxEncoder.convertDeviceAddressToHex("1.2.3").equalsIgnoreCase("1203"));

		assertTrue("KnxEncoder.createAddress('15.14.234') returns " + KnxEncoder.convertDeviceAddressToHex("15.14.234") +
				"; but should return feea", 
				KnxEncoder.convertDeviceAddressToHex("15.14.234").equalsIgnoreCase("feea"));

	}
	
	
//	 JUnit 4 annotations:
//		    @Test kennzeichnet Methoden als ausführbare Testfälle.
//		    @Before und @After markieren Setup- bzw. Teardown-Aufgaben, die für jeden Testfall wiederholt werden sollen.
//		    @BeforeClass und @AfterClass markieren Setup- bzw. Teardown-Aufgaben, die nur einmal pro Testklasse ausgeführt werden sollen.
//		    @Ignore kennzeichnet temporär nicht auszuführende Testfälle.

}

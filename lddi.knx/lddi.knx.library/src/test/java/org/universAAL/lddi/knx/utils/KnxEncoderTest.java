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

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;

/**
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxEncoderTest {

	@Test
	public void createGroupAddressTest() {
		assertTrue(
				"KnxEncoder.createGroupAddress('1/2/3') returns " + KnxEncoder.convertGroupAddressToHex("1/2/3")
						+ "; but should return 0a03",
				KnxEncoder.convertGroupAddressToHex("1/2/3").equalsIgnoreCase("0a03"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("0/0/2").equalsIgnoreCase("0002"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("1/1/1").equalsIgnoreCase("0901"));
		assertTrue(KnxEncoder.convertGroupAddressToHex("29/7/234").equalsIgnoreCase("efea"));
	}

	@Test
	public void getDataLengthTest() {
		byte drl = 1;
		assertTrue("KnxEncoder.getDataLength(0x01) returns " + KnxEncoder.extractPayloadLength(drl)
				+ "; but should return 1", KnxEncoder.extractPayloadLength(drl) == 1);
		drl = (byte) 0xf;
		assertTrue("KnxEncoder.getDataLength((byte) 0xff) returns " + KnxEncoder.extractPayloadLength(drl)
				+ "; but should return 15", KnxEncoder.extractPayloadLength(drl) == 15);

		byte[] message = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 }).getDataLength() returns "
						+ KnxEncoder.decode(message).getDataLength() + "; but should be 1",
				KnxEncoder.decode(message).getDataLength() == 3);
	}

	@Test
	public void priorityTest() {
		byte[] message = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 }).getPriority() is "
						+ KnxEncoder.decode(message).getPriority() + "; but should be AUTO(3)",
				KnxEncoder.decode(message).getPriority() == KnxPacketPriority.AUTO);
	}

	@Test
	public void knxCommandTest() {
		byte[] message = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0, (byte) 0x81 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0, (byte) 0x81}).knxCommand is "
						+ KnxEncoder.decode(message).knxCommand + "; but should be AUTO(3)",
				KnxEncoder.decode(message).knxCommand == KnxCommand.VALUE_WRITE);
	}

	// @Test
	// public void dpt1Test() {
	// byte[] message = new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0, (byte)
	// 0x81};
	// assertTrue(
	// "KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 1, 0,
	// (byte) 0x81}).telegramIsDatapointType1 is "
	// + KnxEncoder.decode(message).telegramIsDatapointType1 + "; but should be
	// true",
	// KnxEncoder.decode(message).telegramIsDatapointType1 == true);
	// byte[] message2 = new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 2, 0, (byte)
	// 0x80, (byte) 0x13};
	// assertFalse(
	// "KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 2, 0,
	// (byte) 0x80, (byte) 0x13}).telegramIsDatapointType1 is "
	// + KnxEncoder.decode(message2).telegramIsDatapointType1 + "; but should be
	// true",
	// KnxEncoder.decode(message2).telegramIsDatapointType1 == true);
	// }

	@Test
	public void dataLengthTest() {
		byte[] message = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 }).telegramDatalengthIsCorrect is "
						+ KnxEncoder.decode(message).telegramDatalengthIsCorrect + "; but should be true",
				KnxEncoder.decode(message).telegramDatalengthIsCorrect == true);
		byte[] message2 = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 4, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 4, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 }) is "
						+ KnxEncoder.decode(message2) + "; but should be NULL",
				KnxEncoder.decode(message2) == null);
		byte[] message3 = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13,
				(byte) 0xAE };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13, (byte) 0xAE }) is "
						+ KnxEncoder.decode(message3) + "; but should be NULL",
				KnxEncoder.decode(message3) == null);
	}

	@Test
	public void dataBytesTest() {
		byte[] message = new byte[] { (byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 };
		byte[] data = new byte[] { 0, (byte) 0x12, (byte) 0x13 };
		assertTrue(
				"KnxEncoder.decode(new byte[] {(byte) 0xbc, 0x0d, 0, 0, 9, 1, 3, 0, (byte) 0x80, (byte) 0x12, (byte) 0x13 }).getDataByte is "
						+ KnxEncoder.convertToReadableHex(KnxEncoder.decode(message).getDataByte())
						+ "; but should be 00:12:13",
				Arrays.equals(data, KnxEncoder.decode(message).getDataByte()));
	}

	@Test
	public void encodeTest() {
		String hexString = "0610053000112900bcd000000901010081";

		assertTrue(
				"KnxEncoder.encode(false, '1/1/1', true, KnxCommand.VALUE_WRITE) returns "
						+ KnxEncoder.convertToReadableHex(KnxEncoder.encode(false, new byte[] { 0, 0 }, "1/1/1",
								new byte[] { 0x1 }, KnxCommand.VALUE_WRITE, true))
						+ "; but should return " + hexString, // KnxEncoder.getHexString(message),
				Arrays.equals(KnxEncoder.encode(false, new byte[] { 0, 0 }, "1/1/1", new byte[] { 0x1 },
						KnxCommand.VALUE_WRITE, true), KnxEncoder.convertToByteArray(hexString)));
	}

	@Test
	public void createAddressTest() {
		assertTrue(
				"KnxEncoder.createAddress('1.2.3') returns " + KnxEncoder.convertDeviceAddressToHex("1.2.3")
						+ "; but should return 1203",
				KnxEncoder.convertDeviceAddressToHex("1.2.3").equalsIgnoreCase("1203"));

		assertTrue(
				"KnxEncoder.createAddress('15.14.234') returns " + KnxEncoder.convertDeviceAddressToHex("15.14.234")
						+ "; but should return feea",
				KnxEncoder.convertDeviceAddressToHex("15.14.234").equalsIgnoreCase("feea"));

	}

	// JUnit 4 annotations:
	// @Test kennzeichnet Methoden als ausf�hrbare Testf�lle.
	// @Before und @After markieren Setup- bzw. Teardown-Aufgaben, die f�r jeden
	// Testfall wiederholt werden sollen.
	// @BeforeClass und @AfterClass markieren Setup- bzw. Teardown-Aufgaben, die
	// nur einmal pro Testklasse ausgef�hrt werden sollen.
	// @Ignore kennzeichnet tempor�r nicht auszuf�hrende Testf�lle.

}

/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
package org.universAAL.lddi.lib.ieeex73std.testchannel20601;

public class ConfirmMeasurePrstAPDUtest {

	byte[] apdu;

	public ConfirmMeasurePrstAPDUtest() {

		apdu = new byte[] { (byte) 0xE7, (byte) 0x00, // APDU CHOICE Type
														// (PrstApdu)
				(byte) 0x00, (byte) 0x12, // CHOICE.length = 18
				(byte) 0x00, (byte) 0x10, // OCTET STRING.length = 16
				(byte) 0x12, (byte) 0x36, // invoke-id = 0x1236 (mirrored from
											// invocation)
				(byte) 0x02, (byte) 0x01, // CHOICE(Remote Operation Response |
											// Confirmed Event Report)
				(byte) 0x00, (byte) 0x0A, // CHOICE.length = 10
				(byte) 0x00, (byte) 0x00, // obj-handle = 0 (MDS object)
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // currentTime
																	// = 0
				(byte) 0x0D, (byte) 0x1D, // event-type =
											// MDC_NOTI_SCAN_REPORT_FIXED
				(byte) 0x00, (byte) 0x00 // event-reply-info.length = 0
		};
	}

	public byte getByte(int i) {
		return apdu[i];
	}

	public byte[] getByteArray() {
		return apdu;
	}

}

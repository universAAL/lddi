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

public class RlreAPDUtest {

	byte[] apdu;

	public RlreAPDUtest() {

		apdu = new byte[] { (byte) 0xE5, (byte) 0x00, // APDU CHOICE Type
														// (RlrqApdu)
				(byte) 0x00, (byte) 0x02, // CHOICE.length = 2
				(byte) 0x00, (byte) 0x00 // reason = normal
		};
	}

	public byte getByte(int i) {
		return apdu[i];
	}

	public byte[] getByteArray() {
		return apdu;
	}

}

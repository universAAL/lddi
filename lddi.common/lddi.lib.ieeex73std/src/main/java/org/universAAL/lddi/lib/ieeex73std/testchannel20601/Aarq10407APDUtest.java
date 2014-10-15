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

public class Aarq10407APDUtest {

	byte[] apdu;
	
	public Aarq10407APDUtest(){
		
		apdu = new byte[]{
				(byte)0xE2, (byte)0x00, 						//choice APDU
				(byte)0x00, (byte)0x32, 						//length
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, // association version
				(byte)0x00, (byte)0x01, (byte)0x00, (byte)0x2A, // data protocol list = 1, length 42
				(byte)0x50, (byte)0x79,  						// data protocol id 20601
				(byte)0x00, (byte)0x26,							// data proto info length
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, // protocol version
				(byte)0xA0, (byte)0x00, 						// encoding rules (MDER)
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,	// nomenclature version
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // functional units
				(byte)0x00, (byte)0x80, (byte)0x00, (byte)0x00, // sysType (agent)
				(byte)0x00, (byte)0x08, 						// sys-id length (8)
				(byte)0x11, (byte)0x22, (byte)0x33, (byte)0x44, // sys-id
				(byte)0x55, (byte)0x66, (byte)0x77, (byte)0x88,
				(byte)0x02, (byte)0xBC, 						 //dev config id (standard 700 -> blood pressure)
				(byte)0x00, (byte)0x01,							 //data request mode flags 
				(byte)0x01, (byte)0x00, 						// data-req-agent-count | data-req-manager-count
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // option list.count | option list length
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

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

public class AareAPDUtest {

	byte[] apdu;
	
	public AareAPDUtest(boolean knownconfig){
		
		/*  */
		
		apdu = new byte[]{
				(byte)0xE3, (byte)0x00, 			//choice APDU
				(byte)0x00, (byte)0x2C, 						//length
				(byte)0x00, (byte)0x00,  						// result accepted-unknown-config <<<<<<<<<<--- SEE THE IF CLAUSE
				(byte)0x50, (byte)0x79,  						// data protocol id 20601
				(byte)0x00, (byte)0x26,							// data proto info length
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, // protocol version
				(byte)0x80, (byte)0x00, 						// encoding rules (MDER)
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00,	// nomenclature version
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // functional units
				(byte)0x80, (byte)0x00, (byte)0x00, (byte)0x00, // sysType (manager)
				(byte)0x00, (byte)0x08, 						// sys-id length (8)
				(byte)0x54, (byte)0x53, (byte)0x42, (byte)0x5f, // sys-id
				(byte)0x4d, (byte)0x61, (byte)0x6e, (byte)0x61,
				(byte)0x00, (byte)0x00, 						 //manager response to config-id (always 0)
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, //manager response to data-req-mode-capab (always 0)
				(byte)0x00, (byte)0x00, 						// option list count
				(byte)0x00, (byte)0x00,     					// option list.length
				};
		
		if (!knownconfig)
		{
			
			apdu[4]=(byte)0x00; apdu[5]=(byte)0x03;
		}
		
			if (knownconfig)
			{
				apdu[4]=(byte)0x00; apdu[5]=(byte)0x00;
			}
				
					
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

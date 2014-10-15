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

public class RealMeasure10415PrstAPDUtest {

	byte[] apdu;
	
	public RealMeasure10415PrstAPDUtest(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						// PRST APDU
				(byte)0x00, (byte)0xBA,							// Length of Prst APDU
				(byte)0x00, (byte)0xB8,							// Length of Data APDU
				(byte)0x10, (byte)0x00,							// invoke ID
				(byte)0x01, (byte)0x01,							// Confirmed event report
				(byte)0x00, (byte)0xB2,							// Length
				(byte)0x00, (byte)0x00,							// MDS 
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,	// event-time
				(byte)0x0D, (byte)0x1E,							// Scan Report var
				(byte)0x00, (byte)0xA8,							// Length
				(byte)0xF0, (byte)0x00,							// Report info fixed
				(byte)0x00, (byte)0x00,							// n of report
				(byte)0x00, (byte)0x05,							// n of measurements
				(byte)0x00, (byte)0xA0,							// total length of report
				
				// Measure 1
				(byte)0x00, (byte)0x01,							// HANDLE = 1
				(byte)0x00, (byte)0x03,							// number of objects = 3
				(byte)0x00, (byte)0x1A,							// Length of measure
				(byte)0x0A, (byte)0x56,							// Object 1: Type: Nu_val_obs_simp
				(byte)0x00, (byte)0x04,							// length
				(byte)0xFE, (byte)0x00,	(byte)0x1C, (byte)0x5C,	// Value (mass)
				(byte)0x09, (byte)0x90,							// Object 2: time_stamp_abs
				(byte)0x00, (byte)0x08,							// Length
				(byte)0x20, (byte)0x12,	(byte)0x05, (byte)0x08, // Time
				(byte)0x15, (byte)0x02,	(byte)0x16, (byte)0x00,
				(byte)0x09, (byte)0x96,							// Object 3: Type: Unit
				(byte)0x00, (byte)0x02,							// Length
				(byte)0x06, (byte)0xC3,							// Kg.
				
				// Measure 2
				(byte)0x00, (byte)0x01,
				(byte)0x00, (byte)0x03,
				(byte)0x00, (byte)0x1A,
				(byte)0x0A, (byte)0x56,
				(byte)0x00, (byte)0x04,
				(byte)0xFE, (byte)0x00,
				(byte)0x1C, (byte)0x66,
				(byte)0x09, (byte)0x90,
				(byte)0x00, (byte)0x08,
				(byte)0x20, (byte)0x12,
				(byte)0x05, (byte)0x08,
				(byte)0x15, (byte)0x04,
				(byte)0x12, (byte)0x00,
				(byte)0x09, (byte)0x96,
				(byte)0x00, (byte)0x02,
				(byte)0x06, (byte)0xC3,
				
				// Measure 3
				(byte)0x00, (byte)0x01,
				(byte)0x00, (byte)0x03,
				(byte)0x00, (byte)0x1A,
				(byte)0x0A, (byte)0x56,
				(byte)0x00, (byte)0x04,
				(byte)0xFE, (byte)0x00,
				(byte)0x1C, (byte)0x5C,
				(byte)0x09, (byte)0x90,
				(byte)0x00, (byte)0x08,
				(byte)0x20, (byte)0x12,
				(byte)0x05, (byte)0x08,
				(byte)0x15, (byte)0x17,
				(byte)0x29, (byte)0x00,
				(byte)0x09, (byte)0x96,
				(byte)0x00, (byte)0x02,
				(byte)0x06, (byte)0xC3,
				
				// Measure 4
				(byte)0x00, (byte)0x01,
				(byte)0x00, (byte)0x03,
				(byte)0x00, (byte)0x1A,
				(byte)0x0A, (byte)0x56,
				(byte)0x00, (byte)0x04,
				(byte)0xFE, (byte)0x00,
				(byte)0x1C, (byte)0x66,
				(byte)0x09, (byte)0x90,
				(byte)0x00, (byte)0x08,
				(byte)0x20, (byte)0x12,
				(byte)0x05, (byte)0x08,
				(byte)0x15, (byte)0x19,
				(byte)0x37, (byte)0x00,
				(byte)0x09, (byte)0x96,
				(byte)0x00, (byte)0x02,
				(byte)0x06, (byte)0xC3,
				
				// Measure 5
				(byte)0x00, (byte)0x01,
				(byte)0x00, (byte)0x03,
				(byte)0x00, (byte)0x1A,
				(byte)0x0A, (byte)0x56,
				(byte)0x00, (byte)0x04,
				(byte)0xFE, (byte)0x00,
				(byte)0x1C, (byte)0x5C,
				(byte)0x09, (byte)0x90,
				(byte)0x00, (byte)0x08,
				(byte)0x20, (byte)0x12,
				(byte)0x05, (byte)0x08,
				(byte)0x15, (byte)0x20,
				(byte)0x55, (byte)0x00,
				(byte)0x09, (byte)0x96,
				(byte)0x00, (byte)0x02,
				(byte)0x06, (byte)0xC3
				};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

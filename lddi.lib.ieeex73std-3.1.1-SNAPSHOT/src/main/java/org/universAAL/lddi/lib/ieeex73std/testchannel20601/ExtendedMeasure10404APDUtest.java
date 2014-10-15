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

public class ExtendedMeasure10404APDUtest {

	byte[] apdu;
	
	public ExtendedMeasure10404APDUtest(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x36, 						//CHOICE.length = 54
				(byte)0x00, (byte)0x34, 						//OCTET STRING.length = 52
				(byte)0x88, (byte)0x88, 						//invoke-id = 0x1236 (sequence number)
				(byte)0x01, (byte)0x01, 						//CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x2E, 						//CHOICE.length = 46
				(byte)0x00, (byte)0x00,  						//obj-handle = 0 (MDS object)
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, //event-time = 0
				(byte)0x0D, (byte)0x1D, 						//event-type = MDC_NOTI_SCAN_REPORT_FIXED
				(byte)0x00, (byte)0x24, 						// event-info.length = 36
				(byte)0xF0, (byte)0x00, 						// ScanReportInfoFixed.data-req-id = 0xF000
				(byte)0x00, (byte)0x00, 						// ScanReportInfoFixed.scan-report-no = 0
				(byte)0x00, (byte)0x02, 						// ScanReportInfoFixed.obs-scan-fixed.count = 2
				(byte)0x00, (byte)0x1C, 						// ScanReportInfoFixed.obs-scan-fixed.length = 28
				
				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
				(byte)0x00, (byte)0x0A, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 10
				(byte)0x00, (byte)0x62, 						//basic-Nu-Observed-Value = 98%
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,	
				
				(byte)0x00, (byte)0x0A, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 10
				(byte)0x00, (byte)0x0A, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 10
				(byte)0x00, (byte)0x48, 						//basic-Nu-Observed-Value = 72 bpm
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00	
				
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

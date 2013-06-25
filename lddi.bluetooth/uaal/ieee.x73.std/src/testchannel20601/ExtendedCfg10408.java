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
package testchannel20601;

public class ExtendedCfg10408 {
	
	byte[] apdu;
	
	public ExtendedCfg10408(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x44, 						//CHOICE.length = 68
				(byte)0x00, (byte)0x42, 						// OCTET STRING.length = 66
				(byte)0x99, (byte)0x99, 						// invoke-id = 0x1235 (start of DataApdu. MDER encoded.)
				(byte)0x01, (byte)0x01,  						// CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x3C,							// CHOICE.length = 60
				(byte)0x00, (byte)0x00, 						// obj-handle = 0 (MDS object)
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, // event-time = 0xFFFFFFFF
				
				
				(byte)0x0D, (byte)0x1C, 						// event-type = MDC_NOTI_CONFIG
				(byte)0x00, (byte)0x32,  						// event-info.length = 50 (start of ConfigReport)
				(byte)0x40, (byte)0x00, 						// config-report-id
				(byte)0x00, (byte)0x01, 						// config-obj-list.count = 1 Measurement objects will be announced
				(byte)0x00, (byte)0x2C,							// config-obj-list.length =44
				(byte)0x00, (byte)0x06, 						//obj-class = MDC_MOC_VMO_METRIC_NU
				
				(byte)0x00, (byte)0x01,							//obj-handle = 1 (1st Measurement is body weight)
				(byte)0x00, (byte)0x04, 						// attributes.count = 4
				(byte)0x00, (byte)0x24,							// attributes.length = 36
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0x4B, (byte)0x5C, //MDC_PART_SCADA, MDC_TEMP_BODY
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0xF0, (byte)0x40,							//intermittent, stored data, upd & msmt aperiodic, agent init, measured
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x17, (byte)0xA0,							//MDC_DIM_DEGC
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C,							// attribute-value.length = 12
				(byte)0x00, (byte)0x02,							//AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x56,	(byte)0x00, (byte)0x04,	// MDC_ATTR_NU_VAL_OBS_SIMP | value length = 4
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08	// MDC_ATTR_TIME_STAMP_ABS | value length = 8
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}

}

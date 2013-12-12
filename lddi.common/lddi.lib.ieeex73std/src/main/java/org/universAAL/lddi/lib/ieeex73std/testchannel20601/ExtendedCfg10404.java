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

public class ExtendedCfg10404 {
	
	byte[] apdu;
	
	public ExtendedCfg10404(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0xA8, 						//CHOICE.length = 168
				(byte)0x00, (byte)0xA6, 						// OCTET STRING.length = 166
				(byte)0x99, (byte)0x99, 						// invoke-id = 0x1235 (start of DataApdu. MDER encoded.)
				(byte)0x01, (byte)0x01,  						// CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0xA0,							// CHOICE.length = 160
				(byte)0x00, (byte)0x00, 						// obj-handle = 0 (MDS object)
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, // event-time = 0xFFFFFFFF
				
				(byte)0x0D, (byte)0x1C, 						// event-type = MDC_NOTI_CONFIG
				(byte)0x00, (byte)0x96,  						// event-info.length = 150 (start of ConfigReport)
				(byte)0x40, (byte)0x00, 						// config-report-id
				(byte)0x00, (byte)0x03, 						// config-obj-list.count = 3 Measurement objects will be announced
				(byte)0x00, (byte)0x90,							// config-obj-list.length = 144
		//144
			//44
				//8
				(byte)0x00, (byte)0x06, 						//obj-class = MDC_MOC_VMO_METRIC_NU
				(byte)0x00, (byte)0x01,							//obj-handle = 1 (1st Measurement is SpO2)
				(byte)0x00, (byte)0x04, 						// attributes.count = 4
				(byte)0x00, (byte)0x24,							// attributes.length = 36
			
				//8
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0x4B, (byte)0xB8, //MDC_PART_SCADA | MDC_PULS_OXIM_SAT_O2
				
				//6
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x40, (byte)0xC0,							//avail-stored-data, acc-manager-init, acc-agent-init, measured
				
				//6
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x02, (byte)0x20,							//MDC_DIM_PERCENT
				
				//16
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C,							// attribute-value.length = 12
				(byte)0x00, (byte)0x02,							//AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x4C,	(byte)0x00, (byte)0x02,	// MDC_ATTR_NU_VAL_OBS_BASIC | value length = 2
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08,	// MDC_ATTR_TIME_STAMP_ABS | value length = 8
		//100
			//44
				//8
				(byte)0x00, (byte)0x06,							//obj-class = MDC_MOC_VMO_METRIC_NU
				(byte)0x00, (byte)0x0A,							//obj-handle = 10 (2nd Measurement is pulse rate)
				(byte)0x00, (byte)0x04, 						// attributes.count = 4
				(byte)0x00, (byte)0x24,							// attributes.length = 36
				
				//8
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0x48, (byte)0x1A, //MDC_PART_SCADA | MDC_PULS_OXIM_PULS_RATE
				
				//6
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x40, (byte)0xC0,							//avail-stored-data, acc-manager-init, acc-agent-init, measured
				
				//6
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x0A, (byte)0xA0,							//MDC_DIM_BEAT_PER_MIN
				
				//16
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C,							// attribute-value.length = 12
				(byte)0x00, (byte)0x02,							//AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x4C,	(byte)0x00, (byte)0x02,	// MDC_ATTR_NU_VAL_OBS_BASIC | value length = 2
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08,	// MDC_ATTR_TIME_STAMP_ABS | value length = 8
				
			//56
				(byte)0x00, (byte)0x06,							//obj-class = MDC_MOC_VMO_METRIC_NU
				
				//6
				(byte)0x00, (byte)0x03,							//obj-handle = 3 (3rd Measurement is SpO2  fast response)
				(byte)0x00, (byte)0x05, 						// attributes.count = 5
				(byte)0x00, (byte)0x30,							// attributes.length = 48
				
				//8
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0x4B, (byte)0xB8, //MDC_PART_SCADA | MDC_PULS_OXIM_SAT_O2
				
				//6
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x40, (byte)0xC0,							//avail-stored-data, acc-manager-init, acc-agent-init, measured

				//6
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x02, (byte)0x20,							//MDC_DIM_PERCENT
				
				//12
				(byte)0x0A, (byte)0x61, 						// attribute-id = MDC_ATTR_SUPPLEMENTAL_TYPES
				(byte)0x00, (byte)0x08, 						// attribute-value.length = 8
				(byte)0x00, (byte)0x01, 						// SupplementalTypeList.count = 1
				(byte)0x00, (byte)0x04, 						// SupplementalTypeList.length = 4
				(byte)0x00, (byte)0x02,	(byte)0x4C, (byte)0x34,	// MDC_PART_SCADA | MDC_MODALITY_FAST
				
				
				//16
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C, 						// attribute-value.length = 12
				(byte)0x00, (byte)0x02, 						// AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x4C,	(byte)0x00, (byte)0x02,	// MDC_ATTR_NU_VAL_OBS_BASIC, 2
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08,	// MDC_ATTR_TIME_STAMP_ABS, 8
				
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}

}

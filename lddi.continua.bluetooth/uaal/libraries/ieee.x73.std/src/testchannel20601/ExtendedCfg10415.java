package testchannel20601;

public class ExtendedCfg10415 {
	
	byte[] apdu;
	
	public ExtendedCfg10415(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0xA2, 						//CHOICE.length = 162
				(byte)0x00, (byte)0xA0, 						// OCTET STRING.length = 160
				(byte)0x12, (byte)0x35, 						// invoke-id = 0x1235 (start of DataApdu. MDER encoded.)
				(byte)0x01, (byte)0x01,  						// CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x9A,							// CHOICE.length = 154
				(byte)0x00, (byte)0x00, 						// obj-handle = 0 (MDS object)
				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, // event-time = 0xFFFFFFFF
				
				
				(byte)0x0D, (byte)0x1C, 						// event-type = MDC_NOTI_CONFIG
				(byte)0x00, (byte)0x90,  						// event-info.length = 144 (start of ConfigReport)
				(byte)0x40, (byte)0x00, 						// config-report-id
				(byte)0x00, (byte)0x03, 						// config-obj-list.count = 3 Measurement objects will be announced
				(byte)0x00, (byte)0x8A,							// config-obj-list.length = 138
				(byte)0x00, (byte)0x06, 						//obj-class = MDC_MOC_VMO_METRIC_NU
				
				(byte)0x00, (byte)0x01,							//obj-handle = 1 (1st Measurement is body weight)
				(byte)0x00, (byte)0x04, 						// attributes.count = 4
				(byte)0x00, (byte)0x24,							// attributes.length = 36
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0xE1, (byte)0x40, //MDC_PART_SCADA | MDC_MASS_BODY_ACTUAL
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0xF0, (byte)0x40,							//intermittent, stored data, upd & msmt aperiodic, agent init, measured
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x06, (byte)0xC3,							//MDC_DIM_KILO_G
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C,							// attribute-value.length = 12
				(byte)0x00, (byte)0x02,							//AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x56,	(byte)0x00, (byte)0x04,	// MDC_ATTR_NU_VAL_OBS_SIMP | value length = 4
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08,	// MDC_ATTR_TIME_STAMP_ABS | value length = 8
				(byte)0x00, (byte)0x06,							//obj-class = MDC_MOC_VMO_METRIC_NU

				(byte)0x00, (byte)0x02,							//obj-handle = 2 (2nd Measurement is body height)
				(byte)0x00, (byte)0x04, 						// attributes.count = 4
				(byte)0x00, (byte)0x24,							// attributes.length = 36
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0xE1, (byte)0x44, //MDC_PART_SCADA | MDC_LEN_BODY_ACTUAL
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				
				(byte)0xF0, (byte)0x48,							//intermittent, stored data, upd & msmt aperiodic, agent init, manual
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x05, (byte)0x11,							//MDC_DIM_CENTI_M
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C,							// attribute-value.length = 12
				(byte)0x00, (byte)0x02,							//AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x56,	(byte)0x00, (byte)0x04,	// MDC_ATTR_NU_VAL_OBS_SIMP | value length = 4
				(byte)0x09, (byte)0x90,	(byte)0x00, (byte)0x08,	// MDC_ATTR_TIME_STAMP_ABS | value length = 8
				(byte)0x00, (byte)0x06,							//obj-class = MDC_MOC_VMO_METRIC_NU
				
				
				(byte)0x00, (byte)0x03,							//obj-handle = 3 3rd Measurement is body mass index)
				(byte)0x00, (byte)0x05, 						// attributes.count = 5
				(byte)0x00, (byte)0x2A,							// attributes.length = 42
				(byte)0x09, (byte)0x2F,							//attribute-id = MDC_ATTR_ID_TYPE
				(byte)0x00, (byte)0x04, 						// attribute-value.length = 4
				(byte)0x00, (byte)0x02,	(byte)0xE1, (byte)0x50, //MDC_PART_SCADA | MDC_RATIO_MASS_BODY_LEN_SQ
				(byte)0x0A, (byte)0x46, 						// attribute-id = MDC_ATTR_METRIC_SPEC_SMALL
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0xF0, (byte)0x42,							//intermittent, stored data, upd & msmt aperiodic, agent init, calculated
				(byte)0x09, (byte)0x96, 						// attribute-id = MDC_ATTR_UNIT_CODE
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x07, (byte)0xA0,							//MDC_DIM_KG_PER_M_SQ
				(byte)0x0A, (byte)0x47, 						// attribute-id = MDC_ATTR_SOURCE_HANDLE_REF
				(byte)0x00, (byte)0x02,							// attribute-value.length = 2
				(byte)0x00, (byte)0x01,							//reference handle = 1
				(byte)0x0A, (byte)0x55, 						// attribute-id = MDC_ATTR_ATTRIBUTE_VAL_MAP
				(byte)0x00, (byte)0x0C, 						// attribute-value.length = 12
				(byte)0x00, (byte)0x02, 						// AttrValMap.count = 2
				(byte)0x00, (byte)0x08, 						// AttrValMap.length = 8
				(byte)0x0A, (byte)0x56,	(byte)0x00, (byte)0x04,	// MDC_ATTR_NU_VAL_OBS_SIMP, 4
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

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
package x73.nomenclature;

public class StatusCodes {

/*
 * From IEEE 11073-20601 Annex G
 */

	//Nomenclature partitions
	public static final int NOM_PART_UNSPEC									= 0;
	public static final int NOM_PART_OBJ						 			= 1;
	public static final int NOM_PART_METRIC						 			= 2;
	public static final int NOM_PART_ALERT						 			= 3;
	public static final int NOM_PART_DIM						 			= 4;
	public static final int NOM_PART_VATTR						 			= 5;
	public static final int NOM_PART_PGRP						 			= 6;
	public static final int NOM_PART_SITES						 			= 7;
	public static final int NOM_PART_INFRASTRUCT						 	= 8;
	public static final int NOM_PART_FEF						 			= 9;
	public static final int NOM_PART_ECG_EXTN								= 10;
	public static final int NOM_PART_PHD_DM						 			= 128;
	public static final int NOM_PART_PHD_HF									= 129;
	public static final int NOM_PART_PHD_AI						 			= 130;
	public static final int NOM_PART_RET_CODE						 		= 255;
	public static final int NOM_PART_EXT_NOM						 		= 256;
	public static final int NOM_PART_PRIV									= 1024;

	//Operational state
	public static final int OS_DISABLED						 				= 0;
	public static final int OS_ENABLED						 				= 1;
	public static final int OS_NOT_AVAILABLE						 		= 2;

	//Product Specification entry
	public static final int UNSPECIFIED						 				= 0;
	public static final int SERIAL_NUMBER						 			= 1;
	public static final int PART_NUMBER						 				= 2;
	public static final int HW_REVISION						 				= 3;
	public static final int SW_REVISION						 				= 4;
	public static final int FW_REVISION						 				= 5;
	public static final int PROTOCOL_REVISION						 		= 6;
	public static final int PROD_SPEC_GMDN						 			= 7;
    
    // Power status
	public static final int ON_MAINS						 				= 0x8000;
	public static final int ON_BATTERY						 				= 0x4000;
	public static final int CHARGING_FULL						 			= 0x0080;
	public static final int CHARGING_TRICKLE						 		= 0x0040;
	public static final int CHARGING_OFF						 			= 0x0020;
	
	// Measurement status
	public static final int MS_INVALID						 				= 0x8000;
	public static final int MS_QUESTIONABLE						 			= 0x4000;
	public static final int MS_NOT_AVAILABLE								= 0x2000;
	public static final int MS_CALIBRATION_ONGOING						 	= 0x1000;
	public static final int MS_TEST_DATA						 			= 0x0800;
	public static final int MS_DEMO_DATA						 			= 0x0400;
	public static final int MS_VALIDATED_DATA						 		= 0x0080;
	public static final int MS_EARLY_INDICATION						 		= 0x0040;
	public static final int MS_MSMT_ONGOING						 			= 0x0020;
	
	// Sample type
	public static final int SAMPLE_TYPE_SIGNIFICANT_BITS_SIGNED_SAMPLES		= 255;
	
	// SaFlags
	public static final int SMOOTH_CURVE						 			= 0x8000;
	public static final int DELAYED_CURVE						 			= 0x4000;
	public static final int STATIC_SCALE						 			= 0x2000;
	public static final int SA_EXT_VAL_RANGE						 		= 0x1000;
	
	// Enum Value
	public static final int OBJ_ID_CHOSEN						 			= 0x0001;
	public static final int TEXT_STRING_CHOSEN						 		= 0x0002;
	public static final int BIT_STR_CHOSEN						 			= 0x0010;
	
	// Confirm mode
	public static final int UNCONFIRMED						 				= 0x0000;
	public static final int CONFIRMED						 				= 0x0001;
	
	// Store Sample Algorithm
	public static final int ST_ALG_NOS										= 0x0000;
	public static final int ST_ALG_MOVING_AVERAGE						 	= 0x0001;
	public static final int ST_ALG_RECURSIVE_						 		= 0x0002;
	public static final int ST_ALG_MIN_PICK						 			= 0x0003;
	public static final int ST_ALG_MAX_PICK						 			= 0x0004;
	public static final int ST_ALG_MEDIAN						 			= 0x0005;
	public static final int ST_ALG_TRENDED						 			= 0x0200;
	public static final int ST_ALG_NO_DOWNSAMPLING						 	= 0x0400;
	
	//Segment selection
	public static final int ALL_SEGMENTS_CHOSEN						 		= 0x0001;
	public static final int SEGM_ID_LIST_CHOSEN						 		= 0x0002;
	public static final int ABS_TIME_RANGE_CHOSEN						 	= 0x0003;
	
	//PMStore capabilities
	public static final int PMSC_VAR_NO_OF_SEGM						 		= 0x8000;
	public static final int PMSC_EPI_SEG_ENTRIES						 	= 0x0800;
	public static final int PMSC_PERI_SEG_ENTRIES						 	= 0x0400;
	public static final int PMSC_ABS_TIME_SELECT						 	= 0x0200;
    public static final int PMSC_CLEAR_SEGM_BY_LIST_SUP						= 0x0100;
	public static final int PMSC_CLEAR_SEGM_BY_TIME_SUP						= 0x0080;
	public static final int PMSC_CLEAR_SEGM_REMOVE						 	= 0x0040;
	public static final int PMSC_MULTI_PERSON						 		= 0x0008;
	
	// Segmnent entry Header
	public static final int SEG_ELEM_HDR_ABSOLUTE_TIME						= 0x8000;
	public static final int SEG_ELEM_HDR_RELATIVE_TIME						= 0x4000;
	public static final int SEG_ELEM_HDR_HIRES_RELATIVE_TIME				= 0x2000;
	
	// Trigger Segment data transfer response
	public static final int TSXR_SUCCESSFUL						 			= 0;
	public static final int TSXR_FAIL_NO_SUCH_SEGMENT						= 1;
	public static final int TSXR_FAIL_SEGM_TRY_LATER						= 2;
	public static final int TSXR_FAIL_SEGM_EMPTY						 	= 3;
	public static final int TSXR_FAIL_OTHER						 			= 512;
	
	// Segment event status
	public static final int SEVTSTA_FIRST_ENTRY						 		= 0x8000;
	public static final int SEVTSTA_LAST_ENTRY						 		= 0x4000;
	public static final int SEVTSTA_AGENT_ABORT						 		= 0x0800;
	public static final int SEVTSTA_MANAGER_CONFIRM						 	= 0x0080;
	public static final int SEVTSTA_MANAGER_ABORT						 	= 0x0008;
	
	// Segment Stat Type
	public static final int SEGM_STAT_TYPE_MINIMUM						 	= 1;
	public static final int SEGM_STAT_TYPE_MAXIMUM						 	= 2;
	public static final int SEGM_STAT_TYPE_AVERAGE						 	= 3;
	
	// Association Version
	public static final int ASSOC_VERSION1						 			= 0x80000000;
	
	
	// Protocol version
	public static final int PROTOCOL_VERSION1						 		= 0x80000000;
	
	// Encoding rules
	public static final int MDER						 					= 0x8000;
	public static final int XER						 						= 0x4000;
	public static final int PER						 						= 0x2000;
	
	// Data protocol ID
	public static final int DATA_PROTO_ID_20601						 		= 20601;
	public static final int DATA_PROTO_ID_EXTERNAL						 	= 65535;
	
	// Associate result
	public static final int ACCEPTED 										= 0;
	public static final int REJECTED_PERMANENT 								= 1;
	public static final int REJECTED_TRANSIENT								= 2;
	public static final int ACCEPTED_UNKNOWN_CONFIG 						= 3;
	public static final int REJECTED_NO_COMMON_PROTOCOL 					= 4;
	public static final int REJECTED_NO_COMMON_PARAMETER 					= 5;
	public static final int REJECTED_UNKNOWN 								= 6;
	public static final int REJECTED_UNAUTHORIZED 							= 7;
	public static final int REJECTED_UNSUPPORTED_ASSOC_VERSION 				= 8;
	
	//Release request 
	public static final int REL_REQ_RE_NORMAL								= 0;
			
	// Release response
	public static final int RELEASE_RESPONSE_REASON_NORMAL					= 0;
	
	// Abort reason
	public static final int ABORT_REASON_UNDEFINED						 	= 0;
	public static final int ABORT_REASON_BUFFER_OVERFLOW					= 1;
	public static final int ABORT_REASON_RESPONSE_TIMEOUT					= 2;
	public static final int ABORT_REASON_CONFIGURATION_TIMEOUT				= 3;
	
	//Modify Operator
	public static final int REPLACE						 					= 0;
	public static final int ADD_VALUES						 				= 1;
	public static final int REMOVE_VALUES						 			= 2;
	public static final int SET_TO_DEFAULT						 			= 3;
	
	// Error
	public static final int NO_SUCH_OBJECT_INSTANCE                         = 1;
	public static final int ACCESS_DENIED						 			= 2;
	public static final int NO_SUCH_ACTION						 			= 9;
	public static final int INVALID_OBJECT_INSTANCE                         = 17;
	public static final int PROTOCOL_VIOLATION						 		= 23;
	public static final int NOT_ALLOWED_BY_OBJECT						 	= 24;
	public static final int ACTION_TIMED_OUT						 		= 25;
	public static final int ACTION_ABORTED						 			= 26;
	
	// Rorj problem
	public static final int UNRECOGNIZED_APDU						 		= 0;
	public static final int BADLY_STRUCTURED_APDU						 	= 2;
	public static final int UNRECOGNIZED_OPERATION						 	= 101;
	public static final int RESOURCE_LIMITATION							 	= 103;
	public static final int UNEXPECTED_ERROR						 		= 303;
	
	// Data APDU
	public static final int ROIV_CMIP_EVENT_REPORT_CHOSEN				 	= 0x0100;
	public static final int ROIV_CMIP_CONFIRMED_EVENT_REPORT_CHOSEN			= 0x0101;
	public static final int ROIV_CMIP_GET_CHOSEN						 	= 0x0103;
	public static final int ROIV_CMIP_SET_CHOSEN						 	= 0x0104;
	public static final int ROIV_CMIP_CONFIRMED_SET_CHOSEN					= 0x0105;
	public static final int ROIV_CMIP_ACTION_CHOSEN						 	= 0x0106;
	public static final int ROIV_CMIP_CONFIRMED_ACTION_CHOSEN				= 0x0107;
	public static final int RORS_CMIP_CONFIRMED_EVENT_REPORT_CHOSEN			= 0x0201;
	public static final int RORS_CMIP_GET_CHOSEN						 	= 0x0203;
	public static final int RORS_CMIP_CONFIRMED_SET_CHOSEN					= 0x0205;
	public static final int RORS_CMIP_CONFIRMED_ACTION_CHOSEN				= 0x0207;
	public static final int ROER_CHOSEN						 				= 0x0300;
	public static final int RORJ_CHOSEN						 				= 0x0400;
	
	// APDU
	public static final int AARQ_CHOSEN						 				= 0xE200;
	public static final int AARE_CHOSEN						 				= 0xE300;
	public static final int RLRQ_CHOSEN						 				= 0xE400;
	public static final int RLRE_CHOSEN						 				= 0xE500;
	public static final int ABRT_CHOSEN						 				= 0xE600;
	public static final int PRST_CHOSEN						 				= 0xE700;
	
	//Nomenclature version
	public static final int NOM_VERSION1						 			= 0x80000000;
	
	//	Functional units
	public static final int FUN_UNITS_UNIDIRECTIONAL						= 0x80000000;
	public static final int FUN_UNITS_HAVETESTCAP						 	= 0x40000000;
	public static final int FUN_UNITS_CREATETESTASSOC						= 0x20000000;
	
	// System type
	public static final int SYS_TYPE_MANAGER						 		= 0x80000000;
	public static final int SYS_TYPE_AGENT						 			= 0x00800000;
	
	// Config ID
	public static final int MANAGER_CONFIG_RESPONSE						 	= 0x0000;
	public static final int STANDARD_CONFIG_START						 	= 0x0001;
	public static final int STANDARD_CONFIG_END						 		= 0x3FFF;
	public static final int EXTENDED_CONFIG_START						 	= 0x4000;
	public static final int EXTENDED_CONFIG_END						 		= 0x7FFF;
	public static final int RESERVED_START						 			= 0x8000;
	public static final int RESERVED_END						 			= 0xFFFF;
	
	// Data req Mode flags
	public static final int DATA_REQ_SUPP_STOP						 		= 0x8000;
	public static final int DATA_REQ_SUPP_SCOPE_ALL						 	= 0x0800;
	public static final int DATA_REQ_SUPP_SCOPE_CLASS						= 0x0400;
	public static final int DATA_REQ_SUPP_SCOPE_HANDLE						= 0x0200;
	public static final int DATA_REQ_SUPP_MODE_SINGLE_RSP					= 0x0080;
	public static final int DATA_REQ_SUPP_MODE_TIME_PERIOD					= 0x0040;
	public static final int DATA_REQ_SUPP_MODE_TIME_NO_LIMIT				= 0x0020;
	public static final int DATA_REQ_SUPP_PERSON_ID						 	= 0x0010;
	public static final int DATA_REQ_SUPP_INIT_AGENT						= 0x0001;
	
	//Mds Time Capabilities state
	public static final int MDS_TIME_CAPAB_REAL_TIME_CLOCK					= 0x8000;
	public static final int MDS_TIME_CAPAB_SET_CLOCK						= 0x4000;
	public static final int MDS_TIME_CAPAB_RELATIVE_TIME					= 0x2000;
	public static final int MDS_TIME_CAPAB_HIGH_RES_RELATIVE_TIME			= 0x1000;
	public static final int MDS_TIME_CAPAB_SYNC_ABS_TIME					= 0x0800;
	public static final int MDS_TIME_CAPAB_SYNC_REL_TIME					= 0x0400;
	public static final int MDS_TIME_CAPAB_SYNC_HI_RES_RELATIVE_TIME		= 0x0200;
	public static final int MDS_TIME_STATE_ABS_TIME_SYNCED					= 0x0080;
	public static final int MDS_TIME_STATE_REL_TIME_SYNCED					= 0x0040;
	public static final int MDS_TIME_STATE_HI_RES_RELATIVE_TIME_SYNCED		= 0x0020;
	public static final int MDS_TIME_MGR_SET_TIME						 	= 0x0010;
	
	// Person ID
	public static final int UNKNOWN_PERSON_ID						 		= 0xFFFF;
	
	// Metric Spec Small
	public static final int MSS_AVAIL_INTERMITTENT						 	= 0x8000;
	public static final int MSS_AVAIL_STORED_DATA						 	= 0x4000;
	public static final int MSS_UPD_APERIODIC						 		= 0x2000;
	public static final int MSS_MSMT_APERIODIC						 		= 0x1000;
	public static final int MSS_MSMT_PHYS_EV_ID						 		= 0x0800;
	public static final int MSS_MSMT_BTB_METRIC						 		= 0x0400;
	public static final int MSS_ACC_MANAGER_INITIATED						= 0x0080;
	public static final int MSS_ACC_AGENT_INITIATED						 	= 0x0040;
	public static final int MSS_CAT_MANUAL						 			= 0x0008;
	public static final int MSS_CAT_SETTING						 			= 0x0004;
	public static final int MSS_CAT_CALCULATION						 		= 0x0002;
	
	// Metric structure small
	public static final int MS_STRUCT_SIMPLE						 		= 0;
	public static final int MS_STRUCT_COMPOUND						 		= 1;
	public static final int MS_STRUCT_RESERVED						 		= 2;
	public static final int MS_STRUCT_COMPOUND_FIX						 	= 3;
	
	// Data request ID
	public static final int DATA_REQ_ID_MANAGER_INITIATED_MIN				= 0x0000;
	public static final int DATA_REQ_ID_MANAGER_INITIATED_MAX				= 0xEFFF;
	public static final int DATA_REQ_ID_AGENT_INITIATED						= 0xF000;
	
	// Config result
	public static final int ACCEPTED_CONFIG						 			= 0x0000;
	public static final int UNSUPPORTED_CONFIG						 		= 0x0001;
	public static final int STANDARD_CONFIG_UNKNOWN						 	= 0x0002;
	
	// Data request Mode
	public static final int DATA_REQ_START_STOP						 		= 0x8000;
	public static final int DATA_REQ_CONTINUATION						 	= 0x4000;
	public static final int DATA_REQ_SCOPE_ALL						 		= 0x0800;
	public static final int DATA_REQ_SCOPE_TYPE						 		= 0x0400;
	public static final int DATA_REQ_SCOPE_HANDLE						 	= 0x0200;
	public static final int DATA_REQ_MODE_SINGLE_RSP						= 0x0080;
	public static final int DATA_REQ_MODE_TIME_PERIOD						= 0x0040;
	public static final int DATA_REQ_MODE_TIME_NO_LIMIT						= 0x0020;
	public static final int DATA_REQ_MODE_DATA_REQ_PERSON_ID				= 0x0008;
	
	// Data request result
	public static final int DATA_REQ_RESULT_NO_ERROR						= 0;
	public static final int DATA_REQ_RESULT_UNSPECIFIC_ERROR				= 1;
	public static final int DATA_REQ_RESULT_NO_STOP_SUPPORT					= 2;
	public static final int DATA_REQ_RESULT_NO_SCOPE_ALL_SUPPORT			= 3;
	public static final int DATA_REQ_RESULT_NO_SCOPE_CLASS_SUPPORT			= 4;
	public static final int DATA_REQ_RESULT_NO_SCOPE_HANDLE_SUPPORT			= 5;
	public static final int DATA_REQ_RESULT_NO_MODE_SINGLE_RSP_SUPPORT		= 6;
	public static final int DATA_REQ_RESULT_NO_MODE_TIME_PERIOD_SUPPORT		= 7;
	public static final int DATA_REQ_RESULT_NO_MODE_TIME_NO_LIMIT_SUPPORT	= 8;
	public static final int DATA_REQ_RESULT_NO_PERSON_ID_SUPPORT			= 9;
	public static final int DATA_REQ_RESULT_UNKNOWN_PERSON_ID				= 11;
	public static final int DATA_REQ_RESULT_UNKNOWN_CLASS					= 12;
	public static final int DATA_REQ_RESULT_UNKNOWN_HANDLE					= 13;
	public static final int DATA_REQ_RESULT_UNSUPP_SCOPE					= 14;
	public static final int DATA_REQ_RESULT_UNSUPP_MODE						= 15;
	public static final int DATA_REQ_RESULT_INIT_MANAGER_OVERFLOW			= 16;
	public static final int DATA_REQ_RESULT_CONTINUATION_NOT_SUPPORTED		= 17;
	public static final int DATA_REQ_RESULT_INVALID_REQ_ID					= 18;

	
}

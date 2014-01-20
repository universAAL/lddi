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
package org.universAAL.lddi.lib.ieeex73std.utils;



import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.ASN1OctetString;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AbsoluteTime;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AbsoluteTimeAdjust;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AttrValMap;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BITS_16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BITS_32;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BasicNuObsValue;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BasicNuObsValueCmp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BatMeasure;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigId;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfirmMode;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.EnumObsValue;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.EnumPrintableString;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.FLOAT_Type;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HANDLE;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HANDLEList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HandleAttrValMap;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HighResRelativeTime;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U32;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.InstNumber;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MdsTimeInfo;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MeasurementStatus;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricIdList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricSpecSmall;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricStructureSmall;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.NomPartition;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.NuObsValue;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.NuObsValueCmp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.OID_Type;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.OperationalState;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PersonId;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PmSegmentEntryMap;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PmStoreCapab;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PowerStatus;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ProductionSpec;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.RegCertDataList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.RelativeTime;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SaSpec;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScaleRangeSpec16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScaleRangeSpec32;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScaleRangeSpec8;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SegmentStatistics;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SimpleNuObsValue;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SimpleNuObsValueCmp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.StoSampleAlg;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SupplementalTypeList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.SystemModel;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.TYPE;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.TypeVerList;



public class ASNUtils {
//
//	
//	public static final int INT_U8	=1;
//	public static final int INT_U16	=2;
//	public static final int INT_U32	=3;
//	public static final int INT_I8	=4;
//	public static final int INT_I16	=5;
//	public static final int INT_I32	=6;
//	
//	public static final int INT_U8_size		=1;
//	public static final int INT_U16_size	=2;
//	public static final int INT_U32_size	=4;
//	public static final int INT_I8_size		=1;
//	public static final int INT_I16_size	=2;
//	public static final int INT_I32_size	=4;
//	
//	public static final int BITSTRING_8		=1;
//	public static final int BITSTRING_16	=2;
//	public static final int BITSTRING_32	=4;
//	
	
	public static Class getAttributeClass(int id) {
		switch(id){
		
			//MDS
			case NomenclatureCodes.MDC_ATTR_ID_HANDLE: 						return HANDLE.class;
			case NomenclatureCodes.MDC_ATTR_SYS_TYPE: 						return TYPE.class;
		    case NomenclatureCodes.MDC_ATTR_ID_MODEL: 						return SystemModel.class;
			case NomenclatureCodes.MDC_ATTR_SYS_ID:							return ASN1OctetString.class;	
			case NomenclatureCodes.MDC_ATTR_DEV_CONFIG_ID:					return ConfigId.class;
			case NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP:       		return AttrValMap.class;	                               
		    case NomenclatureCodes.MDC_ATTR_ID_PROD_SPECN:					return ProductionSpec.class;	                               
			case NomenclatureCodes.MDC_ATTR_MDS_TIME_INFO:					return MdsTimeInfo.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_ABS:						return AbsoluteTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_REL:						return RelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_REL_HI_RES:				return HighResRelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_ABS_ADJUST:        		return AbsoluteTimeAdjust.class;	                               
		    case NomenclatureCodes.MDC_ATTR_POWER_STAT:						return PowerStatus.class;	                               
			case NomenclatureCodes.MDC_ATTR_VAL_BATT_CHARGE:				return INT_U16.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_BATT_REMAIN:				return BatMeasure.class;	                               
			case NomenclatureCodes.MDC_ATTR_REG_CERT_DATA_LIST:				return RegCertDataList.class;	                               
			case NomenclatureCodes.MDC_ATTR_SYS_TYPE_SPEC_LIST:      		return TypeVerList.class;                               
			case NomenclatureCodes.MDC_ATTR_CONFIRM_TIMEOUT: 				return RelativeTime.class;	                               
	
		    //Metric (previously used are commented)
	//		case NomenclatureCodes.MDC_ATTR_ID_HANDLE: 						return HANDLE.class;
		    case NomenclatureCodes.MDC_ATTR_ID_TYPE:						return TYPE.class;	                               
			case NomenclatureCodes.MDC_ATTR_SUPPLEMENTAL_TYPES:      		return SupplementalTypeList.class;	                               
			case NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL:				return MetricSpecSmall.class;	                               
			case NomenclatureCodes.MDC_ATTR_METRIC_STRUCT_SMALL:			return MetricStructureSmall.class;	                               
		    case NomenclatureCodes.MDC_ATTR_MSMT_STAT:						return MeasurementStatus.class;	                               
		    case NomenclatureCodes.MDC_ATTR_ID_PHYSIO: 						return OID_Type.class;	                               
			case NomenclatureCodes.MDC_ATTR_ID_PHYSIO_LIST:					return MetricIdList.class;	                               
			case NomenclatureCodes.MDC_ATTR_METRIC_ID_PART:					return NomPartition.class;	                               
			case NomenclatureCodes.MDC_ATTR_UNIT_CODE:						return OID_Type.class;	                               
	//		case NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP:       		return AttrValMap.class;	                               
			case NomenclatureCodes.MDC_ATTR_SOURCE_HANDLE_REF:				return HANDLE.class;	                               
		    case NomenclatureCodes.MDC_ATTR_ID_LABEL_STRING:				return ASN1OctetString.class;	                               
			case NomenclatureCodes.MDC_ATTR_UNIT_LABEL_STRING:				return ASN1OctetString.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS:					return AbsoluteTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_STAMP_REL:					return RelativeTime.class;	
			case NomenclatureCodes.MDC_ATTR_TIME_STAMP_REL_HI_RES:			return HighResRelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_PD_MSMT_ACTIVE:     		return FLOAT_Type.class;	                               
	
		    //Numeric (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_SIMP:         		return SimpleNuObsValue.class;	                               
			case NomenclatureCodes.MDC_ATTR_NU_CMPD_VAL_OBS_SIMP:  			return SimpleNuObsValueCmp.class;	                               
			case NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_BASIC:				return BasicNuObsValue.class;	                               
			case NomenclatureCodes.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC: 			return BasicNuObsValueCmp.class;	                               
		    case NomenclatureCodes.MDC_ATTR_NU_VAL_OBS:						return NuObsValue.class;	                               
		    case NomenclatureCodes.MDC_ATTR_NU_CMPD_VAL_OBS:				return NuObsValueCmp.class;	                               
		    case NomenclatureCodes.MDC_ATTR_NU_ACCUR_MSMT:					return FLOAT_Type.class;	                               
	
		    //RT-SA (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_TIME_PD_SAMP:					return RelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_SIMP_SA_OBS_VAL:				return ASN1OctetString.class;	                               
			case NomenclatureCodes.MDC_ATTR_SCALE_SPECN_I8:					return ScaleRangeSpec8.class;	                               
			case NomenclatureCodes.MDC_ATTR_SCALE_SPECN_I16:				return ScaleRangeSpec16.class;	                               
			case NomenclatureCodes.MDC_ATTR_SCALE_SPECN_I32:				return ScaleRangeSpec32.class;	                               
			case NomenclatureCodes.MDC_ATTR_SA_SPECN:						return SaSpec.class;	                               
	
			//Enumeration (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_ENUM_OBS_VAL_SIMP_OID:			return OID_Type.class;	                               
			case NomenclatureCodes.MDC_ATTR_ENUM_OBS_VAL_SIMP_BIT_STR: 		return BITS_32.class;	                               
			case NomenclatureCodes.MDC_ATTR_ENUM_OBS_VAL_BASIC_BIT_STR:		return BITS_16.class;	
			case NomenclatureCodes.MDC_ATTR_ENUM_OBS_VAL_SIMP_STR:			return EnumPrintableString.class;
			case NomenclatureCodes.MDC_ATTR_VAL_ENUM_OBS:					return EnumObsValue.class;	                               
			case NomenclatureCodes.MDC_ATTR_ENUM_OBS_VAL_PART:       		return NomPartition.class;	                               
	
			
			//PM-Store (previously used are commented)
	//		case NomenclatureCodes.MDC_ATTR_ID_HANDLE: 						return HANDLE.class;
			case NomenclatureCodes.MDC_ATTR_PM_STORE_CAPAB:					return PmStoreCapab.class;	                               
		    case NomenclatureCodes.MDC_ATTR_METRIC_STORE_SAMPLE_ALG: 		return StoSampleAlg.class;	                               
		    case NomenclatureCodes.MDC_ATTR_METRIC_STORE_USAGE_CNT:		 	return INT_U32.class;	                               
		    case NomenclatureCodes.MDC_ATTR_OP_STAT:						return OperationalState.class;	                               
			case NomenclatureCodes.MDC_ATTR_PM_STORE_LABEL_STRING: 			return ASN1OctetString.class;	                               
	//		case NomenclatureCodes.MDC_ATTR_TIME_PD_SAMP:					return RelativeTime.class;	                               
		    case NomenclatureCodes.MDC_ATTR_NUM_SEG:						return INT_U16.class;	                               
			case NomenclatureCodes.MDC_ATTR_CLEAR_TIMEOUT:					return RelativeTime.class;	                               
	
			// PM-Segment (previously used are commented)
		    case NomenclatureCodes.MDC_ATTR_ID_INSTNO: 						return InstNumber.class;	                               
			case NomenclatureCodes.MDC_ATTR_PM_SEG_MAP:						return PmSegmentEntryMap.class;	                               
			case NomenclatureCodes.MDC_ATTR_PM_SEG_PERSON_ID: 				return PersonId.class;	
	//	    case NomenclatureCodes.MDC_ATTR_OP_STAT:						return OperationalState.class;	                               
	//		case NomenclatureCodes.MDC_ATTR_TIME_PD_SAMP:					return RelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_PM_SEG_LABEL_STRING:     		return ASN1OctetString.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_START_SEG:					return AbsoluteTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TIME_END_SEG:					return AbsoluteTime.class;	                               
	//		case NomenclatureCodes.MDC_ATTR_TIME_ABS_ADJUST:        		return AbsoluteTimeAdjust.class;	                               
			case NomenclatureCodes.MDC_ATTR_SEG_USAGE_CNT:					return INT_U32.class;	                               
			case NomenclatureCodes.MDC_ATTR_SEG_STATS:						return SegmentStatistics.class;	                               
			//case NomenclatureCodes.MDC_ATTR_SEG_FIXED_DATA:				return N/A;	                               
	//		case NomenclatureCodes.MDC_ATTR_CONFIRM_TIMEOUT: 				return RelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TRANSFER_TIMEOUT:        		return RelativeTime.class;	                               
	
			
			
			//Scanner (previously used are commented)
	//		case NomenclatureCodes.MDC_ATTR_ID_HANDLE: 						return HANDLE.class;
	//	    case NomenclatureCodes.MDC_ATTR_OP_STAT:						return OperationalState.class;	                               
			case NomenclatureCodes.MDC_ATTR_SCAN_HANDLE_LIST: 				return HANDLEList.class;	     
			case NomenclatureCodes.MDC_ATTR_SCAN_HANDLE_ATTR_VAL_MAP:		return HandleAttrValMap.class;	                               
	
			//CfgScanner (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_CONFIRM_MODE: 					return ConfirmMode.class;	                               
	//		case NomenclatureCodes.MDC_ATTR_CONFIRM_TIMEOUT: 				return RelativeTime.class;	                               
			case NomenclatureCodes.MDC_ATTR_TX_WIND:						return INT_U16.class;	                               
	
			
			//EpiScanner (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_SCAN_REP_PD_MIN:         		return RelativeTime.class;	                               
	
			//PeriScanner (previously used are commented)
			case NomenclatureCodes.MDC_ATTR_SCAN_REP_PD:					return RelativeTime.class;	                               
	
			
			//Other (commented because they are not found in the standard)
	//		case NomenclatureCodes.MDC_ATTR_AL_OP_STAT: 					return "Alert-Op-State";
	//	    case NomenclatureCodes.MDC_ATTR_LIMIT_CURR:						return "Current-Limits";	                               
	//	    case NomenclatureCodes.MDC_ATTR_METRIC_STORE_CAPAC_CNT:			return "Store-Capacity-Count";	                               
	//		case NomenclatureCodes.MDC_ATTR_AL_COND:						return "Alert-Condition";	                               
	//		case NomenclatureCodes.MDC_ATTR_AL_OP_TEXT_STRING:				return "Alert-Op-Text-String";                               
	//		case NomenclatureCodes.MDC_ATTR_PM_SEG_ELEM_STAT_ATTR:			return "xxx";	    not found in IEEE document! is it not used?                        
	
			default: return null;
		}
	}
	
	
	public static String getUnitName(int code){
		switch (code) {
		case NomenclatureCodes.MDC_DIM_PERCENT: 				return "%";
		case NomenclatureCodes.MDC_DIM_MILLI_L: 				return "ml";
		case NomenclatureCodes.MDC_DIM_X_G: 					return "g";
		case NomenclatureCodes.MDC_DIM_KILO_G: 					return "kg";
		case NomenclatureCodes.MDC_DIM_MILLI_G:					return "mg";
		case NomenclatureCodes.MDC_DIM_MILLI_G_PER_DL:			return "mg/dl";
		case NomenclatureCodes.MDC_DIM_MIN:						return "minutes";
		case NomenclatureCodes.MDC_DIM_HR:						return "hours";
		case NomenclatureCodes.MDC_DIM_DAY:						return "days";
		case NomenclatureCodes.MDC_DIM_BEAT_PER_MIN: 			return "bpm";
		case NomenclatureCodes.MDC_DIM_KILO_PASCAL:				return "kPa";
		case NomenclatureCodes.MDC_DIM_MMHG: 					return "mmHg";
		case NomenclatureCodes.MDC_DIM_MILLI_MOLE_PER_L:		return "mol/l";
		case NomenclatureCodes.MDC_DIM_DEGC:					return "ºC";   
		
		case NomenclatureCodes.MDC_DIM_CENTI_M: 				return "cm";
		case NomenclatureCodes.MDC_DIM_INCH:					return "inches";
		case NomenclatureCodes.MDC_DIM_LB: 						return "lb";
		case NomenclatureCodes.MDC_DIM_KG_PER_M_SQ:				return "kg/m2";
		
		case NomenclatureCodes.MDC_DIM_FAHR:					return "ºF"; 

		default:
			return "unit no declared";
		}
	}
	
	
	
	
	public static int getASN1ClassLength(int key)
	{
		switch (key) {
		case NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_SIMP: 					return 4; //Float-Type
		case NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS: 					return 8; //AbsoluteTime

		default:
			return -1;
		}
	}
	
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    public static String asHex(byte[] buf)
    {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }
    
    
    public static String asHexwithspaces(byte[] buf)
    {
        char[] chars = new char[3 * buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            chars[3 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[3 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
            chars[3 * i + 2] = (byte)0x20;

        }
        return new String(chars);
    }

    
    public static int BCDtoInt(int bcd){
    	return (bcd/16*10+bcd%16);
    }
    public static void printAPDU(byte[] apdu){
    	Logging.logSend(asHexwithspaces(apdu));
    }
}

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
package org.universAAL.lddi.lib.ieeex73std.x73.p104zz;


import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.org.bn.IDecoder;
import org.universAAL.lddi.lib.ieeex73std.org.bn.types.BitString;
import org.universAAL.lddi.lib.ieeex73std.utils.ASNUtils;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.StatusCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AttrValMap;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AttrValMapEntry;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.BITS_16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HANDLE;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U8;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricIdList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricSpecSmall;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.MetricStructureSmall;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.NomPartition;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.OID_Type;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.TYPE;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.dim.Attribute;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.dim.Numeric;


// Blood pressure monitor
//Standard configuration values: 700-799 (0x02BC - 0x31F)


public class p10407BloodPressure extends DeviceSpecialization{

	public LinkedList<Attribute> bloodpressureattributes;
	public LinkedList<Attribute> pulserateattributes;

	
	public p10407BloodPressure(IDecoder decoder) throws Exception{
		super(decoder);
		generateBloodPressureAttributes();
		generatePulserateAttributes();
	}

	private void generateBloodPressureAttributes() throws Exception{
		
		
		Attribute bloodattr;
		bloodpressureattributes = new LinkedList<Attribute>();
		
		// Handle
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(1));
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		
		bloodpressureattributes.add(bloodattr);

//		System.out.println(bloodpressureattributes.keySet());
//		System.out.println(bloodpressureattributes.values());
//		
//		Attribute a = bloodpressureattributes.get(NomenclatureCodes.MDC_ATTR_ID_HANDLE);
//		System.out.println(a);
//		HANDLE h = (HANDLE) a.getAttributeType();
//		System.out.println(h);
//		System.out.println(h.getValue().getValue());
		
		//Type
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_PRESS_BLD_NONINV));
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		type.setCode(type_oid);
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		bloodpressureattributes.add(bloodattr);
		
		// Metric-Spec-Small: can be mss-avail-intermitent, mss-avail-stored-data, mss-upd-aperiodic, mss-msmt-aperiodic, mss-acc-agent-initiated
		
		MetricSpecSmall mss = new MetricSpecSmall();
		//MetricSpecSmall contains a 2-byte BitString
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2];
		
		int bs_val = StatusCodes.MSS_AVAIL_INTERMITTENT | StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_UPD_APERIODIC |
					StatusCodes.MSS_MSMT_APERIODIC | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1] = (byte)(bs_val & 0x000000FF);
		bs_byte[0] = (byte)(bs_val & 0x0000FF00);
//		System.out.println(ASNUtils.asHexwithspaces(bs_byte));
		
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		bloodpressureattributes.add(bloodattr);
		
		
		// Metric Structure Small
		MetricStructureSmall mstructsmall = new MetricStructureSmall();
		mstructsmall.setMs_struct(new INT_U8(StatusCodes.MS_STRUCT_COMPOUND_FIX));
		mstructsmall.setMs_comp_no(new INT_U8(3));
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_STRUCT_SMALL, mstructsmall);
		bloodpressureattributes.add(bloodattr);
		
		
		//Metric id List
		MetricIdList midlist = new MetricIdList();
		midlist.initValue();
		
		OID_Type oid_systolic = new OID_Type();
		oid_systolic.setValue(new INT_U16(NomenclatureCodes.MDC_PRESS_BLD_NONINV_SYS));
		midlist.add(oid_systolic);
		
		OID_Type oid_diastolic = new OID_Type();
		oid_diastolic.setValue(new INT_U16(NomenclatureCodes.MDC_PRESS_BLD_NONINV_DIA));
		midlist.add(oid_diastolic);
		
		OID_Type oid_mean = new	OID_Type();
		oid_mean.setValue(new INT_U16(NomenclatureCodes.MDC_PRESS_BLD_NONINV_MEAN));
		midlist.add(oid_mean);
		
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_PHYSIO_LIST, midlist);
		bloodpressureattributes.add(bloodattr);

		//Unit Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_MMHG));
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		bloodpressureattributes.add(bloodattr);
		
		//Attribute Map
			// - MDC_ATTR_NU_CMPD_VAL_OBS_BASIC 
			// - MDC_ATTR_TIME_STAMP_ABS (size = 8)
		AttrValMap attrmap = new AttrValMap();
		attrmap.initValue();
		
		AttrValMapEntry entry1 = new AttrValMapEntry();
		OID_Type entry1_oid = new OID_Type();
		entry1_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_NU_CMPD_VAL_OBS_BASIC));
		entry1.setAttribute_id(entry1_oid);
		entry1.setAttribute_len(new INT_U16(10)); // 3*2 SFloat + 2 bytes indicating how many items and 2 more with its total length
		
		AttrValMapEntry entry2 = new AttrValMapEntry();
		OID_Type entry2_oid = new OID_Type();
		entry2_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS));
		entry2.setAttribute_id(entry2_oid);
		entry2.setAttribute_len(new INT_U16(ASNUtils.getASN1ClassLength(NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS)));

		attrmap.add(entry1);
		attrmap.add(entry2);
		
		bloodattr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		bloodpressureattributes.add(bloodattr);
		
		
		
		Numeric numeric = new Numeric(bloodpressureattributes);
		
		addObjecttoDim(1,numeric);
	}
	
	

	private void generatePulserateAttributes() throws Exception {

		Attribute pulseattr;
		pulserateattributes = new LinkedList<Attribute>();
		
		// HANDLE
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(2));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		pulserateattributes.add(pulseattr);
		
		// TYPE
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_PULS_RATE_NON_INV));
		type.setCode(type_oid);
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		pulserateattributes.add(pulseattr);
		
		//Metric Spec Small
		MetricSpecSmall mss = new MetricSpecSmall();
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2]; 
		int mss_val = StatusCodes.MSS_AVAIL_INTERMITTENT | StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_UPD_APERIODIC |
					StatusCodes.MSS_MSMT_APERIODIC | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1]=(byte)(mss_val & 0x000000FF);
		bs_byte[0]=(byte)((mss_val>>8)&0x000000FF);
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		pulserateattributes.add(pulseattr);
		
		// Unit-Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_BEAT_PER_MIN));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		pulserateattributes.add(pulseattr);
		
		// Attribute Value Map
			// - MDC_ATTR_NU_VAL_OBS_BASIC (the value of mass) size = 2 (SFloat-Type)
			// - MDC_ATTR_TIME_STAMP_ABS (time of measurement) size = 8 (8xINT_U8)		
		AttrValMap attrmap = new AttrValMap();
		attrmap.initValue();
		
		AttrValMapEntry entry1 = new AttrValMapEntry();
		OID_Type entry1_oid = new OID_Type();
		entry1_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_BASIC));
		entry1.setAttribute_id(entry1_oid);
		entry1.setAttribute_len(new INT_U16(2));
		
		AttrValMapEntry entry2 = new AttrValMapEntry();
		OID_Type entry2_oid = new OID_Type();
		entry2_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS));
		entry2.setAttribute_id(entry2_oid);
		entry2.setAttribute_len(new INT_U16(8));
		
		attrmap.add(entry1);
		attrmap.add(entry2);
		
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		pulserateattributes.add(pulseattr);
		
		// in page 14 of the 11073-10407 tells that there is another Mandatory attribute, but in the examples it doesn't show up.
		
		Numeric pulseattrs = new Numeric(pulserateattributes);
		addObjecttoDim(2,pulseattrs);
		
	}


	public String toString(){
		return "Blood Pressure";
	}
	
}

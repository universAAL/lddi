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
package x73.p104zz;


import java.util.LinkedList;

import org.bn.IDecoder;
import org.bn.types.BitString;

import x73.nomenclature.NomenclatureCodes;
import x73.nomenclature.StatusCodes;
import x73.p20601.AttrValMap;
import x73.p20601.AttrValMapEntry;
import x73.p20601.BITS_16;
import x73.p20601.HANDLE;
import x73.p20601.INT_U16;
import x73.p20601.MetricSpecSmall;
import x73.p20601.NomPartition;
import x73.p20601.OID_Type;
import x73.p20601.TYPE;
import x73.p20601.dim.Attribute;
import x73.p20601.dim.Numeric;

// Glucometer
//Standard configuration values: 1700 - 1799 (0x06A4 - 0x0707)


public class p10417Glucometer extends DeviceSpecialization{

	public LinkedList<Attribute> glucometerattributes;

	
	public p10417Glucometer(IDecoder decoder) throws Exception{
		super(decoder);
		generateTemperatureAttributes();
	}

	private void generateTemperatureAttributes() throws Exception{
		
		Attribute attr;
		glucometerattributes = new LinkedList<Attribute>();
		
			// Handle
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(1));
		attr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		glucometerattributes.add(attr);
		
			// Type
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_CONC_GLU_CAPILLARY_WHOLEBLOOD)); // glucose
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		type.setCode(type_oid);
		attr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		glucometerattributes.add(attr);
		
		// Metric-Spec-Small: can be mss-avail-intermitent, mss-avail-stored-data, mss-upd-aperiodic, mss-msmt-aperiodic, mss-acc-agent-initiated
		
		MetricSpecSmall mss = new MetricSpecSmall();
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2];
		
		int bs_val = StatusCodes.MSS_AVAIL_INTERMITTENT | StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_UPD_APERIODIC |
					StatusCodes.MSS_MSMT_APERIODIC | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1] = (byte)(bs_val & 0x000000FF);
		bs_byte[0] = (byte)(bs_val & 0x0000FF00);
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		attr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		glucometerattributes.add(attr);
		
			//Unit-Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_MILLI_G_PER_DL));
		attr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		glucometerattributes.add(attr);
		
		
			//Attribute map
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
		
		attr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		glucometerattributes.add(attr);
		
			
		Numeric numeric = new Numeric(glucometerattributes);
		addObjecttoDim(1, numeric);

		
	}

	
	public String toString(){
		return "Glucometer";
	}
	
}

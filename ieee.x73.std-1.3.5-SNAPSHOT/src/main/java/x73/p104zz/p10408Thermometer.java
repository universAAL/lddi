package x73.p104zz;


import java.util.LinkedList;

import org.bn.IDecoder;
import org.bn.types.BitString;
import utils.ASNUtils;
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

// Thermometer
//Standard configuration values: 800 - 899 (0x0320 - 0x383)


public class p10408Thermometer extends DeviceSpecialization{

	public LinkedList<Attribute> temperatureattributes;
	
	public p10408Thermometer(IDecoder decoder) throws Exception{
		super(decoder);
		generateTemperatureAttributes();
	}

	private void generateTemperatureAttributes() throws Exception{
		
		
		Attribute tempattr;
		temperatureattributes = new LinkedList<Attribute>();
		
		// Handle
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(1));
		tempattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		
		temperatureattributes.add(tempattr);

		
		//Type
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_TEMP_BODY));
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		type.setCode(type_oid);
		tempattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		temperatureattributes.add(tempattr);
		
		// Metric-Spec-Small: can be mss-avail-intermitent, mss-avail-stored-data, mss-upd-aperiodic, mss-msmt-aperiodic, mss-acc-agent-initiated
		
		MetricSpecSmall mss = new MetricSpecSmall();
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2];
		
		int bs_val = StatusCodes.MSS_AVAIL_INTERMITTENT | StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_UPD_APERIODIC |
					StatusCodes.MSS_MSMT_APERIODIC | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1] = (byte)(bs_val & 0x000000FF);
		bs_byte[0] = (byte)(bs_val & 0x0000FF00);
//		System.out.println(ASNUtils.asHexwithspaces(bs_byte));
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		tempattr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		temperatureattributes.add(tempattr);
		
		

		//Unit Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_DEGC));
		tempattr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		temperatureattributes.add(tempattr);
		
		//Attribute Map
			// - MDC_ATTR_NU_VAL_OBS_BASIC  (size 2)
			// - MDC_ATTR_TIME_STAMP_ABS (size = 8)
		AttrValMap attrmap = new AttrValMap();
		attrmap.initValue();
		
		AttrValMapEntry entry1 = new AttrValMapEntry();
		OID_Type entry1_oid = new OID_Type();
		entry1_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_BASIC));
		entry1.setAttribute_id(entry1_oid);
		entry1.setAttribute_len(new INT_U16(2)); // 2 bytes (stores an SFloatType)
		
		AttrValMapEntry entry2 = new AttrValMapEntry();
		OID_Type entry2_oid = new OID_Type();
		entry2_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS));
		entry2.setAttribute_id(entry2_oid);
		entry2.setAttribute_len(new INT_U16(ASNUtils.getASN1ClassLength(NomenclatureCodes.MDC_ATTR_TIME_STAMP_ABS)));

		attrmap.add(entry1);
		attrmap.add(entry2);
		
		tempattr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		temperatureattributes.add(tempattr);
		
		Numeric numeric = new Numeric(temperatureattributes);
		
		addObjecttoDim(1,numeric);
		
	}

	public String toString(){
		return "Thermometer";
	}
	
}

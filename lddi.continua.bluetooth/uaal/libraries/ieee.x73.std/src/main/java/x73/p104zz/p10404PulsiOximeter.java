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

// Thermometer
//Standard configuration values: 400 - 499 (0x0190 - 0x1F3)


public class p10404PulsiOximeter extends DeviceSpecialization{

	public LinkedList <Attribute> spo2attributes;
	public LinkedList <Attribute> pulserateattributes;
	
	public p10404PulsiOximeter(IDecoder decoder) throws Exception{
		super(decoder);
		generateSpO2attributes() ;
		generatepulserateattributes();
	}


	private void generateSpO2attributes() throws Exception{
		
		
		Attribute spo2attr;
		spo2attributes = new LinkedList <Attribute>();
		
		// Handle
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(1));
		spo2attr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		
		spo2attributes.add(spo2attr);

		
		//Type
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_PULS_OXIM_SAT_O2));
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		type.setCode(type_oid);
		spo2attr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		spo2attributes.add(spo2attr);
		
		// Metric-Spec-Small: can be mss-avail-intermitent, mss-avail-stored-data, mss-upd-aperiodic, mss-msmt-aperiodic, mss-acc-agent-initiated
		
		MetricSpecSmall mss = new MetricSpecSmall();
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2];
		
		int bs_val = StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1] = (byte)(bs_val & 0x000000FF);
		bs_byte[0] = (byte)(bs_val & 0x0000FF00);
//		System.out.println(ASNUtils.asHexwithspaces(bs_byte));
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		spo2attr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		spo2attributes.add(spo2attr);
		
		

		//Unit Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_PERCENT));
		spo2attr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		spo2attributes.add(spo2attr);
		
		//Attribute Map
			// - MDC_ATTR_NU_VAL_OBS_BASIC  (size 2)
		AttrValMap attrmap = new AttrValMap();
		attrmap.initValue();
		
		AttrValMapEntry entry1 = new AttrValMapEntry();
		OID_Type entry1_oid = new OID_Type();
		entry1_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_BASIC));
		entry1.setAttribute_id(entry1_oid);
		entry1.setAttribute_len(new INT_U16(2)); // 2 bytes (stores an SFloatType)

		attrmap.add(entry1);
		
		spo2attr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		spo2attributes.add(spo2attr);
		
		Numeric numeric = new Numeric(spo2attributes);
		
		addObjecttoDim(1,numeric);
		
	}

	

	private void generatepulserateattributes() throws Exception {
			
		Attribute pulseattr;
		pulserateattributes = new LinkedList<Attribute>();
		
		// Handle
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(10));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_HANDLE, handle);
		
		pulserateattributes.add(pulseattr);

		
		//Type
		TYPE type = new TYPE();
		OID_Type type_oid = new OID_Type();
		type_oid.setValue(new INT_U16(NomenclatureCodes.MDC_PULS_OXIM_PULS_RATE));
		type.setPartition(new NomPartition(new INT_U16(NomenclatureCodes.MDC_PART_SCADA)));
		type.setCode(type_oid);
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ID_TYPE, type);
		pulserateattributes.add(pulseattr);
		
		// Metric-Spec-Small: can be mss-avail-intermitent, mss-avail-stored-data, mss-upd-aperiodic, mss-msmt-aperiodic, mss-acc-agent-initiated
		
		MetricSpecSmall mss = new MetricSpecSmall();
		BitString bs = new BitString();
		byte[] bs_byte = new byte[2];
		
		int bs_val = StatusCodes.MSS_AVAIL_STORED_DATA | StatusCodes.MSS_ACC_AGENT_INITIATED;
		
		bs_byte[1] = (byte)(bs_val & 0x000000FF);
		bs_byte[0] = (byte)((bs_val>>8) & 0x000000FF);
//			System.out.println(ASNUtils.asHexwithspaces(bs_byte));
		bs.setValue(bs_byte);
		mss.setValue(new BITS_16(new BitString(bs)));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_METRIC_SPEC_SMALL, mss);
		pulserateattributes.add(pulseattr);
		
		

		//Unit Code
		OID_Type unit_oid = new OID_Type();
		unit_oid.setValue(new INT_U16(NomenclatureCodes.MDC_DIM_BEAT_PER_MIN));
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE, unit_oid);
		pulserateattributes.add(pulseattr);
		
		//Attribute Map
			// - MDC_ATTR_NU_VAL_OBS_BASIC  (size 2)
		AttrValMap attrmap = new AttrValMap();
		attrmap.initValue();
		
		AttrValMapEntry entry1 = new AttrValMapEntry();
		OID_Type entry1_oid = new OID_Type();
		entry1_oid.setValue(new INT_U16(NomenclatureCodes.MDC_ATTR_NU_VAL_OBS_BASIC));
		entry1.setAttribute_id(entry1_oid);
		entry1.setAttribute_len(new INT_U16(2)); // 2 bytes (stores an SFloatType)

		attrmap.add(entry1);
		
		pulseattr = new Attribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP, attrmap);
		pulserateattributes.add(pulseattr);
		
		Numeric numeric = new Numeric(pulserateattributes);
		
		addObjecttoDim(10,numeric);
			
		}

	
	public String toString(){
		return "Thermometer";
	}
	
}

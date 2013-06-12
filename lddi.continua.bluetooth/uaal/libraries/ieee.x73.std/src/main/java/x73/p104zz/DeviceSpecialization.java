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


import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

import org.bn.IDecoder;

import measurements.Measurement;
import utils.ASNUtils;
import utils.Logging;
import x73.nomenclature.NomenclatureCodes;
import x73.nomenclature.StatusCodes;
import x73.p20601.AVA_Type;
import x73.p20601.AbsoluteTime;
import x73.p20601.AbsoluteTimeAdjust;
import x73.p20601.AttrValMap;
import x73.p20601.AttrValMapEntry;
import x73.p20601.AttributeList;
import x73.p20601.BITS_16;
import x73.p20601.ConfigId;
import x73.p20601.ConfigObject;
import x73.p20601.ConfigReport;
import x73.p20601.ConfigReportRsp;
import x73.p20601.ConfigResult;
import x73.p20601.GetResultSimple;
import x73.p20601.INT_U16;
import x73.p20601.OID_Type;
import x73.p20601.ObservationScan;
import x73.p20601.ObservationScanFixed;
import x73.p20601.ScanReportInfoFixed;
import x73.p20601.ScanReportInfoMPFixed;
import x73.p20601.ScanReportInfoMPVar;
import x73.p20601.ScanReportInfoVar;
import x73.p20601.dim.DIM;
import x73.p20601.dim.MDS;
import x73.p20601.dim.Numeric;

/**
 * This class is used for managing the information received from the Agent.
 *
 */
public class DeviceSpecialization extends MDS {
	
	IDecoder decoder = null;
	ByteArrayInputStream bais = null;
	
	/*
	 * TODO PLEASE NOTE THE NEXT OBJECT:
	 * It will be used for correcting time not syncronized between agent and manager, after issuing the GET MDS command.
	 * 
	 * See method getTimeGapofDevice(GetResultSimple rors_cmip_get) implemented, but not used yet 
 	 * because we have problems for finding the right moment to send that command, 
	 */
	int[] time_gap ={0,0,0,0,0,0};

	/**
	 * Analyze a configuration report from the Agent. With it, it describes the objects is using (measurement types) 
	 * @param cfgreport the configuration report from the agent
	 * @return the response to the configuration report. It indicates whether it has been accepted or not. 
	 * 
	 */
	
	public DeviceSpecialization(IDecoder decoder) {
		this.decoder = decoder;
	}
	
	public ConfigReportRsp MDS_Configuration_Event(ConfigReport cfgreport) {
		
		int response_code = StatusCodes.UNSUPPORTED_CONFIG;
		// get the configuration id
		Integer cfg_id = cfgreport.getConfig_report_id().getValue().getValue();
		// obtain the configuration objects. Will be one for each handle reported.
		Collection<ConfigObject> configobjects= cfgreport.getConfig_obj_list().getValue();
		
		Iterator<ConfigObject> it = configobjects.iterator();
		while(it.hasNext()){
			
			ConfigObject cfgobj = it.next();
			//what kind of object it is.
			int obj_class = cfgobj.getObj_class().getValue().getValue();
			
			//TODO other types of measurements (see commented lines)
			switch (obj_class) {
			// numeric metric
			case NomenclatureCodes.MDC_MOC_VMO_METRIC_NU:
				try {
					((ExtendedConfiguration) this).createNumeric(cfgobj);
					// if everything went OK, we accept the configuration because we can handle it.
					response_code = StatusCodes.ACCEPTED_CONFIG;
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
//			case NomenclatureCodes.MDC_MOC_VMO_METRIC_SA_RT:
//				try {
//					((ExtendedConfiguration) this).createRT_SA(cfgobj);
//					// if everything went OK, we accept the configuration because we can handle it.
//					response_code = StatusCodes.ACCEPTED_CONFIG;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			case NomenclatureCodes.MDC_MOC_VMO_METRIC_ENUM:
//				try {
//					((ExtendedConfiguration) this).createEnumeration(cfgobj);
//					// if everything went OK, we accept the configuration because we can handle it.
//					response_code = StatusCodes.ACCEPTED_CONFIG;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//				
//			case NomenclatureCodes.MDC_MOC_SCAN_CFG_EPI:
//				try {
//					((ExtendedConfiguration) this).createEpisodicScanner(cfgobj);
//					// if everything went OK, we accept the configuration because we can handle it.
//					response_code = StatusCodes.ACCEPTED_CONFIG;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//				
//			case NomenclatureCodes.MDC_MOC_SCAN_CFG_PERI:
//				try {
//					((ExtendedConfiguration) this).createPeriodicScanner(cfgobj);
//					// if everything went OK, we accept the configuration because we can handle it.
//					response_code = StatusCodes.ACCEPTED_CONFIG;
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				break;
//			case NomenclatureCodes.MDC_MOC_VMO_PMSTORE:
//				break;
//			case NomenclatureCodes.MDC_MOC_PM_SEGMENT:
//				break;

			default:
				// if not, we reject it and keep waiting for another one (if the Agent has more)
				response_code = StatusCodes.UNSUPPORTED_CONFIG;
			}
		}
		
		// prepare the Config Report Response to send the result of the analysis of the configuration to the agent.
		ConfigReportRsp crr = new ConfigReportRsp();
		crr.setConfig_report_id(new ConfigId(new INT_U16(cfg_id)));
		ConfigResult cr = new ConfigResult(new INT_U16(response_code));
		crr.setConfig_result(cr);
		
		return crr;
	}

	/**
	 * Processing of a variable measurement received.
	 */
	public void MDS_Dynamic_Data_Update_Var(ScanReportInfoVar scanreportinfovar) {
		Logging.log("Initiating processing of a received measurement from a "+this.toString());
		Logging.blankLine();
		
		// create the object where the data will be stored for further representation.
		// the parameter is the number of measurements received.
		Measurement measurement = new Measurement(scanreportinfovar.getObs_scan_var().size());
		
		Iterator<ObservationScan> it = scanreportinfovar.getObs_scan_var().iterator();
		ObservationScan os;
		
		while(it.hasNext()){
			os = it.next();
			// get the attribute list of each peace of data in the measure
			AttributeList attrlist = os.getAttributes();
			int size_of_attrlist = attrlist.getValue().size();
			Iterator<AVA_Type> itlist = attrlist.getValue().iterator();
			int i=0;
			
			// this array will store the decoded objects from the data received.
			Object[] objects = new Object[size_of_attrlist];
			while(itlist.hasNext()){
				
				// from the report of the Agent, we have generic ASN objects. We will have to know which Attribute it is.
				AVA_Type ava_type = itlist.next();
				int ava_id = ava_type.getAttribute_id().getValue().getValue();
				byte[] ava_value = ava_type.getAttribute_value();

				// decode the ASN object as the type of its Attribute and store it.

				try {
					bais = new ByteArrayInputStream(ava_value);
					Object object = decoder.decode(bais, ASNUtils.getAttributeClass(ava_id));
					objects[i]=object;
					i++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			//add the data to the measurement object
			measurement.addMeasurement(objects);
		}
		// represent the data
		measurement.printMeasurement(time_gap);
		
	}

	
	/**
	 * Processing of a fixed measurement
	 */
	public void MDS_Dynamic_Data_Update_Fixed(ScanReportInfoFixed scanreportinfofixed) throws Exception {
		Logging.log("Initiating processing of a received measurement from a "+this.toString());

		// create the object where the data will be stored for further representation.
		// the parameter is the number of measurements received.
		Measurement measurement = new Measurement(scanreportinfofixed.getObs_scan_fixed().size());
		Iterator<ObservationScanFixed> it = scanreportinfofixed.getObs_scan_fixed().iterator();
		
		ObservationScanFixed osf;
		
		while(it.hasNext()){
			osf = it.next();
			
			DIM dim = (DIM) getObjectfromDim(osf.getObj_handle().getValue().getValue());
			
			if (dim.getNomenclatureCode() == NomenclatureCodes.MDC_MOC_VMO_METRIC_NU){
				Numeric numeric = (Numeric) dim;

//				System.out.println(dim.getNomenclatureCode());
				
	//			System.out.println(element);
				byte[] data = osf.getObs_val_data();
	
				OID_Type unit = null;
				
				// get the unit in which the information is stored (kg, cm, ...) for this Handle
				if(numeric.hasAttribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE)){
					unit = (OID_Type) numeric.getAttribute(NomenclatureCodes.MDC_ATTR_UNIT_CODE).getAttributeType();
	//				System.out.println(unit.getValue().getValue());
				}
				
				// get the Attribute Value map for this handle
				AttrValMap avm = (AttrValMap) numeric.getAttribute(NomenclatureCodes.MDC_ATTR_ATTRIBUTE_VAL_MAP).getAttributeType();
				Iterator<AttrValMapEntry> itmap = avm.getValue().iterator();
				int size_of_attr_val_map = avm.getValue().size();
				
				Object[] objects = new Object[size_of_attr_val_map+1];  // reserve another one for the unit.
				int i=0;
				objects[objects.length-1] = unit; // the last one is the unit, so we simplify the array treatment in the next lines of code
				
				int totalreaded = 0;
				
				while(itmap.hasNext()){
					// get the Attribute identificator and its length. Length will be used for controlling that the decoder does not exceeds the bounds of the data byte array
					AttrValMapEntry entry = itmap.next();
					int id  = entry.getAttribute_id().getValue().getValue().intValue();
					int length = entry.getAttribute_len().getValue().intValue();
					
//					System.out.println(id);
					
					bais = new ByteArrayInputStream(data);
					// decode the attribute (and its stored data) and store the information.
					Object object  = decoder.decode(bais, ASNUtils.getAttributeClass(id));
					objects[i++]=object;
					totalreaded += length;
					// discard the already decoded data and prepare the array for the next Attribute
					// if we eliminate the next two lines, nothing wrong should happen, but just in case, we left them.
					if(totalreaded<data.length)
						data = Arrays.copyOfRange(data, totalreaded, data.length); 
					
				}
				// add the data to the measurement object
				measurement.addMeasurement(objects);
			}
		}
		//represent the information of the measurement
		measurement.printMeasurement(time_gap);
	}

	public void MDS_Dynamic_Data_Update_MP_Var(
			ScanReportInfoMPVar scanreportinfompvar) {
		// TODO Multi-Person variable measurement 
		
	}

	public void MDS_Dynamic_Data_Update_MP_Fixed(
			ScanReportInfoMPFixed scanreportinfompfixed) {
		// TODO Multi-Person fixed measurement 
		
	}

	public void GET() {
		// TODO GET method for asking the MDS object of the Agent.
		
	}

	@Override
	public void MDS_Data_Request() {
		// TODO This method allows the manager system to enable or disable measurement data transmission from the agent (see 8.9.3.3.3 for a description).
		
	}

	@Override
	public void Set_Time() {
		// TODO Set the time in the Agent.
		
	}
	
	public void getTimeGapofDevice(GetResultSimple rors_cmip_get) {
		GetResultSimple getInfo = rors_cmip_get;
		AttributeList attr_list =  getInfo.getAttribute_list();
		Iterator <AVA_Type> it = attr_list.getValue().iterator();
		while(it.hasNext()){
			AVA_Type attribute =  it.next();
			OID_Type attr_id = new OID_Type();
			attr_id.setValue(new INT_U16(2439));
			// if the get information has a Time Absolute Attribute...
			AbsoluteTime abstime = null;
			if (attribute.getAttribute_id().getValue().getValue()==2439){
				byte[] attr_data = attribute.getAttribute_value();
				bais = new ByteArrayInputStream(attr_data);
				// decode the attribute (and its stored data) and store the information.
				try {
					abstime  = decoder.decode(bais, AbsoluteTime.class);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(abstime!= null){
				time_gap = new int[6];
				Calendar calendar = Calendar.getInstance();
				int year, day, month, hour, min, secs;
				
				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH)+1;
				day = calendar.get(Calendar.DAY_OF_MONTH);
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				min = calendar.get(Calendar.MINUTE);
				secs = calendar.get(Calendar.SECOND);
				
				
				int device_year, device_month, device_day, device_hour, device_min, device_secs;
				
				device_year = ASNUtils.BCDtoInt(abstime.getCentury().getValue())*100+ASNUtils.BCDtoInt(abstime.getYear().getValue());
				device_month = ASNUtils.BCDtoInt(abstime.getMonth().getValue());
				device_day = ASNUtils.BCDtoInt(abstime.getDay().getValue());
				
				device_hour = ASNUtils.BCDtoInt(abstime.getHour().getValue());
				device_min = ASNUtils.BCDtoInt(abstime.getMinute().getValue());
				device_secs = ASNUtils.BCDtoInt(abstime.getSecond().getValue());
				System.out.println(device_year+" "+device_month+" "+device_day+" "+device_hour+" "+device_min+" "+device_secs);
				
				time_gap[0] = year-device_year;
				time_gap[1] = month-device_month;
				time_gap[2] = day-device_day;
				time_gap[3] = hour-device_hour;
				time_gap[4] = min-device_min;
				time_gap[5] = secs-device_secs;
				
			}
		}
		
	
	}

}

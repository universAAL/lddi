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
package org.universAAL.lddi.lib.ieeex73std.x73.p20601.dim;


import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReport;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReportRsp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoGrouped;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPGrouped;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPVar;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoVar;



/**
 * The PeriCfgScanner class represents a class that can be instantiated. PeriCfgScanner objects are used to
send reports containing Periodic data, that is, data sampled during fixed periods. It buffers any data value
changes to be sent as part of a periodic report. Event reports shall be sent with a time interval equal to the
Reporting-Interval attribute value.
The number of observations for each metric object is dependent on the metric object�s update interval and
the scanner�s Reporting-Interval.
Example: A periodic configurable scanner is set up to �scan� two metric objects with a Reporting-Interval
of 1 s. The two objects update their corresponding observed value periodically with an interval of 1 s and �
s, respectively. The periodic configurable scanner then issues event reports every second containing one
observation scan of metric object #1 and two observation scans of metric object #2.
 *
 */
public class PeriCfgScanner extends CfgScanner implements PeriCfgScanner_Events{

	private LinkedList<Attribute> attrList;
	
	public PeriCfgScanner(LinkedList<Attribute> list) throws Exception {
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;
	}
	
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_SCAN_CFG_PERI;
	}
	

	public ConfigReportRsp MDS_Configuration_Event(ConfigReport cfgreport) {
		// TODO Auto-generated method stub
		return null;
	}

	public void MDS_Dynamic_Data_Update_Var(ScanReportInfoVar scanreportinfovar) {
		// TODO Auto-generated method stub
		
	}

	
	public void MDS_Dynamic_Data_Update_Fixed(
			ScanReportInfoFixed scanreportinfofixed) {
		// TODO Auto-generated method stub
		
	}

	
	public void MDS_Dynamic_Data_Update_MP_Var(
			ScanReportInfoMPVar scanreportinfompvar) {
		// TODO Auto-generated method stub
		
	}

	
	public void MDS_Dynamic_Data_Update_MP_Fixed(
			ScanReportInfoMPFixed scanreportinfompfixed) {
		// TODO Auto-generated method stub
		
	}

	
	public void GET() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void MDS_Data_Request() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Set_Time() {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_Var(ScanReportInfoVar sriv) {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_Fixed(ScanReportInfoFixed srif) {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_Grouped(ScanReportInfoGrouped srig) {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_MP_Var(ScanReportInfoMPVar srimpvar) {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_MP_Fixed(ScanReportInfoMPFixed srimpf) {
		// TODO Auto-generated method stub
		
	}

	
	public void Buf_Scan_Report_MP_Grouped(ScanReportInfoMPGrouped srimpg) {
		// TODO Auto-generated method stub
		
	}

}

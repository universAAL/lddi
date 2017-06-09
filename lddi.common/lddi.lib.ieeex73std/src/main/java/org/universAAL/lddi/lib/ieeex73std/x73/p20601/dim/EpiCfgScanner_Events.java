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

import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoGrouped;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPGrouped;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPVar;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoVar;

/**
 * The EpiCfgScanner class represents a class that can be instantiated.
 * EpiCfgScanner objects are used to send reports containing episodic data, that
 * is, data not having a fixed period between each data value. A report is sent
 * whenever one of the observed attributes changes value; however, two
 * consecutive event reports shall not have a time interval less than the value
 * of the Min-Reporting-Interval attribute.
 * 
 * @author lgigante
 *
 */
public interface EpiCfgScanner_Events {

	/**
	 * Unbuf-Scan-Report-Var: This event style reports summary data about any
	 * objects and attributes that the scanner monitors. The event is triggered
	 * whenever data values change and the variable message format
	 * (type/length/value) is used when reporting data that changed.
	 */
	public void Unbuf_Scan_Report_Var(ScanReportInfoVar sriv);

	/**
	 * Unbuf-Scan-Report-Fixed: This event style is used whenever data values
	 * change and the fixed message format of each object is used to report data
	 * that changed.
	 */
	public void Unbuf_Scan_Report_Fixed(ScanReportInfoFixed srif);

	/**
	 * Unbuf-Scan-Report-Grouped: This style is used when the scanner object is
	 * used to send the data in its most compact format. The Handle-Attr-Val-Map
	 * attribute describes the objects and attributes that are included and the
	 * format of the message.
	 */
	public void Unbuf_Scan_Report_Grouped(ScanReportInfoGrouped srig);

	/**
	 * Unbuf-Scan-Report-MP-Var: This is the same as Unbuf-Scan-Report-Var, but
	 * allows inclusion of data from multiple persons.
	 */
	public void Unbuf_Scan_Report_MP_Var(ScanReportInfoMPVar srimpvar);

	/**
	 * Unbuf-Scan-Report-MP-Fixed: This is the same as Unbuf-Scan-Report-Fixed,
	 * but allows inclusion of data from multiple persons.
	 */
	public void Unbuf_Scan_Report_MP_Fixed(ScanReportInfoMPFixed srimpf);

	/**
	 * Unbuf-Scan-Report-MP-Grouped: This is the same as
	 * Unbuf-Scan-Report-Grouped, but allows inclusion of data from multiple
	 * persons.
	 */
	public void Unbuf_Scan_Report_MP_Grouped(ScanReportInfoMPGrouped srimpg);

}

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
 * All of the event report styles listed in Table 18 are buffered equivalents to
 * their Buffered counterparts in 6.3.9.4.5. One difference is that the scanner
 * buffers data over the reporting interval and sends a single message at the
 * end of the interval. A second difference is that the same objects and
 * attributes are included in each report regardless of whether their values
 * have changed.
 *
 * @author lgigante
 * @see EpiCfgScanner_Events
 */
public interface PeriCfgScanner_Events {

	public void Buf_Scan_Report_Var(ScanReportInfoVar sriv);

	public void Buf_Scan_Report_Fixed(ScanReportInfoFixed srif);

	public void Buf_Scan_Report_Grouped(ScanReportInfoGrouped srig);

	public void Buf_Scan_Report_MP_Var(ScanReportInfoMPVar srimpvar);

	public void Buf_Scan_Report_MP_Fixed(ScanReportInfoMPFixed srimpf);

	public void Buf_Scan_Report_MP_Grouped(ScanReportInfoMPGrouped srimpg);
}

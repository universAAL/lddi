package x73.p20601.dim;

import x73.p20601.ScanReportInfoFixed;
import x73.p20601.ScanReportInfoGrouped;
import x73.p20601.ScanReportInfoMPFixed;
import x73.p20601.ScanReportInfoMPGrouped;
import x73.p20601.ScanReportInfoMPVar;
import x73.p20601.ScanReportInfoVar;

/**
 * All of the event report styles listed in Table 18 are buffered equivalents to their Buffered counterparts in
6.3.9.4.5. One difference is that the scanner buffers data over the reporting interval and sends a single
message at the end of the interval. A second difference is that the same objects and attributes are included
in each report regardless of whether their values have changed.
 * @author lgigante
 *@see EpiCfgScanner_Events
 */
public interface PeriCfgScanner_Events {

	public void Buf_Scan_Report_Var (ScanReportInfoVar sriv);
	public void Buf_Scan_Report_Fixed (ScanReportInfoFixed srif);
	public void Buf_Scan_Report_Grouped (ScanReportInfoGrouped srig);
	public void Buf_Scan_Report_MP_Var (ScanReportInfoMPVar srimpvar);
	public void Buf_Scan_Report_MP_Fixed (ScanReportInfoMPFixed srimpf);
	public void Buf_Scan_Report_MP_Grouped (ScanReportInfoMPGrouped srimpg);
}

package x73.p20601.dim;


import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;
import x73.p20601.ConfigReport;
import x73.p20601.ConfigReportRsp;
import x73.p20601.ScanReportInfoFixed;
import x73.p20601.ScanReportInfoGrouped;
import x73.p20601.ScanReportInfoMPFixed;
import x73.p20601.ScanReportInfoMPGrouped;
import x73.p20601.ScanReportInfoMPVar;
import x73.p20601.ScanReportInfoVar;

public class EpiCfgscanner extends CfgScanner implements EpiCfgScanner_Events{

	private LinkedList<Attribute> attrList;

	public EpiCfgscanner(LinkedList<Attribute> list) throws Exception {
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;

	}
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_SCAN_CFG_EPI;
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
	public void Unbuf_Scan_Report_Var(ScanReportInfoVar sriv) {
		// TODO Auto-generated method stub
		
	}
	public void Unbuf_Scan_Report_Fixed(ScanReportInfoFixed srif) {
		// TODO Auto-generated method stub
		
	}
	public void Unbuf_Scan_Report_Grouped(ScanReportInfoGrouped srig) {
		// TODO Auto-generated method stub
		
	}
	public void Unbuf_Scan_Report_MP_Var(ScanReportInfoMPVar srimpvar) {
		// TODO Auto-generated method stub
		
	}
	public void Unbuf_Scan_Report_MP_Fixed(ScanReportInfoMPFixed srimpf) {
		// TODO Auto-generated method stub
		
	}
	public void Unbuf_Scan_Report_MP_Grouped(ScanReportInfoMPGrouped srimpg) {
		// TODO Auto-generated method stub
		
	}

}

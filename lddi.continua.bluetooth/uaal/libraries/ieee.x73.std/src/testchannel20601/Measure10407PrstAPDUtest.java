package testchannel20601;

public class Measure10407PrstAPDUtest {

	byte[] apdu;
	
	public Measure10407PrstAPDUtest(){
		
		
//		apdu = new byte[]{
//				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
//				(byte)0x00, (byte)0x44, 						//CHOICE.length = 62
//				(byte)0x00, (byte)0x42, 						//OCTET STRING.length = 60
//				(byte)0x00, (byte)0x01, 						//invoke-id = 0x1236 (sequence number)
//				(byte)0x01, (byte)0x01, 						//CHOICE(Remote Operation Invoke | Confirmed Event Report)
//				(byte)0x00, (byte)0x3C, 						//CHOICE.length = 54
//				(byte)0x00, (byte)0x00,  						//obj-handle = 0 (MDS object)
//				(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, //event-time = 0
//				(byte)0x0D, (byte)0x1F, 						//event-type = MDC_NOTI_SCAN_REPORT_FIXED
//				(byte)0x00, (byte)0x32, 						// event-info.length = 44
//				(byte)0xF0, (byte)0x00, 						// ScanReportInfoFixed.data-req-id = 0xF000
//				(byte)0x00, (byte)0x01, 						// ScanReportInfoFixed.scan-report-no = 0
//				(byte)0x00, (byte)0x01, 						// ScanReportInfoFixed.obs-scan-fixed.count = 2
//				(byte)0x00, (byte)0x2A, 						// ScanReportInfoFixed.obs-scan-fixed.length = 36
//				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
//				(byte)0x00, (byte)0x02, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 18
//				(byte)0x00, (byte)0x24, 						//Compound Object count (3 entries)
//				(byte)0x00, (byte)0x01, 						//Compound Object length (6 bytes)
//				(byte)0x00, (byte)0x12, 						//Systolic = 120
//				(byte)0x00, (byte)0x03, 						//Diastolic = 80
//				(byte)0x00, (byte)0x06, 						//MAP = 100
//				(byte)0x00, (byte)0x7E, 						//Systolic = 120
//				(byte)0x00, (byte)0x55, 						//Diastolic = 80
//				(byte)0x00, (byte)0x62, 						//MAP = 100
//				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
//				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,
//				(byte)0x00, (byte)0x02, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 2
//				(byte)0x00, (byte)0x0A,						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 10
//				(byte)0x00, (byte)0x6B, 						//Basic-Nu-Observed-Value = 60.0 (BPM)
//				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
//				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,
//				};
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x3E, 						//CHOICE.length = 62
				(byte)0x00, (byte)0x3C, 						//OCTET STRING.length = 60
				(byte)0x00, (byte)0x54, 						//invoke-id = 0x1236 (sequence number)
				(byte)0x01, (byte)0x01, 						//CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x36, 						//CHOICE.length = 54
				(byte)0x00, (byte)0x00,  						//obj-handle = 0 (MDS object)
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, //event-time = 0
				(byte)0x0D, (byte)0x1D, 						//event-type = MDC_NOTI_SCAN_REPORT_FIXED
				(byte)0x00, (byte)0x2C, 						// event-info.length = 44
				(byte)0xF0, (byte)0x00, 						// ScanReportInfoFixed.data-req-id = 0xF000
				(byte)0x00, (byte)0x00, 						// ScanReportInfoFixed.scan-report-no = 0
				(byte)0x00, (byte)0x02, 						// ScanReportInfoFixed.obs-scan-fixed.count = 2
				(byte)0x00, (byte)0x24, 						// ScanReportInfoFixed.obs-scan-fixed.length = 36
				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
				(byte)0x00, (byte)0x12, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 18
				(byte)0x00, (byte)0x03, 						//Compound Object count (3 entries)
				(byte)0x00, (byte)0x06, 						//Compound Object length (6 bytes)
				(byte)0x00, (byte)0x78, 						//Systolic = 120
				(byte)0x00, (byte)0x50, 						//Diastolic = 80
				(byte)0x00, (byte)0x64, 						//MAP = 100
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,
				(byte)0x00, (byte)0x02, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 2
				(byte)0x00, (byte)0x0A,						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 10
				(byte)0xF2, (byte)0x58, 						//Basic-Nu-Observed-Value = 60.0 (BPM)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

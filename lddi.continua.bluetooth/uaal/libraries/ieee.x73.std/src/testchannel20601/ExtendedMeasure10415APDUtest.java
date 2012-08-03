package testchannel20601;

public class ExtendedMeasure10415APDUtest {

	byte[] apdu;
	
	public ExtendedMeasure10415APDUtest(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x5A, 						//CHOICE.length = 90
				(byte)0x00, (byte)0x58, 						//OCTET STRING.length = 88
				(byte)0x12, (byte)0x36, 						//invoke-id = 0x1236 (sequence number)
				(byte)0x01, (byte)0x01, 						//CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x52, 						//CHOICE.length = 82
				(byte)0x00, (byte)0x00,  						//obj-handle = 0 (MDS object)
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, //event-time = 0
				(byte)0x0D, (byte)0x1D, 						//event-type = MDC_NOTI_SCAN_REPORT_FIXED
				(byte)0x00, (byte)0x48, 						// event-info.length = 72
				(byte)0xF0, (byte)0x00, 						// ScanReportInfoFixed.data-req-id = 0xF000
				(byte)0x00, (byte)0x00, 						// ScanReportInfoFixed.scan-report-no = 0
				(byte)0x00, (byte)0x04, 						// ScanReportInfoFixed.obs-scan-fixed.count = 4
				
				
				(byte)0x00, (byte)0x40, 						// ScanReportInfoFixed.obs-scan-fixed.length = 64
				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
				(byte)0x00, (byte)0x0C, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 12
				(byte)0xFF, (byte)0x00, (byte)0x02, (byte)0xFA, //Simple-Nu-Observed-Value = 76.2 (kg)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,	
				
				(byte)0x00, (byte)0x03, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 3
				(byte)0x00, (byte)0x0C, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 12
				(byte)0xFF, (byte)0x00, (byte)0x00, (byte)0xF3, //Simple-Nu-Observed-Value = 24.3 (kg/m2)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00,	
				
				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
				(byte)0x00, (byte)0x0C, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 12
				(byte)0xFF, (byte)0x00, (byte)0x02, (byte)0xF8, //Simple-Nu-Observed-Value = 76.0 (kg)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T20:05:0000
				(byte)0x20, (byte)0x05, (byte)0x00, (byte)0x00,
				
				(byte)0x00, (byte)0x03, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 3
				(byte)0x00, (byte)0x0C, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 12
				(byte)0xFF, (byte)0x00, (byte)0x00, (byte)0xF2, //Simple-Nu-Observed-Value = 24.2 (kg/m2)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T20:05:0000
				(byte)0x20, (byte)0x05, (byte)0x00, (byte)0x00	
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

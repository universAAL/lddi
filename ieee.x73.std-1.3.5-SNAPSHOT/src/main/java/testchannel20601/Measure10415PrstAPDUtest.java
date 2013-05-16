package testchannel20601;

public class Measure10415PrstAPDUtest {

	byte[] apdu;
	
	public Measure10415PrstAPDUtest(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 						//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x2A, 						//CHOICE.length = 42
				(byte)0x00, (byte)0x28, 						//OCTET STRING.length = 40
				(byte)0x12, (byte)0x36, 						//invoke-id = 0x1236 (sequence number)
				(byte)0x01, (byte)0x01, 						//CHOICE(Remote Operation Invoke | Confirmed Event Report)
				(byte)0x00, (byte)0x22, 						//CHOICE.length = 34
				(byte)0x00, (byte)0x00,  						//obj-handle = 0 (MDS object)
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, //event-time = 0
				(byte)0x0D, (byte)0x1D, 						//event-type = MDC_NOTI_SCAN_REPORT_FIXED
				(byte)0x00, (byte)0x18, 						// event-info.length = 24
				(byte)0xF0, (byte)0x00, 						// ScanReportInfoFixed.data-req-id = 0xF000
				(byte)0x00, (byte)0x00, 						// ScanReportInfoFixed.scan-report-no = 0
				(byte)0x00, (byte)0x01, 						// ScanReportInfoFixed.obs-scan-fixed.count = 1
				(byte)0x00, (byte)0x10, 						// ScanReportInfoFixed.obs-scan-fixed.length = 16
				(byte)0x00, (byte)0x01, 						//ScanReportInfoFixed.obs-scan-fixed.value[0].obj-handle = 1
				(byte)0x00, (byte)0x0C, 						//ScanReportInfoFixed.obs-scan-fixed.value[0]. obs-val-data.length = 12
				(byte)0xFF, (byte)0x00, (byte)0x02, (byte)0xFA, //Simple-Nu-Observed-Value = 76.2 (kg)
				(byte)0x20, (byte)0x07, (byte)0x12, (byte)0x06, //Absolute-Time-Stamp = 2007-12-06T12:10:0000
				(byte)0x12, (byte)0x10, (byte)0x00, (byte)0x00	
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

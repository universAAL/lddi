package testchannel20601;

public class ConfirmMeasurePrstAPDUtest {

	byte[] apdu;
	
	public ConfirmMeasurePrstAPDUtest(){
		
		apdu = new byte[]{
				(byte)0xE7, (byte)0x00, 							//APDU CHOICE Type (PrstApdu)
				(byte)0x00, (byte)0x12, 							//CHOICE.length = 18
				(byte)0x00, (byte)0x10, 							//OCTET STRING.length = 16
				(byte)0x12, (byte)0x36, 							//invoke-id = 0x1236 (mirrored from invocation)
				(byte)0x02, (byte)0x01, 							//CHOICE(Remote Operation Response | Confirmed Event Report)
				(byte)0x00, (byte)0x0A, 							//CHOICE.length = 10
				(byte)0x00, (byte)0x00, 							//obj-handle = 0 (MDS object)
				(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, 	//currentTime = 0
				(byte)0x0D, (byte)0x1D, 							//event-type = MDC_NOTI_SCAN_REPORT_FIXED
				(byte)0x00, (byte)0x00 								//event-reply-info.length = 0
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

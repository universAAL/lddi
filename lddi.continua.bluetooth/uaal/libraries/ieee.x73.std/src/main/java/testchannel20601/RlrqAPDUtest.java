package testchannel20601;

public class RlrqAPDUtest {

	byte[] apdu;
	
	public RlrqAPDUtest(){
		
		apdu = new byte[]{
				(byte)0xE4, (byte)0x00, 	//APDU CHOICE Type (RlrqApdu)
				(byte)0x00 ,(byte)0x02, 	//CHOICE.length = 2
				(byte)0x00, (byte)0x00 		//reason = normal
				};
	}
	
	public byte getByte(int i ){
		return apdu[i]; 
	}
	
	public byte[] getByteArray(){
		return apdu;
	}
	
	
}

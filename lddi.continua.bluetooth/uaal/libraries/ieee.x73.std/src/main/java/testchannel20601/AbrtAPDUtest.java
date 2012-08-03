package testchannel20601;

public class AbrtAPDUtest {

	byte[] apdu;
	
	public AbrtAPDUtest(){
		
		apdu = new byte[]{
				(byte)0xE6, (byte)0x00, 	//APDU CHOICE Type (AbrtApdu)
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

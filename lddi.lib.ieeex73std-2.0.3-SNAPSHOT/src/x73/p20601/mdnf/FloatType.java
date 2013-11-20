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
package x73.p20601.mdnf;

public class FloatType {
	
	
	public final static int NaN 				=  8388607; 	//(2^23)-1 Not a Number
	public final static int NRes 				= -8388608; 	//-(2^23) Not at this Resolution
	public final static int INFINITE_POS 		=  8388606; 	//(2^23)-2
	public final static int INFINITE_NEG 		= -8388606; 	//-((2^23)-2)
	public final static int RESERVED 			= -8388607; 	//-((2^23)-1)
	public final static int MAX_EXP 			=  127; 
	public final static int MIN_EXP 			= -128;
	public final static int MAX_MANTISSA 		=  16777210;	//2((2^23)-3); 
	public final static int MIN_MANTISSA 		= -16777210;	//-2((2^23)-3); 
	
	/*
	 * /___EXPONENT__/___________MANTISSA_______________/
	 *   8 bit (MSB)          24 bit (LSBs)
	 *   
	 *   The number represented is (mantissa)x(10^exponent)
	 */
	private int exponent; // signed 8 bit
	private int mantissa; // signed 24 bit
	
	
	public FloatType(int exp, int man)
	{
		exponent=exp;
		mantissa=man;
	}
	
	public FloatType(long measure) throws Exception {
		
		// get the 4 LSB. The upper 4 are discarded.
		int measureInteger = (int)(measure&0x00000000FFFFFFFF);
		
		int aux_exponent = (measureInteger >> 24); // will be like this: int (4 bytes) 00000000 00000000 00000000 exponent
		int aux_mantissa = (measureInteger & 0x00FFFFFF); // AND operation for extract the mantissa, it will be like this: 00000000 MANTISSA1 MANTISSA2 MANTISSA3
		
		// negative?
		if ((aux_mantissa & 0x00800000)!=0) { // if it has the MSb of the mantissa to one (last 24 bits of the Float)
			aux_mantissa = (aux_mantissa - 16777216);
		}
		
		if(isSpecialFloatValue(aux_mantissa)){
			exponent = 0;
			mantissa = aux_mantissa;
		}else{
			checkLimits(aux_exponent, aux_mantissa);
			exponent = aux_exponent;
			mantissa = aux_mantissa;
		}
	}
	
	public int getExponent()
	{
		return exponent;
	}
	
	public int getMantissa()
	{
		return mantissa;
	}
	
	
	void checkLimits(int exp, int man) throws Exception
	{
		if((exp > MAX_EXP) || (exp < MIN_EXP))
			throw new Exception("Exponent out of bounds for Float-Type");
		if((man > MAX_MANTISSA) || (man < MIN_MANTISSA))
			throw new Exception("Mantissa out of bounds for Float-Type");
	}
	

	
	public double getFloatValue(){
		
		if(isSpecialFloatValue(mantissa))
		{
			return mantissa;
		}
		
		return (mantissa*Math.pow(10, exponent));
		
	}
	
	public boolean isSpecialFloatValue(int mantissa){
		switch (mantissa) {
		case NaN: return true; 
		case NRes: return true;
		case INFINITE_NEG: return true;
		case INFINITE_POS: return true;
		case RESERVED: return true;

		default:
			return false;
		}
	}
	
	
	
	
	

}

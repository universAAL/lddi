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
package org.universAAL.lddi.lib.ieeex73std.x73.p20601.mdnf;

public class SFloatType {
	
	
	public final static short NaN 				=  2047; 	//(2^11)-1 Not a Number
	public final static short NRes 				= -2048; 	//-(2^11) Not at this Resolution
	public final static short INFINITE_POS 		=  2046; 	//(2^11)-2
	public final static short INFINITE_NEG 		= -2046; 	//-((2^11)-2)
	public final static short RESERVED 			= -2047; 	//-((2^11)-1)
	public final static short MAX_EXP 			=  7; 
	public final static short MIN_EXP 			= -8;
	public final static short MAX_MANTISSA 		=  4090;	//2((2^11)-3); 
	public final static short MIN_MANTISSA 		= -4090;	//-2((2^11)-3); 
	
	/*
	 * /___EXPONENT__/___________MANTISSA_______________/
	 * 4 bit (1/2 MSB)     12 bit (1/2 MSB and LSB)
	 *   
	 *   The number represented is (mantissa)x(10^exponent)
	 */
	private short exponent; // signed 4 bit
	private short mantissa; // signed 12 bit
	
	
	public SFloatType(short exp, short man)
	{
		exponent=exp;
		mantissa=man;
	}
	
	public SFloatType(short num) throws Exception
	{
		short aux_exponent = (short) (num >> 12); // will be like this: short (2 bytes)  00000000 0000exponent(4bits)
		short aux_mantissa = (short) (num & 0x0FFF); // AND operation for extract the mantissa, it will be like this: 0000 MANTISSA(4 bits) MANTISSA2
		
		// negative?
		if ((aux_mantissa & 0x0800)!=0) { // if it has the MSb of the mantissa to one (last 12 bits of the SFloat)
			aux_mantissa = (short) (aux_mantissa - 4096);
		}
		
		if(isSpecialFloatValue(aux_mantissa)){
			exponent = 0;
			mantissa = aux_mantissa;
		}else{
			checkConstraint(aux_exponent, aux_mantissa);
			exponent = aux_exponent;
			mantissa = aux_mantissa;
		}
	}
	
	
	public short getExponent()
	{
		return exponent;
	}
	
	public short getMantissa()
	{
		return mantissa;
	}
	
	
	void checkConstraint(short exp, short man) throws Exception
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
	
	public boolean isSpecialFloatValue(short mantissa){
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

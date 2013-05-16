package x73.p104zz;


import java.util.LinkedList;

import org.bn.IDecoder;

import x73.p20601.dim.Attribute;

//Strength Fitness Equipment
//The Strength Fitness Equipment does not have any Standard Configuration


public class p10442Strength extends DeviceSpecialization{

	public LinkedList<Attribute> strengthattributes;

	
	public p10442Strength(IDecoder decoder) throws Exception{
		super(decoder);
		generateStrengtheAttributes();
	}

	private void generateStrengtheAttributes() throws Exception{
		
	}

	
	public String toString(){
		return "Strength fitness equipment";
	}
	
}

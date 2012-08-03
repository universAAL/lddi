package x73.p104zz;


import java.util.LinkedList;

import org.bn.IDecoder;

import x73.p20601.dim.Attribute;

// Activity Hub
// The Activity Hub does not have any Standard Configuration	


public class p10471ActivityHub extends DeviceSpecialization{

	public LinkedList<Attribute> activityhubattributes;

	
	public p10471ActivityHub(IDecoder decoder) throws Exception{
		super(decoder);
		generateActivityHubAttributes();
	}

	private void generateActivityHubAttributes() throws Exception{
		
		
	}
	public String toString(){
		return "Activity Hub";
	}
	
}

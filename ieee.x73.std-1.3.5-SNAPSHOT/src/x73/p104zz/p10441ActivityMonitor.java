package x73.p104zz;


import java.util.LinkedList;

import org.bn.IDecoder;

import x73.p20601.dim.Attribute;

// Cardiovascular fitness and Activity Monitor
//The Activity Monitor does not have any Standard Configuration



public class p10441ActivityMonitor extends DeviceSpecialization{

	public LinkedList<Attribute> activitymonitorattributes;

	
	public p10441ActivityMonitor(IDecoder decoder) throws Exception{
		super(decoder);
		generateActivityMonitorAttributes();
	}

	private void generateActivityMonitorAttributes() throws Exception{
		
			//TODO		
		
	}
	
	public String toString(){
		return "Cardiovascular fitness and activity monitor";
	}
	
}

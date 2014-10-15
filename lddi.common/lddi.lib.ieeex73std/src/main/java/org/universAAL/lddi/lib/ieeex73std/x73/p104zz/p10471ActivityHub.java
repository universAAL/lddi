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
package org.universAAL.lddi.lib.ieeex73std.x73.p104zz;


import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.org.bn.IDecoder;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.dim.Attribute;


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

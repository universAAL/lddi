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
package org.universAAL.lddi.lib.ieeex73std.x73.p20601.dim;



import java.util.Hashtable;
import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.HANDLE;




/**
 * Each personal health device agent is defined by an object-oriented model as shown in Figure 4. The toplevel
object of each agent is instantiated from the MDS class. Each agent has one MDS object. The MDS
represents the identification and status of the agent through its attributes.
 *
 */
public abstract class MDS extends DIM implements MDS_Events, GET_Service{

	public MDS(){	}
	

	public int getNomenclatureCode(){
		return NomenclatureCodes.MDC_MOC_VMS_MDS_SIMP;
	}
	
	
	/*
	 //TODO
	MDS-Data-Request:
		This method allows the manager system to enable or disable measurement data transmission from the
		agent (see 8.9.3.3.3 for a description).
	*/
	public abstract void MDS_Data_Request();
	
	/*
	 // TODO
	Set-Time:
		This method allows the manager system to set a real-time clock (RTC) with the absolute time. The
		agent indicates whether the Set-Time command is valid by using the mds-time-capab-set-clock bit in
		the Mds-Time-Info attribute (see Table 2).
		
	*/
	public abstract void Set_Time();
	
}

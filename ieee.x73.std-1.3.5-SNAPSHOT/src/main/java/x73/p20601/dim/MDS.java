package x73.p20601.dim;



import java.util.Hashtable;
import java.util.LinkedList;


import x73.nomenclature.NomenclatureCodes;
import x73.p20601.HANDLE;


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

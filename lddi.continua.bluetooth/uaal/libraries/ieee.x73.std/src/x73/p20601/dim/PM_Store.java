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
package x73.p20601.dim;


import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;
import x73.p20601.SegmSelection;
import x73.p20601.SegmentInfoList;
import x73.p20601.TrigSegmDataXferReq;
import x73.p20601.TrigSegmDataXferRsp;

/**
 * An instance of the PM-store class provides long-term storage capabilities for metric data. Data are stored in
a variable number of PM-segment objects (see 6.3.8). The stored data of the PM-store object are requested
from the agent by the manager using object access services (see 7.3). Anybody not familiar with the PMstore
concept may wish to read Annex C for a conceptual overview prior to reading the following
subclauses.
 * @author lgigante
 *
 */
public abstract class PM_Store extends DIM implements GET_Service, PM_Store_Events{
	
	private LinkedList<PM_Segment> segmentlist;
	
	
	@Override
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_VMO_PMSTORE;
	}

	/**Clear-Segments:
		This method allows the manager to delete the data currently stored in one or more selected PMsegments.
		All entries in the selected PM-segments are deleted. If the agent supports a variable number
		of PM-segments, the agent may delete empty PM-segments. Additionally, the agent may clear PMsegments
		without direction from the manager (e.g., the user of the agent could choose to delete data
		stored on the agent); however, if doing so while in an Associated state, the Instance-Number shall
		remain empty for the duration of the association. The Instance-Number of all other PM-segments shall
		be unaffected by clearing a segment. If this method is invoked on a PM-segment that has the
		Operational-State attribute set to enabled, the agent shall reply with a not-allowed-by-object error
		(roer) with a return code of MDC_RET_CODE_OBJ_BUSY.
		Note that the behavior of the Clear-Segments method is application specific. The method may remove
		all entries from the specified PM-segment, leaving it empty, or it may remove the defined PM-segment
		completely. This behavior is defined in the PM-Store-Capab attribute. For specific applications,
		recommendations are defined in corresponding device specializations, making use of the PM-store.
	 */
	public abstract void Clear_Segments(SegmSelection segsel);
 
	/**Get-Segment-Info:
		This method allows the manager to retrieve PM-segment attributes of one or more PM-segments, with
		the exception of the Fixed-Segment-Data attribute which contains the actual stored data and is
		retrieved by using the Trig-Segment-Data-Xfer method. In particular, the Get-Segment-Info method
		allows the manager to retrieve the Instance-Number attributes of the PM-segment object instances and
		their data contents.
	*/
	public abstract SegmentInfoList Get_Segment_Info(SegmSelection segsel);
	
	/**Trig-Segment-Data-Xfer:
		This method allows the manager to start the transfer of the Fixed-Segment-Data attribute of a specified
		PM-segment. The agent indicates in the response if it accepts or denies this request. If the agent accepts
		the request, the agent sends Segment-Data-Event messages as described in 6.3.7.5. If this method is
		invoked on a PM-segment that has the Operational-State attribute set to enabled, the agent shall reply
		with a not-allowed-by-object error (roer) with a return code of MDC_RET_CODE_OBJ_BUSY.
	*/
	public abstract TrigSegmDataXferRsp Trig_Segment_Data_Xfer(TrigSegmDataXferReq trigseg);
	

}

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

import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReport;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReportRsp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoMPVar;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoVar;

public interface MDS_Events {

	/**
	 * This event is sent by the agent during the configuring state of startup
	 * if the manager does not already know the agent's configuration from past
	 * associations. The event provides static information about the supported
	 * measurement capabilities of the agent.
	 *
	 */
	public ConfigReportRsp MDS_Configuration_Event(ConfigReport cfgreport);

	/**
	 * This event provides dynamic data (typically measurements) from the agent
	 * for some or all of the objects that the agent supports. Data for reported
	 * objects are reported using a generic attribute list variable format (see
	 * 7.4.5 for details on event report formats). The event is triggered by an
	 * MDS-Data- Request from the manager system, or it is sent as an
	 * unsolicited message by the agent. For agents that support
	 * manager-initiated measurement data transmission, refer to 8.9.3.3.3 for
	 * information on controlling the activation and/or period of the data
	 * transmission. For agents that do not support manager-initiated
	 * measurement data transmission, refer to 8.9.3.3.2 for information on the
	 * limited control a manager can assert.
	 */
	public void MDS_Dynamic_Data_Update_Var(ScanReportInfoVar scanreportinfovar);

	/**
	 * This event provides dynamic data (typically measurements) from the agent
	 * for some or all of the metric objects or the MDS object that the agent
	 * supports. Data are reported in the fixed format defined by the
	 * Attribute-Value-Map attribute for reported metric objects or the MDS
	 * object (see 7.4.5 for details on event report formats). The event is
	 * triggered by an MDS-Data-Request from the manager system (i.e., a
	 * manager-initiated measurement data transmission), or it is sent as an
	 * unsolicited message by the agent (i.e., an agent-initiated measurement
	 * data transmission). For agents that support manager-initiated measurement
	 * data transmission, refer to 8.9.3.3.3 for information on controlling the
	 * activation and/or period of the data transmission. For agents that do not
	 * support manager-initiated measurement data transmission, refer to
	 * 8.9.3.3.2 for information on the limited control a manager can assert.
	 *
	 * @throws Exception
	 */
	public void MDS_Dynamic_Data_Update_Fixed(ScanReportInfoFixed scanreportinfofixed) throws Exception;

	/**
	 * This is the same as MDS-Dynamic-Data-Update-Var, but allows inclusion of
	 * data from multiple persons.
	 */
	public void MDS_Dynamic_Data_Update_MP_Var(ScanReportInfoMPVar scanreportinfompvar);

	/**
	 * This is the same as MDS-Dynamic-Data-Update-Fixed, but allows inclusion
	 * of data from multiple persons.
	 */
	public void MDS_Dynamic_Data_Update_MP_Fixed(ScanReportInfoMPFixed scanreportinfompfixed);

}

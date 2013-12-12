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

import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;


/**
 * An instance of the PM-segment class represents a persistently stored episode of measurement data. A PMsegment
object is not part of the static agent configuration because the number of instantiated PM-segment
instances may dynamically change. The manager accesses PM-segment objects indirectly by methods and
events of the PM-store object.
 *
 */
public class PM_Segment extends Metric {

	
	private LinkedList<Attribute> attrList;

	
	public PM_Segment (LinkedList<Attribute> list) throws Exception{
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;
		

		
	}
	
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_PM_SEGMENT;
	}

	
}

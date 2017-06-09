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
 * A scanner object is an observer and �summarizer� of object attribute values.
 * It observes attributes of metric objects (e.g., numeric objects) and
 * generates summaries in the form of notification event reports. See Figure 5
 * for the class hierarchy of the scanner classes. Each class is described in
 * 6.3.9.2 through 6.3.9.5, respectively.
 *
 */
public abstract class Scanner extends MDS {

	public Scanner() {
	}

	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_SCAN;
	}

}

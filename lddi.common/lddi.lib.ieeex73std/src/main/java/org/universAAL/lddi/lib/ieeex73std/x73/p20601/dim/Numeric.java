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

import java.util.Iterator;
import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;

public class Numeric extends Metric {

	private LinkedList<Attribute> attrList;

	/**
	 * An instance of the numeric class represents a numerical measurement. The
	 * values of a numeric object are sent from the agent to the manager using
	 * the EVENT REPORT service (see 7.3). This class is derived from the metric
	 * base class.
	 */
	public Numeric(LinkedList<Attribute> list) throws Exception {
		if (list.isEmpty() || list == null) {
			throw new Exception("Error: trying to create a empty DIM");
		}
		attrList = list;

	}

	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_VMO_METRIC_NU;
	}

	public Attribute getAttribute(int id) {

		Iterator<Attribute> it = attrList.iterator();
		Attribute attr;
		while (it.hasNext()) {
			attr = (Attribute) it.next();
			if (attr.getAttributeID() == id) {
				return attr;
			}
		}
		return null;

	}

	public boolean hasAttribute(int id) {
		Iterator<Attribute> it = attrList.iterator();
		Attribute attr;
		while (it.hasNext()) {
			attr = (Attribute) it.next();
			if (attr.getAttributeID() == id) {
				return true;
			}
		}
		return false;
	}

}

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

public class Attribute {

	private int id;
	private Object type;

	/*
	 * we need an attribute ID because there are pairs of different attributes,
	 * which use the same class, but not the same ID from the nomenclature.
	 * Without this id, there is no way for differencing them.
	 *
	 * i.e.: MDC_ATTR_SYS_TYPE(TYPE.class) and MDC_ATTR_ID_TYPE(TYPE.class)
	 *
	 * @see utils.ASNUtils auxiliar class.
	 */

	public Attribute(int id, Object type) throws Exception {
		if (id < 0 || id > 65536) {
			throw new Exception("Attribute ID not valid (0-65536): " + id);
		}

		this.id = id;
		this.type = type;
	}

	public int getAttributeID() {
		return this.id;
	}

	public Object getAttributeType() {
		return this.type;
	}

}

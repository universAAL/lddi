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
package org.universAAL.lddi.lib.ieeex73std.measurements;

import org.universAAL.lddi.lib.ieeex73std.x73.p20601.OID_Type;

/**
 * This class is the one who has the Measurement values prepared to be
 * represented.
 * 
 * @author lgigante
 *
 */
public class MeasurementValue {

	private Object time;
	private OID_Type unit;
	private Object value_obj;

	public MeasurementValue(Object timemeasure, OID_Type unit_code, Object measure_value_object) {

		time = timemeasure;
		value_obj = measure_value_object;
		unit = unit_code;

	}

	public Object getTimeObject() {
		return time;
	}

	public OID_Type getOIDType() {
		return unit;
	}

	public Object getMeasureObject() {
		return value_obj;
	}

}

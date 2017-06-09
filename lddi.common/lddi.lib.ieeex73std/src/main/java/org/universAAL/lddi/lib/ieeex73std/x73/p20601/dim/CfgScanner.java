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

import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;

/**
 * The CfgScanner class is an abstract class defining attributes, methods,
 * events, and services that are common for its subclasses. In particular, it
 * defines the communication behavior of a configurable scanner object. As such,
 * it cannot be instantiated.
 *
 */
public abstract class CfgScanner extends Scanner {

	public CfgScanner() {
	}

	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_SCAN_CFG;
	}

}

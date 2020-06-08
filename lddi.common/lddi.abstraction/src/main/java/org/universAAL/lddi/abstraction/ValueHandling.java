/**
 * 
 */
package org.universAAL.lddi.abstraction;

/**
 * @author mtazari
 *
 */
enum ValueHandling {
	changesLocked, nullValue, unknownDatapoint, conversionFailed,
	changeFailed, noInternalChange, intermediateChangeIgnored
}


class ReflectedValue {
	private Object val = null;
	
	ReflectedValue(Object o) {
		val = o;
	}
	
	Object getValue() {
		return (val == ValueHandling.nullValue)? null : val;
	}
}

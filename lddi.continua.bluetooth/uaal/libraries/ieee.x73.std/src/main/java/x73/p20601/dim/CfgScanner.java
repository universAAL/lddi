package x73.p20601.dim;

import x73.nomenclature.NomenclatureCodes;


/**
 * The CfgScanner class is an abstract class defining attributes, methods, events, and services that are common
for its subclasses. In particular, it defines the communication behavior of a configurable scanner object. As
such, it cannot be instantiated.
 *
 */
public abstract class CfgScanner extends Scanner{

	
	public CfgScanner(){}
	
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_SCAN_CFG;
	}
	

}

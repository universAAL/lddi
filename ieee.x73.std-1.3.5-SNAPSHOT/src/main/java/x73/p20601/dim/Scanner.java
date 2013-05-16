package x73.p20601.dim;

import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;


/**
 * A scanner object is an observer and �summarizer� of object attribute values. It observes attributes of metric
objects (e.g., numeric objects) and generates summaries in the form of notification event reports. See Figure
5 for the class hierarchy of the scanner classes. Each class is described in 6.3.9.2 through 6.3.9.5,
respectively.
 *
 */
public abstract class Scanner extends MDS {

	public Scanner(){}
	
	public int getNomenclatureCode(){
		return NomenclatureCodes.MDC_MOC_SCAN;
	}
	

}

package x73.p20601.dim;

import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;

/**
 * An instance of the enumeration class represents status information and/or annotation information. The
values of the enumeration object are coded in the form of normative codes (as defined in ISO/IEEE 11073-
10101 [B12]) or in the form of free text. The values of the enumeration object are sent from the agent to the
manager using the EVENT REPORT service (see 7.3). This class is derived from the metric base class.
 *
 */
public class Enumeration extends Metric{

	private LinkedList<Attribute> attrList;
	
	public Enumeration(LinkedList<Attribute> list) throws Exception {
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;
	}
	
	public int getNomenclatureCode(){
		return NomenclatureCodes.MDC_MOC_VMO_METRIC_ENUM;
	}
	
	

}

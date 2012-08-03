package x73.p20601.dim;

import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;

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

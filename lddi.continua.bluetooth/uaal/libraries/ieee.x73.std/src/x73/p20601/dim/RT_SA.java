package x73.p20601.dim;


import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;




/**
 * An instance of the RT-SA class represents a wave form measurement. The values of the RT-SA object are
sent from the agent to the manager using the EVENT REPORT service (see 7.3). This class is derived from
the metric base class.
 *
 */
public class RT_SA extends Metric{

	private LinkedList<Attribute> attrList;

	
	public RT_SA(LinkedList<Attribute> list) throws Exception {
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;
	}
	
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_VMO_METRIC_SA_RT;
	}

	
	/*
	 * Scale-and-Range-Specification:
		The Scale-and-Range-Specification attribute defines the coefficients for an algorithm to map the scaled
		values into their absolute values. The manager shall apply the following algorithm:
		Y = M � X + B
		where
		Y = the converted absolute value
		M = (upper-absolute-value � lower-absolute-value) / (upper-scaled-value � lower-scaled-value)
		B = upper-absolute-value � (M � upper-scaled-value)
		X = the scaled value
		An example of this algorithm in use can be found in Annex B.
	 */
}

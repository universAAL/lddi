package x73.p20601.dim;

import java.util.Iterator;
import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;

public class Numeric extends Metric{

	private LinkedList<Attribute> attrList;
	
	/**
	 * An instance of the numeric class represents a numerical measurement. The values of a numeric object are
sent from the agent to the manager using the EVENT REPORT service (see 7.3). This class is derived from
the metric base class.
	 */
	public Numeric (LinkedList<Attribute> list) throws Exception{
		if (list.isEmpty() || list == null){
			throw new Exception ("Error: trying to create a empty DIM");
		}
		attrList = list;
		
	}

	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_VMO_METRIC_NU;
	}
	
public Attribute getAttribute (int id){
		
		Iterator<Attribute> it = attrList.iterator();
		Attribute attr;
		while(it.hasNext()){
			attr = (Attribute) it.next();
			if(attr.getAttributeID()==id){
				return attr;
			}
		}
		return null;
		
	}
	
	public boolean hasAttribute(int id){
		Iterator<Attribute> it = attrList.iterator();
		Attribute attr;
		while(it.hasNext()){
			attr = (Attribute) it.next();
			if(attr.getAttributeID()==id){
				return true;
			}
		}
		return false;
	}
	
	
}

package x73.p20601.dim;

import java.util.LinkedList;

import x73.nomenclature.NomenclatureCodes;

/**
 * The metric class is the base class for all objects representing measurements, status, and context data. The
metric class is not instantiated; therefore, it is never part of the agent configuration. As a base class, it
defines all attributes, methods, events, and services that are common for all objects representing
measurements.
 *
 */
public abstract class Metric extends DIM {

	public Metric(){};
	
	@Override
	public int getNomenclatureCode() {
		return NomenclatureCodes.MDC_MOC_VMO_METRIC;
	}

	
}

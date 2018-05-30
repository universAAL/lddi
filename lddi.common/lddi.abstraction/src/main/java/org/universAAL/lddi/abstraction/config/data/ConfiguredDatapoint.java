/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data;

import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.lddi.config.datapoints.Datapoint;
import org.universAAL.ontology.lddi.config.datapoints.DatapointValueType;

/**
 * @author mtazari
 *
 */
class ConfiguredDatapoint implements ExternalDatapoint {
	
	private Datapoint dp;
	private DatapointValueType exVType = null;
	private MergedRestriction inVType = null;
	private ExternalComponent ec = null;
	
	ConfiguredDatapoint(Datapoint dp) {
		this.dp = dp;
	}
	
	public Object getExternalValueType() {
		return exVType;
	}
	
	public MergedRestriction getInternalValueType() {
		return inVType;
	}

	public ExternalComponent getComponent() {
		return ec;
	}
	
	void setComponent(ExternalComponent ec) {
		if (ec != null  &&  this.ec == null)
			this.ec = ec;		
	}
	
	void setExternalValueType(DatapointValueType vt) {
		if (vt != null  &&  exVType == null)
			exVType = vt;			
	}
	
	void setInternalValueType(MergedRestriction vt) {
		if (vt != null  &&  inVType == null)
			inVType = vt;			
	}

	public boolean isReadOnly() {
		return dp.getPullAddress() != null  &&  dp.getSetAddress() == null;
	}

	public boolean isWriteOnly() {
		return dp.getPullAddress() == null
				&&  dp.getPushAddress() == null
				&&  dp.getSetAddress() != null;
	}

	public String getProperty() {
		return dp.getProperty();
	}

	public String getPullAddress() {
		return dp.getPullAddress();
	}

	public String getPushAddress() {
		return dp.getPushAddress();
	}

	public String getSetAddress() {
		return dp.getSetAddress();
	}
	
	int getComponentID() {
		return dp.getComponentID();
	}

	int getTypeID() {
		return dp.getTypeID();
	}
}

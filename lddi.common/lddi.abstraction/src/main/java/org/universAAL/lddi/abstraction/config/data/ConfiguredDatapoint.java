/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data;

import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.ontology.lddi.config.datapoints.Datapoint;

/**
 * @author mtazari
 *
 */
public class ConfiguredDatapoint implements ExternalDatapoint {
	
	private Datapoint dp;
	private ExternalComponent ec = null;
	
	ConfiguredDatapoint(Datapoint dp) {
		this.dp = dp;
	}

	public ExternalComponent getComponent() {
		return ec;
	}
	
	void setComponent(ExternalComponent ec) {
		if (ec != null  &&  this.ec == null)
			this.ec = ec;		
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
	
	public Object getProperty(String prop) {
		return (prop == null)?  null : dp.getProperty(prop);
	}
	
	public void setProperty(String prop, Object value) {
		if (prop != null  &&  value != null)
			dp.setProperty(prop, value);
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
}

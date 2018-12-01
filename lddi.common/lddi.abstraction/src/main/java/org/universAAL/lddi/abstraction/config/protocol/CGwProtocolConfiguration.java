/**
 * 
 */
package org.universAAL.lddi.abstraction.config.protocol;

import java.util.Hashtable;

import org.universAAL.lddi.abstraction.Activator;
import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.middleware.interfaces.configuration.ConfigurableModule;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.scope.AppPartScope;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;

/**
 * @author mtazari
 *
 */
public class CGwProtocolConfiguration implements ConfigurableModule {

	private Hashtable<String, ConfigurationParameter> configurations = new Hashtable<String, ConfigurationParameter>();
	private CommunicationGateway cgw;
	
	public CGwProtocolConfiguration(CommunicationGateway cgw, ConfigurationParameter[] pConfParams) {
		this.cgw = cgw;
		if (pConfParams != null  &&  pConfParams.length > 0) {
			for (int i=pConfParams.length-1;  i>-1;  i--)
				if (pConfParams[i].getScope() instanceof AppPartScope
						&&  cgw.getConfigAppID().equals(((AppPartScope) pConfParams[i].getScope()).getAppID())
						&&  CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID.equals(((AppPartScope) pConfParams[i].getScope()).getPartID())) {
					configurations.put(pConfParams[i].getScope().getId(), pConfParams[i]);
					// System.out.println("CGwProtocolConfiguration->constructor() registered: "+((AppPartScope) pConfParams[i].getScope()).getAppID()+"#"+pConfParams[i].getScope().getId());
				}
			Activator.getConfigManager().register(configurations.values().toArray(new ConfigurationParameter[configurations.size()]), this);
		}
	}

	public synchronized boolean configurationChanged(Scope confParam, Object paramValue) {
		if (!(confParam instanceof AppPartScope)
				||  (!"CommunicationGateway".equals(((AppPartScope) confParam).getAppID())
						&&  !cgw.getConfigAppID().equals(((AppPartScope) confParam).getAppID()))
				||  !CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID.equals(((AppPartScope) confParam).getPartID()))
			return false;
		String id = confParam.getId();
		if (configurations.containsKey(id))
			return cgw.handleProtocolConfParam(this, id, paramValue);
		
		return false;
	}
}

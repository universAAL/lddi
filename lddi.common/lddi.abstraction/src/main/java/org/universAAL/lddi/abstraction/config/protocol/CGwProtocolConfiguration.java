/**
 * 
 */
package org.universAAL.lddi.abstraction.config.protocol;

import java.util.Hashtable;

import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.middleware.interfaces.configuration.ConfigurableModule;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.scope.AppPartScope;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.managers.api.ConfigurationManager;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.rdf.TypeMapper;

/**
 * @author mtazari
 *
 */
public class CGwProtocolConfiguration implements ConfigurableModule {
	
	public static final String CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE = "operationMode";

	public static ConfigurationParameter operationModeParam = null;

	private Hashtable<String, ConfigurationParameter> configurations = new Hashtable<String, ConfigurationParameter>();
	private CommunicationGateway cgw;
	
	public CGwProtocolConfiguration(CommunicationGateway cgw, ConfigurationParameter[] pConfParams, ConfigurationManager confMgr) {
		this.cgw = cgw;
		if (pConfParams != null)
			for (int i=pConfParams.length-1;  i>-1;  i--)
				if (pConfParams[i].getScope() instanceof AppPartScope
						&&  cgw.getConfigAppID().equals(((AppPartScope) pConfParams[i].getScope()).getAppID())
						&&  CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID.equals(((AppPartScope) pConfParams[i].getScope()).getPartID()))
					configurations.put(pConfParams[i].getScope().getId(), pConfParams[i]);
		if (confMgr != null)
			confMgr.register(configurations.values().toArray(new ConfigurationParameter[configurations.size()]), this);
	}
	
	public void registerOperationModeParameter(ConfigurationManager confMgr) {
		if (operationModeParam == null) {
			operationModeParam = CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE, CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID, 
					"If 1, start the tool for address test; if 2, simulate all datapoints. In all other case, actual operation in production.", 
					MergedRestriction.getAllValuesRestrictionWithCardinality(ConfigurationParameter.PROP_CONFIG_LITERAL_VALUE, 
							TypeMapper.getDatatypeURI(Integer.class), 0, 1), CommunicationGateway.OPERATION_MODE_IN_PRODUCTION);
			confMgr.register(new ConfigurationParameter[] { operationModeParam }, this);
		}
	}

	public synchronized boolean configurationChanged(Scope confParam, Object paramValue) {
		if (!(confParam instanceof AppPartScope)
				||  (!"CommunicationGateway".equals(((AppPartScope) confParam).getAppID())
						&&  !cgw.getConfigAppID().equals(((AppPartScope) confParam).getAppID()))
				||  !CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID.equals(((AppPartScope) confParam).getPartID()))
			return false;
		String id = confParam.getId();
		if (CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE.equals(id)) {
			if (String.valueOf(paramValue).equals("1"))
				return cgw.setOperationMode(this, CommunicationGateway.OPERATION_MODE_ADDRESS_TEST);
			if (String.valueOf(paramValue).equals("2"))
				return cgw.setOperationMode(this, CommunicationGateway.OPERATION_MODE_SIMULATION);
			return cgw.setOperationMode(this, CommunicationGateway.OPERATION_MODE_IN_PRODUCTION);
		} else if (configurations.containsKey(id))
			return cgw.handleProtocolConfParam(this, id, paramValue);
		
		return false;
	}
}

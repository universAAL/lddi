package org.universAAL.lddi.abstraction;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.interfaces.configuration.ConfigurableModule;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.scope.AppPartScope;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.managers.api.ConfigurationEditor;
import org.universAAL.middleware.managers.api.ConfigurationManager;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.rdf.TypeMapper;
 
public class Activator implements BundleActivator, ConfigurableModule {
    static ModuleContext context = null;
    public static final Object[] edConverterParams = new Object[] { ExternalDataConverter.class.getName() };
    public static final Object[] cgwSharingParams = new Object[] { CommunicationGateway.class.getName() };
    static ConfigurationManager confMgr;
    static ConfigurationEditor confEditor;
	
	public static final String CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE = "operationMode";
	private static ConfigurationParameter operationModeParam = null;
	private static BundleContext osgiBC = null;
    
    public void start(BundleContext bcontext) throws Exception {
    	Activator.osgiBC = bcontext;
        Activator.context = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { bcontext });

        confMgr = (ConfigurationManager) context.getContainer().fetchSharedObject(context,
                new Object[] { ConfigurationManager.class.getName() });
        confEditor = (ConfigurationEditor) context.getContainer().fetchSharedObject(context,
        		new Object[] { ConfigurationEditor.class.getName() });
        
		operationModeParam = CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE, CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID, 
				"If 1, start the tool for address test; if 2, simulate all datapoints. In all other case, actual operation in production.", 
				MergedRestriction.getAllValuesRestrictionWithCardinality(ConfigurationParameter.PROP_CONFIG_LITERAL_VALUE, 
						TypeMapper.getDatatypeURI(Integer.class), 0, 1), CommunicationGateway.OPERATION_MODE_IN_PRODUCTION);
		confMgr.register(new ConfigurationParameter[] { operationModeParam }, this);
    }
 
    public void stop(BundleContext arg0) throws Exception {
    	// TODO
    }

	public synchronized boolean configurationChanged(Scope confParam, Object paramValue) {
		if (!(confParam instanceof AppPartScope)
				||  !CommunicationGateway.class.getSimpleName().equals(((AppPartScope) confParam).getAppID())
				||  !CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID.equals(((AppPartScope) confParam).getPartID()))
			return false;
		String id = confParam.getId();
		if (CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE.equals(id)) {
			if (String.valueOf(paramValue).equals(Integer.toString(CommunicationGateway.OPERATION_MODE_ADDRESS_TEST)))
				return CommunicationGateway.setOperationMode(CommunicationGateway.OPERATION_MODE_ADDRESS_TEST);
			if (String.valueOf(paramValue).equals(Integer.toString(CommunicationGateway.OPERATION_MODE_SIMULATION)))
				return CommunicationGateway.setOperationMode(CommunicationGateway.OPERATION_MODE_SIMULATION);
			return CommunicationGateway.setOperationMode(CommunicationGateway.OPERATION_MODE_IN_PRODUCTION);
		}
		
		return false;
	}
	
	public static ModuleContext getMC() {
		return context;
	}
	
	public static ConfigurationEditor getConfigEditor() {
		return confEditor;
	}
	
	public static ConfigurationManager getConfigManager() {
		return confMgr;
	}
	
	static Object getSharedObjectRemoveHook(Object o) {
		try {
			ServiceReference[] srs = osgiBC.getServiceReferences(o.getClass().getName(), null);
			for (ServiceReference sr : srs)
				if (osgiBC.getService(sr) == o)
					return sr;
		} catch (Exception e) {}
		return new Object();
	}
}

/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.lddi.abstraction.ExternalComponentDiscoverer;
import org.universAAL.ontology.lddi.config.datapoints.Component;
import org.universAAL.ontology.lddi.config.datapoints.Datapoint;
import org.universAAL.ontology.lddi.config.datapoints.DatapointValueType;
import org.universAAL.middleware.interfaces.configuration.ConfigurableModule;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurableEntityEditor;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurableEntityEditorListener;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurationParameterEditor;
import org.universAAL.middleware.interfaces.configuration.scope.AppPartScope;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.OntologyManagement;
import org.universAAL.middleware.rdf.Resource;

/**
 * @author mtazari
 *
 */
public class CGwDataConfiguration implements ConfigurableModule, ConfigurableEntityEditorListener, ExternalComponentDiscoverer {
	
	public static final String CONF_PARAM_CGW_DATA_COMPONENTS = "components";
	public static final String CONF_PARAM_CGW_DATA_DATAPOINTS = "datapoints";
	public static final String CONF_PARAM_CGW_DATA_VALUE_TYPES = "valueTypes";

	public static ConfigurationParameter[] configurations = { 
			CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_DATA_VALUE_TYPES, CommunicationGateway.CGW_CONF_APP_PART_DATA_ID, "...", 
					MergedRestriction.getAllValuesRestriction(ConfigurationParameter.PROP_CONFIG_VALUE, 
							DatapointValueType.MY_URI), null),
			CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_DATA_COMPONENTS, CommunicationGateway.CGW_CONF_APP_PART_DATA_ID, "...", 
					MergedRestriction.getAllValuesRestriction(ConfigurationParameter.PROP_CONFIG_VALUE, 
							Component.MY_URI), null),
			CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_DATA_DATAPOINTS, CommunicationGateway.CGW_CONF_APP_PART_DATA_ID, "...", 
					MergedRestriction.getAllValuesRestriction(ConfigurationParameter.PROP_CONFIG_VALUE, 
							Datapoint.MY_URI), null)
	};
	
	private CommunicationGateway cgw;
	private Vector<Component> components = new Vector<Component>();
	private Vector<ConfiguredDatapoint> datapoints = new Vector<ConfiguredDatapoint>();
	private Vector<DatapointValueType> valueTypes = new Vector<DatapointValueType>();
	
	public CGwDataConfiguration(CommunicationGateway cgw) {
		this.cgw = cgw;
	}

	public synchronized boolean configurationChanged(Scope confParam, Object paramValue) {
		if (!(confParam instanceof AppPartScope)
				||  !CommunicationGateway.CGW_CONF_APP_ID.equals(((AppPartScope) confParam).getAppID())
				||  !CommunicationGateway.CGW_CONF_APP_PART_DATA_ID.equals(((AppPartScope) confParam).getPartID()))
			return false;
		String id = confParam.getId();
		if (CONF_PARAM_CGW_DATA_COMPONENTS.equals(id)) {
			Vector<Component> validatedPValues = new Vector<Component>();
			if (paramValue instanceof List<?>) {
				for (Object o : (List<?>) paramValue)
					if (o instanceof Component)
						validatedPValues.add((Component) o);
					else
						return false;
			} else if (paramValue instanceof Component) {
				validatedPValues.add((Component) paramValue);
			} else if (paramValue != null)
				return false;
			// the values passed for this conf param are all valid
			// now sort them using their sequence numbers
			int size = validatedPValues.size();
			Component[] carr = new Component[size];
			for (Component c : validatedPValues) {
				int seqNo = c.getSeqNoInConfig();
				if (seqNo < size)
					carr[seqNo] = c;
				else
					return false;
			}
			// ready to accept the new value
			components.clear();
			for (int i=0; i<size; i++)
				components.add(carr[i]);
		} else if (CONF_PARAM_CGW_DATA_DATAPOINTS.equals(id)) {
			Vector<ConfiguredDatapoint> validatedPValues = new Vector<ConfiguredDatapoint>();
			if (paramValue instanceof List<?>) {
				for (Object o : (List<?>) paramValue)
					if (o instanceof Datapoint)
						validatedPValues.add(new ConfiguredDatapoint((Datapoint) o));
					else
						return false;
			} else if (paramValue instanceof Datapoint) {
				validatedPValues.add(new ConfiguredDatapoint((Datapoint) paramValue));
			} else if (paramValue != null)
				return false;
			// ready to accept the new value
			datapoints = validatedPValues;
		} else if (CONF_PARAM_CGW_DATA_VALUE_TYPES.equals(id)) {
			Vector<DatapointValueType> validatedPValues = new Vector<DatapointValueType>();
			if (paramValue instanceof List<?>) {
				for (Object o : (List<?>) paramValue)
					if (o instanceof DatapointValueType)
						validatedPValues.add((DatapointValueType) o);
					else
						return false;
			} else if (paramValue instanceof DatapointValueType) {
				validatedPValues.add((DatapointValueType) paramValue);
			} else if (paramValue != null)
				return false;
			// the values passed for this conf param are all valid
			// now sort them using their sequence numbers
			int size = validatedPValues.size();
			DatapointValueType[] carr = new DatapointValueType[size];
			for (DatapointValueType vt : validatedPValues) {
				int seqNo = vt.getID();
				if (seqNo < size)
					carr[seqNo] = vt;
				else
					return false;
			}
			// ready to accept the new value
			valueTypes.clear();
			for (int i=0; i<size; i++)
				valueTypes.add(carr[i]);
		} else
			return false;
		
		if (isConsistent())
			notifyCGw();
		return true;
	}
	
	private boolean isConsistent() {
		int noOfCs = components.size();
		if (noOfCs > 0  &&  components.get(noOfCs-1).getSeqNoInConfig() == noOfCs-1) {
			int noOfVTs = valueTypes.size();
			if (noOfVTs > 0  &&  valueTypes.get(noOfVTs-1).getID() == noOfVTs-1) {
				for (ConfiguredDatapoint dp : datapoints) {
						if (dp.getTypeID() >= noOfVTs  ||  dp.getComponentID() >= noOfCs)
							return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	private void addComponentDescription(ManagedIndividual mi, Resource r) {
		for (Enumeration<?> e = r.getPropertyURIs();  e.hasMoreElements(); ) {
			Object o = e.nextElement();
			if (o instanceof String  &&  !Resource.PROP_RDF_TYPE.equals(o))
				mi.setProperty((String) o, r.getProperty((String) o));
		}
	}

	private void notifyCGw() {
		if (components.isEmpty())
			return;
		
		// construct the external components and collect them in a list
		Vector<ExternalComponent> constructedECs = new Vector<ExternalComponent>();
		for (Component c : components) {
			ExternalComponent ec = new ExternalComponent(cgw, c.getTypeURI());
			addComponentDescription(ec.getOntResource(), c.getOntDescription());
			// add all data-points referring to this component
			int id = c.getSeqNoInConfig();
			for (ConfiguredDatapoint dp : datapoints) {
				if (dp.getComponentID() == id) {
					dp.setComponent(ec);
					ec.addPropMapping(dp.getProperty(), dp);
				}
			}
			// finish handling this component 
			constructedECs.add(ec);
		}
		
		// resolve value types of data-points
		for (ConfiguredDatapoint dp : datapoints) {
			dp.setExternalValueType(valueTypes.get(dp.getTypeID()));
			dp.setInternalValueType(OntologyManagement.getInstance().getOntClassInfo(dp.getComponent().getTypeURI()).getRestrictionsOnProp(dp.getProperty()));
		}
		
		cgw.replaceComponents(constructedECs, this);
	}

	public void ConfigurationChanged(ConfigurableEntityEditor entityChanged) {
		if (entityChanged instanceof ConfigurationParameterEditor)
		configurationChanged(entityChanged.getScope(), ((ConfigurationParameterEditor) entityChanged).getConfiguredValue());
	}
}

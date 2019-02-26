/**
 * 
 */
package org.universAAL.lddi.abstraction.config.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.universAAL.lddi.abstraction.Activator;
import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.lddi.abstraction.ExternalComponentDiscoverer;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.ontology.lddi.config.datapoints.Component;
import org.universAAL.ontology.lddi.config.datapoints.Datapoint;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.interfaces.configuration.ConfigurableModule;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurableEntityEditor;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurationParameterEditor;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.ApplicationPartPattern;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.ApplicationPattern;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.EntityPattern;
import org.universAAL.middleware.interfaces.configuration.scope.AppPartScope;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.rdf.Resource;
// import org.universAAL.middleware.serialization.MessageContentSerializer;

/**
 * @author mtazari
 *
 */
public class CGwDataConfiguration implements ConfigurableModule, /*ConfigurableEntityEditorListener,*/ ExternalComponentDiscoverer {
	
	public static final String CONF_PARAM_CGW_DATA_COMPONENTS = "components";
	public static final String CONF_PARAM_CGW_DATA_DATAPOINTS = "datapoints";

	public static ConfigurationParameter[] configurations = { 
			CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_DATA_COMPONENTS, CommunicationGateway.CGW_CONF_APP_PART_DATA_ID, "...", 
					MergedRestriction.getAllValuesRestriction(ConfigurationParameter.PROP_CONFIG_OBJECT_VALUE, 
							Component.MY_URI), null),
			CommunicationGateway.newCGwConfParam(CONF_PARAM_CGW_DATA_DATAPOINTS, CommunicationGateway.CGW_CONF_APP_PART_DATA_ID, "...", 
					MergedRestriction.getAllValuesRestriction(ConfigurationParameter.PROP_CONFIG_OBJECT_VALUE, 
							Datapoint.MY_URI), null)
	};
	
	private CommunicationGateway cgw;
	private Vector<Component> components = new Vector<Component>();
	private Vector<ConfiguredDatapoint> datapoints = new Vector<ConfiguredDatapoint>();
	/**
	 * Used in {@link #configurationChanged} with bit #0 for {@link #CONF_PARAM_CGW_DATA_COMPONENTS} and bit #1 for {@link #CONF_PARAM_CGW_DATA_DATAPOINTS}.
	 */
	private int paramsBitPattern = 0;
	private boolean ignoreOnce = false;
	
	public CGwDataConfiguration(CommunicationGateway cgw) {
		this.cgw = cgw;
	}

	public synchronized boolean configurationChanged(Scope confParam, Object paramValue) {
		if (!(confParam instanceof AppPartScope)
				||  !cgw.getConfigAppID().equals(((AppPartScope) confParam).getAppID())
				||  !CommunicationGateway.CGW_CONF_APP_PART_DATA_ID.equals(((AppPartScope) confParam).getPartID()))
			return false;
		String id = confParam.getId();
		if (CONF_PARAM_CGW_DATA_COMPONENTS.equals(id)  &&  paramValue != components) {
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
				if (seqNo > -1  &&  seqNo < size)
					carr[seqNo] = c;
				else {
					LogUtils.logWarn(cgw.getOwnerContext(), getClass(), "configurationChanged", "Ignoring " + c.getOntDescription()+" with an out-of-bound seqNo equal to "+seqNo);
					return false;
				}
			}
			// ready to accept the new value
			components.clear();
			for (int i=0; i<size; i++)
				components.add(carr[i]);
			if ((paramsBitPattern & 1) == 0)
				paramsBitPattern++;
		} else if (CONF_PARAM_CGW_DATA_DATAPOINTS.equals(id)) {
			if (ignoreOnce) {
				ignoreOnce = false;
				return true;
			}
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
			if ((paramsBitPattern & 2) == 0)
				paramsBitPattern += 2;
		} else
			return false;
		
		if (paramsBitPattern == 3  &&  isConsistent()) {
			notifyCGw();
			paramsBitPattern = 0;
		}
		return true;
	}
	
	private boolean isConsistent() {
		int noOfCs = components.size();
		if (noOfCs > 0  &&  components.get(noOfCs-1).getSeqNoInConfig() == noOfCs-1) {
			boolean[] check = new boolean[noOfCs];
			for (ConfiguredDatapoint dp : datapoints) {
				int id = dp.getComponentID();
				if (id >= noOfCs)
					return false;
				else
					check[id] = true;
			}
			for (int i = 0;  i < noOfCs;  i++)
				if (!check[i])
					return false;
			return true;
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
			ExternalComponent ec = new ExternalComponent(cgw, c.getOntDescription(), c.getExternalTypeSystem().getURI());
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
		
		cgw.addComponents(constructedECs, this);
	}

	public void saveComponents(Collection<List<ExternalComponent>> colOfLists) {
		components.clear();
		ArrayList<Datapoint> dps = new ArrayList<Datapoint>(datapoints.size());
		for (List<ExternalComponent> l : colOfLists)
			for (ExternalComponent ec : l) {
				int seqNo = -1;
				for (Enumeration<ExternalDatapoint> e = ec.enumerateDatapoints(); e.hasMoreElements();) {
					ExternalDatapoint edp = e.nextElement();
					if (edp instanceof ConfiguredDatapoint) {
						Datapoint dp = ((ConfiguredDatapoint) edp).dp;
						int compID = dp.getComponentID();
						if (compID > -1) {
							if (seqNo == -1)
								seqNo = compID;
							else if (compID != seqNo)
								LogUtils.logWarn(cgw.getOwnerContext(), getClass(), "saveComponents", "Ignoring datapoint that belongs to a component with a different seqNo!");
						} else
							LogUtils.logWarn(cgw.getOwnerContext(), getClass(), "saveComponents", "Ignoring datapoint tha tbelongs to a component with a different seqNo!");
						dps.add(dp);
					}
				}
				if (seqNo < 0)
					LogUtils.logWarn(cgw.getOwnerContext(), getClass(), "saveComponents()", "Ignoring component without datapoint!");
				else {
					Component c = new Component();
					c.setProperty(Component.PROP_CONFIG_SEQ_NO, seqNo);
					c.setProperty(Component.PROP_DESCRIPTION, ec.getOntResource());
					c.setProperty(Component.PROP_EXTERNAL_TYPE_SYSTEM, ec.getExternalTypeSystem());
					components.add(c);
				}
			}
		
//		Resource dummy = new Resource("urn:debug#dummy");
//		dummy.setProperty("urn:debug#dps", dps);
//		dummy.setProperty("urn:debug#cs", components);
//		MessageContentSerializer serializer = (MessageContentSerializer) Activator.context.getContainer().fetchSharedObject(Activator.context,
//				new Object[] { MessageContentSerializer.class.getName() });
//		System.out.println(serializer.serialize(dummy));
		
		List<EntityPattern> patterns = new ArrayList<EntityPattern>();
		patterns.add(new ApplicationPattern(cgw.getConfigAppID()));
		patterns.add(new ApplicationPartPattern(CommunicationGateway.CGW_CONF_APP_PART_DATA_ID));
		List<ConfigurableEntityEditor> configs = Activator.getConfigEditor().getMatchingConfigurationEditors(patterns, Locale.ENGLISH);
		try {
			for (ConfigurableEntityEditor configParam : configs) {
				String id = ((ConfigurationParameterEditor) configParam).getScope().getId();
				if (CONF_PARAM_CGW_DATA_COMPONENTS.equals(id))
					((ConfigurationParameterEditor) configParam).setValue(components);
				else if (CONF_PARAM_CGW_DATA_DATAPOINTS.equals(id)) {
					ignoreOnce = true;
					((ConfigurationParameterEditor) configParam).setValue(dps);
				}
			}
		} catch (Exception e) {
			LogUtils.logError(cgw.getOwnerContext(), getClass(), "saveComponents", e.getMessage());
		}
	}
}

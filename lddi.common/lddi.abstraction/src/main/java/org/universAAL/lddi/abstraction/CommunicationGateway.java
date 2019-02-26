/*
	Copyright 2013-2016 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institute for Computer Graphics Research

	See the NOTICE file distributed with this work for additional
	information regarding copyright ownership

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	  http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.lddi.abstraction;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.universAAL.lddi.abstraction.config.data.CGwDataConfiguration;
import org.universAAL.lddi.abstraction.config.protocol.CGwProtocolConfiguration;
import org.universAAL.lddi.abstraction.config.tool.DatapointIntegrationScreener;
import org.universAAL.lddi.abstraction.simulation.SimulationTool;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.interfaces.configuration.configurationDefinitionTypes.ConfigurationParameter;
import org.universAAL.middleware.interfaces.configuration.scope.Scope;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.rdf.Resource;

/**
 * A gateway providing a bridge to a network of external components making it
 * for {@link ComponentIntegrator component integrators} possible to gain access
 * to related {@link ExternalDatapoint external datapoints}.
 *
 * For a better understanding, please refer to both the package documentation
 * and the documentation of the methods further below.
 */
public abstract class CommunicationGateway {
	
	private static String CGW_CONF_APP_ID = "lddi.abstract.CommunicationGateway"; 
	static final Hashtable<String, ExternalDataConverter> edConverters = new Hashtable<String, ExternalDataConverter>();
	
	public static final String CGW_CONF_APP_PART_DATA_ID = "dataConfParams"; 
	public static final String CGW_CONF_APP_PART_PROTOCOL_ID = "protocolConfParams";

	public static final int OPERATION_MODE_IN_PRODUCTION = 0;
	public static final int OPERATION_MODE_ADDRESS_TEST = 1;
	public static final int OPERATION_MODE_SIMULATION = 2;
	
	public static final short DEFAULT_AUTO_PULL_INTERVAL = 15;
	
	public static boolean isShorterAutoPullInterval(short first, short second) {
		if (first < 1)
			first = DEFAULT_AUTO_PULL_INTERVAL;
		if (second < 1)
			second = DEFAULT_AUTO_PULL_INTERVAL;
		return first < second;
	}
	
	public static ConfigurationParameter newCGwConfParam(final String id, final String appPartID, final String description, final MergedRestriction type, final Object defaultVal) {
		return new ConfigurationParameter() {

			public Object getDefaultValue() {
				return defaultVal;
			}

			public String getDescription(Locale loc) {
				return description;
			}

			public Scope getScope() {
				return Scope.applicationPartScope(id, ((id == Activator.CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE)?
						CommunicationGateway.class.getSimpleName() : CGW_CONF_APP_ID), appPartID);
			}

			public MergedRestriction getType() {
				return type;
			}
		};
	}
	
	private class Subscription {
		private ExternalDatapoint datapoint;
		private int simulationInterval = -1;
		private HashSet<ComponentIntegrator> subscribers = new HashSet<ComponentIntegrator>(3);
		private Object value, lastValueWritten = null;
		private int meanDelayMilliseconds;
		boolean noChangeReported;
		
		Subscription(ExternalDatapoint dp) {
			datapoint = dp;
			if (dp.getPushAddress() == null) {
				if (dp.getPullAddress() == null)
					throw new RuntimeException("The datapoint is write only!");
				simulationInterval = dp.getAutoPullWaitSeconds();
				if (simulationInterval < 1)
					simulationInterval = DEFAULT_AUTO_PULL_INTERVAL;
				meanDelayMilliseconds = simulationInterval * 500; // the half --> mean value
			} else
				meanDelayMilliseconds = dp.getPushDeadSeconds() * 500; // the half --> mean value
			value = readValue(dp);
			noChangeReported = (value == null);
		}
		
		void addSubscriber(ComponentIntegrator ci) {
			subscribers.add(ci);
		}
		
		void eventTicker(long ticker, final long timestamp) {
			if (simulationInterval > 0  &&  ticker % simulationInterval == 0)
				new Thread (new Runnable() { 
					public void run() { 
						Subscription.this.notifySubscribers(
								CommunicationGateway.this.readValue(Subscription.this.datapoint),
								timestamp); 
					}
				}).start();
		}
		
		boolean isSubscribed(ComponentIntegrator ci) {
			return subscribers.contains(ci);
		}
		
		void reflect(Object value) {
			lastValueWritten = value;
		}
		
		private void notifySubscribers(Object value, long timestamp) {
			boolean isReflected = false;
			if (lastValueWritten != null) {
				isReflected = lastValueWritten.equals(value);
				lastValueWritten = null;
			}

			ExternalComponent ec = datapoint.getComponent();
			String propURI = datapoint.getProperty();
			long actualOccurrenceTime = 0, meanOccurrentTime = 0;
			if (meanDelayMilliseconds > 0)
				if (timestamp > 0)
					meanOccurrentTime = timestamp - meanDelayMilliseconds;
				else
					meanOccurrentTime = System.currentTimeMillis() - meanDelayMilliseconds;
			else
				actualOccurrenceTime = timestamp;
			
			if (value == null) {
				if (this.value == null) {
					if (!noChangeReported) {
						noChangeReported = true;
						for (ComponentIntegrator ci : subscribers)
							ci.propertyStoppedToChange(ec.getOntResource(), propURI, isReflected, actualOccurrenceTime, meanOccurrentTime);
					}
					return;
				}
			} else if (value.equals(this.value)) {
				if (!noChangeReported) {
					noChangeReported = true;
					for (ComponentIntegrator ci : subscribers)
						ci.propertyStoppedToChange(ec.getOntResource(), propURI, isReflected, actualOccurrenceTime, meanOccurrentTime);
				}
				return;
			}
			
			this.value = value;
			value = ec.changeProperty(propURI, value);
			if (value == Resource.RDF_EMPTY_LIST) {
				LogUtils.logWarn(owner, getClass(), "notifySubscribers", "Setting the external value '"+this.value+"' for "+propURI+" of "+ec.getOntResource().getLocalName()+" failed --> Ignored!");
				return;
			}

			if (dpIntegrationScreener != null)
				dpIntegrationScreener.publish(ec.getOntResource(), propURI, value);

			
			if (this.value == null)
				for (ComponentIntegrator ci : subscribers)
					ci.propertyDeleted(ec.getOntResource(), propURI, isReflected, actualOccurrenceTime, meanOccurrentTime);
			else
				for (ComponentIntegrator ci : subscribers)
					ci.propertyChanged(ec.getOntResource(), propURI, value, isReflected, actualOccurrenceTime, meanOccurrentTime);
		}
	}
	
	// private String componentURIprefix;
	// map typeURI to list of components of that type
	private Hashtable<String, List<ExternalComponent>> discoveredComponents = new Hashtable<String, List<ExternalComponent>>();
	/**
	 * @see #addDiscoverer(ExternalComponentDiscoverer)
	 */
	private HashSet<ExternalComponentDiscoverer> discoverers = new HashSet<ExternalComponentDiscoverer>(3);
	
	// private long eventingSimulationTicker = 0;
	private static DatapointIntegrationScreener dpIntegrationScreener = null;
	private static SimulationTool simulationTool = null;
	
	// remember which integrators are interested in which types of components
	private Hashtable<String, ArrayList<ComponentIntegrator>> registeredIntegrators = new Hashtable<String, ArrayList<ComponentIntegrator>>();
	
	/**
	 * To be used in the following methods:
	 * 
	 * @see #startEventing(ComponentIntegrator, ExternalComponent, byte)
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte)
	 * @see #startEventing(ComponentIntegrator, String, String, byte)
	 * @see #stopEventing(ComponentIntegrator, ExternalDatapoint)
	 */
	private Hashtable<String, Subscription> subscriptions = new Hashtable<String, Subscription>();
	
	private CGwDataConfiguration dataConf = null;
	private CGwProtocolConfiguration protocolConf = null;
	
	ExternalComponent getEC(ManagedIndividual mi) {
		if (mi == null)
			return null;
		
		List<ExternalComponent> ecs = discoveredComponents.get(mi.getClassURI());
		if (ecs == null)
			return null;
		
		for (ExternalComponent ec : ecs)
			if (ec.getOntResource() == mi)
				return ec;
		
		return null;
	}
	
	public void addComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  !components.isEmpty()  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// group by type
				Hashtable<String, ArrayList<ExternalComponent>> news = new Hashtable<String, ArrayList<ExternalComponent>>();
				for (ExternalComponent ec : components) {
					String type = ec.getTypeURI();
					ArrayList<ExternalComponent> newECs = news.get(type);
					if (newECs == null) {
						newECs = new ArrayList<ExternalComponent>();
						news.put(type, newECs);
					}
					newECs.add(ec);
				}
				
				for (String type : news.keySet()) {
					List<ExternalComponent> ecs = discoveredComponents.get(type);
					if (ecs == null) {
						ecs = new ArrayList<ExternalComponent>();
						discoveredComponents.put(type,  ecs);
					}
					ecs.addAll(news.get(type));
					
					List<ComponentIntegrator> cis = registeredIntegrators.get(type);
					if (cis != null)
						for (ComponentIntegrator ci : cis)
							ci.componentsAdded(news.get(type));
				}
				
				if (dpIntegrationScreener != null)
					dpIntegrationScreener.integrateComponents(components);
				else if (simulationTool != null)
					simulateDatapoints(components);
			}
		}
	}
	
	public String getConfigAppID() {
		return CGW_CONF_APP_ID;
	}
	
	public final void saveComponentsConfiguration() {
		dataConf.saveComponents(discoveredComponents.values());
	}
	
	/**
	 * Only subclasses can introduce discoverers, which just serves as a sort of "registered certificate".
	 * Calling "component discovery methods" ({@link #addComponents(List, ExternalComponentDiscoverer) addComponents},
	 * {@link #removeComponents(List, ExternalComponentDiscoverer) removeComponents}, {@link #replaceComponents(List,
	 * ExternalComponentDiscoverer) replaceComponents} and {@link #updateComponents(List, ExternalComponentDiscoverer)
	 * updateComponents}) is only possible, if such a previously "registered certificate" is passed to those methods.
	 * 
	 * @param d
	 */
	protected final void addDiscoverer(ExternalComponentDiscoverer d) {
		if (d != null)
			discoverers.add(d);
	}
	
//	String getComponentURIprefix() {
//		return componentURIprefix;
//	}
	
	protected final ExternalDatapoint getSubscribedDatapoint(String address) {
		return subscriptions.get(address).datapoint;
	}
	
	/**
	 * Subclasses must make sure to read the value of the external data-point at the given <code>pullAddress</code>
	 * and convert it from the original <code>externalType</code> to the target <code>internalType</code> before
	 * returning the value object.
	 * @param pullAddress
	 * @param externalType
	 * @param internalType
	 * @return
	 */
	protected abstract Object getValue(String pullAddress);
	
	private ModuleContext owner = null;
	public ModuleContext getOwnerContext() {
		return owner;
	}
	
	/**
	 * 
	 * @param mc               The {@link ModuleContext module context} of your communication gateway.
	 * @param cgwSharingParams This is needed for sharing your communication gateway within the current container so that
	 *                         {@link ComponentIntegrator component integrator}s can find it. If your communication gateway
	 *                         runs on top of OSGi, the value to pass here should be <code>"new Object[]
	 *                         {CommunicationGateway.class.getName()}"</code>. 
	 * @param confMgr If not null, it means that you want to use the standard configuration mechanism implemented by this
	 *                abstract framework. In your code under OSGi, you can get the confMgr by 
	 *                <code>"(ConfigurationManager) mc.getContainer().fetchSharedObject(mc,
	 *				  new Object[] { ConfigurationManager.class.getName() })"</code>, where "mc" is assumed to be your ModuleContext.
	 *				  <br /><b>Note:</b>The interface "ConfigurationManager" has been defined by the artifact "mw.managers.api.core"
	 *				  from the "org.universAAL.middleware" group.
	 */
	public final void init(ModuleContext mc, boolean needsEventingSimulation, boolean useStandardDataConfig, ConfigurationParameter[] pConfParams) {
		owner = mc;
		CGW_CONF_APP_ID = getClass().getSimpleName();
		protocolConf = new CGwProtocolConfiguration(this, pConfParams);
		
		if (edConverters.isEmpty()) {
			Object[] converters = mc.getContainer().fetchSharedObject(mc, Activator.edConverterParams, null);
			if (converters != null)
				for (Object converter : converters)
					if (converter instanceof ExternalDataConverter)
						edConverters.put(((ExternalDataConverter) converter).getExternalTypeSystemURI(), (ExternalDataConverter) converter);
		}
				
		if (useStandardDataConfig) {
			dataConf = new CGwDataConfiguration(this);
			addDiscoverer(dataConf);
			
			Activator.confMgr.register(CGwDataConfiguration.configurations, dataConf);
		}
		
		if (needsEventingSimulation)
			new Thread(new Runnable() {
				public void run() {
					long now = System.currentTimeMillis();
					long eventingSimulationTicker = (now / 1000) + 1;
					long actionTime = eventingSimulationTicker * 1000;
					synchronized (CommunicationGateway.this) {
						while (true) {
							// wait for one second
							try {
								while (actionTime > now) {
									CommunicationGateway.this.wait(actionTime-now);
									now = System.currentTimeMillis();
								}
							} catch (Exception e) {
								RuntimeException re = new RuntimeException("An Exception has been caught (see 'Caused by'). This is just to print the stack trace for logging purposes.", e);
								re.printStackTrace();
							}

							for (Enumeration<Subscription> e=CommunicationGateway.this.subscriptions.elements(); e.hasMoreElements();)
								e.nextElement().eventTicker(eventingSimulationTicker, actionTime);
							
							eventingSimulationTicker++;
							actionTime += 1000;
						}
					}
				}	
			}, "lddi.abstraction.cgw.auto.pull").start();

		mc.getContainer().shareObject(mc, this, Activator.cgwSharingParams);
	}
	
	/**
	 * Subclasses should call this method in order to pass an external event to universAAL environment.
	 * The value is expected to be of a type defined in the underlying ontological model; this is why 
	 * the expected target type is passed to the subclass as the last parameter in {@link #subscribe(String, Object, MergedRestriction)}.
	 * However, if the subclass does not need an own management of individual subscriptions and hence has an empty implementation of
	 * {@link #subscribe(String, Object, MergedRestriction)} without memorizing the type info, it my use {@link #getSubscribedDatapoint(String)}
	 * and then fetch the type info by calling {@link ExternalDatapoint#getExternalValueType()} and{@link ExternalDatapoint#getInternalValueType()}. 
	 * @param address
	 * @param value
	 */
	protected final synchronized void notifySubscribers(String address, Object value, long actualOccurrenceTime) {
		if (address == null)
			return;
		Subscription s = subscriptions.get(address);
		// System.out.println(">>>>>>> "+getClass().getSimpleName()+"->notifySubscribers(): "+s+" found for "+address);
		if (s != null)
			s.notifySubscribers(value, actualOccurrenceTime);
	}
	
	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * get the value of a given property of a given external component by
	 * specifying the related external datapoint. Note that this implies that
	 * the external components made known by gateways to integrators must
	 * include the property mapping to datapoints.
	 */
	synchronized Object readValue(ExternalDatapoint datapoint) {
		if (datapoint != null) {
			ExternalComponent ec = datapoint.getComponent();
			return (simulationTool == null)?  getValue(datapoint.getPullAddress())
					: ec.converter.exportValue(ec.getTypeURI(), datapoint.getProperty(),
							simulationTool.getDatapointValue(datapoint));
		}
		return null;
	}
	
	/**
	 * To be used by {@link ComponentIntegrator component integrators} to
	 * register to this gateway for external components of the given type. The
	 * implementation must (1) add the given integrator to the list of
	 * integrators interested in the given type of components, and (2) call
	 * {@link ComponentIntegrator#processComponents( ExternalComponent[]) the
	 * related notification method} of the integrator with the list of
	 * components of the given type, both for those already known at the time of
	 * registration and at any time in future when new components of the same
	 * type are added to the external network.
	 */
	void register(String componentTypeURI, ComponentIntegrator integrator) {
		synchronized (discoverers) {
			ArrayList<ComponentIntegrator> integrators = registeredIntegrators.get(componentTypeURI);
			if (integrators == null) {
				integrators = new ArrayList<ComponentIntegrator>();
				registeredIntegrators.put(componentTypeURI, integrators);
			}
			integrators.add(integrator);
			
			List<ExternalComponent> components = discoveredComponents.get(componentTypeURI);
			if (components != null)
				integrator.componentsAdded(components);
		}
	}

	public void removeComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}
	
//	public void replaceComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
//		if (components != null  &&  discoverers.contains(discoverer)) {
//			synchronized (discoverers) {
//				discoveredComponents.clear();
//				for (ExternalComponent ec : components) {
//					String type = ec.getTypeURI();
//					ArrayList<ExternalComponent> ecs = discoveredComponents.get(type);
//					if (ecs == null) {
//						ecs = new ArrayList<ExternalComponent>();
//						discoveredComponents.put(type,  ecs);
//					}
//					ecs.add(ec);
//				}
//				// notify the registered component integrators
//				if (dpIntegrationScreener != null)
//					dpIntegrationScreener.integrateComponents(components.toArray(new ExternalComponent[components.size()]));
//				else if (simulationTool != null)
//					simulateDatapoints();
//				for (Iterator<Entry<String, ArrayList<ComponentIntegrator>>> i = registeredIntegrators.entrySet().iterator(); i.hasNext();) {
//					Entry<String, ArrayList<ComponentIntegrator>> entry = i.next();
//					ArrayList<ExternalComponent> ecs = discoveredComponents.get(entry.getKey());
//					ExternalComponent[] ecArr = ecs.toArray(new ExternalComponent[ecs.size()]);
//					for (ComponentIntegrator ci : entry.getValue())
//						ci.componentsReplaced(ecArr);
//				}
//			}
//		}
//	}
	
	/**
	 * Subclasses must make sure to convert the passed input <code>value</code> from the original <code>internalType</code> to the target <code>externalType</code> before
	 * sending the value to the external system under <code>setAddress</code>.
	 * @param setAddress
	 * @param value
	 * @param externalType
	 * @param internalType
	 */
	protected abstract void setValue(String setAddress, Object value);
	
	/**
	 * Serves as means for subscribing for events related to the changes of the
	 * value of any property of the given external component. Compared to
	 * {@link #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it
	 * wildcards all datapoints within the scope of the given external
	 * component.
	 *
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte)
	 */
	void startEventing(ComponentIntegrator integrator, ExternalComponent component) {
		if (component == null)
			return;
		
		for (ExternalDatapoint dp : component.datapoints())
			startEventing(integrator, dp);
	}

	/**
	 * <p>
	 * Serves as means for subscribing for events related to the changes of the
	 * value of a given property of a given external component by specifying the
	 * related external datapoint. Note that this implies that the external
	 * components made known by gateways to integrators must include the
	 * property mapping to datapoints.
	 * </p>
	 *
	 * <p>
	 * To be used by {@link ComponentIntegrator component integrators} to inform
	 * this gateway about the interest to be notified as soon as the value of
	 * the given external datapoint changes. The implementation must (1) add the
	 * given integrator to the list of integrators interested in the same kind
	 * of events, and (2) call
	 * {@link ComponentIntegrator#processEvent(ExternalDatapoint, Object) the
	 * related notification method} of the integrator both with the current
	 * value at the time of registration and at any time in future when the
	 * value changes.
	 * </p>
	 *
	 * @param integrator
	 *            the integrator registing for notification.
	 * @param datapoint
	 *            the datapoint whose changes of values must fire events.
	 * @param intervalSeconds
	 *            needed when the external communication protocol doesn't
	 *            support "real-time" eventing and hence the gateway has to
	 *            implement the eventing mechanism by pulling the value every n
	 *            seconds and check if it has changed or not. In that case, this
	 *            parameter indicates the related preference of the integrator.
	 * @return The current value of the datapoint, unless<ul><li>no subscription was possible, then Float.NaN will be returned.</li>
	 *         <li>If this is a redundant subscription, then null will be returned.<7li></ul> 
	 */
	Object startEventing(ComponentIntegrator integrator, ExternalDatapoint datapoint) {
		if (integrator == null  ||  datapoint == null)
			return Float.NaN;
		
		String subscriptionKey = getDPsubscriptionKey(datapoint);
		if (subscriptionKey == null)
			return Float.NaN;
		
		Subscription s = subscriptions.get(subscriptionKey);
		if (s == null) {
			s = new Subscription(datapoint);
			subscriptions.put(subscriptionKey, s);
			if (s.simulationInterval < 0)
				subscribe(subscriptionKey);
		} else if (s.isSubscribed(integrator))
			// signal that you have already been subscribed 
			return null;

		s.addSubscriber(integrator);
		// System.out.println(">>>>>>> "+getClass().getSimpleName()+"->startEventing(): "+integrator.getClass().getSimpleName()+" subscribed for "+subscriptionKey);
		return s.value;
	}

	/**
	 * If the given property URI is not null, serves as means for subscribing
	 * for events related to the changes of the value of the given property of
	 * any external component of the given type; otherwise, with propURI ==
	 * null, it can be used to subscribe for the changes of the value of any
	 * property of any external component of the given type. Compared to
	 * {@link #startEventing(ComponentIntegrator, ExternalDatapoint, byte)}, it
	 * wildcards all datapoints within the scope of all external components of
	 * the given type, either by selecting those that correspond to a given
	 * property or not.
	 *
	 * @see #startEventing(ComponentIntegrator, ExternalDatapoint, byte)
	 */
	void startEventing(ComponentIntegrator integrator, String componentTypeURI, String propURI) {
		List<ExternalComponent> ecs = (componentTypeURI == null)? null
				: discoveredComponents.get(componentTypeURI);
		if (ecs == null  ||  ecs.isEmpty())
			return;
		
		if (propURI == null)
			for (ExternalComponent ec : ecs)
				startEventing(integrator, ec);
		else
			for (ExternalComponent ec : ecs)
				startEventing(integrator, ec.getDatapoint(propURI));
	}

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * unsubscribe a subscription done previously.
	 *
	 * @param eventingID
	 *            the ID returned previously when calling
	 *            {@link #startEventing( ComponentIntegrator, ExternalDatapoint, byte)}
	 *            or any of the wildcarding versions of it.
	 */
	void stopEventing(ComponentIntegrator integrator, ExternalDatapoint datapoint) {
		String subscriptionKey = getDPsubscriptionKey(datapoint);
		if (subscriptionKey == null)
			return;
		
		Subscription s = subscriptions.get(subscriptionKey);
		if (s == null)
			return;
		
		s.subscribers.remove(integrator);
		if (s.subscribers.isEmpty())
			subscriptions.remove(subscriptionKey);
	}

	/**
	 * If the subclass subscribes anyhow for all datapoints, then the implementation of this method can be just empty!
	 * @param pushAddress
	 * @param externalType Values linked with the given <code>pushAddress</code> and passed in the original related events from the external system 
	 *                     are expected to be of this type.
	 * @param internalType Values linked with the given <code>pushAddress</code> and to be passed when calling {@link #notifySubscribers(String, Object)} have to be of this type.
	 */
	protected abstract void subscribe(String pushAddress);

	public void updateComponents(List<ExternalComponent> components, ExternalComponentDiscoverer discoverer) {
		if (components != null  &&  discoverers.contains(discoverer)) {
			synchronized (discoverers) {
				// To-Do
			}
		}
	}

	/**
	 * {@link ComponentIntegrator component integrators} can use this method to
	 * change the value of a given property of a given external component by
	 * specifying the related external datapoint and the new value. Note that
	 * this implies that the external components made known by gateways to
	 * integrators must include the property mapping to datapoints.
	 */
	synchronized void writeValue(ExternalDatapoint datapoint, Object value) {
		if (datapoint != null) {
			ExternalComponent ec = datapoint.getComponent();
			if (simulationTool == null)
				setValue(datapoint.getSetAddress(), value);
			else
				simulationTool.setDatapointValue(datapoint,
						ec.converter.importValue(value, ec.getTypeURI(), datapoint.getProperty()));
			
			String sk = getDPsubscriptionKey(datapoint);
			if (sk != null) {
				Subscription s = subscriptions.get(sk);
				if (s != null)
					s.reflect(value);
			}
		}
	}
	
	private static String getDPsubscriptionKey(ExternalDatapoint dp) {
		String s = dp.getPushAddress();
		return (s == null)? dp.getPullAddress() : s;
	}
	
	static final synchronized boolean setOperationMode(int mode) {
		switch (mode) {
		case OPERATION_MODE_IN_PRODUCTION:
			setSimulationMode(false);
			setAddressTestMode(false);
			break;
		case OPERATION_MODE_ADDRESS_TEST:
			setSimulationMode(false);
			setAddressTestMode(true);
			break;
		case OPERATION_MODE_SIMULATION:
			setSimulationMode(true);
			setAddressTestMode(false);
			break;
		default:
			return false;
		}
		return true;
	}

	private static final void setAddressTestMode(boolean isAddressTestMode) {
		if (isAddressTestMode == (dpIntegrationScreener == null)) {
			if (dpIntegrationScreener == null) {
				dpIntegrationScreener = new DatapointIntegrationScreener();
				dpIntegrationScreener.showTool();
				for (CommunicationGateway cgw : getAllCGws())
					for (List<ExternalComponent> ecs : cgw.discoveredComponents.values())
						dpIntegrationScreener.integrateComponents(ecs);
			} else {
				dpIntegrationScreener.stop();
				dpIntegrationScreener = null;
			}
		}
	}
	
	private static CommunicationGateway[] getAllCGws() {
		Object[] cgws = Activator.context.getContainer().fetchSharedObject(Activator.context, Activator.cgwSharingParams, null);
		if (cgws == null)
			return new CommunicationGateway[0];
		
		CommunicationGateway[] result = new CommunicationGateway[cgws.length];
		for (int i=cgws.length-1; i>-1; i--)
			result[i] = (CommunicationGateway) cgws[i];
		return result;
	}

	private static final void setSimulationMode(boolean isSimulationMode) {
		if (isSimulationMode == (simulationTool == null)) {
			if (simulationTool == null) {
				simulationTool = new SimulationTool();
				for (CommunicationGateway cgw : getAllCGws())
					for (List<ExternalComponent> ecs : cgw.discoveredComponents.values())
						simulateDatapoints(ecs);
				simulationTool.setVisible(true);
			} else {
				simulationTool.setVisible(false);
				simulationTool.dispose();
				simulationTool = null;
			}
		}
	}
	
	private static void simulateDatapoints(List<ExternalComponent> components) {
		for (ExternalComponent ec : components)
			for (ExternalDatapoint dp : ec.datapoints()) {
				Hashtable<Object, URL> altValues = ec.enumerateAltValues(dp);
				if (altValues != null  &&  !altValues.isEmpty()) {
					URL iconURL = altValues.get(ExternalDataConverter.NON_DISCRETE_VALUE_TYPE);
					if (iconURL == null)
						simulationTool.addDataPoint(dp, altValues, ec.getInitialValue(dp));
					else
						simulationTool.addDataPoint(dp, ec.isPercentage(dp), iconURL, ec.getInitialValue(dp));
				}
			}
	}
	
	protected final boolean isSimulationMode() {
		return simulationTool != null;
	}
	
	protected abstract void switchOperationMode(int mode);
	
	public static final void handleSimulatedEvent(SimulationTool st, ExternalDatapoint dp, Object value) {
		if (dp != null  &&  st != null  &&  st == simulationTool) {
			ExternalComponent ec = dp.getComponent();
			for (CommunicationGateway cgw : getAllCGws())
				cgw.notifySubscribers(getDPsubscriptionKey(dp),
						ec.converter.exportValue(ec.getTypeURI(), dp.getProperty(), value), 0);
		}
	}

	public final boolean handleProtocolConfParam(CGwProtocolConfiguration protocolConf, String id, Object paramValue) {
		// System.out.println("CommunicationGateway->handleProtocolConfParam() conf param: "+CGW_CONF_APP_ID+"#"+id+"="+paramValue);
		return (this.protocolConf == null  ||  protocolConf == this.protocolConf)?
				protocolConfParamChanged(id, paramValue)
				: false;
	}
	
	protected abstract boolean protocolConfParamChanged(String id, Object paramValue);

}
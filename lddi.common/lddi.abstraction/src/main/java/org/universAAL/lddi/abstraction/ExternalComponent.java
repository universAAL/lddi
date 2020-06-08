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

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.middleware.util.ResourceUtil;
import org.universAAL.ontology.lddi.config.datapoints.ExternalTypeSystem;
import org.universAAL.ontology.location.Location;
import org.universAAL.ontology.phThing.PhysicalThing;

/**
 * <p>
 * An external component is a hard- or software component that is outside the
 * universAAL network and can be reached via a {@link CommunicationGateway
 * gateway}.
 * </p>
 *
 * <p>
 * Instances of this class should be created only by instances of
 * {@link CommunicationGateway}. The gateway that have created an instance can
 * be fetched by calling {@link #getGateway()}, which is important for the
 * instances of {@link ComponentIntegrator} when they are notified by gateways
 * about the existence of external components.
 * </p>
 *
 * <p>
 * To support gateways in mapping the ontological way of "addressing" in
 * universAAL to the kind of addressing in the external network, this class
 * provides the methods {@link #addPropMapping(String, ExternalDatapoint)} and
 * {@link #getDatapoint(String)} in terms of mapping ontological properties to
 * read- and writable datapoints. Gateways are expected to use this class as
 * means for grouping external datapoints that correspond to a distinct
 * component of a distinct type. Then, in each external component, all
 * properties mapped to the selected group of external datapoints must be
 * defined for that type in the used ontology. This type must be set by gateways
 * and can be fetched by calling {@link #getTypeURI()}. Thinking in terms of
 * devices, this means that all properties must belong to a single device whose
 * type is returned by {@link #getTypeURI()}.
 * </p>
 *
 */
public final class ExternalComponent {
	private CommunicationGateway gw;
	private ManagedIndividual ontResource;
	ExternalDataConverter converter;
	private Hashtable<String, ExternalDatapoint> propMappings = new Hashtable<String, ExternalDatapoint>();
	
	// objects used for the synchronization between read/write actions
	private Hashtable<String, Object> changeLocks = new Hashtable<String, Object>();
	private Hashtable<String, Object> ignoredChanges = new Hashtable<String, Object>();
	private Hashtable<String, Object> valuesOverwritten = new Hashtable<String, Object>();
	
	private void ignoreChanges(String prop) {
		synchronized (changeLocks) {
			while (changeLocks.contains(prop)) {
				try { changeLocks.wait(); } catch (Exception e) {}
			}
			changeLocks.put(prop, ValueHandling.changesLocked);
			if (!valuesOverwritten.contains(prop)) {
				Object o = ontResource.getProperty(prop);
				valuesOverwritten.put(prop, (o == null)? ValueHandling.nullValue : o);
			}
		}
	}
	
	private boolean isChangeBlocked(String prop) {
		return changeLocks.contains(prop);
	}
	
	private void acceptChanges(String prop, boolean restored) {
		synchronized (changeLocks) {
			Object lastIgnoredVal = ignoredChanges.remove(prop);
			if (restored) {
				Object valueOverwritten = valuesOverwritten.remove(prop);
				if (lastIgnoredVal != null) {
					if (lastIgnoredVal == ValueHandling.nullValue)
						lastIgnoredVal = null;
										
					if (ontResource.changeProperty(prop, lastIgnoredVal)) {
						if (valueOverwritten == ValueHandling.nullValue)
							valueOverwritten = null;
						if (!areEqual(lastIgnoredVal, valueOverwritten))
							// we must now notify the subscribers about the change of the value because they still assume 'valueOverwritten'
							gw.catchUpNotification(propMappings.get(prop), valueOverwritten);
					}
				}
			}
			changeLocks.remove(prop);
			changeLocks.notifyAll();
		}
	}

	/**
	 * The constructor has been made 'protected' in order to force communication
	 * gateways to create own subclasses so that each communication gateway can
	 * create only instances defined by itself.
	 */
	public ExternalComponent(CommunicationGateway gw, ManagedIndividual description, String externaltypeSystem) {
		if (gw == null  ||  description == null  ||  externaltypeSystem == null)
			throw new NullPointerException("ExternalComponent constructor: parameter null!");

		this.gw = gw;
		ontResource = description;
		converter = CommunicationGateway.edConverters.get(externaltypeSystem);
	}
	
	public ExternalTypeSystem getExternalTypeSystem() {
		for (String uri : CommunicationGateway.edConverters.keySet())
			if (CommunicationGateway.edConverters.get(uri) == converter)
				return ExternalTypeSystem.getLocallyRegisteredInstanceByURI(uri);
		return null;
	}

	public void addPropMapping(String propURI, ExternalDatapoint datapoint) {
		if (propURI == null || datapoint == null)
			return;

		propMappings.put(propURI, datapoint);
	}
	
	Object changeProperty(String propURI, Object value) {
		ExternalDatapoint edp = propMappings.get(propURI);
		if (edp == null)
			return ValueHandling.unknownDatapoint;
		
		synchronized (edp) {
			Object newVal = converter.importValue(value, getTypeURI(), propURI);
			if (newVal == null  &&  value != null) {
				StringBuffer sb = new StringBuffer(512);
				sb.append("Conversion of ").append(value).append(" for ");
				ResourceUtil.addResource2SB(ontResource, sb);
				sb.append("->").append(propURI).append(" failed!");
				LogUtils.logWarn(gw.getOwnerContext(), getClass(), "changeProperty", sb.toString());
				return ValueHandling.conversionFailed;
			}

			Object currVal = ontResource.getProperty(propURI);
			if (isChangeBlocked(propURI)) {
				// in this case, the current value is actually reflecting a required change that is not confirmed yet
				// but the change is waiting either for an amount of time to elapse or for a notification from here
				// we can send the notification if the received event confirms that the expected value is now effective
				if (areEqual(newVal, currVal)) {
					Object valueOverwritten = valuesOverwritten.remove(propURI);
					edp.notifyAll();
					return new ReflectedValue(valueOverwritten);
				} else {
					ignoredChanges.put(propURI, (newVal == null)? ValueHandling.nullValue : newVal);
					return ValueHandling.intermediateChangeIgnored;
				}
			} else {
				Object valueOverwritten = valuesOverwritten.remove(propURI);
				if (areEqual(newVal, currVal))
					if (valueOverwritten != null)
						return new ReflectedValue(valueOverwritten);
					else
						return ValueHandling.noInternalChange;
			}
			
			// in success case, the newValue is anyhow set for the ontResource
			// --> in that case, return the old value in order to comply with the protocol for notifying subscribes
			// in fail case, we signal this by returning the empty list
			return (ontResource.changeProperty(propURI, newVal))?  currVal
					:  ValueHandling.changeFailed;
		}
	}
	
	public String currentValueAsString(String propURI) {
		return converter.toString(getTypeURI(), propURI, ontResource.getProperty(propURI));
	}
	
	Collection<ExternalDatapoint> datapoints() {
		return propMappings.values();
	}
	
	Hashtable<Object, URL> enumerateAltValues(ExternalDatapoint dp) {
		try {
			return (dp == null  ||  dp.getComponent() != this)?  null
					: converter.getAlternativeValues(ontResource.getClassURI(), dp.getProperty());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public Enumeration<ExternalDatapoint> enumerateDatapoints() {
		return propMappings.elements();
	}

	public Enumeration<String> enumerateProperties() {
		return propMappings.keys();
	}
	
	public String getComponentURI() {
		return ontResource.getURI();
	}
	
	public ExternalDatapoint getDatapoint(String propURI) {
		return propMappings.get(propURI);
	}
	
	public String getExternalValue(String propURI, String pullAddress) {
		if (propURI == null  ||  pullAddress == null)
			return null;
		
		return converter.toString(getTypeURI(), propURI, converter.importValue(gw.getValue(pullAddress), getTypeURI(), propURI));
	}
	
	public CommunicationGateway getGateway() {
		return gw;
	}
	
	Object getInitialValue(ExternalDatapoint dp) {
		return (dp == null  ||  dp.getComponent() != this)?  null
				: converter.getInitialValue(ontResource.getClassURI(), dp.getProperty());
	}
	
	public Location getLocation() {
		if (ontResource instanceof PhysicalThing)
			return getLocation((PhysicalThing) ontResource);
		return null;
	}
	
	private Location getLocation(PhysicalThing phTh) {
		Location l = phTh.getLocation();
		if (l == null) {
			Object o = phTh.getProperty(PhysicalThing.PROP_PART_OF);
			if (o instanceof PhysicalThing)
				l = getLocation((PhysicalThing) o);
			if (l == null) {
				o = phTh.getProperty(PhysicalThing.PROP_IS_IN);
				if (o instanceof PhysicalThing)
					l = getLocation((PhysicalThing) o);
			}
		}
		return l;	
	}
	
	public ManagedIndividual getOntResource() {
		return ontResource;
	}
	
	public Object getPropertyValue(String propURI) {
		if (propURI == null)
			return null;
		ExternalDatapoint edp = propMappings.get(propURI);
		if (edp == null)
			return ontResource.getProperty(propURI);
		Object value = converter.importValue(gw.readValue(edp), getTypeURI(), propURI);
		ontResource.changeProperty(propURI, value);
		return value;
	}
	
	public String getTypeURI() {
		return ontResource.getClassURI();
	}

	public String internalValueAsString(String propURI, Object value) {
		return converter.toString(getTypeURI(), propURI, value);
	}
	
	public Object internalValueOf(String propURI, String valStr) {
		return converter.valueOf(valStr, getTypeURI(), propURI);
	}
	
	boolean isPercentage(ExternalDatapoint dp) {
		return (dp == null  ||  dp.getComponent() != this)?  false
				: converter.isPercentage(ontResource.getClassURI(), dp.getProperty());
	}
	
	public void setDatapointValue(String propURI, String setAddress, String value) {
		if (propURI != null  &&  setAddress != null) {
			Object o = converter.exportValue(getTypeURI(), propURI,
					converter.valueOf(value, getTypeURI(), propURI));
			if (o != null)
				gw.setValue(setAddress, o);
		}
	}
	
	public boolean setLocation(String locURI) {
		if (locURI != null  &&  ontResource instanceof PhysicalThing) {
			((PhysicalThing) ontResource).setLocation(new Location(locURI));
			return true;
		}
		return false;
	}
	
	public boolean setPropertyValue(String propURI, Object value) {
		return setPropertyValue(propURI, value, 0);
	}
	
	public boolean setPropertyValue(String propURI, Object value, int msDelay) {
		if (propURI == null)
			return false;

		boolean succeeded;
		ExternalDatapoint edp = propMappings.get(propURI);
		if (edp == null)
			succeeded = ontResource.changeProperty(propURI, value);
		else {
			synchronized (edp) {
				ignoreChanges(propURI);
				succeeded = ontResource.changeProperty(propURI, value);
				if (succeeded) {
					Object exValue = converter.exportValue(getTypeURI(), propURI, value);
					gw.writeValue(edp, exValue);
					if (msDelay > 0) {
						// in this case, it takes time until the change takes effect
						long now = System.currentTimeMillis();
						long endWait = now + msDelay;
						while (msDelay > 0) {
							// wait for the write to take effect
							try { 
								edp.wait(msDelay);
								msDelay = 0;
							} catch (Exception e) {
								now = System.currentTimeMillis();
								msDelay = (int) (endWait - now);
							}
						}
					}
				}
				acceptChanges(propURI, !succeeded);
			}
		}

		if (!succeeded) {
			StringBuffer sb = new StringBuffer(512);
			sb.append("Setting ").append(value).append(" for the datapoint ");
			ResourceUtil.addResource2SB(ontResource, sb);
			sb.append("->").append(propURI).append(" failed!");
			LogUtils.logWarn(gw.getOwnerContext(), getClass(), "setPropertyValue", sb.toString());
		}
		
		return succeeded;
	}
	
	boolean areEqual(Object o1, Object o2) {
		if (o1 == o2)
			return true;
		
		if ((o1 == null  &&  o2 != null)
				|| (o2 == null  &&  o1 != null)
				||  !o1.getClass().equals(o2.getClass()))
			return false;
		
		if (o1.getClass().isArray()) {
			int n = Array.getLength(o1);
			if (Array.getLength(o2) != n)
				return false;
			for (int i=0; i<n; i++)
				if (!areEqual(Array.get(o1, i), Array.get(o2, i)))
					return false;
			return true;
		} else if (o1 instanceof List<?>) {
			int n = ((List<?>) o1).size();
			if (((List<?>) o2).size() != n)
				return false;
			for (int i=0; i<n; i++)
				if (!areEqual(((List<?>) o1).get(i), ((List<?>) o2).get(i)))
					return false;
			return true;
		}
		
		return o1.equals(o2);
	}
}

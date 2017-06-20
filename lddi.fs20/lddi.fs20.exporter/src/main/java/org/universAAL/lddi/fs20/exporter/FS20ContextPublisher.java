/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
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

package org.universAAL.lddi.fs20.exporter;

import org.universAAL.lddi.fs20.devicemodel.FS20Device;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.activityhub.UsageSensor;
import org.universAAL.ontology.activityhub.UsageSensorEvent;
import org.universAAL.ontology.device.MotionSensor;

/**
 * Sends context events to uAAL context bus.
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20ContextPublisher {

	ModuleContext mc;

	// Default context publisher
	private ContextPublisher cp;
	// Context provider info (provider type)
	ContextProvider cpInfo = new ContextProvider();

	public static final String FS20_SERVER_NAMESPACE = Resource.NAMESPACE_PREFIX + "FS20Manager.owl#";

	/**
	 * Constructor.
	 * 
	 * @param mc
	 * @param fs20Manager
	 */
	public FS20ContextPublisher(ModuleContext mc) { // , FS20Manager fs20Manager

		this.mc = mc;

		// prepare for context publishing
		ContextProvider info = new ContextProvider(FS20_SERVER_NAMESPACE + "FS20ContextPublisher");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
	}

	/**
	 * Public context event to uAAL context bus
	 * 
	 * @param fs20device
	 *            = the modified device
	 * @param value
	 *            = the value of the modified device
	 */
	public void publishContextEvent(FS20Device fs20device, int value) {
		boolean val = false;

		if (fs20device != null) {
			switch (fs20device.getDeviceType()) {
			case FS20FMS:
				UsageSensor us = new UsageSensor(fs20device.getDeviceURI());
				if (value > 0)
					val = true;
				else
					val = false;
				us.setProperty(UsageSensor.PROP_HAS_VALUE,
						val ? UsageSensorEvent.USAGE_STARTED : UsageSensorEvent.USAGE_ENDED);
				cp.publish(new ContextEvent(us, UsageSensor.PROP_HAS_VALUE));
				break;
			case FS20PIRx:
				MotionSensor ms = new MotionSensor(fs20device.getDeviceURI());
				if (value > 0)
					val = true;
				else
					val = false;
				ms.setProperty(MotionSensor.PROP_HAS_VALUE, val);
				cp.publish(new ContextEvent(ms, MotionSensor.PROP_HAS_VALUE));
				break;
			}
		} else {
			LogUtils.logError(mc, FS20ContextPublisher.class, "publishKnxEvent", new Object[] { "Device is null!" },
					null);
			return;
		}
	}

}

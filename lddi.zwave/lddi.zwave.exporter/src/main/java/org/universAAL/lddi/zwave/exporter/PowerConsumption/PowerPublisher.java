/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
package org.universAAL.lddi.zwave.exporter.PowerConsumption;

import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.device.DimmerSensor;

public class PowerPublisher {

	private ContextPublisher cp;
	ContextProvider info = new ContextProvider();
	ModuleContext mc;
	public final static String NAMESPACE = "http://tsbtecnologias.es/PowerConsumptionPublisher#";

	public PowerPublisher(BundleContext context) {
		System.out.print("New Publisher\n");
		info = new ContextProvider("http://www.tsbtecnologias.es/ContextProvider.owl#ZWaveEventPublisher");
		mc = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { context });
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
	}

	public void publishPowerConsumption(String name, int value) {
		DimmerSensor ds = new DimmerSensor(NAMESPACE + name);
		ds.setValue(value);
		System.out.print("Publishing Power Values for " + NAMESPACE + name + " = " + ds.getValue() + "\n");
		LogUtils.logTrace(mc, getClass(), "publishPowerConsumption",
				"Publishing Power Values for " + NAMESPACE + name + " = " + ds.getValue());
		cp.publish(new ContextEvent(ds, DimmerSensor.PROP_HAS_VALUE));
	}

}

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
package org.universAAL.lddi.zwave.exporter.MotionContact;

import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.activityhub.MotionSensorEvent;
import org.universAAL.ontology.activityhub.MotionSensor;
import org.universAAL.ontology.activityhub.ContactClosureSensorEvent;
import org.universAAL.ontology.activityhub.ContactClosureSensor;
import org.universAAL.ontology.location.Location;

public class MotionContactSensorPublisher {

	private ContextPublisher cp;
	ContextProvider info = new ContextProvider();
	ModuleContext mc;
	public final static String NAMESPACE = "http://tsbtecnologias.es/MotionSensorPublisher#";

	public MotionContactSensorPublisher(BundleContext context) {
		System.out.print("New Publisher\n");
		info = new ContextProvider(
				"http://www.tsbtecnologias.es/ContextProvider.owl#ZWaveEventPublisher");
		mc = uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context });
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
	}

	public void publishMotionDetection(String message) {
		System.out.println("THA MESSAGE-------------------->>>" + message);
		String[] veraResponse = message.split(" ");

		if (veraResponse[0].compareTo("Motion") == 0) {
			String msURL = NAMESPACE + veraResponse[0];
			System.out.println("MOTION EVENT PROCESSING:"+veraResponse[0]+" "+veraResponse[1]+" "+veraResponse[2]);
			MotionSensorEvent mse = null;
			if (veraResponse[1].equalsIgnoreCase("start")) {
				mse = MotionSensorEvent.motion_detected;
				System.out.println("MOTION EVENT START");
			} else {
				mse = MotionSensorEvent.no_condition_detected;
				System.out.println("MOTION EVENT STOP");
			}

			MotionSensor ms = new MotionSensor(msURL);
			ms.setMeasuredValue(mse);
			ms.setLocation(new Location(NAMESPACE
					+ "ZWaveMotionDetectorLocation", veraResponse[2]));

			System.out.print("Publishing motion\n");
			cp.publish(new ContextEvent(ms, MotionSensor.PROP_MEASURED_VALUE));
		} else if (veraResponse[0].compareTo("Contact") == 0) {
			String msURL = NAMESPACE + veraResponse[1];
			ContactClosureSensorEvent cce;
			if (veraResponse[1].equalsIgnoreCase("DoorOpen")) {
				cce = ContactClosureSensorEvent.contact_opened;
			} else {
				cce = ContactClosureSensorEvent.contact_closed;
			}

			ContactClosureSensor cc = new ContactClosureSensor(msURL);
			cc.setLocation(new Location(NAMESPACE
					+ "ZWaveContactClosureLocation", veraResponse[2]));
			cc.setMeasuredValue(cce);
			System.out.print("Publishing contact\n");
			cp.publish(new ContextEvent(cc,
					ContactClosureSensor.PROP_MEASURED_VALUE));
		}
	}
}

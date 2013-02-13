package org.universAAL.lddi.hw.simulator.activityhub;

import java.util.Random;

import org.osgi.service.log.LogService;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.owl.TypeURI;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.ontology.activityhub.ActivityHubSensor;
import org.universAAL.ontology.activityhub.ActivityHubSensorEvent;
import org.universAAL.ontology.activityhub.ContactClosureSensor;
import org.universAAL.ontology.activityhub.ContactClosureSensorEvent;
import org.universAAL.ontology.activityhub.MotionSensor;
import org.universAAL.ontology.activityhub.MotionSensorEvent;
import org.universAAL.ontology.activityhub.SwitchSensor;
import org.universAAL.ontology.activityhub.SwitchSensorEvent;
import org.universAAL.ontology.activityhub.TemperatureSensor;
import org.universAAL.ontology.activityhub.TemperatureSensorEvent;
import org.universAAL.ontology.activityhub.UsageSensor;
import org.universAAL.ontology.activityhub.UsageSensorEvent;
import org.universAAL.ontology.activityhub.factory.ActivityHubEventFactory;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class AHContextPublisherSimulator {

	// define namespace URIs
	public static final String ACTIVITYHUB_SERVER_NAMESPACE = 
		Resource.uAAL_NAMESPACE_PREFIX + "ActivityHubServer.owl#";

	private DefaultContextPublisher cp;
	private AHSimulator ahSimulator;
	private LogService logger;
	private Random randomGenerator = new Random();
	private int interval;

	public AHContextPublisherSimulator(ModuleContext mc, AHSimulator ahSimulator) {
		this.ahSimulator = ahSimulator;
		this.logger = this.ahSimulator.getLogger();

		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				ACTIVITYHUB_SERVER_NAMESPACE + "AHContextPublisherSimulator");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(providedEvents());
		cp = new DefaultContextPublisher(mc, info);

		this.logger.log(LogService.LOG_INFO,
				"Activated ActivityHub ContextEvent Patterns");

		startSimulation();
	}

	private static ContextEventPattern[] providedEvents() {
		// look at sample server...
		// which events (subjects, predicates, objects) ???

		// ahManager controls only ActivityHub Sensors

		// subject: ActivityHubSensor
		// predicate: PROP_MEASURED_VALUE from phTing.Sensor
		// object: ActivityHubSensorEvent

		// the following is to say that the subject of my context events is
		// always one single instance of ActivityHubSensor
		MergedRestriction subjectRestriction = MergedRestriction
				.getAllValuesRestrictionWithCardinality(
						ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
								ActivityHubSensor.MY_URI, false), 1, 1);

		// the event is always about the change of measured value
		MergedRestriction predicateRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
						ActivityHubSensor.PROP_MEASURED_VALUE);

		// the reported value will always be of type ActivityHubSensorEvent
		// MergedRestriction objectRestriction = MergedRestriction
		// .getAllValuesRestrictionWithCardinality(
		// ContextEvent.PROP_RDF_OBJECT,
		// new TypeURI(ActivityHubSensorEvent.MY_URI, false)
		// , 1, 1);

		ContextEventPattern cep1 = new ContextEventPattern();
		cep1.addRestriction(subjectRestriction);
		cep1.addRestriction(predicateRestriction);
		// cep1.addRestriction(objectRestriction);


		// Motion Sensor
		subjectRestriction = MergedRestriction.getAllValuesRestrictionWithCardinality(
				ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
						MotionSensor.MY_URI, false), 1, 1);
		
		predicateRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
				MotionSensor.PROP_MEASURED_VALUE);

		ContextEventPattern cep2 = new ContextEventPattern();
		cep2.addRestriction(subjectRestriction);
		cep2.addRestriction(predicateRestriction);

		// ContactClosureSensor
		subjectRestriction = MergedRestriction.getAllValuesRestrictionWithCardinality(
				ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
						ContactClosureSensor.MY_URI, false), 1, 1);
		
		predicateRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
				ContactClosureSensor.PROP_MEASURED_VALUE);

		ContextEventPattern cep3 = new ContextEventPattern();
		cep3.addRestriction(subjectRestriction);
		cep3.addRestriction(predicateRestriction);


		// UsageSensor
		subjectRestriction = MergedRestriction.getAllValuesRestrictionWithCardinality(
				ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
						UsageSensor.MY_URI, false), 1, 1);
		
		predicateRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
				UsageSensor.PROP_MEASURED_VALUE);

		ContextEventPattern cep4 = new ContextEventPattern();
		cep4.addRestriction(subjectRestriction);
		cep4.addRestriction(predicateRestriction);


		// SwitchSensor
		subjectRestriction = MergedRestriction.getAllValuesRestrictionWithCardinality(
				ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
						SwitchSensor.MY_URI, false), 1, 1);
		
		predicateRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
				SwitchSensor.PROP_MEASURED_VALUE);

		ContextEventPattern cep5 = new ContextEventPattern();
		cep5.addRestriction(subjectRestriction);
		cep5.addRestriction(predicateRestriction);

		// TemperatureSensor
		subjectRestriction = MergedRestriction.getAllValuesRestrictionWithCardinality(
				ContextEvent.PROP_RDF_SUBJECT, new TypeURI(
						TemperatureSensor.MY_URI, false), 1, 1);
		
		predicateRestriction = MergedRestriction.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
				TemperatureSensor.PROP_MEASURED_VALUE);

		ContextEventPattern cep6 = new ContextEventPattern();
		cep6.addRestriction(subjectRestriction);
		cep6.addRestriction(predicateRestriction);


		// Register all patterns
		return new ContextEventPattern[] { cep1, cep2, cep3, cep4, cep5, cep6 };
	}

	private synchronized void startSimulation() {
		while (true) {
			int sensorIndex;
			int eventIndex;

			// select sensor type randomly
			switch (randomGenerator.nextInt(5)) {
			
			case 0: // MotionSensor
				
				// startover condition
				if (this.ahSimulator.motionSensors.size() < 1)
					continue;

				// select motion sensor from list randomly
				sensorIndex = randomGenerator
						.nextInt(this.ahSimulator.motionSensors.size());
				MotionSensor ms = this.ahSimulator.motionSensors.get(sensorIndex);

				// choose motion sensor event randomly
				eventIndex = randomGenerator.nextInt(4);

				MotionSensorEvent msEventURI = MotionSensorEvent.getEventByOrder(eventIndex);

				if (ms != null) {
					ms.setMeasuredValue(msEventURI);

					LogUtils
							.logDebug(
									Activator.mc,
									AHContextPublisherSimulator.class,
									"startSimulation",
									new Object[] { "publishing motion sensor context event" },
									null);
					// publish event
					cp.publish(new ContextEvent(ms,
							MotionSensor.PROP_MEASURED_VALUE));
				} else {
					LogUtils.logError(Activator.mc,
							AHContextPublisherSimulator.class,
							"startSimulation",
							new Object[] { "MotionSensor object is NULL" },
							null);
				}
				break;
				
			case 1: // ContactClosureSensors
				
				// startover condition
				if (this.ahSimulator.contactClosureSensors.size() < 1)
					continue;
				
				// select contact closure sensor from list randomly
				sensorIndex = randomGenerator
						.nextInt(this.ahSimulator.contactClosureSensors.size());
				ContactClosureSensor ccs = this.ahSimulator.contactClosureSensors.get(sensorIndex);

				// choose contact closure sensor event randomly
				eventIndex = randomGenerator.nextInt(3);

				ContactClosureSensorEvent ccsEventURI = ContactClosureSensorEvent.getEventByOrder(eventIndex);

				if (ccs != null) {
					ccs.setMeasuredValue(ccsEventURI);

					LogUtils
							.logDebug(
									Activator.mc,
									AHContextPublisherSimulator.class,
									"startSimulation",
									new Object[] { "publishing contact closure sensor context event" },
									null);
					// publish event
					cp.publish(new ContextEvent(ccs,
							ContactClosureSensor.PROP_MEASURED_VALUE));
				} else {
					LogUtils.logError(Activator.mc,
							AHContextPublisherSimulator.class,
							"startSimulation",
							new Object[] { "ContactClosureSensor object is NULL" },
							null);
				}
				break;

			case 2: // UsageSensors
				
				// startover condition
				if (this.ahSimulator.usageSensors.size() < 1)
					continue;
				
				// select usage sensor from list randomly
				sensorIndex = randomGenerator
						.nextInt(this.ahSimulator.usageSensors.size());
				UsageSensor us = this.ahSimulator.usageSensors.get(sensorIndex);

				// choose usage sensor event randomly
				eventIndex = randomGenerator.nextInt(6);

				UsageSensorEvent usEventURI = UsageSensorEvent.getEventByOrder(eventIndex);

				if (us != null) {
					us.setMeasuredValue(usEventURI);

					LogUtils
							.logDebug(
									Activator.mc,
									AHContextPublisherSimulator.class,
									"startSimulation",
									new Object[] { "publishing usage sensor context event" },
									null);
					// publish event
					cp.publish(new ContextEvent(us,
							UsageSensor.PROP_MEASURED_VALUE));
				} else {
					LogUtils.logError(Activator.mc,
							AHContextPublisherSimulator.class,
							"startSimulation",
							new Object[] { "UsageSensor object is NULL" },
							null);
				}
				break;

			case 3: // SwitchSensors
				
				// startover condition
				if (this.ahSimulator.switchSensors.size() < 1)
					continue;

				// select switch sensor from list randomly
				sensorIndex = randomGenerator
						.nextInt(this.ahSimulator.switchSensors.size());
				SwitchSensor ss = this.ahSimulator.switchSensors.get(sensorIndex);

				// choose usage sensor event randomly
				eventIndex = randomGenerator.nextInt(3);

				SwitchSensorEvent ssEventURI = SwitchSensorEvent.getEventByOrder(eventIndex);

				if (ss != null) {
					ss.setMeasuredValue(ssEventURI);

					LogUtils
							.logDebug(
									Activator.mc,
									AHContextPublisherSimulator.class,
									"startSimulation",
									new Object[] { "publishing switch sensor context event" },
									null);
					// publish event
					cp.publish(new ContextEvent(ss,
							SwitchSensor.PROP_MEASURED_VALUE));
				} else {
					LogUtils.logError(Activator.mc,
							AHContextPublisherSimulator.class,
							"startSimulation",
							new Object[] { "SwitchSensor object is NULL" },
							null);
				}
				break;

			case 4: // TempSensors
				
				// startover condition
				if (this.ahSimulator.tempSensors.size() < 1)
					continue;

				// select temp sensor from list randomly
				sensorIndex = randomGenerator
						.nextInt(this.ahSimulator.tempSensors.size());
				TemperatureSensor ts = this.ahSimulator.tempSensors.get(sensorIndex);

				// choose temp sensor event randomly
				eventIndex = randomGenerator.nextInt(4);

				TemperatureSensorEvent tsEventURI = TemperatureSensorEvent.getEventByOrder(eventIndex);

				if (ts != null) {
					ts.setMeasuredValue(tsEventURI);

					LogUtils
							.logDebug(
									Activator.mc,
									AHContextPublisherSimulator.class,
									"startSimulation",
									new Object[] { "publishing temp sensor context event" },
									null);
					// publish event
					cp.publish(new ContextEvent(ts,
							TemperatureSensor.PROP_MEASURED_VALUE));
				} else {
					LogUtils.logError(Activator.mc,
							AHContextPublisherSimulator.class,
							"startSimulation",
							new Object[] { "TemperatureSensor object is NULL" },
							null);
				}
				break;

			}

			try {
				if (this.ahSimulator.getEventIntervall() < 1)
					interval = 1;
				else
					interval = this.ahSimulator.getEventIntervall();
				
				wait(interval * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

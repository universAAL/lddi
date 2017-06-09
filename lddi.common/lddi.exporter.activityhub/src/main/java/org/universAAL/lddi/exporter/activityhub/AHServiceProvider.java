/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
     
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

package org.universAAL.lddi.exporter.activityhub;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.osgi.service.log.LogService;
import org.universAAL.lddi.lib.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.activityhub.*;
import org.universAAL.ontology.activityhub.factory.ActivityHubFactory;
import org.universAAL.ontology.location.indoor.Room;

/**
 * uAAL service provider (service bus) for the ActivityHub no ontology
 * definitions here!
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class AHServiceProvider extends ServiceCallee {

	AHManager ahManager;
	private LogService logger;

	static final String LOCATION_URI_PREFIX = "urn:aal_space:myHome#";
	static final String ACTIVITYHUB_LOCATION_PREFIX = "MDC_AI_LOCATION_";

	private static ActivityHubFactory factory = new ActivityHubFactory();

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param realizedServices
	 */
	public AHServiceProvider(AHManager ahManager) {
		/** register my services on uAAL service bus */
		super(Activator.mc, AHServiceOntology.profiles);

		this.ahManager = ahManager;
		this.logger = ahManager.getLogger();

		this.logger.log(LogService.LOG_INFO, AHServiceOntology.profiles.length
				+ " ActivityHubServer services registered on uAAL service bus:" + getserviceNames());
	}

	// this is just to prepare a standard error message for later use
	private static final ServiceResponse invalidInput = new ServiceResponse(CallStatus.serviceSpecificFailure);
	private static final ServiceResponse internalError = new ServiceResponse(CallStatus.serviceSpecificFailure);

	static {
		invalidInput.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invalid input!"));
		internalError.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Internal Error!"));
	}

	/**
	 * create a service response with all available ActivityHub sensors
	 * 
	 * @return ServiceResponse with output objects
	 */
	private ServiceResponse getControlledActivityHubSensors() {

		ServiceResponse sr = null;
		// try {
		// create a list including the available sensors and sensor types
		Map<String, Integer> sensorList = new TreeMap<String, Integer>();

		// fetch data from my server
		ahManager.getActivityHubSensorList(sensorList);

		// LogUtils.logInfo(Activator.mc, AHServiceProvider.class,
		// "getControlledActivityHubSensors",
		// new Object[] { "sensorList size: " + sensorList.size() }, null);

		// return if no sensors available
		if (sensorList.size() == 0) {
			ServiceResponse errorResponse = new ServiceResponse(CallStatus.serviceSpecificFailure);
			errorResponse.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR,
					"No ActivityHub Sensors available!"));
			return errorResponse;
		}

		// prepare array for service output
		ArrayList activityHubSensorList = new ArrayList(sensorList.size());

		// iterate through sensor list and add sensors to output array
		Iterator<Entry<String, Integer>> it = sensorList.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Integer> entry = it.next();

			ActivityHubSensor ahs = (ActivityHubSensor) factory.createInstance(null,
					constructActivityHubSensorURIfromLocalID(entry.getKey()), entry.getValue().intValue());
			// ahs.setLocation ??

			this.logger.log(LogService.LOG_INFO,
					"ActivityHubSensor URI: " + ahs.getURI() + " sensorType: " + ahs.getSensorType());

			// LogUtils.logInfo(Activator.mc, AHServiceProvider.class,
			// "getControlledActivityHubSensors",
			// new Object[] { "ActivityHubSensor URI: " + ahs.getURI() +
			// " sensorType: " + ahs.getSensorType() }, null);

			activityHubSensorList.add(ahs);
		}

		sr = new ServiceResponse(CallStatus.succeeded);

		// create and add a ProcessOutput-Event that binds the output URI to the
		// created list of sensors
		sr.addOutput(new ProcessOutput(AHServiceOntology.OUTPUT_CONTROLLED_ACTIVITYHUB_SENSORS, activityHubSensorList));
		// return sr;

		// } catch (Exception e) {
		// LogUtils.logInfo(Activator.mc, AHServiceProvider.class,
		// "getControlledActivityHubSensors",
		// new Object[] { "ERROR on service response: " + e.getMessage() },
		// null);
		// e.printStackTrace();
		// sr = internalError;
		// }
		// sr.getOutput(paramURI, true);
		return sr;
	}

	private static String constructActivityHubSensorURIfromLocalID(String localID) {
		return AHServiceOntology.DEVICE_URI_PREFIX + localID;
	}

	/**
	 * create a service response with information about the requested
	 * ActivityHub device
	 * 
	 * @param activityHubDeviceURI
	 * @return ServiceResponse with output objects
	 */
	private ServiceResponse getActivityHubDeviceInfo(ActivityHubSensor activityHubSensor) {
		// this.logger.log(LogService.LOG_INFO, "Service called:
		// getActivityHubDeviceInfo" +
		// " for sensorURI: " + activityHubSensor.getURI());

		LogUtils.logDebug(Activator.mc, AHServiceProvider.class, "getActivityHubDeviceInfo",
				new Object[] {
						"Service called: getActivityHubDeviceInfo" + " for sensorURI: " + activityHubSensor.getURI() },
				null);

		try {
			// collect the needed data
			String deviceId = extractLocalIDfromdeviceUri(activityHubSensor.getURI());

			LogUtils.logDebug(Activator.mc, AHServiceProvider.class, "getActivityHubDeviceInfo",
					new Object[] { "extracted deviceId: " + deviceId }, null);

			// this.logger.log(LogService.LOG_INFO, "extracted deviceId: " +
			// deviceId);

			// check device
			if (!ahManager.validateDevice(deviceId)) {
				// no such device !
				LogUtils.logError(Activator.mc, AHServiceProvider.class, "getActivityHubDeviceInfo",
						new Object[] { "no such device found! " + deviceId }, null);
				// this.logger.log(LogService.LOG_ERROR, "no such device found!
				// " + deviceId);
				ServiceResponse sr = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return sr;
			}

			LogUtils.logInfo(Activator.mc, AHServiceProvider.class, "getActivityHubDeviceInfo",
					new Object[] { "matching device found for " + deviceId }, null);
			// this.logger.log(LogService.LOG_INFO, "matching device found for "
			// + deviceId);

			ActivityHubLocation loc = ahManager.getDeviceLocation(deviceId);

			// which format needed for event?
			int lastDeviceEvent = ahManager.getLastDeviceEvent(deviceId);

			ActivityHubSensorEvent ahse = createSensorEvent(lastDeviceEvent, activityHubSensor);
			if (ahse == null) {
				LogUtils.logError(Activator.mc, AHServiceProvider.class, "getActivityHubDeviceInfo",
						new Object[] { "Could not create a SensorEvent object for: " + activityHubSensor.getURI()
								+ " and sensor value: " + lastDeviceEvent },
						null);
				// this.logger.log(LogService.LOG_ERROR, "Could not create a
				// SensorEvent object " +
				// "for: " + activityHubSensor.getURI() + " and sensor value: "
				// + lastDeviceEvent);
				ServiceResponse sr = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return sr;
			}

			// We assume that the Service-Call always succeeds
			ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);

			// create and add a ProcessOutput-Event that binds the output URI to
			// the last event of the device
			sr.addOutput(new ProcessOutput(AHServiceOntology.OUTPUT_SENSOR_MEASUREMENT, ahse));

			// create and add a ProcessOutput-Event that binds the output URI to
			// the location of the device
			// loc maybe null !!
			sr.addOutput(new ProcessOutput(AHServiceOntology.OUTPUT_SENSOR_LOCATION,
					new Room(constructLocationURIfromLocalID(loc))));
			return sr;

		} catch (Exception e) {
			return invalidInput;
		}
	}

	/**
	 * Creates sensor event object from ontology (from enums). The abstract
	 * class ActivityHubSensorEvent is used here.
	 * 
	 * @param lastSensorEvent
	 * @return sensor specific event object from ontology
	 */
	private ActivityHubSensorEvent createSensorEvent(int lastSensorEvent, ActivityHubSensor input) {

		/**
		 * list of sensors:
		 * org.universAAL.ontology.activityhub.util.ActivityHubSensorType
		 */
		if (input instanceof FallSensor)
			return FallSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof PersSensor)
			return PersSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof SmokeSensor)
			return EnvironmentalSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof CoSensor)
			return EnvironmentalSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof WaterSensor)
			return EnvironmentalSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof GasSensor)
			return EnvironmentalSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof MotionSensor)
			return MotionSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof PropertyExitSensor)
			return PropertyExitSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof EnuresisSensor)
			return EnuresisSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof ContactClosureSensor)
			return ContactClosureSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof UsageSensor)
			return UsageSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof SwitchSensor)
			return SwitchSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof MedicationDosageSensor)
			return MedicationDosageSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof TemperatureSensor)
			return TemperatureSensorEvent.getEventByOrder(lastSensorEvent);

		LogUtils.logError(Activator.mc, AHServiceProvider.class, "createSensorEvent",
				new Object[] { "No matching ActivityHubSensorType found for service bus call: " + input.getURI() },
				null);

		// this.logger.log(LogService.LOG_ERROR, "No matching ActivityHubSensor
		// found for service bus call: " +
		// input.toString());
		return null;
	}

	private static String extractLocalIDfromdeviceUri(String deviceUri) {
		return deviceUri.substring(AHServiceOntology.DEVICE_URI_PREFIX.length());
	}

	/**
	 * If param loc is null "unknown" is returned
	 * 
	 * @param loc
	 * @return URI of room
	 */
	private static String constructLocationURIfromLocalID(ActivityHubLocation loc) {
		if (loc == null)
			return LOCATION_URI_PREFIX + "unknown";
		else
			return LOCATION_URI_PREFIX + loc.toString().substring(ACTIVITYHUB_LOCATION_PREFIX.length());
	}

	/**
	 * handle service request from uAAL service bus
	 * 
	 * @see org.universAAL.middleware.service.ServiceCallee#handleCall(org.universAAL.middleware.service.ServiceCall)
	 */
	@Override
	public ServiceResponse handleCall(ServiceCall call) {
		// LogUtils.logDebug(Activator.mc, AHServiceProvider.class,
		// "handleCall",
		// new Object[] { "I'm here" }, null);

		if (call == null)
			return null;

		String operation = call.getProcessURI();
		if (operation == null)
			return null;

		LogUtils.logDebug(Activator.mc, AHServiceProvider.class, "handleCall",
				new Object[] { "operation: " + operation }, null);

		/** operations without input */
		if (operation.startsWith(AHServiceOntology.SERVICE_GET_CONTROLLED_ACTIVITYHUB_SENSORS))
			return getControlledActivityHubSensors();

		/** following operations all need input value */
		Object input = call.getInputValue(AHServiceOntology.INPUT_SENSOR_URI);
		if (input == null) {
			LogUtils.logWarn(Activator.mc, AHServiceProvider.class, "handleCall",
					new Object[] { "Incoming call but input is missing!" }, null);
			return invalidInput;
		}

		// input sollte ein ActivityHubSensor sein

		LogUtils.logDebug(Activator.mc, AHServiceProvider.class, "handleCall",
				new Object[] { "Incoming call for: " + ((ActivityHubSensor) input).getURI() }, null);

		if (operation.startsWith(AHServiceOntology.SERVICE_GET_ACTIVITYHUB_SENSOR_INFO)) {
			return getActivityHubDeviceInfo((ActivityHubSensor) input);
		}

		// no match
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.universAAL.middleware.service.ServiceCallee#
	 * communicationChannelBroken()
	 */
	@Override
	public void communicationChannelBroken() {
		LogUtils.logWarn(Activator.mc, AHServiceProvider.class, "communicationChannelBroken",
				new Object[] { "Service Bus is stopped by the uAAL middleware!" }, null);
	}

	/**
	 * provide names (URI) of service profiles
	 * 
	 * @return
	 */
	private String getserviceNames() {
		StringBuffer serviceNames = new StringBuffer();
		for (ServiceProfile prof : AHServiceOntology.profiles) {
			serviceNames.append("\n"
					// prof.getServiceName() + "; " + //null
					// prof.getNamespace() + "; " + //null
					// prof.getNumberOfInputs() + "; " + //0
					// + prof.getProcessURI() + "; "
					// //http://ontology.universAAL.org/ActivityHubServer.owl#getControlledActivityHubSensorsProcess
					// prof.getType()+ "; " +
					// //http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile
					// prof.getURI()+ "; " +
					// //urn:anonymous:_:c0a83c6bc8d08bb4:457
					// prof.getTheService().MY_URI + "; "
					// //http://www.daml.org/services/owl-s/1.1/Service.owl#Service
					// + prof.PROP_OWLS_PROFILE_SERVICE_NAME + "; "
					// //http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName
					// + prof.MY_URI + "; "
					// //http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile
					// + prof.OWLS_PROFILE_NAMESPACE + "; "
					// //http://www.daml.org/services/owl-s/1.1/Profile.owl#

					// we have a winner !
					+ prof.getTheService().getURI() // http://ontology.universAAL.org/ActivityHubServer.owl#getControlledActivityHubSensors

			// + prof.getTheService().getClassURI()+ "; "
			// //http://ontology.universAAL.org/ActivityHub.owl#ActivityHub

			//
			// // also good!
			// + prof.getTheService().getLocalName()+ "; "
			// //getControlledActivityHubSensors
			//
			// + prof.getTheService().MY_URI+ "; "
			// //http://www.daml.org/services/owl-s/1.1/Service.owl#Service

			// + " and"
			);
		}
		return serviceNames.toString();
	}
}

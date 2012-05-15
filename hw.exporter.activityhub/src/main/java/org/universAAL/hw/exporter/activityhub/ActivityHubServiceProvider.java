package org.universAAL.hw.exporter.activityhub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import org.osgi.service.log.LogService;
import org.universAAL.iso11073.activityhub.location.ActivityHubLocationUtil.ActivityHubLocation;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.activityhub.ActivityHubSensor;
import org.universAAL.ontology.activityhub.ActivityHubSensorEvent;
import org.universAAL.ontology.activityhub.ContactClosureSensor;
import org.universAAL.ontology.activityhub.ContactClosureSensorEvent;
import org.universAAL.ontology.activityhub.MotionSensor;
import org.universAAL.ontology.activityhub.MotionSensorEvent;
import org.universAAL.ontology.activityhub.SwitchSensor;
import org.universAAL.ontology.activityhub.SwitchSensorEvent;
import org.universAAL.ontology.activityhub.factory.ActivityHubFactory;
import org.universAAL.ontology.location.indoor.Room;

/**
 * uAAL service provider (service bus) for the ActivityHub
 * no ontology definitions here!
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubServiceProvider extends ServiceCallee {

	ActivityHubBusServer theServer;
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
	public ActivityHubServiceProvider(ModuleContext mc, ActivityHubBusServer busServer) {
		/** register my services on uAAL service bus */
		super(mc, ActivityHubServiceOntology.profiles);

		this.theServer = busServer;
		this.logger = busServer.getLogger();

		this.logger.log(LogService.LOG_INFO, ActivityHubServiceOntology.profiles.length + 
				" ActivityHubServer services registered on uAAL service bus:" + getserviceNames() );
	}


	// this is just to prepare a standard error message for later use
    private static final ServiceResponse invalidInput = new ServiceResponse(
	    CallStatus.serviceSpecificFailure);
    private static final ServiceResponse internalError = new ServiceResponse(
    	    CallStatus.serviceSpecificFailure);
    
    static {
    	invalidInput.addOutput(new ProcessOutput(
    			ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invalid input!"));
    	internalError.addOutput(new ProcessOutput(
    			ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Internal Error!"));
    }
    

	/**
	 * create a service response with all available ActivityHub sensors
	 * 
	 * @return ServiceResponse with output objects 
	 */
	private ServiceResponse getControlledActivityHubSensors() {

		ServiceResponse sr = null;
//		try {
			// create a list including the available sensors and sensor types
			Hashtable<String,Integer> sensorList = new Hashtable<String,Integer>();

			// fetch data from my server
			theServer.getActivityHubSensorList(sensorList);

//			LogUtils.logInfo(Activator.moduleContext, ActivityHubServiceProvider.class,
//					"getControlledActivityHubSensors",
//					new Object[] { "sensorList size: " + sensorList.size() }, null);
			
			// return if no sensors available
			if (sensorList.size() == 0) {
				ServiceResponse errorResponse = new ServiceResponse(CallStatus.serviceSpecificFailure);
				errorResponse.addOutput(new ProcessOutput(
		    			ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "No ActivityHub Sensors available!"));
				return errorResponse;
			}
			
			// prepare array for service output
			ArrayList activityHubSensorList = new ArrayList(sensorList.size());

			// iterate through sensor list and add sensors to output array
			Iterator<Entry<String, Integer>> it = sensorList.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> entry = it.next();

				ActivityHubSensor ahs = (ActivityHubSensor) factory.createInstance(null, 
						constructActivityHubSensorURIfromLocalID(entry.getKey()),
						entry.getValue().intValue());
				// ahs.setLocation ??

				this.logger.log(LogService.LOG_INFO, "ActivityHubSensor URI: " + ahs.getURI() +
						" sensorType: " + ahs.getSensorType());
				
//				LogUtils.logInfo(Activator.moduleContext, ActivityHubServiceProvider.class,
//						"getControlledActivityHubSensors",
//						new Object[] { "ActivityHubSensor URI: " + ahs.getURI() +
//							" sensorType: " + ahs.getSensorType() }, null);
				
				activityHubSensorList.add(ahs);
			}

			sr = new ServiceResponse(CallStatus.succeeded);

			// create and add a ProcessOutput-Event that binds the output URI to the
			// created list of sensors
			sr.addOutput(new ProcessOutput(
					ActivityHubServiceOntology.OUTPUT_CONTROLLED_ACTIVITYHUB_SENSORS, activityHubSensorList));
//			return sr;
			
//		} catch (Exception e) {
//			LogUtils.logInfo(Activator.moduleContext, ActivityHubServiceProvider.class,
//					"getControlledActivityHubSensors",
//					new Object[] { "ERROR on service response: " + e.getMessage() }, null);
//			e.printStackTrace();
//			sr = internalError;
//		}
//		sr.getOutput(paramURI, true);
		return sr;
	}
	
    private static String constructActivityHubSensorURIfromLocalID(String localID) {
    	return ActivityHubServiceOntology.DEVICE_URI_PREFIX + localID;
    }
	
	/**
	 * create a service response with information about the requested ActivityHub device
	 * 
	 * @param activityHubDeviceURI
	 * @return ServiceResponse with output objects 
	 */
	private ServiceResponse getActivityHubDeviceInfo(String deviceUri, Object input) {
		this.logger.log(LogService.LOG_INFO, "Service called: getActivityHubDeviceInfo" +
				" with parameter: " + deviceUri);
		try {
			// collect the needed data
			String deviceId = extractLocalIDfromdeviceUri(deviceUri);
			
			this.logger.log(LogService.LOG_INFO, "extracted deviceId: " + deviceId);
			
			// check device
			if ( theServer.validateDevice(deviceId) ) {
				// no such device !
				this.logger.log(LogService.LOG_ERROR, "no such device found! " + deviceId);
				ServiceResponse sr = new ServiceResponse(CallStatus.noMatchingServiceFound);
				return sr;
			}
			
			ActivityHubLocation loc = theServer.getDeviceLocation(deviceId);
			
			// which format needed for event?
			int lastDeviceEvent = theServer.getLastDeviceEvent(deviceId);
			
			ActivityHubSensorEvent ahse = createSensorEvent(lastDeviceEvent, input);
			if (ahse == null) {
				this.logger.log(LogService.LOG_ERROR, "Could not create a SensorEvent object " +
						"for: " + input.toString() + " and sensor value: " + lastDeviceEvent); 
				ServiceResponse sr = new ServiceResponse(CallStatus.serviceSpecificFailure);
				return sr;
			}

			// We assume that the Service-Call always succeeds
			ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);

			// create and add a ProcessOutput-Event that binds the output URI to
			// the last event of the device
			sr.addOutput(new ProcessOutput(
					ActivityHubServiceOntology.OUTPUT_SENSOR_EVENT, ahse));
			
			
			// create and add a ProcessOutput-Event that binds the output URI to
			// the location of the device
			sr.addOutput(new ProcessOutput(
					ActivityHubServiceOntology.OUTPUT_SENSOR_LOCATION, new Room(
							constructLocationURIfromLocalID(loc))));
			
			return sr;
			
		} catch (Exception e) {
			return invalidInput;
		}
	}

    /**
	 * @param lastSensorEvent
	 * @return
	 */
	private ActivityHubSensorEvent createSensorEvent(int lastSensorEvent, Object input) {
		if (input instanceof MotionSensor)
			return MotionSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof ContactClosureSensor)
			return ContactClosureSensorEvent.getEventByOrder(lastSensorEvent);
		else if (input instanceof SwitchSensor)
			return SwitchSensorEvent.getEventByOrder(lastSensorEvent);

		//...fill
		
		this.logger.log(LogService.LOG_ERROR, "No matching ActivityHubSensor found for service bus call: " + 
				input.toString());
		return null;
	}

	private static String extractLocalIDfromdeviceUri(String deviceUri) {
    	return deviceUri.substring(ActivityHubServiceOntology.DEVICE_URI_PREFIX.length());
    }

    private static String constructLocationURIfromLocalID(ActivityHubLocation loc) {
    	return LOCATION_URI_PREFIX + loc.toString().substring(
    			ACTIVITYHUB_LOCATION_PREFIX.length());
    }


	/**
	 * handle service request from uAAL service bus
	 * @see org.universAAL.middleware.service.ServiceCallee#handleCall(org.universAAL.middleware.service.ServiceCall)
	 */
	@Override
	public ServiceResponse handleCall(ServiceCall call) {
		if (call == null)
		    return null;

		String operation = call.getProcessURI();
		if (operation == null)
		    return null;

		
		/** operations without input */
		if (operation.startsWith(ActivityHubServiceOntology.SERVICE_GET_CONTROLLED_ACTIVITYHUB_SENSORS))
			    return getControlledActivityHubSensors();
		
		
		/** following operations all need input value */
		Object input = call.getInputValue(ActivityHubServiceOntology.INPUT_SENSOR_URI);
		if (input == null) return null;

		this.logger.log(LogService.LOG_INFO, "incoming call for: " + input.toString());

		if (operation
			.startsWith(ActivityHubServiceOntology.SERVICE_GET_ACTIVITYHUB_SENSOR_INFO)) {

			return getActivityHubDeviceInfo(input.toString(), input);
		}
		    
		// no match
		return null;
	}


	/* (non-Javadoc)
	 * @see org.universAAL.middleware.service.ServiceCallee#communicationChannelBroken()
	 */
	@Override
	public void communicationChannelBroken() {
		// TODO Auto-generated method stub

	}


    /**
     * provide names (URI) of service profiles 
	 * @return
	 */
	private String getserviceNames() {
		StringBuffer serviceNames = new StringBuffer();
		for ( ServiceProfile prof : ActivityHubServiceOntology.profiles ) {
			serviceNames.append("\n" 
//					prof.getServiceName() + "; " + //null
//					prof.getNamespace() + "; " + //null
//					prof.getNumberOfInputs() + "; " + //0
//					+ prof.getProcessURI() + "; " //http://ontology.universAAL.org/ActivityHubServer.owl#getControlledActivityHubSensorsProcess
//					prof.getType()+ "; " + //http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile
//					prof.getURI()+ "; " + //urn:anonymous:_:c0a83c6bc8d08bb4:457
//					prof.getTheService().MY_URI + "; " //http://www.daml.org/services/owl-s/1.1/Service.owl#Service
//					+ prof.PROP_OWLS_PROFILE_SERVICE_NAME + "; " //http://www.daml.org/services/owl-s/1.1/Profile.owl#serviceName
//					+ prof.MY_URI + "; " //http://www.daml.org/services/owl-s/1.1/Profile.owl#Profile
//					+ prof.OWLS_PROFILE_NAMESPACE + "; " //http://www.daml.org/services/owl-s/1.1/Profile.owl#
					
					// we have a winner !
					+ prof.getTheService().getURI() //http://ontology.universAAL.org/ActivityHubServer.owl#getControlledActivityHubSensors
					
//					+ prof.getTheService().getClassURI()+ "; "  //http://ontology.universAAL.org/ActivityHub.owl#ActivityHub
																
//					
//					// also good!
//					+ prof.getTheService().getLocalName()+ "; " //getControlledActivityHubSensors
//					
//					+ prof.getTheService().MY_URI+ "; " //http://www.daml.org/services/owl-s/1.1/Service.owl#Service

//					+ " and"
			);
		}
		return serviceNames.toString();
	}
}

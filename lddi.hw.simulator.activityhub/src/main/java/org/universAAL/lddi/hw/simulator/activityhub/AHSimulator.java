package org.universAAL.lddi.hw.simulator.activityhub;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.ontology.activityhub.ContactClosureSensor;
import org.universAAL.ontology.activityhub.MotionSensor;
import org.universAAL.ontology.activityhub.SwitchSensor;
import org.universAAL.ontology.activityhub.TemperatureSensor;
import org.universAAL.ontology.activityhub.UsageSensor;

/**
 * Instantiates several ActivityHub devices according to config.
 * Starts the context publisher thread.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class AHSimulator implements ManagedService {
	
	private BundleContext context;
	private ModuleContext mc;
	private LogService logger;
//	private static ActivityHubFactory factory = new ActivityHubFactory();
	private Thread simulaotrThread;

	// properties
	private int numberOfMotionSensors;
	private int numberOfContactClosureSensors;
	private int numberOfUsageSensors;
	private int numberOfSwitchSensors;
	private int numberOfTempSensors;
	private int eventIntervall;

	// device lists
	public List<MotionSensor> motionSensors = new ArrayList<MotionSensor>();
	public List<ContactClosureSensor> contactClosureSensors = new ArrayList<ContactClosureSensor>();
	public List<UsageSensor> usageSensors = new ArrayList<UsageSensor>();
	public List<SwitchSensor> switchSensors = new ArrayList<SwitchSensor>();
	public List<TemperatureSensor> tempSensors = new ArrayList<TemperatureSensor>();

	// big sensor list
//	List<ActivityHubSensor> mySensorCollection = new ArrayList<ActivityHubSensor>();


	public AHSimulator(BundleContext context, LogService logger, ModuleContext mc) {
		this.context = context;
		this.logger = logger;
		this.mc = mc;

		this.registerManagedService();
	}
    
	/***
	 * Register this class as Managed Service
	 */
	private void registerManagedService() {
		Properties propManagedService = new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle()
				.getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this,
				propManagedService);
	}

	/**
	 * Create simulated ActivityHub devices
	 */
	private void initSimulator() {
		// create motion sensor devices from ontology
		for (int i = 1; i <= numberOfMotionSensors; i++) {
	    	String instanceURI = "ActivityHubMotionSensor" + i;
	    	MotionSensor ms = new MotionSensor(instanceURI);
			motionSensors.add(ms);
		}
		this.logger.log(LogService.LOG_INFO, "MotionSensors created: " + this.motionSensors.size());

		// create contact closure sensor devices from ontology
		for (int i = 1; i <= numberOfContactClosureSensors; i++) {
	    	String instanceURI = "ActivityHubContactClosureSensor" + i;
	    	ContactClosureSensor ms = new ContactClosureSensor(instanceURI);
	    	contactClosureSensors.add(ms);
		}
		this.logger.log(LogService.LOG_INFO, "ContactClosureSensors created: " + this.contactClosureSensors.size());

		// create usage sensor devices from ontology
		for (int i = 1; i <= numberOfUsageSensors; i++) {
	    	String instanceURI = "ActivityHubUsageSensor" + i;
	    	UsageSensor ms = new UsageSensor(instanceURI);
	    	usageSensors.add(ms);
		}
		this.logger.log(LogService.LOG_INFO, "UsageSensors created: " + this.usageSensors.size());

		// create switch sensor devices from ontology
		for (int i = 1; i <= numberOfSwitchSensors; i++) {
	    	String instanceURI = "ActivityHubSwitchSensor" + i;
	    	SwitchSensor ms = new SwitchSensor(instanceURI);
	    	switchSensors.add(ms);
		}
		this.logger.log(LogService.LOG_INFO, "SwitchSensors created: " + this.switchSensors.size());

		// create temp sensor devices from ontology
		for (int i = 1; i <= numberOfTempSensors; i++) {
	    	String instanceURI = "ActivityHubTempSensor" + i;
	    	TemperatureSensor ms = new TemperatureSensor(instanceURI);
	    	tempSensors.add(ms);
		}
		this.logger.log(LogService.LOG_INFO, "TempSensors created: " + this.tempSensors.size());

//		this.logger.log(LogService.LOG_INFO, "Created a list of "
//				+ this.mySensorCollection.size()
//				+ " ActivityHubSensors for simulation");

		// // fill overall sensor collection
		// mySensorCollection.add(motionSensors);
		// mySensorCollection.add(contactClosureSensors);
		// mySensorCollection.add(usageSensors);
		// mySensorCollection.add(switchSensors);
		// mySensorCollection.add(tempSensors);

	}

	
	/**
	 * Start context publisher simulator thread
	 */
	private void startSimulator() {
		// start uAAL service provider
		MyThread runnable = new MyThread(this);
		simulaotrThread = new Thread(runnable);
		simulaotrThread.start();
	}

	
	public LogService getLogger() {
		return this.logger;
	}


	/**
	 * Get updated from ConfigurationAdmin.
	 */
	public void updated(@SuppressWarnings("unchecked") Dictionary properties)
			throws ConfigurationException {
		this.logger.log(LogService.LOG_INFO, "AHSimulator.updated: "
				+ properties);

		try {
			if (properties != null) {
				this.setNumberOfMotionSensors(Integer
						.valueOf((String) properties
								.get("numberOfMotionSensors")));
				this.setNumberOfContactClosureSensors(Integer
						.valueOf((String) properties
								.get("numberOfContactClosureSensors")));
				this.setNumberOfUsageSensors(Integer
						.valueOf((String) properties
								.get("numberOfUsageSensors")));
				this.setNumberOfSwitchSensors(Integer
						.valueOf((String) properties
								.get("numberOfSwitchSensors")));
				this.setNumberOfTempSensors(Integer.valueOf((String) properties
						.get("numberOfTempSensors")));
				this.setEventIntervall(Integer.valueOf((String) properties
						.get("eventInterval")));

				// init simulator devices
				initSimulator();
				
				// activate simulation publisher
				startSimulator();

			} else {
				this.logger.log(LogService.LOG_ERROR,
						"Property file for ActivityHub Simulator not found!");
			}
		} catch (NumberFormatException nfe) {
			this.logger.log(LogService.LOG_ERROR, "NumberFormatException\n"
					+ nfe.getMessage());
		}
	}

	/**
	 * @param numberOfContactClosureSensors
	 *            the numberOfContactClosureSensors to set
	 */
	public void setNumberOfContactClosureSensors(
			int numberOfContactClosureSensors) {
		this.numberOfContactClosureSensors = numberOfContactClosureSensors;
	}

	/**
	 * @return the numberOfContactClosureSensors
	 */
	public int getNumberOfContactClosureSensors() {
		return numberOfContactClosureSensors;
	}

	/**
	 * @param numberOfMotionSensors
	 *            the numberOfMotionSensors to set
	 */
	public void setNumberOfMotionSensors(int numberOfMotionSensors) {
		this.numberOfMotionSensors = numberOfMotionSensors;
	}

	/**
	 * @return the numberOfMotionSensors
	 */
	public int getNumberOfMotionSensors() {
		return numberOfMotionSensors;
	}

	/**
	 * @param numberOfUsageSensors
	 *            the numberOfUsageSensors to set
	 */
	public void setNumberOfUsageSensors(int numberOfUsageSensors) {
		this.numberOfUsageSensors = numberOfUsageSensors;
	}

	/**
	 * @return the numberOfUsageSensors
	 */
	public int getNumberOfUsageSensors() {
		return numberOfUsageSensors;
	}

	/**
	 * @param numberOfSwitchSensors
	 *            the numberOfSwitchSensors to set
	 */
	public void setNumberOfSwitchSensors(int numberOfSwitchSensors) {
		this.numberOfSwitchSensors = numberOfSwitchSensors;
	}

	/**
	 * @return the numberOfSwitchSensors
	 */
	public int getNumberOfSwitchSensors() {
		return numberOfSwitchSensors;
	}

	/**
	 * @param numberOfTempSensors
	 *            the numberOfTempSensors to set
	 */
	public void setNumberOfTempSensors(int numberOfTempSensors) {
		this.numberOfTempSensors = numberOfTempSensors;
	}

	/**
	 * @return the numberOfTempSensors
	 */
	public int getNumberOfTempSensors() {
		return numberOfTempSensors;
	}

	/**
	 * @param eventIntervall
	 *            the eventIntervall to set
	 */
	public void setEventIntervall(int eventIntervall) {
		this.eventIntervall = eventIntervall;
	}

	/**
	 * @return the eventIntervall
	 */
	public int getEventIntervall() {
		return eventIntervall;
	}

	
	/**
	 * Runnable helper class for access to running servers/threads.
	 * 
	 * @author Thomas Fuxreiter (foex@gmx.at)
	 */
	private class MyThread implements Runnable {
		AHSimulator ahSimulator;
		
		public MyThread(AHSimulator ahSimulator) {
			this.ahSimulator = ahSimulator;
		}

		public void run() {
			// contextProvider =
			new AHContextPublisherSimulator(mc, ahSimulator);
		}
	}
}

package org.universAAL.knx.dpt1refinementdriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxNetwork;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.device.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.knx.devicecategory.KnxDpt1;
import org.universAAL.knx.dpt1refinementdriver.util.LogTracker;

/**
 * when an attached device service is unregistered:
 * drivers must take the appropriate action to release this device service
 * and peform any necessary cleanup, as described in their device category spec
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class KnxDpt1RefinementDriver implements Driver, ServiceTrackerCustomizer
, ManagedService
{

	private BundleContext context;
	private LogService logger;
	private static final String MY_DRIVER_ID = "org.universAAL.knx.dpt1.0.0.1"; //"Knx_DoorWindowActuator"
	private static final String MY_KNX_DEVICE_CATEGORY = "KnxDpt1";
	private static final String KNX_DRIVER_CONFIG_NAME = "knx.refinementdriver";

	String filterQuery=String.format("(%s=%s)", org.osgi.framework.Constants.OBJECTCLASS,KnxNetwork.class.getName());
	private KnxNetwork network;
	private ServiceRegistration regDriver;
	private Set<KnxDpt1Instance> connectedDriver;
//	private Dictionary<String,String> knxIsoMappingProperties;
//	private Map<String,String> knxIsoMappingProperties;
	private Properties knxIsoMappingProperties;
	private ServiceTracker tracker;

	/**
	 * @param context
	 * @param logTracker
	 */
	public KnxDpt1RefinementDriver(BundleContext context, LogTracker log) {
		this.context=context;
		this.logger=log;
		
		this.connectedDriver = new HashSet<KnxDpt1Instance>();
		
		this.registerManagedService();

		// track on KnxNetwork service
		try {
			ServiceTracker st=new ServiceTracker(context,this.context.createFilter(filterQuery), this);
			st.open();
		} catch (InvalidSyntaxException e) {
			this.logger.log(LogService.LOG_ERROR, "exception",e);
			e.printStackTrace();
		}
	}

	/**
	 * Register this class as Managed Service
	 */
	public void registerManagedService() {
//		this.logger.log(LogService.LOG_ERROR, "Register managed service!");

		Properties propManagedService=new Properties();
		propManagedService.put(org.osgi.framework.Constants.SERVICE_PID, 
				KNX_DRIVER_CONFIG_NAME);
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#match(org.osgi.framework.ServiceReference)
	 */
	public int match(ServiceReference reference) throws Exception {
		// reference = device service
		int matchValue=Device.MATCH_NONE;
		String deviceCategory=(String)reference.getProperty(Constants.DEVICE_CATEGORY);
		// more possible properties to match: description, serial, id

		if ( deviceCategory.equals(MY_KNX_DEVICE_CATEGORY) ) {
			matchValue = KnxDpt1.MATCH_CLASS;
		}
		return matchValue; //must be > 0 to match
	}


	/* (non-Javadoc)
	 * @see org.osgi.service.device.Driver#attach(org.osgi.framework.ServiceReference)
	 */
	public String attach(ServiceReference reference) throws Exception {

		// create "driving" instance
		KnxDpt1Instance instance = new KnxDpt1Instance(this.context, reference, network,
				this.logger);
		
		// pass knx-to-iso config
		instance.setKnxIsoMappingProperties(this.knxIsoMappingProperties);
		
		// init service tracker
		tracker = new ServiceTracker(this.context, reference, instance);
		tracker.open();
		
		// register managed service
//		instance.registerManagedService();
		
		// from DOG
//		KnxActuatorInstance instance=new KnxActuatorInstance(this.network, 
//				(ControllableDevice) this.context.getService(reference));
		
		synchronized(this.connectedDriver){
			if ( ! this.connectedDriver.add(instance) )
				this.logger.log(LogService.LOG_ERROR, "Duplicate Element in HashSet connectedDriver");
		}

		return null; // if attachment is correct
	}
	
	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		this.network=(KnxNetwork)this.context.getService(reference);
		this.registerDriver();
		return reference;
	}

	private void registerDriver() {
		Properties propDriver=new Properties();
		propDriver.put(Constants.DRIVER_ID, MY_DRIVER_ID );
		this.regDriver=this.context.registerService(Driver.class.getName(), this, propDriver);
	}
	
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked service KnxNetwork was modified. Going to update the KnxDpt1Refinement driver");
		removedService(reference, service);
		addingService(reference);		
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		// When knx.networkservice disappears:
		// unregister my driver and release device service
		if(this.regDriver!=null){
			this.regDriver.unregister();
		}
		this.context.ungetService(reference);
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
	 */
	public void updated(Dictionary properties) throws ConfigurationException {
		this.logger.log(LogService.LOG_INFO, "KnxDpt1Driver updated. " +
				"Mapping from KNX device to ISO 11073 device. " + properties);

		if (properties != null) {
			this.knxIsoMappingProperties = new Properties();
			
			/** groupAddress configuration format is "A-B-C" because Felix ConfigurationManager says '/' is an illegal character;
			* change here to "A/B/C" */
			Enumeration<String> en = properties.keys(); 
			while (en.hasMoreElements()) {
				String key = en.nextElement();
				String newKey = key.replace('-', '/');

				String value = (String) properties.get(key);
				this.knxIsoMappingProperties.put(newKey, value);
			}
			this.logger.log(LogService.LOG_INFO, "KNX-ISO mapping config: " + this.knxIsoMappingProperties);
		} else {
			this.logger.log(LogService.LOG_ERROR, "No configuration found for Knx to ISO device mapping!");
			return;
		}
	}


	/**
	 * @return the connectedDriver
	 */
	public Set<KnxDpt1Instance> getConnectedDriver() {
		return connectedDriver;
	}

}

package org.universAAL.knx.dpt1driver;

import java.util.Properties;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxNetwork;

import org.osgi.framework.BundleContext;
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
import org.universAAL.knx.dpt1driver.util.LogTracker;

/**
 * when an attached device service is unregistered:
 * drivers must take the appropriate action to release this device service
 * and peform any necessary cleanup, as described in their device category spec
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public class KnxDpt1Driver implements Driver, ServiceTrackerCustomizer {

	private BundleContext context;
	private LogService logger;
	private static String MY_DRIVER_ID = "org.universAAL.knx.dpt1.0.0.1"; //"Knx_DoorWindowActuator"
	private static String MY_KNX_DEVICE_CATEGORY = "KnxDpt1";

	String filterQuery=String.format("(%s=%s)", org.osgi.framework.Constants.OBJECTCLASS,KnxNetwork.class.getName());
	private KnxNetwork network;
	private ServiceRegistration regDriver;
//	private Vector<KnxActuatorInstance> connectedDriver;
	
	/**
	 * @param context
	 * @param logTracker
	 */
	public KnxDpt1Driver(BundleContext context, LogTracker log) {
		this.context=context;
		this.logger=log;
		
		// track on KnxNetwork service
		try {
			ServiceTracker st=new ServiceTracker(context,this.context.createFilter(filterQuery), this);
			st.open();
		} catch (InvalidSyntaxException e) {
			this.logger.log(LogService.LOG_ERROR, "exception",e);
			e.printStackTrace();
		}
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

		// CONNECT DRIVER TO DEVICE .............................
		
		// from DOG
//		KnxActuatorInstance instance=new KnxActuatorInstance(this.network, 
//				(ControllableDevice) this.context.getService(reference));
//		synchronized(this.connectedDriver){
//			this.connectedDriver.add(instance);
//		}

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
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void removedService(ServiceReference reference, Object service) {
		// TODO Auto-generated method stub
		
	}

}

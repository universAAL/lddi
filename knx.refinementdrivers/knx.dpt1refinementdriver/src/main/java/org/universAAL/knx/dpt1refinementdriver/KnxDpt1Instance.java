package org.universAAL.knx.dpt1refinementdriver;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver;
import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxNetwork;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.knx.devicecategory.KnxDpt1;
import org.universAAL.knx.devicemodel.KnxDevice;
import org.universAAL.knx.devicemodel.KnxDpt1Device;

/**
 * Working instance of the KnxDpt1 driver. Registers a service/device.
 * Tracks on the KNX device service passed in the attach method in KnxDpt1RefinementDriver class. 
 * When the KNX device disappears, this service/device is unregistered.
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Instance extends KnxDriver implements KnxDpt1, ServiceTrackerCustomizer, 
Constants, ManagedService {

	private static final String KNX_DRIVER_CONFIG_NAME = "knx.refinementdriver";
	private BundleContext context;
	private LogService logger;
	private Dictionary<String, String> knxIsoMappingProperties;

	// called from the driver.attach method
	public KnxDpt1Instance(BundleContext c, ServiceReference sr, KnxNetwork network, LogService log) throws InvalidSyntaxException {
		super(network);

		this.context=c;
		this.logger=log;
		
//		this.registerManagedService();
		
		ServiceTracker st=new ServiceTracker(c,sr, this);
		st.open();
		
	}
	
	/**
	 * Register this class as Managed Service
	 */
	public void registerManagedService() {
		Properties propManagedService=new Properties();
		propManagedService.put(org.osgi.framework.Constants.SERVICE_PID, 
				KNX_DRIVER_CONFIG_NAME);
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
	}

	/*** ServiceTracker ***/
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		// found device service
		KnxDpt1Device knxDev = (KnxDpt1Device) context.getService(reference);
		
		// register driver in knx.network driverList
		setDevice( (KnxDevice) knxDev);
		
		
		
		// knx device and knx driver are coupled now
		// Regeln für die decodierung in device_category ??
		
		
		// passendes ISO device erzeugen..........................
		if ( !this.knxIsoMappingProperties.isEmpty() && this.device.getGroupAddress() != null ) {
			
			String mappingParam = this.knxIsoMappingProperties.get(this.device.getGroupAddress());
			
			if (mappingParam != null) {

				// do something..................
				this.logger.log(LogService.LOG_INFO, "KNX - ISO mapping parameter: " + mappingParam);
				
				
				
				
				
				
				
			} else {
				this.logger.log(LogService.LOG_ERROR, "No configuration parameter found for Knx" +
						" to Iso device mapping for knx device " + this.device.getGroupAddress());
			}
		} else {
			this.logger.log(LogService.LOG_ERROR, "No configuration parameter found for " +
					"Knx to ISO mapping!");
		}

		
		
		
		return knxDev;
	}

	
	/* (non-Javadoc)
	 * @see it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver#setDevice(org.universAAL.knx.devicemodel.KnxDevice)
	 */
	@Override
	public void setDevice(KnxDevice device) {
		super.setDevice(device);
	}

	
	public void removedService(ServiceReference reference, Object service) {
		// removed device service
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTrackerCustomizer#modifiedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	public void modifiedService(ServiceReference reference, Object service) {
		this.logger.log(LogService.LOG_INFO, "Tracked knx device service was modified. Going to update the KnxDpt1Instance");
		removedService(reference, service);
		addingService(reference);			
	}
	
	
	
	/*** from device category ***/
	/* (non-Javadoc)
	 * @see org.universAAL.knx.devicecategory.KnxDpt1#receivePacket(long)
	 */
	public byte[] receivePacket(long timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.universAAL.knx.devicecategory.KnxDpt1#sendPacket(byte[])
	 */
	public void sendPacket(byte[] data) {
		// TODO Auto-generated method stub

	}

	
	/*** from KnxDriver ***/
	/* (non-Javadoc)
	 * @see it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver#newMessageFromHouse(java.lang.String, byte[])
	 */
	@Override
	public void newMessageFromHouse(String deviceAddress, byte[] message) {
		
		// und wos tua ma jetzt??
		
		this.logger.log(LogService.LOG_INFO, "Incoming message " + message + " from address " + 
				deviceAddress);
		
		
	}

	/* (non-Javadoc)
	 * @see it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver#specificConfiguration()
	 */
	@Override
	protected void specificConfiguration() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.osgi.service.cm.ManagedService#updated(java.util.Dictionary)
	 */
	public void updated(Dictionary properties) throws ConfigurationException {
		this.logger.log(LogService.LOG_INFO, "KnxDpt1Driver updated. " +
				"Mapping from KNX device to ISO 11073 device. " + properties);

		if (properties != null) {
			this.knxIsoMappingProperties = properties;
		} else {
			this.logger.log(LogService.LOG_ERROR, "No configuration found for Knx to ISO device mapping!");
		}
			
	}



}

package org.universAAL.knx.dpt1refinementdriver;

import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver;
import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxNetwork;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.knx.devicecategory.KnxDpt1;
import org.universAAL.knx.devicemodel.KnxDevice;

/**
 * Working instance of the KnxDpt1 driver. Registers a service/device.
 * Tracks on the KNX device service passed in the attach method in KnxDpt1RefinementDriver class. 
 * When the KNX device disappears, this service/device is unregistered.
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Instance extends KnxDriver implements KnxDpt1, ServiceTrackerCustomizer, Constants {

	private BundleContext context;
	private LogService logger;

	// called from the driver.attach method
	public KnxDpt1Instance(BundleContext c, ServiceReference sr, KnxNetwork network, LogService log) throws InvalidSyntaxException {
		super(network);

		this.context=c;
		this.logger=log;
		
		ServiceTracker st=new ServiceTracker(c,sr, this);
		st.open();
		
	}
	
	/*** ServiceTracker ***/
	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
	 */
	public Object addingService(ServiceReference reference) {
		// found device service
		KnxDevice knxDev = (KnxDevice) context.getService(reference);
		
		// register driver in knx.network driverList
		setDevice(knxDev);
		
		
		
		// knx device and knx driver are coupled now
		// Regeln für die decodierung
		// passendes ISO device erzeugen..........................
		
		
		
		
		return knxDev;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	
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
		
		
		
		
	}

	/* (non-Javadoc)
	 * @see it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver#specificConfiguration()
	 */
	@Override
	protected void specificConfiguration() {
		// TODO Auto-generated method stub
		
	}



}

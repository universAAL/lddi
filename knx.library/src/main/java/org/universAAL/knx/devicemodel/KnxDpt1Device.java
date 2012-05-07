package org.universAAL.knx.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.knx.devicecategory.KnxDpt1;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Device extends KnxDevice implements KnxDpt1 {

	/**
	 * empty constructor for factory
	 */
	public KnxDpt1Device() {
		super();
	}

	@Override
	public void newMessageFromHouse(String deviceAddress, byte event) {
		this.logger.log(LogService.LOG_INFO, "Device " + this.getDeviceId() + " got event: " + event);
		
//		this.newEventFromHardware(event);
		//send event to my driver...
	}

	public void newEventFromHardware(byte event) {
		// TODO Auto-generated method stub
		
	}


//	/* (non-Javadoc)
//	 * @see org.universAAL.knx.devicecategory.KnxDpt1#receivePacket(long)
//	 */
//	public byte[] receivePacket(long timeout) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.universAAL.knx.devicecategory.KnxDpt1#sendPacket(byte[])
//	 */
//	public void sendPacket(byte[] data) {
//		// TODO Auto-generated method stub
//		
//	}

}

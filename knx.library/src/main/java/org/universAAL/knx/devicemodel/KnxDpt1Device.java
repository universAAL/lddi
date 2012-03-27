package org.universAAL.knx.devicemodel;

import org.osgi.service.log.LogService;
import org.universAAL.knx.devicecategory.KnxDpt1;
import org.universAAL.knx.utils.*;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxDpt1Device extends KnxDevice implements KnxDpt1 {

	/**
	 * @param knxDeviceProperties
	 * @param logger
	 */
	public KnxDpt1Device(KnxGroupAddress knxDeviceProperties, LogService logger) {
		super(knxDeviceProperties, logger);
		// TODO Auto-generated constructor stub
	}

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

}

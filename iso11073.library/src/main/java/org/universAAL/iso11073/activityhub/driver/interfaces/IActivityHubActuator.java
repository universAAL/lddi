package org.universAAL.iso11073.activityhub.driver.interfaces;

/**
 * An actuator device is controllable. It should implement this interface.
 *  
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public interface IActivityHubActuator {
	/***
	 * The specific drivers instances have to implement this method
	 * to receive events from the consuming bundles (e.g. uAAL context bus events) 
	 * 
	 * @param deviceAddress  address of the device or the group that fire the message
	 * @param message array of byte containing the information of the status or command
	 */
	public abstract void newActuatorCommand(String deviceId,byte[] message);
	
}

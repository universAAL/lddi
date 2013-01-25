package org.universAAL.lddi.knx.interfaces;

import org.universAAL.lddi.knx.devicemodel.KnxDevice;
import org.universAAL.lddi.knx.utils.KnxCommand;

public interface KnxNetwork {
	public static String MANUFACTURER = "KONNEX";
	public static String GROUP_ADDRESS = "groupAddress";
//	public static String NOTIFICATION_ADDRESS = "notificationAddress";
	public static String COMMAND_NAME = "realName";
	public static String COMMAND_VALUE = "hexValue";
	
	public static String TIME = "time";
	public static char DAFAULT_READ_CHAR = '4';
	public static char DAFAULT_STATUS_CHAR = '8';
//	public static Object NOTIFICATION_NAME = "notificationName";
	public void requestState(String deviceId);
	public void sendCommand(String deviceId,boolean command);
	public void sendCommand(String deviceId,boolean command, KnxCommand commandType);
	public void addDevice(String deviceId,KnxDevice device);
	public void removeDevice(String deviceId,KnxDevice device);
	//public KnxConfiguration parseConfiguration(Properties configuration);
	
}

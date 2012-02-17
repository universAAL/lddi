package it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces;

import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxEncoder.KnxMessageType;

public interface KnxNetwork {
	public static String MANUFACTURER = "KONNEX";
	public static String GROUP_ADDRESS = "groupAddress";
	public static String NOTIFICATION_ADDRESS = "notificationAddress";
	public static String COMMAND_NAME = "realName";
	public static String COMMAND_VALUE = "hexValue";
	
	public static String TIME = "time";
	public static char DAFAULT_READ_CHAR = '4';
	public static char DAFAULT_STATUS_CHAR = '8';
	public static Object NOTIFICATION_NAME = "notificationName";
	public void readState(String device);
	public void sendCommand(String device,String command);
	public void sendCommand(String device,String command, KnxMessageType messageType);
	public void addDriver(String device,KnxDriver driver);
	public void removeDriver(String device,KnxDriver driver);
	//public KnxConfiguration parseConfiguration(Properties configuration);
	
}

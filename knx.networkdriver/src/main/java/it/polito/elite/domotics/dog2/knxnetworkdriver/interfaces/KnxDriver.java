package it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.universAAL.knx.devicemodel.KnxDevice;

//import it.polito.elite.domotics.dog2.doglibrary.DeviceState;
//import it.polito.elite.domotics.dog2.doglibrary.DogDeviceCostants;
//import it.polito.elite.domotics.dog2.doglibrary.DogDriver;
//import it.polito.elite.domotics.dog2.doglibrary.DogElementDescription;
//import it.polito.elite.domotics.dog2.doglibrary.devicecategory.ControllableDevice;
import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxCommand;
import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxNotification;
//import it.polito.elite.domotics.model.notification.StateChangeNotification;



/**
 * This abstract class is designed to help developing a knx driver.
 * It stores information about the groups addresses and commands retrieved from a KnxConfiguration object.
 * It registers all the groups address to the gateway (KnxNetwork.addDriver method).
 * It provides an service tracker for the attached device service.
 * 
 * @author Emiliano Castellina
 *
 */
public abstract class KnxDriver 
//extends ServiceTracker
//implements DogDriver 
{
	protected KnxNetwork network;
	protected KnxDevice device;
	

	/**Driver state*/
//	protected DeviceState currentState;
	protected Set<String> groupAddressList;
	protected Map<String,KnxCommand> knxCommands;
	/**Map that stores notifications. Key: notification name, Value=KnxCommand containing notification details */
	protected HashMap<String, Set<KnxNotification>> knxNotifications;
	/**Map that stores generic notifications. Key: groupAddress, Value=KnxCommand containing notification details */
	protected HashMap<String, Set<KnxNotification>> knxGroupAddressNotificationsMap;
	/**Map that stores generic notifications. Key: groupAddress, Value=KnxCommand containing notification details */
	protected HashMap<String, Set<KnxNotification>> knxGroupAddressHexNotificationsMap;
	/***
	 * The specific drivers have to implement this method to receive low level messages from the network
	 * @param deviceAddress  address of the device or the group that fire the message
	 * @param message array of byte containing the information of the status or command
	 */
	public abstract void newMessageFromHouse(String deviceAddress,byte[] message);
	

	public KnxDriver(KnxNetwork network
//			, BundleContext c, ServiceReference sr 
//			,ServiceTrackerCustomizer stCustomizer
//			,ControllableDevice device
			){

		// initialize service tracker on device (sr)
//		super(c, sr, null);
//		open();
		
		// my knx.network instance
	    this.network=network;
	    
//	    this.device=device;
	    //create the needed objects
	    this.knxCommands=new HashMap<String, KnxCommand>();
	    this.groupAddressList=new HashSet<String>();
	    this.knxNotifications=new HashMap<String, Set<KnxNotification>>();
	    this.knxGroupAddressHexNotificationsMap=new HashMap<String, Set<KnxNotification>>();
	    this.knxGroupAddressNotificationsMap=new HashMap<String, Set<KnxNotification>>();
//	    this.configure();
	
	}

	/**
	 * Add this driver to the driver list in knx network driver
	 * 
	 * @param device the device to set
	 */
	public final void setDevice(KnxDevice device) {
		this.device = device;
		
		// add driver to driverList in knx.networkdriver
		this.network.addDriver(this.device.getGroupAddress(), this);
	}
	
	/**
	 * Remove this driver from the driver list in knx network driver
	 * 
	 * @param device the device to set
	 */
	public final void removeDriver() {
//		this.device = device;
		
		// add driver to driverList in knx.networkdriver
		this.network.removeDriver(this.device.getGroupAddress(), this);
	}
	
	/***
	 * Default method called to configure the KNX devices
	 */
	protected  void configure(){//list of group addresses
//	this.groupAddressList=this.device.getDeviceDescriptor().getDevSimpleConfigurationParams().get(KnxNetwork.GROUP_ADDRESS);
	//get specific command parameters. 
//	Set<DogElementDescription> commandsSpecificParameters=this.device.getDeviceDescriptor().getDevCommandSpecificParams();
	//get the notification parameters.
//	Set<DogElementDescription> notificationsSpecificParameters=this.device.getDeviceDescriptor().getDevNotificationSpecificParams();
	//add the specific command to the command list. They overwrite the default commands
//	for(DogElementDescription command:commandsSpecificParameters){
//		String realName=command.getElementParams().get(KnxNetwork.COMMAND_NAME);
//		String groupAddress= command.getElementParams().get(KnxNetwork.GROUP_ADDRESS);
//		String hexValue= command.getElementParams().get(KnxNetwork.COMMAND_VALUE);
//		if(realName!=null && groupAddress !=null){
//			if(hexValue!=null){
//				this.knxCommands.put(realName, new KnxCommand(realName,groupAddress,hexValue));
//			}else {
//				this.knxCommands.put(realName, new KnxCommand(realName,groupAddress));
//			}
//		}
//	}
	//add the notification to the command list
//	for(DogElementDescription notification:notificationsSpecificParameters){
//		String realName=DogDeviceCostants.NOTIFICATIONS_PREFIX+notification.getElementType() ;
//		String groupAddress= notification.getElementParams().get(KnxNetwork.GROUP_ADDRESS);
//		String hexValue= notification.getElementParams().get(KnxNetwork.COMMAND_VALUE);
//		KnxNotification currentNotification;
//		//check the hexValue and distinguish two cases:
//		//the hex value is not null -->add the notification to the knxGroupAddressHexNotificationsMap
//		//the hex value is null --> add the notification to the knxGroupAddressNotificationsMap
//		if(hexValue!=null){
//			currentNotification=new KnxNotification(realName,groupAddress,hexValue);
//			KnxDriver.addNotificationToMap(this.knxGroupAddressHexNotificationsMap, currentNotification, currentNotification.getAddressHex());
//			
//		} else {
//			currentNotification=new KnxNotification(realName,groupAddress);
//			KnxDriver.addNotificationToMap(this.knxGroupAddressNotificationsMap, currentNotification, currentNotification.getGroupAddress());			
//		}
//		KnxDriver.addNotificationToMap(knxNotifications, currentNotification, currentNotification.getName());
//
//		
//		**************************
//		this.network.addDriver(groupAddress, this);
//		**************************
//	}
	
	}

	/**This method must be implemented to modify the configuration in according with the 
	 * specific device needs*/
	protected abstract void specificConfiguration();
	
	public void detachDriver(String id) {
		// TODO Auto-generated method stub
		
	}

    /**
     * This method remove, if present, the "0x" prefix of the hexValue variable
     * @param hexValue string containing an hex value
     * @return the same string without prefix
     */
	public static String clearHexValue(String hexValue) {
		String correctHexValue;
		if(hexValue.startsWith("0x")){
			correctHexValue=hexValue.substring(2);
		}else
		{
			correctHexValue=hexValue;
		}
		return correctHexValue;
	}
	
	/**
	 * This method check if the map contains the key, retrieves or create an HashSet<KnxNotification> associated to the key,
	 * add the notification to the set
	 * @param notificationMap   
	 * @param notification
	 * @param key
	 */
	public static void addNotificationToMap(HashMap<String, Set<KnxNotification>> notificationMap,KnxNotification notification,String key){
		Set<KnxNotification> notificationSet=notificationMap.get(key);
		if(notificationSet==null){
			notificationSet=new HashSet<KnxNotification>();
			notificationMap.put(key,notificationSet);
		}
		notificationSet.add(notification);
	}
	
	/**
	 * Try to retrieve the real state from knx device
	 */
	public void getRealState() {
		//Is there any readable addresses?
			//device can have more than one notification address.
			//What we should do?
			//Method 1.Send only the first.
			//Method 2. Send all the read requests.
//		if(this.knxNotifications.containsKey(StateChangeNotification.notificationName)){
//			//Implemented method2
//			for(KnxNotification notificationAdd:this.knxNotifications.get(StateChangeNotification.notificationName)){
//				this.network.readState(notificationAdd.getGroupAddress());
//			}
//		}
		
	}

}

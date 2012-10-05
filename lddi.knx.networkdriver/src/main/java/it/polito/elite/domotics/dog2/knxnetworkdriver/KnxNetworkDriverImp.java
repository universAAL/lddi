package it.polito.elite.domotics.dog2.knxnetworkdriver;

//import it.polito.elite.dog2.doglibrary.util.DogLogInstance;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

//import org.eclipse.osgi.framework.console.CommandInterpreter;
//import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;
import org.universAAL.lddi.knx.devicemodel.KnxDevice;
import org.universAAL.lddi.knx.networkdriver.KnxNetwork;
import org.universAAL.lddi.knx.utils.KnxEncoder.KnxMessageType;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 *
 */
public final class KnxNetworkDriverImp implements ManagedService, KnxNetwork
//,CommandProvider 
{
	
	/**OSGi Framework*/
	BundleContext context;
	LogService logger;
	
//	public static String manifacturer="KNX";
	
	private String knxGatewayIp;
	private String multicastIp;
	private int knxGatewayPort;
	private int myUdpPort;
	private int sleepTime;
	private int timeout;
	private long checkingTime;
	
	KnxCommunication network;
	public ServiceRegistration regServiceKnx = null;
	
	/**
	 * List of devices per groupAddress
	 * key = groupAddress
	 * value = Set of devices
	 */
	private Hashtable<String, Set<KnxDevice>> deviceList;
	
	/**
	 * Class constructor
	 *
	 * @param context   OSGi framework
	 * @param log 
	 */
	public KnxNetworkDriverImp(BundleContext context, LogService log){
		this.context=context;
		this.logger=log;
		this.network=null;
		
		this.deviceList = new Hashtable<String, Set<KnxDevice>>();

		//		this.context.registerService(CommandProvider.class.getName(), this, null);
		//		TODO: change from equinox to felix shell command (uAAL is running on felix)
		//		http://felix.apache.org/site/apache-felix-shell.html#ApacheFelixShell-creating

		this.registerManagedService();
		this.logger.log(LogService.LOG_DEBUG,"KnxNetworkDriverImp started!");
	}

	/***
	 * Register this class as Managed Service
	 */
	private void registerManagedService() {
		Properties propManagedService=new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle().getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
	}

//	@Override
	public void updated(@SuppressWarnings("unchecked") Dictionary settings) throws ConfigurationException {
		this.logger.log(LogService.LOG_DEBUG, "KnxNetworkDriverImp.updated: " + settings);

		try {
			if (settings != null){
				this.knxGatewayIp = (String) settings.get("knxGatewayIp");
				this.knxGatewayPort = Integer.valueOf((String) settings.get("knxGatewayPort"));	
				this.multicastIp = (String)settings.get("multicastIp");
				this.myUdpPort = Integer.valueOf((String) settings.get("udpPort"));		
						
				this.setSleepTime(Integer.parseInt((String) settings.get("sleepTime")));	
				this.setTimeout(Integer.parseInt((String) settings.get("timeoutTime")));	
				this.setCheckingTime(Long.parseLong((String) settings.get("checkingTime")));
				// properties numTry and myIP not used yet
				
				if(this.network!=null){
					this.unRegister();
//					this.network.stopCommunication();
					this.network = null;
				}
				
				this.network=new KnxCommunication(this);
//				this.network.start();
				this.network.init();
				
			} else {
				this.logger.log(LogService.LOG_ERROR,"Unconfigured knx network driver!");
			}
		}
		catch (NumberFormatException nfe){
			this.logger.log(LogService.LOG_ERROR, "NumberFormatException\n" + nfe.getMessage());
		}
		catch (Exception e){
			this.logger.log(LogService.LOG_ERROR, e.getMessage());
		}
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void networkConnected() {
		this.regServiceKnx=this.context.registerService(KnxNetwork.class.getName(), this,null);
		
	}
	
	public void networkDisconnected() {
		this.unRegister();
	}

	public LogService getLogger() {
		return this.logger;
	}

	public int getHousePort() {
		return this.knxGatewayPort;
	}

	public void setCheckingTime(long checkingTime) {
		this.checkingTime = checkingTime;
	}

	public long getCheckingTime() {
		return checkingTime;
	}

	public String getHouseIp() {
		
		return this.knxGatewayIp;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public String getMulticastIp() {
		return this.multicastIp;
	}

	public int getMyUdpPort() {
		return myUdpPort;
	}

	/**
	 * Forward the message from the house to the device; mapping on groupAddress
	 * 
	 * @param groupAddress the knx groupAddress
	 * @param b knx command/status bytes (representing e.g. on, off)  
	 */
	public void newMessageFromHouse(String groupAddress, byte event) {
		if ( this.deviceList.containsKey(groupAddress) ) {
			synchronized(this.deviceList)
			{
				for( KnxDevice device : this.deviceList.get(groupAddress) ) {
					device.newMessageFromHouse(groupAddress, event);
			}
			}
		}
	}

	public void unRegister() {
		// stop reader thread
		this.network.stopCommunication();
		
		// unregister network service
		if(this.regServiceKnx!=null){
			this.regServiceKnx.unregister();
			this.regServiceKnx = null;
		}
	}
	
	/**
	 * Devices can register here to get events from the knx bus
	 */
	public void addDevice(String deviceId, KnxDevice device) {

		Set<KnxDevice> devices = this.deviceList.get(deviceId);
		if ( devices == null ) {
			devices = new HashSet<KnxDevice>();
			
			synchronized(this.deviceList)
			{
				this.deviceList.put(deviceId, devices);
			}
		}
		devices.add(device);
		this.logger.log(LogService.LOG_DEBUG, "New device added for groupAddress " + deviceId);
	}
	

	/**
	 * Devices can unregister here to stop getting events from the knx bus
	 */
	public void removeDevice(String deviceId, KnxDevice device) {
		Set<KnxDevice> devices = this.deviceList.get(deviceId);
		devices.remove(device);
		this.logger.log(LogService.LOG_INFO, "Removed device for groupAddress " + deviceId);
	}
	
	
	public void sendCommand(String deviceId, String command) {
		this.sendCommand(deviceId,command,KnxMessageType.WRITE);
		
	}
	
	public void sendCommand(String device, String command,
			KnxMessageType messageType) {
		this.network.sendCommand(device, command,messageType);
		
	}
	/*@Override
	public KnxConfiguration parseConfiguration(Properties propConfiguration) {
		//initialize knx configuration object
		KnxConfiguration knxConf=null;
		//Check not null configuration
		if(propConfiguration!=null){
			//Create a vector for store knxCommand objects
			Vector<KnxCommand> commands=new Vector<KnxCommand>();
			
			//Retrieve the command list from propConfiguration
			Vector<Properties> commandsList= (Vector<Properties>) propConfiguration.get(DogDeviceCostants.DEVICEFUNCTIONALITY);
			//Check not null command list
			if(commandsList!=null){
				//Creates KnxCommand objects and adds them to the vector
				for(Properties propCommand:commandsList){
					KnxCommand knxCmd=new KnxCommand(propCommand);
					commands.add(knxCmd);
					
				}
				
			}
			//There is any useful configuration data?
			if(commands.size()>0 ||propConfiguration.size()>0){
				knxConf=new KnxConfiguration(commands, propConfiguration);
			}
			
		}
		return knxConf;
	}
	*/
	
	public void readState(String deviceId) {
		this.network.readState(deviceId);
		
	}
	
//	/*Commandline help*/
//	public String getHelp() {
//		
//		return "KNX usage:  knx address-command (e.g. knx 2/3/0-80)";
//	}
//	
//	/*Commandline command*/
//	public void _knx(CommandInterpreter ci){
//		  String devicecommand=ci.nextArgument();
//		  String splittedString[]=devicecommand.split("-");
//		  String device=splittedString[0];
//		  String command=null;
//		  if(splittedString.length>1){
//			  command=splittedString[1];
//		  }
//		  if(command!=null){
//			  this.network.sendCommand(device, command);
//			  ci.print(String.format("KNX  device %s  command %s",device,command));
//		  }
//		  else{
//			  this.network.readState(device);
//			  ci.print(String.format("KNX  request device state  %s",device));
//		  }
//		  
//		  
//	}
	
}

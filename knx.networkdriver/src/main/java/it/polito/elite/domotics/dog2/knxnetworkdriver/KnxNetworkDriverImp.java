package it.polito.elite.domotics.dog2.knxnetworkdriver;

//import it.polito.elite.dog2.doglibrary.util.DogLogInstance;
import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxEncoder.KnxMessageType;
import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxDriver;
import it.polito.elite.domotics.dog2.knxnetworkdriver.interfaces.KnxNetwork;

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

public class KnxNetworkDriverImp implements ManagedService, KnxNetwork
//,CommandProvider 
{
	
	/**OSGi Framework*/
	BundleContext context;
	LogService logger;
	
	public static String manifacturer="KNX";
	

	
	private String houseIp;
	private String multicastIp;
	private int housePort;
	private int myUdpPort;
	private int sleepTime;
	private int timeout;
	private long checkingTime;
	
	KnxCommunication network;
	private ServiceRegistration regServiceKnx;
	private Hashtable<String, Set<KnxDriver>> driverList;
	
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
		this.driverList=new Hashtable<String, Set<KnxDriver>>();

//		this.context.registerService(CommandProvider.class.getName(), this, null);
//		TODO: change from equinox to felix shell command (uAAL is running on felix)
//		http://felix.apache.org/site/apache-felix-shell.html#ApacheFelixShell-creating
		
		this.registerManagedService();
		
	}
   /***
    * Register this class as Managed Service
    */
	private void registerManagedService() {
		Properties propManagedService=new Properties();
		propManagedService.put(Constants.SERVICE_PID, this.context.getBundle().getSymbolicName());
		this.context.registerService(ManagedService.class.getName(), this, propManagedService);
		
	}

	//@Override
	public void updated(@SuppressWarnings("rawtypes") Dictionary settings) throws ConfigurationException {
		this.logger.log(LogService.LOG_INFO, "KnxNetworkDriverImp.updated: " + settings);
//        System.out.println("******** KnxNetworkDriverImp.updated *********");

		try {
			if (settings != null){
				this.houseIp = (String) settings.get("houseIp");
				this.housePort = Integer.valueOf((String) settings.get("housePort"));	
				this.multicastIp = (String)settings.get("multicastIp");
				this.myUdpPort = Integer.valueOf((String) settings.get("udpPort"));		
						
				this.setSleepTime(Integer.parseInt((String) settings.get("sleepTime")));	
				this.setTimeout(Integer.parseInt((String) settings.get("timeoutTime")));	
				this.setCheckingTime(Long.parseLong((String) settings.get("checkingTime")));
				
				if(this.network!=null){
					this.network.stopCommunication();
				}
				
				this.network=new KnxCommunication(this);
				this.network.start();
				
			} else {
//				System.out.println("********* Unconfigured knx network driver! *******");
				this.logger.log(LogService.LOG_ERROR,"Unconfigured knx network driver!");
			}
		}
		catch (NumberFormatException nfe){
			this.logger.log(LogService.LOG_ERROR, "NumberFormatException\n" + nfe.getMessage());
//	        System.out.println("******** NumberFormatException *********");
//	        System.out.println(nfe.getMessage());
		}
		catch (Exception e){
			this.logger.log(LogService.LOG_ERROR, e.getMessage());
//	        System.out.println("******** Exception *********");
//	        System.out.println(e.getMessage());
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
		this.regServiceKnx.unregister();
		
	}

	public LogService getLogger() {
		return this.logger;
	}

	public int getHousePort() {
		return this.housePort;
	}

	public void setCheckingTime(long checkingTime) {
		this.checkingTime = checkingTime;
	}

	public long getCheckingTime() {
		return checkingTime;
	}

	public String getHouseIp() {
		
		return this.houseIp;
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
		// TODO Auto-generated method stub
		return myUdpPort;
	}

	/**Forward the message from the house */
	public void newMessageFromHouse(String knxDevice, byte[] statusByte) {
		if(this.driverList.containsKey(knxDevice)){
			synchronized(this.driverList)
			{
			for(KnxDriver driver:this.driverList.get(knxDevice)){
				driver.newMessageFromHouse(knxDevice, statusByte);
			}
			}
		}
		
	}

	public void unRegister() {
		if(this.regServiceKnx!=null){
			this.regServiceKnx.unregister();
		}
	}
	public void addDriver(String device, KnxDriver driver) {
		Set<KnxDriver> drivers=this.driverList.get(device);
		if(drivers==null){
			drivers=new HashSet<KnxDriver>();
			
			synchronized(this.driverList)
			{
			this.driverList.put(device, drivers);
			}
		}
		drivers.add(driver);
		
	}
	public void removeDriver(String device, KnxDriver driver) {
		// TODO Auto-generated method stub
		
	}
	public void sendCommand(String device, String command) {
		this.sendCommand(device,command,KnxMessageType.WRITE);
		
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
	
	public void readState(String device) {
		this.network.readState(device);
		
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
	
	public void sendCommand(String device, String command,
			KnxMessageType messageType) {
		this.network.sendCommand(device, command,messageType);
		
	}

	
	

}

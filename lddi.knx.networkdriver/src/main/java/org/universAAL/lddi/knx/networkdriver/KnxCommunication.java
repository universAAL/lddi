package org.universAAL.lddi.knx.networkdriver;

import org.universAAL.lddi.knx.utils.KnxCommand;

/**
 * Manages reader and writer for communication to the knx gateway.
 * 
 * This class is no longer a thread because the KNX gateway isn't pingable.
 * Therefore this class doesn't check the gateway continuously.
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxCommunication
//extends Thread
{
	
	KnxNetworkDriverImp driver;
	private KnxWriter writer;
	private KnxReader reader;
	private Thread readerThread;


	public KnxCommunication(KnxNetworkDriverImp driver){
//		super();
		this.driver=driver;
//		this.running=true;
	}

//	@Override
//	public void run() {
	public void init() {
//		while(running){
			
			// Siemens KNX/IP gateway N146 is not pingable!!!!!!!!!
			//
			//        boolean netReachable = false;
			//		while (!netReachable){
			//			if (this.testHouse())
			//				netReachable = true;
			//			else {
			//				try {
			//					this.driver.logger.log(LogService.LOG_WARNING,"Network unreachable. Trying again in " +
			//							this.driver.getSleepTime() /1000 + " seconds!");
			//					Thread.sleep(this.driver.getSleepTime());
			//				} 
			//				catch (InterruptedException e) {
			//					e.printStackTrace();
			//				}
			//			}	
			//		}

			
			// Starting the server listening from the gateway
			reader = new KnxReader(this.driver);
			readerThread = new Thread(reader);
			readerThread.start();

			// Starting the server writing to the gateway
			writer = new KnxWriter(this.driver);

			//***
			this.driver.networkConnected();


//		try {
//			boolean goOnChecking = true;
//			while (goOnChecking){					
//				Thread.sleep(this.driver.getCheckingTime());
//
////				InetAddress houseAddress = InetAddress.getByName(this.driver.getHouseIp());
////				if (!houseAddress.isReachable(this.driver.getTimeout())){
////
////					// House is unreachable
////					goOnChecking = false;
////					this.driver.logger.log(LogService.LOG_ERROR,"Connection down!");
////					
////					
////			}
//		}
//		}
//		catch(Exception e) {
//			this.driver.logger.log(LogService.LOG_ERROR,e.getMessage());
//		}
		
			
//			this.driver.networkDisconnected();
			
			
//		}// end testing network
	    
	}

	
//	/***
//	 * ping the gateway
//	 * @return boolean
//	 */
//	private boolean testHouse() {
//		boolean reacheble=false;
//		try {
//
//			this.driver.getLogger().log(LogService.LOG_INFO,"Testing if " + this.driver.getHouseIp() + " is reachable"); 
//
//			InetAddress houseAddress = InetAddress.getByName(this.driver.getHouseIp());
//			if (houseAddress.isReachable(this.driver.getTimeout())){
//				this.driver.getLogger().log(LogService.LOG_INFO,this.driver.getHouseIp()+ " REACHABLE!");
//				reacheble= true;
//			}
//		}catch (Exception e) {
//			this.driver.getLogger().log(LogService.LOG_ERROR, "exception", e);
//		}
//			
//		return reacheble;
//	}
	
	public void stopCommunication(){
//		this.running=false;

		this.readerThread.interrupt();
		//the MulticastSocket.receive is blocking -> manually close
		this.reader.stopReader();
	}

	public void sendCommand(String device, boolean command) {
		this.writer.write(device, command);
		
	}

	public void readState(String deviceId) {
		this.writer.requestDeviceStatus(deviceId);
		
	}

	public void sendCommand(String device, boolean command, KnxCommand commandType) {
		this.writer.write(device, command, commandType);
		
	}
	
	public byte[] getLastSentPacket() {
		return this.writer.getLastPacketSent();
	}

}

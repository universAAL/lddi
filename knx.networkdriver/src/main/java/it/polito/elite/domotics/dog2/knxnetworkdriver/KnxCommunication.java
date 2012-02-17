package it.polito.elite.domotics.dog2.knxnetworkdriver;

import it.polito.elite.domotics.dog2.knxnetworkdriver.KnxEncoder.KnxMessageType;

import java.net.InetAddress;

import org.osgi.service.log.LogService;

public class KnxCommunication extends Thread{
	
	KnxNetworkDriverImp driver;
	private KnxWriter writer;
	private KnxReader reader;
	private boolean running;
	
	
	public KnxCommunication(KnxNetworkDriverImp driver){
		super();
		this.driver=driver;
		this.running=true;
		
	}
	
	@Override
	public void run() {
		while(running){
			
		//Siemens KNX/IP gateway N146 is not pingable!
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


		

		// Starting the server listening from the house
		 reader = new KnxReader(this.driver);
		reader.start();
		
		// Starting the server listening from the house
		writer = new KnxWriter(this.driver);
		
		

		this.driver.networkConnected();


		// Inizio parte test rete successiva

		// Sleep per un po e poi ritesta rete: se � arrivato fin qui significa che la rete c'era,
		//	quindi se non la trova + deve riavviarsi a cercare (stile BTicino), mentre se la trova
		//		tutto va bene.
		try {
			boolean goOnChecking = true;
			while (goOnChecking){					
				Thread.sleep(this.driver.getCheckingTime());

//				InetAddress houseAddress = InetAddress.getByName(this.driver.getHouseIp());
//				if (!houseAddress.isReachable(this.driver.getTimeout())){
//
//					// House is unreachable
//					goOnChecking = false;
//					this.driver.logger.log(LogService.LOG_ERROR,"Connection down!");
//					
//					
//			}
		}
		}
		catch(Exception e) {
			this.driver.logger.log(LogService.LOG_ERROR,e.getMessage());
		}
		this.driver.networkDisconnected();
		}// end testing network
	    
	}

	private boolean testHouse() {
		boolean reacheble=false;
		try {

			this.driver.getLogger().log(LogService.LOG_INFO,"Testing if " + this.driver.getHouseIp() + " is reachable"); 

			InetAddress houseAddress = InetAddress.getByName(this.driver.getHouseIp());
			if (houseAddress.isReachable(this.driver.getTimeout())){
				this.driver.getLogger().log(LogService.LOG_INFO,this.driver.getHouseIp()+ " REACHABLE!");
				reacheble= true;
			}
		}catch (Exception e) {
			this.driver.getLogger().log(LogService.LOG_ERROR, "exception", e);
		}
			
		return reacheble;
	}
	
	public void stopCommunication(){
		this.running=false;
		this.reader.stopReader();
	}

	public void sendCommand(String device, String command) {
		this.writer.write(device, command);
		
	}

	public void readState(String device) {
		this.writer.read(device);
		
	}

	public void sendCommand(String device, String command,
			KnxMessageType messageType) {
		this.writer.write(device, command,messageType);
		
	}
	
	

}

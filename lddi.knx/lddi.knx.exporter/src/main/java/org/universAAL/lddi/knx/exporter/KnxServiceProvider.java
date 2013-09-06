package org.universAAL.lddi.knx.exporter;

import org.universAAL.middleware.container.ModuleContext;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxServiceProvider {

	private MyThread myThread;
	private static KnxManager knxManager;

	/**
	 * @param mc
	 * @param knxManager
	 */
	public KnxServiceProvider(ModuleContext mc, KnxManager knxManager) {

		this.knxManager = knxManager;

		// start simulator
		this.myThread = new MyThread();
		myThread.start();
	}

	/**
	 * Runnable helper class for simulating incoming service requests.
	 * Constantly looping.
	 * 
	 * @author Thomas Fuxreiter (foex@gmx.at)
	 */
	static class MyThread extends Thread {
	    private volatile boolean active = true;
		public MyThread() {	}
		public void run() {
			while (active) {
					try {
						Thread.sleep(10000);
						knxManager.sendSensorEvent("0/0/4", true);
						Thread.sleep(10000);
						knxManager.sendSensorEvent("0/0/4", false);
						
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/0", 5, 1, Float.parseFloat("35"));
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/0", 5, 1, Float.parseFloat("20"));
						
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/1", 5, 1, Float.parseFloat("35"));
						Thread.sleep(10000);
						knxManager.sendSensorEvent("1/0/1", 5, 1, Float.parseFloat("20"));
						
					} catch (InterruptedException e) {
				          System.out.println("Thread interrupted " + e.getMessage());
					}
				
			}
		}
		public void stopThread() {
			active = false;
		}
	}
	
	public void stop() {
		myThread.stopThread();
	}

}

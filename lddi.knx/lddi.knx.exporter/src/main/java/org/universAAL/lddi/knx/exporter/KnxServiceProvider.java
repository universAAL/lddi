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

		// start uAAL service provider
		this.myThread = new MyThread();
//		thread = new Thread(runnable);
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
						Thread.sleep(5000);
						knxManager.sendSensorEvent("0/0/4", true);
						Thread.sleep(5000);
						knxManager.sendSensorEvent("0/0/4", false);
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

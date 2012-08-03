package main;

import manager.Manager;
import events.EventIEEEManager;

public class Middleware {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		EventIEEEManager evtmanager = new EventIEEEManager();
		Manager manager = new Manager(evtmanager);
	}
}

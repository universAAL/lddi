package org.universAAL.lddi.abstraction;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.universAAL.lddi.abstraction.config.tool.DatapointConfigTool;
import org.universAAL.middleware.owl.ManagedIndividual;

public class DatapointIntegrationScreener extends ComponentIntegrator {
	
	private ArrayList<ExternalComponent[]> newComponents = new ArrayList<ExternalComponent[]>();
	private Hashtable<String, ExternalComponent> receivedEvents = new Hashtable<String, ExternalComponent>();
	private final Lock lock = new ReentrantLock();
	private final Condition publishCond = lock.newCondition();
	private final Condition componentsReplacedCond = lock.newCondition();
	
	
	DatapointIntegrationScreener() {
	}
	
	void showTool() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new DatapointConfigTool(newComponents, receivedEvents, lock, publishCond, componentsReplacedCond).setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void publish(ManagedIndividual ontResource, String propURI, Object oldValue) {
		if (propURI == null  ||  ontResource == null)
			return;
		
		lock.lock();
		try {
			receivedEvents.put(propURI, connectedComponents.get(ontResource.getURI()));
			publishCond.signal();
		} finally {
			lock.unlock();
		}
	}
	
	void stop() {
		lock.lock();
		try {
			newComponents.add(null);
			componentsReplacedCond.signal();
		} finally {
			lock.unlock();
		}
	}

	@Override
	void componentsReplaced(ExternalComponent[] components) {
		if (components == null)
			return;
		
		super.componentsReplaced(components);
		
		lock.lock();
		try {
			newComponents.add(components);
			componentsReplacedCond.signal();
		} finally {
			lock.unlock();
		}
	}

}

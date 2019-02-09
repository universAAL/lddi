package org.universAAL.lddi.abstraction.config.tool;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.universAAL.lddi.abstraction.Activator;
import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.owl.ManagedIndividual;

public class DatapointIntegrationScreener {
	
	private ArrayList<List<ExternalComponent>> newComponents = new ArrayList<List<ExternalComponent>>();
	private Hashtable<String, ExternalComponent> receivedEvents = new Hashtable<String, ExternalComponent>();
	private Hashtable<String, ExternalComponent> connectedComponents = new Hashtable<String, ExternalComponent>();
	private final Lock lock = new ReentrantLock();
	private final Condition publishCond = lock.newCondition();
	private final Condition componentsReplacedCond = lock.newCondition();
	
	
	public DatapointIntegrationScreener() {
	}
	
	public void showTool() {
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

	public void publish(ManagedIndividual ontResource, String propURI, Object oldValue) {
		if (propURI == null  ||  ontResource == null)
			return;
		
		lock.lock();
		try {
			receivedEvents.put(propURI, connectedComponents.get(ontResource.getURI()));
			publishCond.signal();
		} catch (Exception e) {
			LogUtils.logWarn(Activator.getMC(), this.getClass(), "publish", 
					new Object[] {"Got exception when trying to reflect external event on ",
							propURI, " of ", ontResource, " in the address test tool: " },
					e);
		} finally {
			lock.unlock();
		}
	}
	
	public void stop() {
		lock.lock();
		try {
			newComponents.add(null);
			componentsReplacedCond.signal();
		} finally {
			lock.unlock();
		}
	}

	public void integrateComponents(List<ExternalComponent> components) {
		if (components == null)
			return;
		
		for (ExternalComponent ec : components)
			connectedComponents.put(ec.getComponentURI(), ec);
		
		lock.lock();
		try {
			newComponents.add(components);
			componentsReplacedCond.signal();
		} finally {
			lock.unlock();
		}
	}

}

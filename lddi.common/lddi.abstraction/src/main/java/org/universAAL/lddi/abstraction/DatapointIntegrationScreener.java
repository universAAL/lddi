package org.universAAL.lddi.abstraction;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Hashtable;

import org.universAAL.lddi.abstraction.config.tool.DatapointConfigTool;
import org.universAAL.middleware.owl.ManagedIndividual;

public class DatapointIntegrationScreener extends ComponentIntegrator {
	
	private ArrayList<ExternalComponent[]> newComponents = new ArrayList<ExternalComponent[]>();
	private Hashtable<String, ManagedIndividual> receivedEvents = new Hashtable<String, ManagedIndividual>();
	
	
	DatapointIntegrationScreener() {
	}
	
	void showTool() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new DatapointConfigTool(newComponents, receivedEvents).setVisible(true);
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
		
		synchronized (receivedEvents) {
			receivedEvents.put(propURI, ontResource);
			notifyAll();
		}
	}
	
	void stop() {
		synchronized (newComponents) {
			newComponents.add(null);
			notifyAll();
		}
	}

	@Override
	void componentsReplaced(ExternalComponent[] components) {
		if (components == null)
			return;
		
		super.componentsReplaced(components);
		
		synchronized (newComponents) {
			newComponents.add(components);
			notifyAll();
		}
	}

}

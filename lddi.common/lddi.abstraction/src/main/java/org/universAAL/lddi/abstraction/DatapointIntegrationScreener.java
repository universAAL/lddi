package org.universAAL.lddi.abstraction;

import java.awt.EventQueue;

import org.universAAL.lddi.abstraction.config.tool.DatapointConfigTool;
import org.universAAL.middleware.owl.ManagedIndividual;

public class DatapointIntegrationScreener extends ComponentIntegrator {
	
	DatapointConfigTool theTool;
	
	DatapointIntegrationScreener() {
	}
	
	void showTool() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					theTool = new DatapointConfigTool();
					theTool.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	protected void publish(ManagedIndividual ontResource, String propURI, Object oldValue) {
		ExternalComponent ec = (ExternalComponent) ontResource.getProperty(PROP_CORRESPONDING_EXTERNAL_COMPONENT);
		theTool.newNotification(ec, propURI, ec.valueAsString(propURI, ontResource.getProperty(propURI)));
	}
	
	void stop() {
		theTool.setVisible(false);
		theTool.dispose();
	}

	@Override
	void componentsReplaced(ExternalComponent[] components) {
		super.componentsReplaced(components);
		theTool.componentsReplaced(components);
	}

}

/*
 Copyright 2008-2011 ITACA-TSB, http://www.tsb.upv.es
 Instituto Tecnologico de Aplicaciones de Comunicacion 
 Avanzadas - Grupo Tecnologias para la Salud y el 
 Bienestar (TSB)

 See the NOTICE file distributed with this work for additional 
 information regarding copyright ownership

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.universAAL.hw.exporter.zigbee.ha.devices.listeners;

import it.cnr.isti.zigbee.ha.device.api.lighting.OnOffLight;

import java.util.HashMap;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.devices.OnOffLightCallee;

/**
 * OSGi Service Listener that looks for a specific service published by the
 * abstraction layer and creates and updates the appropriate exporter callee.
 * 
 * @author alfiva
 * 
 */
public class OnOffLightListener implements ServiceListener {
    private final static String filter = "(" + Constants.OBJECTCLASS + "="
	    + OnOffLight.class.getName() + ")";
    private Object discovery = new Object();
    private BundleContext context;
    private HashMap onOffLigthDevices;
    private ServiceReference[] srs;
    private final static Logger log = LoggerFactory
	    .getLogger(OnOffLightListener.class);

    /**
     * Constructor to be used in the exporter. Configures the listener and
     * performs initial search.
     * 
     * @param context
     *            The OSGi context
     * @throws InvalidSyntaxException
     *             If the service to listen defined in the class is not
     *             appropriate
     */
    public OnOffLightListener(BundleContext context)
	    throws InvalidSyntaxException {
	this.context = context;
	synchronized (discovery) {
	    try {
		context.addServiceListener(this, filter);
	    } catch (InvalidSyntaxException e) {
		e.printStackTrace();
	    }
	    onOffLigthDevices = new HashMap();
	    srs = context.getServiceReferences(null, filter);
	    if (srs != null) {
		log.debug("Detected a new device(s) by {} ", this.getClass()
			.getName());
		for (int i = 0; i < srs.length; i++) {
		    doRegisteruAALService(srs[i]);
		}
	    }
	}
    }

    public void serviceChanged(ServiceEvent event) {
	synchronized (discovery) {
	    ServiceReference sr = event.getServiceReference();
	    switch (event.getType()) {
	    case ServiceEvent.REGISTERED: {
		doRegisteruAALService(sr);
	    }
		;
		break;

	    case ServiceEvent.MODIFIED: {
		// never modified
	    }
		;
		break;

	    case ServiceEvent.UNREGISTERING: {
		douAALUnregistering(sr);
	    }
		;
		break;
	    }
	}
    }

    private void doRegisteruAALService(ServiceReference sr) {
	log.debug("Creating a instance of device in uAAL");
	OnOffLight lightService = (OnOffLight) context.getService(sr);
	onOffLigthDevices.put(sr, new OnOffLightCallee(Activator.moduleContext,
		lightService));
    }

    private void douAALUnregistering(ServiceReference sr) {
	log.debug("Removing a instance of device in uAAL");
	((OnOffLightCallee) onOffLigthDevices.remove(sr)).unregister();
	context.ungetService(sr);
    }

    /**
     * Disconnects and removes all instantiated exported devices of this type.
     */
    public void douAALUnregistering() {
	log.debug("Removing all instances of these devices in uAAL");
	Iterator iter = onOffLigthDevices.keySet().iterator();
	for (; iter.hasNext();) {
	    ServiceReference sref = (ServiceReference) iter.next();
	    ((OnOffLightCallee) onOffLigthDevices.get(sref)).unregister();
	    iter.remove();
	    context.ungetService(sref);
	}
	onOffLigthDevices.clear();
    }

}

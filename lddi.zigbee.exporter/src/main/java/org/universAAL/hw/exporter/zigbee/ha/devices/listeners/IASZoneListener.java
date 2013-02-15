/*
 Copyright 2008-2014 ITACA-TSB, http://www.tsb.upv.es
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

import java.util.Iterator;


import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.devices.IASZoneCallee;
import org.universAAL.lddi.zigbee.commissioning.devices.api.IAS_ZoneAAL;

/**
 * OSGi Service Listener that looks for a specific service published by the
 * abstraction layer and creates and updates the appropriate exporter callee.
 * 
 * @author alfiva
 * 
 */
public class IASZoneListener extends ExporterListener {
    private final static Logger log = LoggerFactory
	    .getLogger(IASZoneListener.class);

    static {
	filter = "(" + Constants.OBJECTCLASS + "=" + IAS_ZoneAAL.class.getName()
		+ ")";
    }

    public IASZoneListener(BundleContext context)
	    throws InvalidSyntaxException {
	super(context);
    }

    @Override
    protected void doRegisteruAALService(ServiceReference sr) {
	log.debug("Creating a instance of device in uAAL");
	IAS_ZoneAAL service = (IAS_ZoneAAL) context.getService(sr);
	setOfDevices.put(sr, new IASZoneCallee(Activator.moduleContext,
		service));
    }

    @Override
    protected void douAALUnregistering(ServiceReference sr) {
	log.debug("Removing a instance of device in uAAL");
	((IASZoneCallee) setOfDevices.remove(sr)).unregister();
	context.ungetService(sr);
    }

    @Override
    public void douAALUnregistering() {
	log.debug("Removing all instances of these devices in uAAL");
	Iterator<ServiceReference> iter = setOfDevices.keySet().iterator();
	for (; iter.hasNext();) {
	    ServiceReference sref = (ServiceReference) iter.next();
	    ((IASZoneCallee) setOfDevices.get(sref)).unregister();
	    iter.remove();
	    context.ungetService(sref);
	}
	setOfDevices.clear();
    }
}

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

import it.cnr.isti.zigbee.ha.device.api.lighting.OnOffLight;

import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.universAAL.hw.exporter.zigbee.ha.Activator;
import org.universAAL.hw.exporter.zigbee.ha.devices.OnOffLightCallee;
import org.universAAL.middleware.container.utils.LogUtils;

/**
 * OSGi Service Listener that looks for a specific service published by the
 * abstraction layer and creates and updates the appropriate exporter callee.
 * 
 * @author alfiva
 * 
 */
public class OnOffLightListener extends ExporterListener {

    static {
	filter = "(" + Constants.OBJECTCLASS + "=" + OnOffLight.class.getName()
		+ ")";
    }

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
	super(context);
    }

    @Override
    protected void registeruAALService(ServiceReference sr) {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightListener.class,
		"registeruAALService",
		new String[] { "Creating a instance of device in uAAL" }, null);
	OnOffLight lightService = (OnOffLight) context.getService(sr);
	setOfDevices.put(sr, new OnOffLightCallee(Activator.moduleContext,
		lightService));
    }

    @Override
    protected void unregisteruAALService(ServiceReference sr) {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightListener.class,
		"registeruAALService",
		new String[] { "Removing a instance of device in uAAL"}, null);
	((OnOffLightCallee) setOfDevices.remove(sr)).unregister();
	context.ungetService(sr);
    }

    @Override
    public void unregisteruAALService() {
	LogUtils.logDebug(Activator.moduleContext, OnOffLightListener.class,
		"registeruAALService",
		new String[] { "Removing all instances of these devices in uAAL" }, null);
	Iterator<ServiceReference> iter = setOfDevices.keySet().iterator();
	for (; iter.hasNext();) {
	    ServiceReference sref = (ServiceReference) iter.next();
	    ((OnOffLightCallee) setOfDevices.get(sref)).unregister();
	    iter.remove();
	    context.ungetService(sref);
	}
	setOfDevices.clear();
    }

}

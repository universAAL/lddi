package org.universAAL.lddi.smarthome.exporter.devices;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.phThing.Device;

public abstract class AbstractCallee extends ServiceCallee implements GenericDevice{
    String shDeviceName;
    Device ontDevice;

    protected AbstractCallee(ModuleContext context,
	    ServiceProfile[] realizedServices) {
	super(context, realizedServices);
    }
    
}

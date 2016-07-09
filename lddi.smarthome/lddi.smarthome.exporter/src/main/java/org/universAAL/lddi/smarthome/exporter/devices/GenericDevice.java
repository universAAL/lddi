package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;

public interface GenericDevice {
    /**
     * This must be called from the single receiver once it determines it is up
     * to this wrapper to publish its state. It assumes the passed event is of
     * the right type and payload.
     * 
     * @param event
     *            The event data where to take info from
     */
    public void publish(Event event);

    /**
     * This will be called when its time to close. Free any resources (This is
     * intended for Callees, that should be closed).
     */
    public void unregister();
}

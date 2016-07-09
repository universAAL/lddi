package org.universAAL.lddi.smarthome.exporter;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventFilter;
import org.eclipse.smarthome.core.events.EventSubscriber;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.universAAL.lddi.smarthome.exporter.devices.GenericDevice;

public class Receiver implements EventSubscriber {

    public Set<String> getSubscribedEventTypes() {
	Set<String> c = Collections.singleton(ALL_EVENT_TYPES);
	return c;
    }

    public EventFilter getEventFilter() {
	return null;
    }

    public void receive(final Event event) {
	new Thread() {
	    public void run() {
		System.out.println("EVENT [SRC "+event.getSource()+"] [TOP "+event.getTopic()+"] [TYP "+event.getType()+"] [DATA "+event.getPayload()+"]");
		if (event instanceof ItemStateEvent) {
		    ItemStateEvent stateEvent = (ItemStateEvent) event;
		    GenericDevice dev = Activator.getSetOfDevices()
			    .get(stateEvent.getItemName());
		    if (dev != null) {
			dev.publish(event);// TODO stateevent
		    }
		} // else dont bother TODO log
	    }
	}.start();
    }

}

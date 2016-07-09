package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchActuator;

public class SwitchActuatorWrapper extends AbstractStatusValueCallee {
    public static final int TYPE_ID=18;
    
    public SwitchActuatorWrapper(ModuleContext context, String itemName) {
	super(context,
		getServiceProfiles(Activator.NAMESPACE + itemName + "handler",
			new SwitchActuator(Activator.NAMESPACE + itemName)),
		Activator.NAMESPACE + itemName + "handler");

	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchActuatorWrapper.class, "SwitchControllerWrapper",
		new String[] { "Ready to subscribe" }, null);
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new SwitchActuator(deviceURI);

	// Commissioning
	// TODO Set location based on tags?

	// Context reg
	ContextProvider info = new ContextProvider(deviceURI + "Provider");
	info.setType(ContextProviderType.controller);
	ContextEventPattern cep = new ContextEventPattern();
	MergedRestriction subjectRestriction = MergedRestriction
		.getFixedValueRestriction(ContextEvent.PROP_RDF_SUBJECT,
			ontDevice);
	MergedRestriction predicateRestriction = MergedRestriction
		.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
			SwitchActuator.PROP_HAS_VALUE);
	// TODO Object restr
	cep.addRestriction(subjectRestriction);
	cep.addRestriction(predicateRestriction);
	info.setProvidedEvents(new ContextEventPattern[] { cep });
    }

    @Override
    public StatusValue executeGet() {
	OnOffType value = (OnOffType) Activator.getOpenhab().get(shDeviceName)
		.getStateAs((Class<? extends State>) OnOffType.class);
	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchActuatorWrapper.class, "getStatus",
		new String[] { "The service called was 'get the status'" },
		null);
	if (value == null)
	    return null;
	return (value.compareTo(OnOffType.ON) == 0) ? StatusValue.Activated
		: StatusValue.NotActivated;
    }

    @Override
    public boolean executeSet(StatusValue value) {
	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchActuatorWrapper.class, "setStatus",
		new String[] {
			"The service called was 'set the status' " + value },
		null);

	try {
	    ItemCommandEvent itemCommandEvent = ItemEventFactory
		    .createCommandEvent(shDeviceName,
			    value.equals(StatusValue.Activated) ? OnOffType.ON
				    : OnOffType.OFF);
	    Activator.getPub().post(itemCommandEvent);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    public void publish(Event event) {
	//kept for the interface
    }

    public void unregister() {
	super.unregister();
    }

}

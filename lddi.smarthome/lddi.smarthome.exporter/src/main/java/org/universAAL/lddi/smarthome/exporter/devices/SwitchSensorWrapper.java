package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.StatusValue;
import org.universAAL.ontology.device.SwitchSensor;

public class SwitchSensorWrapper extends AbstractStatusValueCallee {
    public static final int TYPE_ID=20;
    
    private DefaultContextPublisher cp;

    public SwitchSensorWrapper(ModuleContext context, String itemName) {
	super(context,
		new ServiceProfile[]{getServiceProfileGET(Activator.NAMESPACE + itemName + "handler",
			new SwitchSensor(Activator.NAMESPACE + itemName))},
		Activator.NAMESPACE + itemName + "handler");

	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchSensorWrapper.class, "SwitchControllerWrapper",
		new String[] { "Ready to subscribe" }, null);
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new SwitchSensor(deviceURI);

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
			SwitchSensor.PROP_HAS_VALUE);
	// TODO Object restr
	cep.addRestriction(subjectRestriction);
	cep.addRestriction(predicateRestriction);
	info.setProvidedEvents(new ContextEventPattern[] { cep });
	cp = new DefaultContextPublisher(context, info);
    }

    @Override
    public StatusValue executeGet() {
	OnOffType value = (OnOffType) Activator.getOpenhab().get(shDeviceName)
		.getStateAs((Class<? extends State>) OnOffType.class);
	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchSensorWrapper.class, "getStatus",
		new String[] { "The service called was 'get the status'" },
		null);
	if (value == null)
	    return null;
	return (value.compareTo(OnOffType.ON) == 0) ? StatusValue.Activated
		: StatusValue.NotActivated;
    }

    @Override
    public boolean executeSet(StatusValue value) {
	return false;
    }

    public void publish(Event event) {
	Boolean theValue = null;
	LogUtils.logDebug(Activator.getModuleContext(),
		SwitchSensorWrapper.class, "changedCurrentLevel",
		new String[] { "Changed-Event received" }, null);
	if (event instanceof ItemStateEvent) {
	    ItemStateEvent stateEvent = (ItemStateEvent) event;
	    State s = stateEvent.getItemState();
	    if (s instanceof OnOffType) {
		theValue = Boolean
			.valueOf(((OnOffType) s).compareTo(OnOffType.ON) == 0);
	    }
	}
	if (theValue != null) {
	    SwitchSensor d = (SwitchSensor) ontDevice;
	    d.setValue(theValue.booleanValue() ? StatusValue.Activated
		    : StatusValue.NotActivated);
	    cp.publish(new ContextEvent(d, SwitchSensor.PROP_HAS_VALUE));
	} // else dont bother TODO log
    }

    public void unregister() {
	super.unregister();
	cp.close();
    }

}

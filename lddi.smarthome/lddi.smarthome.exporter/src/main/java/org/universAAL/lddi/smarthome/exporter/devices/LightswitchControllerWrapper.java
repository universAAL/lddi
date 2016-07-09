package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
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
import org.universAAL.ontology.device.LightController;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class LightswitchControllerWrapper extends AbstractIntegerCallee {
    public static final int TYPE_ID=15;
    private DefaultContextPublisher cp;

    /**
     * Constructor to be used in the exporter, which sets up all the exporting
     * process.
     * 
     * @param context
     *            The OSGi context
     * @param serv
     *            The OSGi service backing the interaction with the device in
     *            the abstraction layer
     */
    public LightswitchControllerWrapper(ModuleContext context, String itemName) {
	super(context,
		getServiceProfiles(Activator.NAMESPACE + itemName + "handler",
			new LightController(Activator.NAMESPACE + itemName)),
		Activator.NAMESPACE + itemName + "handler");
	
	LogUtils.logDebug(Activator.getModuleContext(),
		LightswitchControllerWrapper.class, "LightControllerWrapper",
		new String[] { "Ready to subscribe" }, null);
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new LightController(deviceURI);

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
			LightController.PROP_HAS_VALUE);
	//TODO Object restr
	cep.addRestriction(subjectRestriction);
	cep.addRestriction(predicateRestriction);
	info.setProvidedEvents(new ContextEventPattern[] { cep });
	cp = new DefaultContextPublisher(context, info);
    }

    @Override
    public Integer executeGet() {
	OnOffType value = (OnOffType) Activator.getOpenhab()
		.get(shDeviceName)
		.getStateAs((Class<? extends State>) OnOffType.class);
	LogUtils.logDebug(Activator.getModuleContext(), LightswitchActuatorWrapper.class,
		"getStatus",
		new String[] { "The service called was 'get the status'" },
		null);
	if (value == null)
	    return null;
	return (value.compareTo(OnOffType.ON) == 0) ? Integer.valueOf(100)
		: Integer.valueOf(0);
    }

    @Override
    public boolean executeSet(Integer value) {
	LogUtils.logDebug(Activator.getModuleContext(), LightswitchActuatorWrapper.class,
		"setStatus",
		new String[] {
			"The service called was 'set the status' " + value },
		null);

	try {
	    ItemCommandEvent itemCommandEvent = ItemEventFactory
		    .createCommandEvent(shDeviceName,
			    value.intValue()==0 ? OnOffType.OFF
				    : OnOffType.ON);
	    Activator.getPub().post(itemCommandEvent);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    public void publish(Event event) {
	Integer theValue = null;
	LogUtils.logDebug(Activator.getModuleContext(), LightswitchControllerWrapper.class,
		"changedCurrentLevel",
		new String[] { "Changed-Event received" }, null);
	if (event instanceof ItemStateEvent) {
	    ItemStateEvent stateEvent = (ItemStateEvent) event;
	    State s = stateEvent.getItemState();
	    if (s instanceof PercentType) {
		theValue = Integer.valueOf(((PercentType) s).intValue());
	    } else if (s instanceof OnOffType) {
		if (((OnOffType) s).compareTo(OnOffType.OFF) == 0) {
		    theValue = Integer.valueOf(0);
		} else {
		    theValue = Integer.valueOf(100);
		}
	    }
	}
	if (theValue != null) {
	    LightController d = (LightController) ontDevice;
	    d.setValue(theValue.intValue());
	    cp.publish(new ContextEvent(d, LightController.PROP_HAS_VALUE));
	} // else dont bother TODO log
    }
    
    public void unregister(){
	super.unregister();
	cp.close();
    }

}
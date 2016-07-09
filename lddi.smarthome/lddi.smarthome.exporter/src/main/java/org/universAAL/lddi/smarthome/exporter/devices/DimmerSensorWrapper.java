package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
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
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.DimmerSensor;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class DimmerSensorWrapper extends AbstractIntegerCallee {
    public static final int TYPE_ID=10;
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
    public DimmerSensorWrapper(ModuleContext context, String itemName) {
	super(context,
		new ServiceProfile[]{getServiceProfileGET(Activator.NAMESPACE + itemName + "handler",
			new DimmerSensor(Activator.NAMESPACE + itemName))},
		Activator.NAMESPACE + itemName + "handler");
	
	LogUtils.logDebug(Activator.getModuleContext(),
		DimmerSensorWrapper.class, "DimmerControllerWrapper",
		new String[] { "Ready to subscribe" }, null);
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new DimmerSensor(deviceURI);

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
			DimmerSensor.PROP_HAS_VALUE);
	//TODO Object restr
	cep.addRestriction(subjectRestriction);
	cep.addRestriction(predicateRestriction);
	info.setProvidedEvents(new ContextEventPattern[] { cep });
	cp = new DefaultContextPublisher(context, info);
    }

    @Override
    public Integer executeGet() {
	PercentType value = (PercentType) Activator.getOpenhab()
		.get(shDeviceName)
		.getStateAs((Class<? extends State>) PercentType.class);
	LogUtils.logDebug(Activator.getModuleContext(), DimmerSensorWrapper.class,
		"getStatus",
		new String[] { "The service called was 'get the status'" },
		null);
	if (value == null)
	    return null;
	return Integer.valueOf(value.intValue());
    }

    @Override
    public boolean executeSet(Integer value) {
	return false;
    }

    public void publish(Event event) {
	Integer theValue = null;
	LogUtils.logDebug(Activator.getModuleContext(), DimmerSensorWrapper.class,
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
	    DimmerSensor d = (DimmerSensor) ontDevice;
	    d.setValue(theValue.intValue());
	    cp.publish(new ContextEvent(d, DimmerSensor.PROP_HAS_VALUE));
	} // else dont bother TODO log
    }
    
    public void unregister(){
	super.unregister();
	cp.close();
    }

}

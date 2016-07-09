package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.device.TemperatureSensor;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class TemperatureSensorWrapper extends AbstractFloatCallee {
    private DefaultContextPublisher cp;
    public static final int TYPE_ID=21;
    
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
    public TemperatureSensorWrapper(ModuleContext context, String itemName) {
	super(context,
		new ServiceProfile[]{getServiceProfileGET(Activator.NAMESPACE + itemName + "handler",
			new TemperatureSensor(Activator.NAMESPACE + itemName))},
		Activator.NAMESPACE + itemName + "handler");
	
	Activator.logD("DimmerControllerWrapper", "Ready to subscribe" );
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new TemperatureSensor(deviceURI);

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
			TemperatureSensor.PROP_HAS_VALUE);
	//TODO Object restr
	cep.addRestriction(subjectRestriction);
	cep.addRestriction(predicateRestriction);
	info.setProvidedEvents(new ContextEventPattern[] { cep });
	cp = new DefaultContextPublisher(context, info);
    }

    @Override
    public Float executeGet() {
	DecimalType value = (DecimalType) Activator.getOpenhab()
		.get(shDeviceName)
		.getStateAs((Class<? extends State>) DecimalType.class);
	Activator.logD("getStatus","The service called was 'get the status'");
	if (value == null)
	    return null;
	return Float.valueOf(value.floatValue());
    }

    @Override
    public boolean executeSet(Float value) {
	return false;//Sensor cant set
    }

    public void publish(Event event) {
	Float theValue = null;
	Activator.logD("changedCurrentLevel","Changed-Event received");
	if (event instanceof ItemStateEvent) {
	    ItemStateEvent stateEvent = (ItemStateEvent) event;
	    State s = stateEvent.getItemState();
	    if (s instanceof DecimalType) {
		theValue = Float.valueOf(((DecimalType) s).floatValue());
	    } 
	}
	if (theValue != null) {
	    TemperatureSensor d = (TemperatureSensor) ontDevice;
	    d.setValue(theValue.floatValue());
	    cp.publish(new ContextEvent(d, TemperatureSensor.PROP_HAS_VALUE));
	} // else dont bother TODO log
    }
    
    public void unregister(){
	super.unregister();
	cp.close();
    }

}

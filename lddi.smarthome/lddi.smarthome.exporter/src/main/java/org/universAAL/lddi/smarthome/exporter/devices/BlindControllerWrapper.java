package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.items.events.ItemCommandEvent;
import org.eclipse.smarthome.core.items.events.ItemEventFactory;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.UpDownType;
import org.eclipse.smarthome.core.types.State;
import org.universAAL.lddi.smarthome.exporter.Activator;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.middleware.owl.MergedRestriction;
import org.universAAL.ontology.device.BlindController;

/**
 * Exporter class that acts as wrapper towards uAAL. Connects interaction of the
 * device with the uAAL middleware through the service and context buses.
 * 
 * @author alfiva
 * 
 */
public class BlindControllerWrapper extends AbstractIntegerCallee {
    public static final int TYPE_ID=2;
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
    public BlindControllerWrapper(ModuleContext context, String itemName) {
	super(context,
		getServiceProfiles(Activator.NAMESPACE + itemName + "handler",
			new BlindController(Activator.NAMESPACE + itemName)),
		Activator.NAMESPACE + itemName + "handler");
	
	Activator.logD( "DimmerControllerWrapper", "Ready to subscribe" );
	shDeviceName = itemName;

	// URI must be the same declared in the super constructor
	String deviceURI = Activator.NAMESPACE + itemName;
	ontDevice = new BlindController(deviceURI);

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
			BlindController.PROP_HAS_VALUE);
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
	Activator.logD("getStatus","The service called was 'get the status'" );
	if (value == null)
	    return null;
	return Integer.valueOf(value.intValue());
    }

    @Override
    public boolean executeSet(Integer value) {
	Activator.logD("setStatus","The service called was 'set the status' " + value);
	try {
	    ItemCommandEvent itemCommandEvent;
	    if(value.intValue()==0){
		itemCommandEvent = ItemEventFactory
			    .createCommandEvent(shDeviceName,
				    UpDownType.DOWN);
	    }else if(value.intValue()==100){
		itemCommandEvent = ItemEventFactory
			    .createCommandEvent(shDeviceName,
				    UpDownType.UP);
	    }else{
		itemCommandEvent = ItemEventFactory
		    .createCommandEvent(shDeviceName,
			    PercentType.valueOf(value.toString()));
	    }
	    Activator.getPub().post(itemCommandEvent);
	} catch (Exception e) {
	    return false;
	}
	return true;
    }

    public void publish(Event event) {
	Integer theValue = null;
	Activator.logD("changedCurrentLevel","Changed-Event received");
	if (event instanceof ItemStateEvent) {
	    ItemStateEvent stateEvent = (ItemStateEvent) event;
	    State s = stateEvent.getItemState();
	    if (s instanceof PercentType) {
		theValue = Integer.valueOf(((PercentType) s).intValue());
	    } else if (s instanceof UpDownType) {
		//TODO Anything?
	    }
	}
	if (theValue != null) {
	    BlindController d = (BlindController) ontDevice;
	    d.setValue(theValue.intValue());
	    cp.publish(new ContextEvent(d, BlindController.PROP_HAS_VALUE));
	} // else dont bother TODO log
    }
    
    public void unregister(){
	super.unregister();
	cp.close();
    }

}
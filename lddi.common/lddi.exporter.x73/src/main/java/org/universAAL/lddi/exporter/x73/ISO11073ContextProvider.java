package org.universAAL.lddi.exporter.x73;

import org.osgi.service.log.LogService;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.context.ContextEventPattern;
import org.universAAL.middleware.context.ContextPublisher;
import org.universAAL.middleware.context.DefaultContextPublisher;
import org.universAAL.middleware.context.owl.ContextProvider;
import org.universAAL.middleware.context.owl.ContextProviderType;
import org.universAAL.ontology.X73.AbsoluteTimeStamp;
import org.universAAL.ontology.X73.BloodPressureMonitor;
import org.universAAL.ontology.X73.BodyWeight;
import org.universAAL.ontology.X73.Pulse;
import org.universAAL.ontology.X73.SystemModel;
import org.universAAL.ontology.X73.WeighingScale;
import org.universAAL.ontology.X73.X73Ontology;

/**
 * Provides context event patterns for the uAAL context bus
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 * @author Patrick Stern (sternp@gmx.at)
 */
public class ISO11073ContextProvider {

    private ContextPublisher cp;
	private ISO11073DBusServer theServer;
	private LogService logger;
	
	public ISO11073ContextProvider(ModuleContext mc,
			ISO11073DBusServer x73Server) {
		
		this.theServer = x73Server;
		this.logger = x73Server.getLogger();
		
		// prepare for context publishing
		ContextProvider info = new ContextProvider(
				X73Ontology.NAMESPACE
				+ "x73ContextProvider");
		info.setType(ContextProviderType.gauge);
		info.setProvidedEvents(getWeighingCEP());
		// Set the provided events to unknown with an empty pattern
		//info.setProvidedEvents(new ContextEventPattern[] { new ContextEventPattern() });
		cp = new DefaultContextPublisher(mc, info);
		
		//theServer.addListener(this);
		
		this.logger.log(LogService.LOG_INFO, "Activated x73 ContextEvent Patterns");
	}

	private static ContextEventPattern[] getWeighingCEP() {
			
		/*MergedRestriction subjectRestriction = MergedRestriction
				.getAllValuesRestriction(ContextEvent.PROP_RDF_SUBJECT,
						WeighingScale.MY_URI);
		
		MergedRestriction predicateRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_PREDICATE,
						WeighingScale.PROP_HAS_MEASURED_WEIGHT);
		
		MergedRestriction objectRestriction = MergedRestriction
				.getFixedValueRestriction(ContextEvent.PROP_RDF_OBJECT,
						MDSAttribute.basicNuObservedValue);
		*/
		
		ContextEventPattern cep_weight = new ContextEventPattern();
		//cep_weight.addRestriction(subjectRestriction);
		//cep_weight.addRestriction(predicateRestriction);
		//cep_weight.addRestriction(objectRestriction);
	
		return new ContextEventPattern[] { cep_weight };
	}

	//called by MyAgent.Disassociated
	public void publishDevData(String deviceId, String manufacturer, String modelNumber, int century, int year, int month, int day, int hour, int minute, int second, int sec_fractions, SystemModel sm, AbsoluteTimeStamp as) {

		sm.setManufacturer(manufacturer);
		cp.publish(new ContextEvent(sm, SystemModel.PROP_MANUFACTURER));
		sm.setModelNumber(modelNumber);
		cp.publish(new ContextEvent(sm, SystemModel.PROP_MODEL_NUMBER));
	
		as.setCentury(century);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_CENTURY));
		as.setYear(year);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_YEAR));
		as.setMonth(month);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_MONTH));
		as.setDay(day);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_DAY)); 
		as.setHour(hour);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_HOUR));
		as.setMinute(minute);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_MINUTE));
		as.setSecond(second);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_SECOND));
		as.setSecfractions(sec_fractions);
		cp.publish(new ContextEvent(as, AbsoluteTimeStamp.PROP_SECFRACTIONS));
	}
	
	// This method is called by MyAgent.Disassociated and publishes the measurements of the bathing scale
	// to the context bus. Such a method has to be implemented for every device model.
	public void publishWeight(String deviceId, String weight, String unitCode, int century, int year, int month, int day, int hour, int minute, int second, int sec_fractions, String manufacturer, String modelNumber, String systemId, String typeSpecList) {

		SystemModel sm = new SystemModel(SystemModel.MY_URI + systemId);
		AbsoluteTimeStamp as = new AbsoluteTimeStamp(AbsoluteTimeStamp.MY_URI + systemId); 
		publishDevData(deviceId,manufacturer,modelNumber, century, year, month, day, hour, minute, second, sec_fractions,sm,as);
		
		BodyWeight bw = new BodyWeight(BodyWeight.MY_URI + systemId); 
		bw.setAbsoluteTimeStamp(as);
		cp.publish(new ContextEvent(bw, BodyWeight.PROP_ABSOLUTE_TIME_STAMP));
		bw.setBasicNuObservedValue(weight);
		cp.publish(new ContextEvent(bw, BodyWeight.PROP_BASIC_NU_OBSERVED_VALUE));
		bw.setUnitCode(unitCode);
		cp.publish(new ContextEvent(bw, BodyWeight.PROP_UNIT_CODE));

		WeighingScale ws = new WeighingScale(WeighingScale.MY_URI + systemId); 
		ws.setSystemId(systemId);
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_SYSTEM_ID));
		ws.setSystemTypeSpecList(typeSpecList);
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_SYSTEM_TYPE_SPEC_LIST));
		ws.setSystemModel(sm);
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_SYSTEM_MODEL));
		ws.setHasMeasuredWeight(bw);
		cp.publish(new ContextEvent(ws, WeighingScale.PROP_HAS_MEASURED_WEIGHT));
	}
	
	// This method is called by MyAgent.Disassociated and publishes the pulse measurements
	// to the context bus. Such a method has to be implemented for every device model.
	public void publishPulse(String deviceId, String pulse, String unitCode, int century, int year, int month, int day, int hour, int minute, int second, int sec_fractions, String manufacturer, String modelNumber, String systemId, String typeSpecList) {


		SystemModel sm = new SystemModel(SystemModel.MY_URI + systemId);
		AbsoluteTimeStamp as = new AbsoluteTimeStamp(AbsoluteTimeStamp.MY_URI + systemId); 
		publishDevData(deviceId,manufacturer,modelNumber, century, year, month, day, hour, minute, second, sec_fractions,sm,as);
		
		Pulse pu = new Pulse(Pulse.MY_URI + systemId); 
		pu.setAbsoluteTimeStamp(as);
		cp.publish(new ContextEvent(pu, Pulse.PROP_ABSOLUTE_TIME_STAMP));
		pu.setBasicNuObservedValue(pulse);
		cp.publish(new ContextEvent(pu, Pulse.PROP_BASIC_NU_OBSERVED_VALUE));
		pu.setUnitCode(unitCode);
		cp.publish(new ContextEvent(pu, Pulse.PROP_UNIT_CODE));

		BloodPressureMonitor bpm = new BloodPressureMonitor(BloodPressureMonitor.MY_URI + systemId); 
		bpm.setSystemId(systemId);
		cp.publish(new ContextEvent(bpm, BloodPressureMonitor.PROP_SYSTEM_ID));
		bpm.setSystemTypeSpecList(typeSpecList);
		cp.publish(new ContextEvent(bpm, BloodPressureMonitor.PROP_SYSTEM_TYPE_SPEC_LIST));
		bpm.setSystemModel(sm);
		cp.publish(new ContextEvent(bpm, BloodPressureMonitor.PROP_SYSTEM_MODEL));
		bpm.setHasMeasuredPulse(pu);
		cp.publish(new ContextEvent(bpm, BloodPressureMonitor.PROP_HAS_MEASURED_PULSE));
	}
	
	private static String constructx73URIfromLocalID(String localID) {
		return X73Ontology.NAMESPACE + localID;
	}

}
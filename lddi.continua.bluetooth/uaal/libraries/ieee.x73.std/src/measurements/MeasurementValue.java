package measurements;

import x73.p20601.OID_Type;

/**
 * This class is the one who has the Measurement values prepared to be represented.
 * @author lgigante
 *
 */
public class MeasurementValue {

	private Object time;
	private OID_Type unit;
	private Object value_obj;
	
	public MeasurementValue(Object timemeasure, OID_Type unit_code, Object measure_value_object){
		
		time = timemeasure;
		value_obj = measure_value_object;
		unit  = unit_code;
		
	}
	
	
	public Object getTimeObject(){
		return time;
	}
	public OID_Type getOIDType(){
		return unit;
	}
	
	public Object getMeasureObject(){
		return value_obj;
	}
	
}

package org.universAAL.lddi.iso11073.activityhub.devicemodel;

/**
 * sensor events of temperature sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum TemperatureSensorEvent {
	HIGH_TEMPERATURE_DETECTED(0),
	LOW_TEMPERATURE_DETECTED(1),
	RATE_OF_CHANGE_TOO_FAST(2),
	NO_CONDITION_DETECTED(3);
	
	private final int value;

	private TemperatureSensorEvent(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static TemperatureSensorEvent toTemperatureSensorEvent(String str)
    {
        try {
            return TemperatureSensorEvent.valueOf(str);
        } 
        catch (Exception ex) {
            //IllegalArgumentException - if the specified enum type has no constant with the specified name, or the specified class object does not represent an enum type
            //NullPointerException - if enumType or name is null
        	ex.printStackTrace();
            return null;
        }
    }
    
	/**
	 * convert int to enum item
	 * 
	 * @param int
	 * @return enum item
	 */
    public static TemperatureSensorEvent getTemperatureSensorEvent(int val) {
            for (TemperatureSensorEvent ccse : TemperatureSensorEvent.values()) {
            	if ( ccse.value == val ) return ccse;
            }
            throw new AssertionError();
    }
}

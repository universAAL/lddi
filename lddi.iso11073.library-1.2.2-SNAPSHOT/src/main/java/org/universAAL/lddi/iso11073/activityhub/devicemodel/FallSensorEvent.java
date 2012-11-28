package org.universAAL.lddi.iso11073.activityhub.devicemodel;

/**
 * sensor events of fall sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum FallSensorEvent {
	FALL_DETECTED(0),
	NO_CONDITION_DETECTED(1);
	
	private final int value;

	private FallSensorEvent(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static FallSensorEvent toFallSensorEvent(String str)
    {
        try {
            return FallSensorEvent.valueOf(str);
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
    public static FallSensorEvent getFallSensorEvent(int val) {
            for (FallSensorEvent ccse : FallSensorEvent.values()) {
            	if ( ccse.value == val ) return ccse;
            }
            throw new AssertionError();
    }
}

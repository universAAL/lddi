package org.universAAL.lddi.iso11073.activityhub.devicemodel;

/**
 * sensor events of usage sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum UsageSensorEvent {
	USAGE_STARTED(0),
	USAGE_ENDED(1),
	EXPECTED_USE_START_VIOLATION(2), 
	EXPECTED_USE_STOP_VIOLATION(3), 
	ABSENCE_VIOLATION(4), 
	NO_CONDITION_DETECTED(5);

	
	private final int value;

	private UsageSensorEvent(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static UsageSensorEvent toUsageSensorEvent(String str)
    {
        try {
            return UsageSensorEvent.valueOf(str);
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
    public static UsageSensorEvent getUsageSensorEvent(int val) {
            for (UsageSensorEvent ccse : UsageSensorEvent.values()) {
            	if ( ccse.value == val ) return ccse;
            }
            throw new AssertionError();
    }
}

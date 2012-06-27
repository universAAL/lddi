package org.universAAL.lddi.iso11073.activityhub.devicemodel;

/**
 * sensor events of switch sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum SwitchSensorEvent {
	SWITCH_ON(0),
	SWITCH_OFF(1),
	NO_CONDITION_DETECTED(2);
	
	private final int value;

	private SwitchSensorEvent(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static SwitchSensorEvent toSwitchSensorEvent(String str)
    {
        try {
            return SwitchSensorEvent.valueOf(str);
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
    public static SwitchSensorEvent getSwitchSensorEvent(int val) {
            for (SwitchSensorEvent sse : SwitchSensorEvent.values()) {
            	if ( sse.value == val ) return sse;
            }
            throw new AssertionError();
    }

}

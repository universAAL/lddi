package org.universAAL.iso11073.activityhub.devicemodel;

/**
 * sensor events of contact closure sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum ContactClosureSensorEvent {
	CONTACT_OPENED(0),
	CONTACT_CLOSED(1),
	NO_CONDITION_DETECTED(2);
	
	private final int value;

	private ContactClosureSensorEvent(int value) {
		this.value = value;
	}
	
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static ContactClosureSensorEvent toContactClosureSensorEvent(String str)
    {
        try {
            return ContactClosureSensorEvent.valueOf(str);
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
    public static ContactClosureSensorEvent getContactClosureSensorEvent(int val) {
            for (ContactClosureSensorEvent ccse : ContactClosureSensorEvent.values()) {
            	if ( ccse.value == val ) return ccse;
            }
            throw new AssertionError();
    }

}

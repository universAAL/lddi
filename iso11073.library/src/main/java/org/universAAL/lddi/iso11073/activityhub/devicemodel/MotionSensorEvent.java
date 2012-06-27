package org.universAAL.lddi.iso11073.activityhub.devicemodel;

/**
 * sensor events of motion sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum MotionSensorEvent {
	MOTION_DETECTED(0),
	MOTION_DETECTED_DELAYED(1),
	TAMPER_DETECTED(2), 
	NO_CONDITION_DETECTED(3);
		
	private final int value;
		
	private MotionSensorEvent(int value) {
		this.value = value;
	}
		
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static MotionSensorEvent toMotionSensorEvent(String str)
    {
        try {
            return MotionSensorEvent.valueOf(str);
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
    public static MotionSensorEvent getMotionSensorEvent(int val) {
            for (MotionSensorEvent mse : MotionSensorEvent.values()) {
            	if ( mse.value == val ) return mse;
            }
            throw new AssertionError();
    }

}

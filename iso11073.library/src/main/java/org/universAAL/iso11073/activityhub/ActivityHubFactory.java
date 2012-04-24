package org.universAAL.iso11073.activityhub;

/**
 * Factory for the ActivityHub sensors according to ISO 11073 - 
 * Part 10471 (Indepentend living activity hub), edition 2010-05-01
 * 
 * @author Thomas Fuxreiter
 */
public class ActivityHubFactory {

	private enum ActivityHubDevice {
		ISO11073_CONTACTCLOSURE,
		ISO11073_MOTIONSENSOR,
		ISO11073_SWITCH
	}
	
    private static ActivityHubDevice toActivityHubDevice(String str)
    {
        try {
            return ActivityHubDevice.valueOf(str);
        } 
        catch (Exception ex) {
            return null;
        }
    }
	
    public static ActivityHubSensor createInstance(String deviceName) {
	
    	switch (toActivityHubDevice(deviceName)) {
    		case ISO11073_CONTACTCLOSURE:
    			return new ContactClosureSensor();
    		case ISO11073_MOTIONSENSOR:
    			return new MotionSensor();
    		case ISO11073_SWITCH:
    			return new SwitchSensor();
    	}

    	return null;
    }
}

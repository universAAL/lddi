package org.universAAL.iso11073.activityhub.devicemodel;


/**
 * Factory for the ActivityHub sensors according to ISO 11073 - 
 * Part 10471 (Indepentend living activity hub), edition 2010-05-01
 * 
 * @author Thomas Fuxreiter
 */
public class ActivityHubFactory {

	private enum ActivityHubDeviceCategory {
		ISO11073_CONTACTCLOSURESENSOR,
		ISO11073_MOTIONSENSOR,
		ISO11073_SWITCHSENSOR
	}
	
    private static ActivityHubDeviceCategory toActivityHubDevice(String str)
    {
        try {
            return ActivityHubDeviceCategory.valueOf(str);
        } 
        catch (Exception ex) {
            return null;
        }
    }
	
    public static ActivityHubDevice createInstance(String deviceName) {
	
    	switch (toActivityHubDevice(deviceName)) {
    		case ISO11073_CONTACTCLOSURESENSOR:
    			return new ContactClosureSensor();
    		case ISO11073_MOTIONSENSOR:
    			return new MotionSensor();
    		case ISO11073_SWITCHSENSOR:
    			return new SwitchSensor();
    	}

    	return null;
    }
}

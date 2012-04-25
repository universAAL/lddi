package org.universAAL.iso11073.activityhub.driver;

import org.osgi.framework.BundleContext;
import org.universAAL.iso11073.activityhub.driver.interfaces.ActivityHubDriverClient;

/**
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubDriverManager {
	
	public static void startAllDrivers(ActivityHubDriverClient user,
			BundleContext context){

		new Iso11073ContactClosureDriver(user, context);
		new Iso11073MotionSensorDriver(user, context);
		new Iso11073SwitchSensorDriver(user, context);
		
	}

}

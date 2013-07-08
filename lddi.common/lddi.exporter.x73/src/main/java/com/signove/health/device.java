/*
 * Copyright (c) 2012 Signove Tecnologia S/A
 *
 * Modified by Patrick Stern, AIT
 * patrick.stern@ait.ac.at
 * 2012-07-24
 */

package com.signove.health;

import org.freedesktop.dbus.*;

@DBusInterfaceName("com.signove.health.device")
public interface device extends DBusInterface {
	void RequestDeviceAttributes();
	String GetConfiguration();
}

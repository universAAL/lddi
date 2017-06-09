/*
	 Copyright (c) 2012 Itsaso Aranburu <itsasoaranburu@gmail.com>, Signove Tecnologia S/A

     See the NOTICE file distributed with this work for additional
     information regarding copyright ownership

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
*/

package com.signove.health;

import java.io.*;
import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusInterfaceName;
import org.freedesktop.dbus.exceptions.DBusException;

@DBusInterfaceName("com.signove.health.manager")
public interface manager extends DBusInterface {
	void ConfigurePassive(agent agt, int data_types[]);

	void Configure(agent agt, String addr, int data_types[]);
}
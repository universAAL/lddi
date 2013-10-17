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

import org.freedesktop.dbus.*;

@DBusInterfaceName("com.signove.health.agent")
public interface agent extends DBusInterface {
  	void Connected(String dev, String addr);
	void Associated(String dev, String data);
	void MeasurementData(String dev, String data);
	void DeviceAttributes(String dev, String data);
	void Disassociated(String dev);
	void Disconnected(String dev);
}

/*
	Copyright 2016 ITACA-SABIEN, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion
	Avanzadas - Grupo Tecnologias para la Salud y el
	Bienestar (SABIEN)

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
package org.universAAL.lddi.smarthome.exporter.devices;

import org.eclipse.smarthome.core.events.Event;

public interface GenericDevice {
	/**
	 * This must be called from the single receiver once it determines it is up
	 * to this wrapper to publish its state. It assumes the passed event is of
	 * the right type and payload.
	 *
	 * @param event
	 *            The event data where to take info from
	 */
	public void publish(Event event);

	/**
	 * This will be called when its time to close. Free any resources (This is
	 * intended for Callees, that should be closed).
	 */
	public void unregister();
}

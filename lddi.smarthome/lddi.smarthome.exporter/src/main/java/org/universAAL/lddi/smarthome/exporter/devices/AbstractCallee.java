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

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.owls.profile.ServiceProfile;
import org.universAAL.ontology.phThing.Device;

public abstract class AbstractCallee extends ServiceCallee implements GenericDevice {
	String shDeviceName;
	Device ontDevice;

	protected AbstractCallee(ModuleContext context, ServiceProfile[] realizedServices) {
		super(context, realizedServices);
	}

}

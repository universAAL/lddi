/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

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
/**
 * Activator for the Continua HDP manager service.
 *
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 *
 */

// Package
package org.universAAL.lddi.hdp;

// Imports
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.OSGiContainer;
import org.universAAL.middleware.container.utils.LogUtils;

// Main class
public class Activator implements BundleActivator {

	// Attributes

	private ContinuaHdpManagerProvider provider = null;
	public static ModuleContext mc;

	// Methods

	/** Start method */
	public void start(final BundleContext context) throws Exception {
		// Config
		mc = OSGiContainer.THE_CONTAINER.registerModule(new Object[] { context });
		provider = new ContinuaHdpManagerProvider(context);
		// Log
		LogUtils.logInfo(mc, getClass(), "start", new String[] { "Start Continua HDP manager" }, null);
	}

	/** Stop method */
	public void stop(BundleContext arg0) throws Exception {
		// Log
		LogUtils.logInfo(mc, getClass(), "stop", new String[] { "Stop Continua HDP manager" }, null);
		// Closing references
		if (provider != null) {
			provider.close();
			provider = null;
		}
	}
}
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
 * Services offered to other applications by the Continua HDP Manager
 * 
 * @author Angel Martinez-Cavero TSB Technologies for Health and Well-being
 * 
 */

// Package
package org.universAAL.continua.hdp;

// Imports
import org.osgi.framework.BundleContext;
import org.universAAL.continua.manager.publisher.hdpManager;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.osgi.uAALBundleContainer;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.ontology.continua.ContinuaHealthDevice;

// Main class
public class ContinuaHdpManagerProvider extends ServiceCallee {
	
	// Attributes
	
	private ModuleContext mctx;
	
	/** Bundle context object */
	private BundleContext ctx = null;
	
	/** HDP manager object */
	private hdpManager manager = null;

	// Constructor
	
	protected ContinuaHdpManagerProvider(ModuleContext context) {
		super(context,ProvidedContinuaHdpManagerService.profiles);
		mctx = context;
	}

	protected ContinuaHdpManagerProvider(BundleContext context) {
		super(uAALBundleContainer.THE_CONTAINER
				.registerModule(new Object[] { context }),
				ProvidedContinuaHdpManagerService.profiles);
		ctx = context;
	}

	// Methods
	
	/** Called when our class loses connection to the bus */
	@Override
	public void communicationChannelBroken() {
		// TODO Do something if proceed...
	}
	
	/** Called when a service request matches the service profile registered by our service callee */
	@Override
	public ServiceResponse handleCall(ServiceCall call) {
		LogUtils.logDebug(mctx,getClass(),"ServiceProvided",new String[] { "Handling service call..." }, null);
		if (call == null) {
			return null;
		} else {
			String operation = call.getProcessURI();
			if (operation == null) {
				return null;
			} else if (operation
					.startsWith(ProvidedContinuaHdpManagerService.SERVICE_SWITCH_ON)) {
				LogUtils.logTrace(mctx,getClass(),"ServiceProvided",new String[] { "Start HDP manager" },null);
				Object input = call.getInputValue(ProvidedContinuaHdpManagerService.INPUT_CONTINUA_HEALTH_DEVICE);				
				manager = new hdpManager(((ContinuaHealthDevice)input).getDataType(),((ContinuaHealthDevice)input).getMAC(),ctx);
				manager.init();				
				return new ServiceResponse(CallStatus.succeeded);
			} else if (operation
					.startsWith(ProvidedContinuaHdpManagerService.SERVICE_SWITCH_OFF)) {
				LogUtils.logTrace(mctx,getClass(),"ServiceProvided",new String[] { "Stop HDP manager" },null);
				Object input = call.getInputValue(ProvidedContinuaHdpManagerService.INPUT_CONTINUA_HEALTH_DEVICE);				
				if(manager != null) {
					manager.exit(((ContinuaHealthDevice)input).getDataType(),((ContinuaHealthDevice)input).getMAC());
					manager = null;				
				}				
				return new ServiceResponse(CallStatus.succeeded);			
			}
		}
		return new ServiceResponse(CallStatus.serviceSpecificFailure);
	}
}
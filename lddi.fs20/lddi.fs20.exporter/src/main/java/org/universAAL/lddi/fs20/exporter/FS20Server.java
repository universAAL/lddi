/*	
	Copyright 2007-2014 Fraunhofer IGD, http://www.igd.fraunhofer.de
	Fraunhofer-Gesellschaft - Institut f�r Graphische Datenverarbeitung
	
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

package org.universAAL.lddi.fs20.exporter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.universAAL.lddi.fs20.devicemodel.FS20RGBSADevice;
import org.universAAL.lddi.fs20.devicemodel.FS20FMSDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20PIRxDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20SIGDevice;
import org.universAAL.lddi.fs20.devicemodel.FS20STDevice;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCall;
import org.universAAL.middleware.service.ServiceCallee;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.owls.process.ProcessOutput;
import org.universAAL.ontology.activityhub.UsageSensor;
import org.universAAL.ontology.av.device.LoudSpeaker;
import org.universAAL.ontology.device.LightActuator;
import org.universAAL.ontology.device.MotionSensor;
import org.universAAL.ontology.device.SwitchActuator;

/**
 * Handles service calls from uAAL and reacts on them
 *
 *
 * @author Steeven Zeiss Fraunhofer IGD (steeven.zeiss@igd.fraunhofer.de)
 * @date 30.05.2013
 */
public class FS20Server extends ServiceCallee {

	// a help variable to process the hexdecimal FS20 device command
	private HashMap<Integer, Byte> animationmap = new HashMap<Integer, Byte>();

	private static Vector<FS20RGBSADevice> displays = Activator.getDisplays();
	private static Vector<FS20PIRxDevice> motionsensors = Activator.getMotionsensors();
	private static Vector<FS20SIGDevice> gongs = Activator.getGongs();
	private static Vector<FS20FMSDevice> fms = Activator.getFMSs();
	private static Vector<FS20STDevice> switches = Activator.getSwitches();

	// the standard service response if an error appears
	private static final ServiceResponse invalidInput = new ServiceResponse(CallStatus.serviceSpecificFailure);

	static {
		invalidInput.addOutput(new ProcessOutput(ServiceResponse.PROP_SERVICE_SPECIFIC_ERROR, "Invalid input!"));
	}

	/**
	 * Constructor with a given ModuleContext
	 * 
	 * @param context
	 *            = the given ModuleContext
	 */
	protected FS20Server(ModuleContext context) {
		super(context, ProvidedFS20Service.profiles);
		initMap();
	}

	/**
	 * Handles the service call and react on it
	 * 
	 * @param call
	 *            = the call from uAAL
	 */
	public ServiceResponse handleCall(ServiceCall call) {

		if (call == null) {
			System.err.println("call error");
			return null;
		}

		String operation = call.getProcessURI();

		System.err.println(operation);

		if (operation == null) {
			System.err.println("operation error");
			return null;
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_GET_FS20GONG)) {
			return getFS20Gong();
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_GET_FS20DISPLAY)) {
			return getFS20Display();
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_GET_FS20FMS)) {
			return getFS20FMS();
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_GET_FS20PIRX)) {
			return getFS20PIRx();
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_GET_FS20ST)) {
			return getFS20ST();
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_ACTIVATE_GONG)) {

			Object deviceURI = call.getInputValue(ProvidedFS20Service.INPUT_URI_GONG);

			if (!(deviceURI instanceof LoudSpeaker))
				return invalidInput;

			return activateGong(((LoudSpeaker) deviceURI).getURI());
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_TURN_ON_FS20ST)) {

			Object deviceURI = call.getInputValue(ProvidedFS20Service.INPUT_URI_FS20ST);

			if (!(deviceURI instanceof SwitchActuator))
				return invalidInput;

			return turnOnFS20ST(((SwitchActuator) deviceURI).getURI());
		}

		if (operation.startsWith(ProvidedFS20Service.SERVICE_TURN_OFF_FS20ST)) {

			Object deviceURI = call.getInputValue(ProvidedFS20Service.INPUT_URI_FS20ST);

			if (!(deviceURI instanceof SwitchActuator))
				return invalidInput;

			return turnOffFS20ST(((SwitchActuator) deviceURI).getURI());
		}

		if (operation.startsWith(ProvidedFS20Service.FS20_SERVER_NAMESPACE + "displayAnimation")) {

			Object deviceURI = call.getInputValue(ProvidedFS20Service.INPUT_URI_DISPLAY);

			if (!(deviceURI instanceof LightActuator))
				return invalidInput;

			for (int i = 1; i < 13; i++) {
				if (operation.startsWith(
						ProvidedFS20Service.FS20_SERVER_NAMESPACE + "displayAnimation" + new Integer(i).toString())) {
					return startAnimation(((LightActuator) deviceURI).getURI(), animationmap.get(i));
				}
			}

		}

		return null;
	}

	/**
	 * Get all loudspeaker alias FS20SIG
	 * 
	 * @return returns all loudspeaker alias FS20SIG
	 */
	private ServiceResponse getFS20Gong() {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		Vector<LoudSpeaker> list = new Vector<LoudSpeaker>();

		for (Iterator<FS20SIGDevice> it = gongs.iterator(); it.hasNext();) {
			list.add(new LoudSpeaker(((FS20SIGDevice) it.next()).getDeviceURI()));
		}
		sr.addOutput(new ProcessOutput(ProvidedFS20Service.OUTPUT_RadioControlledBell, list));
		return sr;

	}

	/**
	 * Get all LightActuators alisa FS20RGBSA
	 * 
	 * @return returns all LightActuators alisa FS20RGBSA
	 */
	private ServiceResponse getFS20Display() {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		Vector<LightActuator> list = new Vector<LightActuator>();

		for (Iterator<FS20RGBSADevice> it = displays.iterator(); it.hasNext();) {
			list.add(new LightActuator(((FS20RGBSADevice) it.next()).getDeviceURI()));
		}

		sr.addOutput(new ProcessOutput(ProvidedFS20Service.OUTPUT_FS20DISPLAYS, list));

		return sr;
	}

	/**
	 * Get all UsageSensors alias FS20FMS
	 * 
	 * @return returns all UsageSensors alias FS20FMS
	 */
	private ServiceResponse getFS20FMS() {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		Vector<UsageSensor> list = new Vector<UsageSensor>();

		for (Iterator<FS20FMSDevice> it = fms.iterator(); it.hasNext();) {
			list.add(new UsageSensor(((FS20FMSDevice) it.next()).getDeviceURI()));
		}

		sr.addOutput(new ProcessOutput(ProvidedFS20Service.OUTPUT_FS20FMS, list));

		return sr;
	}

	/**
	 * Get all SwitchActuators alias FS20ST
	 * 
	 * @return return all SwitchActuators alias FS20ST
	 */
	private ServiceResponse getFS20ST() {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		Vector<SwitchActuator> list = new Vector<SwitchActuator>();

		for (Iterator<FS20STDevice> it = switches.iterator(); it.hasNext();) {
			list.add(new SwitchActuator(((FS20STDevice) it.next()).getDeviceURI()));
		}

		sr.addOutput(new ProcessOutput(ProvidedFS20Service.OUTPUT_FS20ST, list));

		return sr;
	}

	/**
	 * Return all MotionSensors alias FS20PIRx
	 * 
	 * @return returns all MotionSensors alias FS20PIRx
	 */
	private ServiceResponse getFS20PIRx() {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		// list.clear();
		Vector<MotionSensor> list = new Vector<MotionSensor>();

		for (Iterator<FS20PIRxDevice> it = motionsensors.iterator(); it.hasNext();) {
			list.add(new MotionSensor(((FS20PIRxDevice) it.next()).getDeviceURI()));
		}

		sr.addOutput(new ProcessOutput(ProvidedFS20Service.OUTPUT_FS20PIRX, list));

		return sr;
	}

	/**
	 * Turn FS20ST device on by URI
	 * 
	 * @param uri
	 *            = the URI of the FS20 device
	 * @return returns the CallStatus
	 */
	private ServiceResponse turnOnFS20ST(String uri) {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);

		_turnOnFS20ST(uri);

		return sr;
	}

	/**
	 * Turn FS20ST device off by URI
	 * 
	 * @param uri
	 *            = the URI of the FS20ST device
	 * @return returns the CallStatus
	 */
	private ServiceResponse turnOffFS20ST(String uri) {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);

		_turnOffFS20ST(uri);

		return sr;
	}

	/**
	 * Activates the FS20SIG by URI
	 * 
	 * @param uri
	 *            = the URI of the FS20SIG device
	 * @return returns the CallStatus
	 */
	private ServiceResponse activateGong(String uri) {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);

		_activateGong(uri);

		return sr;
	}

	/**
	 * Starts the animation with a given number on a FS20RGBSA device by URI
	 * 
	 * @param animation
	 * @param uri
	 *            = the URI of the FS20ST device
	 * @return returns the CallStatus
	 */
	private ServiceResponse startAnimation(String uri, byte animation) {
		ServiceResponse sr = new ServiceResponse(CallStatus.succeeded);
		_startAnimation(uri, new Byte(animation));
		return sr;
	}

	/**
	 * initialize animation map help method to operate the device
	 * command @FS20Display
	 */
	private void initMap() {
		byte j = 0x00;
		for (int i = 1; i < 13; i++)
			animationmap.put(i, new Byte(j++));
	}

	@Override
	public void communicationChannelBroken() {

	}

	/**
	 * @see startAnimation
	 */
	public boolean _startAnimation(String URI, Byte animation) {
		FS20RGBSADevice d = null;
		// System.err.println("displayList.size= " +displayList.size());

		if (URI == null)
			System.err.println("startAnimation() in FS20Server: illegal parameter!");
		else {

			for (int i = 0; i < displays.size(); i++) {
				if (displays.get(i).getDeviceURI().contains(URI)) {
					d = displays.get(i);
					break;
				}
			}

			if (d == null)
				System.err.println("startAnimation() in FS20Server: unknown display!");
			else {
				try {
					d.startAnimation(animation.intValue());
					return new Boolean(true);
				} catch (Exception e) {
					return new Boolean(false);
				}
			}

		}
		return false;
	}

	/**
	 * @see activateGong
	 */
	public boolean _activateGong(String URI) {
		System.err.println("in activate Gong");
		FS20SIGDevice d = null;

		if (URI == null)
			System.err.println("activateGong() in FS20Server: illegal parameter!");
		else {

			for (int i = 0; i < gongs.size(); i++) {
				if (gongs.get(i).getDeviceURI().contains(URI)) {
					d = gongs.get(i);
					break;
				}
			}

			if (d == null)
				System.err.println("activateGong() in FS20Server: unknown gong!");
			else {
				try {
					d.activateSignal();
					return new Boolean(true);
				} catch (Exception e) {
					System.err.println(e);
					return new Boolean(false);
				}
			}

		}
		return false;
	}

	/**
	 * @see turnOnFS20ST
	 */
	public boolean _turnOnFS20ST(String URI) {
		System.err.println("switch on");
		FS20STDevice d = null;

		if (URI == null)
			System.err.println("_turnOnFS20ST() in FS20Server: illegal parameter!");
		else {

			for (int i = 0; i < switches.size(); i++) {
				if (switches.get(i).getDeviceURI().contains(URI)) {
					d = switches.get(i);
					break;
				}
			}

			if (d == null)
				System.err.println("_turnOnFS20ST() in FS20Server: unknown switch!");
			else {
				try {
					d.switchOn();
					return new Boolean(true);
				} catch (Exception e) {
					System.err.println(e);
					return new Boolean(false);
				}
			}

		}
		return false;
	}

	/**
	 * @see turnOffFS20ST
	 */
	public boolean _turnOffFS20ST(String URI) {
		System.err.println("switch off");
		FS20STDevice d = null;

		if (URI == null)
			System.err.println("_turnOffFS20ST() in FS20Server: illegal parameter!");
		else {

			for (int i = 0; i < switches.size(); i++) {
				if (switches.get(i).getDeviceURI().contains(URI)) {
					d = switches.get(i);
					break;
				}
			}

			if (d == null)
				System.err.println("_turnOffFS20ST() in FS20Server: unknown switch!");
			else {
				try {
					d.switchOff();
					return new Boolean(true);
				} catch (Exception e) {
					System.err.println(e);
					return new Boolean(false);
				}
			}

		}
		return false;
	}
}

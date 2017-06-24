/*
     Copyright 2010-2014 AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at

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

package org.universAAL.lddi.knx.exporter;

import org.universAAL.ontology.device.BlindActuator;
import org.universAAL.ontology.device.BlindController;
import org.universAAL.ontology.device.BlindSensor;
import org.universAAL.ontology.device.ContactSensor;
import org.universAAL.ontology.device.DimmerActuator;
import org.universAAL.ontology.device.DimmerController;
import org.universAAL.ontology.device.DimmerSensor;
import org.universAAL.ontology.device.MotionSensor;
import org.universAAL.ontology.device.SwitchActuator;
import org.universAAL.ontology.device.SwitchController;
import org.universAAL.ontology.device.SwitchSensor;
import org.universAAL.ontology.device.TemperatureSensor;
import org.universAAL.ontology.device.ValueDevice;
import org.universAAL.ontology.device.WindowActuator;
import org.universAAL.ontology.device.WindowController;
import org.universAAL.ontology.device.WindowSensor;

/**
 * Mapping of KNX datapoint types to uAAL Device Ontology. Not all ontology
 * concepts can be mapped certainly to one KNX DPT. e.g. environmental sensors
 * like gas, smoke, water can have the same KNX DPT (e.g. Alarm 1.005) this must
 * be mapped by a human according to name and description of the KNX group
 * address configured in ETS.
 *
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class KnxToDeviceOntologyMappingFactory {

	public enum DeviceOntologyType {
		Actuator, Controller, Sensor;
	}

	/**
	 * Returns null if no suitable ontology concept found
	 *
	 * @param knxDptMain
	 * @param knxDptSub
	 * @param deviceId
	 * @param devOntType
	 * @return ValueDevice (parent concept of all device concepts)
	 */
	public static ValueDevice getDeviceOntologyInstanceForKnxDpt(int knxDptMain, int knxDptSub, String deviceId,
			DeviceOntologyType devOntType) {

		switch (knxDptMain) {
		// knx datapoint type main number 1 (1 bit encoding; values for 0/1 in
		// parentheses)
		case 1:

			switch (knxDptSub) {
			case 1: // DPT_Switch ( 0 = off / 1 = on)
			case 2: // DPT_Bool (false/true)
			case 3: // DPT_Enable (disable/enable)
			case 4: // DPT_Ramp (no ramp/ramp)
			case 5: // DPT_Alarm (no alarm/alarm)
			case 6: // DPT_BinaryValue (low/high)
			case 7: // DPT_Step (decrease/increase)
			case 8: // DPT_UpDown (up/down)
			case 10: // DPT_Start (start/stop)
			case 11: // DPT_State (inactive/active)
			case 12: // DPT_Invert (not inverted/inverted)
			case 13: // DPT_DimSendStyle (start-stop/cyclically)
			case 14: // DPT_InputSource (fixed/calculated)
			case 15: // DPT_Reset (no action (dummy)/reset command (trigger))
			case 16: // DPT_Ack (no action (dummy)/acknowledge command (trigger)
						// e.g. for alarming)
			case 17: // DPT_Trigger (trigger/trigger)
			case 21: // DPT_LogicalFunction (logical function OR/logical
						// function AND)
			case 22: // DPT_Scene_AB (scene A/scene B)
			case 23: // DPT_ShutterBlinds_Mode (only move Up/Down (shutter)/move
						// Up/Down + StepStop mode (blind))

				switch (devOntType) {
				case Actuator:
					return new SwitchActuator("SwitchActuator" + deviceId);
				case Controller:
					return new SwitchController("SwitchController" + deviceId);
				case Sensor:
					return new SwitchSensor("SwitchSensor" + deviceId);
				}

			case 9: // DPT_OpenClose (open/close)
				return new ContactSensor("ContactSensor" + deviceId);

			case 18: // DPT_Occupancy (not occupied/occupied)
				return new MotionSensor("MotionSensor" + deviceId);

			case 19: // DPT_Window_Door (closed/open) values differ from
						// DPT_OpenClose!!
				switch (devOntType) {
				case Actuator:
					return new WindowActuator("WindowActuator" + deviceId);
				case Controller:
					return new WindowController("WindowController" + deviceId);
				case Sensor:
					return new WindowSensor("WindowSensor" + deviceId);
				}
			}

			// knx datapoint type main number 3 (4 bit)
		case 3:
			switch (knxDptSub) {
			case 7: // DPT_Control_Dimming
				switch (devOntType) {
				case Actuator:
					return new DimmerActuator("DimmerActuator" + deviceId);
				case Controller:
					return new DimmerController("DimmerController" + deviceId);
				case Sensor:
					return new DimmerSensor("DimmerSensor" + deviceId);
				}
			case 8: // DPT_Control_Blinds
				switch (devOntType) {
				case Actuator:
					return new BlindActuator("BlindActuator" + deviceId);
				case Controller:
					return new BlindController("BlindController" + deviceId);
				case Sensor:
					return new BlindSensor("BlindSensor" + deviceId);
				}
			}
			break;

		// knx datapoint type main number 5 (8 Bit - Unsigned Value)
		case 5:
			switch (knxDptSub) {
			case 1: // DPT_Scaling (percentage 0 - 100%)
				switch (devOntType) {
				case Actuator:
					return new DimmerActuator("DimmerActuator" + deviceId);
				case Controller:
					return new DimmerController("DimmerController" + deviceId);
				case Sensor:
					return new DimmerSensor("DimmerSensor" + deviceId);
				}
			}
			break;

		// knx datapoint type main number 9 (2-Octet Float Value)
		case 9:
			switch (knxDptSub) {
			case 1: // DPT_Value_Temp
				return new TemperatureSensor("TemperatureSensor" + deviceId);
			}
			break;

		} // end outer switch statement
		return null;
	}

	//
	// /**
	// * Returns null if no suitable ontology concept found
	// * @param knxDptMain
	// * @param knxDptSub
	// * @return ontology concept
	// */
	// public static DeviceOntologyType getOntologyConceptForKnxDpt(int
	// knxDptMain, int knxDptSub){
	//
	// switch(knxDptMain) {
	// // knx datapoint type main number 1 (1 bit encoding; values for 0/1 in
	// parentheses)
	// case 1:
	// switch(knxDptSub){
	// case 1: // DPT_Switch (off/on)
	// return DeviceOntologyType.SwitchController;
	// case 2:
	// return null;
	// //...
	// case 6: // DPT_BinaryValue (low/high)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_TEMP;
	// case 7:
	// return null;
	// case 8: // DPT_UpDown (up/down)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_SWITCH;
	// case 9: // DPT_OpenClose (open/close)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_CONTACTCLOSURE;
	// case 10: // DPT_Start (start/stop)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_USAGE;
	// //...
	// case 18: // DPT_Occupancy (not occupied/occupied)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_MOTION;
	// case 19: // DPT_Window_Door (closed/open) values differ from
	// DPT_OpenClose!!
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_CONTACTCLOSURE;
	//
	// }
	// break;
	//
	// // knx datapoint type main number 2
	// case 2:
	// switch(knxDptSub){
	// case 1:
	// return null;
	// case 2:
	// return null;
	// //...
	// }
	// break;
	//
	// // knx datapoint type main number 3 (4 bit)
	// case 3:
	// switch(knxDptSub){
	// case 7: // DPT_Control_Dimming ***************************NEEDED
	// return null;
	// case 8: // DPT_Control_Blinds
	// return null;
	// }
	// break;
	//
	// // knx datapoint type main number 4 (8 bit - Character Set)
	// case 4:
	// switch(knxDptSub){
	// case 1: // DPT_Char_ASCII
	// return null;
	// case 2: // DPT_Char_8859_1 ***************************NEEDED
	// return null;
	// }
	// break;
	//
	// // knx datapoint type main number 5 (8 Bit - Unsigned Value)
	// case 5:
	// switch(knxDptSub){
	// case 1: // DPT_Scaling
	// // WHICH NEW TYPE SHOULD BE DEFINED FOR THIS ????????
	// //return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_SWITCH;
	// //...
	// }
	// break;
	//
	// // knx datapoint type main number 9 (2-Octet Float Value)
	// case 9:
	// switch(knxDptSub){
	// case 1: // DPT_Value_Temp ***************************NEEDED
	// return null;
	// case 2:
	// return null;
	// //...
	// }
	// break;
	//
	// // knx datapoint type main number 20
	// case 20:
	// switch(knxDptSub){
	// case 3: //DPT_OccMode (occupied/standby/not occupied)
	// // return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_MOTION;
	// //...
	// }
	// break;
	//
	// default:
	// return null;
	//
	// // knx datapoint type main number ...
	// //...
	//
	//
	// }
	//
	// return null;
	// }

}

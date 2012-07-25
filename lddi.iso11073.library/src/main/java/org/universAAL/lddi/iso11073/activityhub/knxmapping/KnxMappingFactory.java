package org.universAAL.lddi.iso11073.activityhub.knxmapping;

import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;

/**
 * Mapping of KNX datapoint types to ActivityHubSensors
 * Not all ActivityHub sensor types can be mapped certainly to one KNX DPT
 * e.g. environmental sensors like gas, smoke, water can have the same KNX DPT (e.g. Alarm 1.005)
 * this must be mapped by a human according to name and description of the KNX group address
 * configured in ETS
 * 
 * @author Thomas Fuxreiter
 */
public class KnxMappingFactory {

	/**
	 * Returns null if no suitable ActivityHubDeviceCategory found
	 * @param knxDptMain
	 * @param knxDptSub
	 * @return ActivityHubDeviceCategory
	 */
	public static ActivityHubDeviceCategory getAHDevCatForKnxDpt(int knxDptMain, int knxDptSub){

		switch(knxDptMain) {
		// knx datapoint type main number 1 (1 bit encoding; values for 0/1 in parentheses)
		case 1:
			switch(knxDptSub){
			case 1: // DPT_Switch (off/on)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_SWITCH;
			case 2:
				return null;
			//...
			case 6: // DPT_BinaryValue (low/high)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_TEMP;
			case 7:
				return null;
			case 8: // DPT_UpDown (up/down)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_SWITCH;
			case 9: // DPT_OpenClose (open/close)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_CONTACTCLOSURE;
			case 10: // DPT_Start (start/stop)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_USAGE;
			//...
			case 18: // DPT_Occupancy (not occupied/occupied)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_MOTION;
			case 19: // DPT_Window_Door (closed/open) values differ from DPT_OpenClose!! 
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_CONTACTCLOSURE;

			}
			break;

		// knx datapoint type main number 2
		case 2:
			switch(knxDptSub){
			case 1:
				return null;
			case 2:
				return null;
			//...
			}
			break;

		// knx datapoint type main number 3 (4 bit)
		case 3:
			switch(knxDptSub){
			case 7: // DPT_Control_Dimming ***************************NEEDED
				return null;
			case 8: // DPT_Control_Blinds
				return null;
			}
			break;

		// knx datapoint type main number 4 (8 bit - Character Set)
		case 4:
			switch(knxDptSub){
			case 1: // DPT_Char_ASCII
				return null;
			case 2: // DPT_Char_8859_1 ***************************NEEDED
				return null;
			}
			break;

		// knx datapoint type main number 5 (8 Bit - Unsigned Value)
		case 5:
			switch(knxDptSub){
			case 1: // DPT_Scaling ***************************NEEDED
				return null;
			//...
			}
			break;

		// knx datapoint type main number 9 (2-Octet Float Value)
		case 9:
			switch(knxDptSub){
			case 1: // DPT_Value_Temp ***************************NEEDED
				return null;
			case 2:
				return null;
			//...
			}
			break;

		// knx datapoint type main number 20
		case 20:
			switch(knxDptSub){
			case 3: //DPT_OccMode (occupied/standby/not occupied)
				return ActivityHubDeviceCategory.MDC_AI_TYPE_SENSOR_MOTION;
			//...
			}
			break;

		default:
			return null;
		
		// knx datapoint type main number ...
		//...
		
		
		}
		
		return null;
	}
}

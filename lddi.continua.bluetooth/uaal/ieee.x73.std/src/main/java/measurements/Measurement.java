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
package measurements;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import x73.p20601.mdnf.FloatType;
import x73.p20601.mdnf.SFloatType;

import utils.ASNUtils;
import utils.Logging;
import x73.p20601.AbsoluteTime;
import x73.p20601.BasicNuObsValue;
import x73.p20601.BasicNuObsValueCmp;
import x73.p20601.FLOAT_Type;
import x73.p20601.NuObsValue;
import x73.p20601.OID_Type;
import x73.p20601.SFLOAT_Type;
import x73.p20601.SimpleNuObsValue;
import x73.p20601.SimpleNuObsValueCmp;

public class Measurement {
	
	private String weightValue = null;
	private String weightUnit = null;
	private String weightTimestamp = null;
	
	public static Double weightMeasurement = null;
	public static Double sysMeasurement = null;
	public static Double diaMeasurement = null;
	public static Double hrMeasurement = null;


	private static MeasurementValue[] measures;
	int index = 0;
	
	public Measurement(){
	}
	
	public Measurement(int i){
		measures = new MeasurementValue[i];
	}
	
	
	public int getLength(){
		return measures.length;
	}
	
	
	public void addMeasurement(Object time, OID_Type unit, Object value){
		MeasurementValue measure = new MeasurementValue(time, unit, value);
		measures[index] = measure;
		index++;
	}
	
	
	
	public void printMeasurement(int[] time_gap){
		
		for (int i = 0; i<measures.length; i++){
			
			MeasurementValue measure = measures[i];
			Object time = measure.getTimeObject();
			OID_Type unit = measure.getOIDType();
			int unit_code = unit.getValue().getValue();
			Object measure_value = measure.getMeasureObject();
			printObject(measure_value, unit_code, time_gap);
			printObject(time, unit_code,time_gap);
			Logging.Separator();
		}
		
		
	}

	// process the array of measurements extracted in class DeviceSpecialization
	// we will store them in a specific order: time stamp of the measure, the observed value and its unit.
	public void addMeasurement(Object[] objects) {
		
		Object time = null, value = null;
		OID_Type unit = null;
		
		for (int i = 0 ; i<objects.length; i++)
		{
			if(isTime(objects[i])){
				time = objects[i];
			}else if(isValue(objects[i])){
				value = objects[i];
			}else if(isUnit(objects[i])){
				unit = (OID_Type)objects[i];
			}
		}
		
		addMeasurement(time, unit, value);
		
	}

	private boolean isUnit(Object object) {
		if (object.getClass().equals(OID_Type.class)){
			return true;
		}else{
			return false;
		}
	}

	private boolean isValue(Object object) {
		if (object.getClass().equals(BasicNuObsValue.class) ||
				object.getClass().equals(FloatType.class) ||
				object.getClass().equals(SFloatType.class) ||
				object.getClass().equals(SimpleNuObsValue.class) ||
				object.getClass().equals(FLOAT_Type.class) ||
				object.getClass().equals(NuObsValue.class) ||
				object.getClass().equals(BasicNuObsValueCmp.class) ||
				object.getClass().equals(SimpleNuObsValueCmp.class)
				){
			return true;
		}else{
			return false;
		}
	}

	private boolean isTime(Object object) {
		if (object.getClass().equals(AbsoluteTime.class)){
			return true;
		}else{
			return false;
		}
	}
	
	// represent the information. The representation will depend of its type.
	protected void printObject(Object obj, int unit_code, int[] time_gap){
		
		if (obj instanceof AbsoluteTime){
			printDate((AbsoluteTime)obj, time_gap);
		}else if (obj instanceof BasicNuObsValueCmp){
			printBasicNuObsValueCmp((BasicNuObsValueCmp)obj, unit_code);
		}else if (obj instanceof BasicNuObsValue){
			printBasicNuObsValue((BasicNuObsValue)obj, unit_code);
		}else if (obj instanceof OID_Type){
			printOIDType((OID_Type)obj);
		}else if (obj instanceof SimpleNuObsValueCmp){
			printSimpleNuObsValueCmp((SimpleNuObsValueCmp) obj, unit_code);
		}else if (obj instanceof SimpleNuObsValue){
			printSimpleNuObsValue((SimpleNuObsValue) obj, unit_code);
		}
		
	}
	


	private void printSimpleNuObsValue(SimpleNuObsValue obj, int unit_code) {		
		FLOAT_Type float_type = obj.getValue();		
		FloatType measure;
		try {
			measure = new FloatType(float_type.getValue().getValue());
			DecimalFormat df = generateFloatFormat(measure.getExponent());			
			Logging.logMeasurement(df.format(measure.getFloatValue()) + " " + ASNUtils.getUnitName(unit_code));
			weightValue = df.format(measure.getFloatValue());
			// Weight measurement from weight scale
			setWeightMeasurement(measure.getFloatValue());			
			weightUnit = ASNUtils.getUnitName(unit_code);	
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void printBasicNuObsValue(BasicNuObsValue obj, int unit_code) {		
		SFLOAT_Type sfloat_type = obj.getValue();
		SFloatType measure;
		try {
			measure = new SFloatType(sfloat_type.getValue().getValue().shortValue());
			
			DecimalFormat df = generateFloatFormat(measure.getExponent());			
			Logging.logMeasurement(df.format(measure.getFloatValue()) + " " + ASNUtils.getUnitName(unit_code));
			weightValue = df.format(measure.getFloatValue());
			// Heart rate measurement from blood pressure monitor
			setHeartRateMeasurement(measure.getFloatValue());
			weightUnit = ASNUtils.getUnitName(unit_code);	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	/*
	 * This method is used to represent a float point value with the same significant numbers as it was sent (set by the negative exponent value)
	 */
	private DecimalFormat generateFloatFormat(int exponent) {
		
		String significantdigits="#";
		if(exponent<0){
			significantdigits = significantdigits+".";
			for(int i = 0; i>exponent; i--){
				significantdigits = significantdigits+"0";
			}
		}
		return new DecimalFormat(significantdigits);
		
		
	}

	
	private void printSimpleNuObsValueCmp(SimpleNuObsValueCmp obj, int unit_code) {		
		Iterator<SimpleNuObsValue> it = obj.getValue().iterator();
		while (it.hasNext()){
			SimpleNuObsValue value = it.next();
			FLOAT_Type float_value = value.getValue();
			FloatType measure;
			try {
				measure = new FloatType(float_value.getValue().getValue());
				DecimalFormat df = generateFloatFormat(measure.getExponent());			
				Logging.logMeasurement(df.format(measure.getFloatValue()) + " " + ASNUtils.getUnitName(unit_code));
				weightValue = df.format(measure.getFloatValue());
				weightUnit = ASNUtils.getUnitName(unit_code);	
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	// Final data value
	public String getWeighValue() {
		return weightValue;
	}
	
	// Final data unit
	public String getWeightUnit() {
		return weightUnit; 
	}
	
	// Final data timestamp
	public String getWeightTimestamp() {
		return weightTimestamp;
	}
	
	private void printBasicNuObsValueCmp(BasicNuObsValueCmp obj, int unit_code) {	
		int counter = 0;
		Iterator<BasicNuObsValue> it = obj.getValue().iterator();
		while (it.hasNext()){
			BasicNuObsValue value = it.next();
			SFLOAT_Type sfloat_value = value.getValue();
			SFloatType measure;
			try {
				measure = new SFloatType(sfloat_value.getValue().getValue().shortValue());
				DecimalFormat df = generateFloatFormat(measure.getExponent());			
				Logging.logMeasurement(df.format(measure.getFloatValue()) + " " + ASNUtils.getUnitName(unit_code));
				weightValue = df.format(measure.getFloatValue());
				if(counter == 0)
					// Sys measurement from blood pressure monitor
					setSysMeasurement(measure.getFloatValue());
				else if(counter == 1) 
					// Dia measurement from blood pressure monitor
					setDiaMeasurement(measure.getFloatValue());				
				weightUnit = ASNUtils.getUnitName(unit_code);	
			} catch (Exception e) {
				e.printStackTrace();
			}
			counter++;
		}
	}
	
	
	private void printDate(AbsoluteTime abstime, int[] time_gap){
		
		int year_gap, day_gap, month_gap, hour_gap, min_gap, secs_gap;
		int device_year, device_month, device_day, device_hour, device_min, device_secs;
		int year_fixed, day_fixed, month_fixed, hour_fixed, min_fixed, secs_fixed;
		int[] modified_values = new int[6];
		
		year_gap = time_gap[0];
		day_gap = time_gap[1];
		month_gap = time_gap[2];
		hour_gap = time_gap[3];
		min_gap = time_gap[4];
		secs_gap = time_gap[5];
		
		device_year = ASNUtils.BCDtoInt(abstime.getCentury().getValue())*100+ASNUtils.BCDtoInt(abstime.getYear().getValue());
		device_month = ASNUtils.BCDtoInt(abstime.getMonth().getValue());
		device_day = ASNUtils.BCDtoInt(abstime.getDay().getValue());
		device_hour = ASNUtils.BCDtoInt(abstime.getHour().getValue());
		device_min = ASNUtils.BCDtoInt(abstime.getMinute().getValue());
		device_secs = ASNUtils.BCDtoInt(abstime.getSecond().getValue());
		
		modified_values[0] = device_year+year_gap;
		modified_values[1] = device_month+month_gap;
		modified_values[2] = device_day+day_gap;
		modified_values[3] = device_hour+hour_gap;
		modified_values[4] = device_min+min_gap;
		modified_values[5] = device_secs+secs_gap;
		
		
		
//		fixDate(new int[]{2000, 12, 31, 23, 59, 61});
//		fixDate(new int[]{2012, 2, 29, 23, 59, 61});
//		fixDate(new int[]{2011, 1, 1, 0, 0, -1});
		

		
		modified_values=fixDate(modified_values);
		
		String year = String.format("%04d", modified_values[0]);
		String month = String.format("%02d",  modified_values[1]);
		String day = String.format("%02d",  modified_values[2]);
		String hour = String.format("%02d",  modified_values[3]);
		String min = String.format("%02d",  modified_values[4]);
		String sec = String.format("%02d",  modified_values[5]);
		
		Logging.logMeasurement("Date of measurement: "+hour+":"+min+":"+sec+" - "+day+"/"+month+"/"+year);
		weightTimestamp = hour+":"+min+":"+sec+" - "+day+"/"+month+"/"+year;
	}
	


	private void printOIDType(OID_Type obj) {
		
		System.out.println(obj.getValue().getValue());
		
	}
	
	private int[] fixDate(int[] modified_values) {
		
		int[] DAYS_IN_MONTH = {0,31,28,31,30,31,30,31,31,30,31,30,31};
		
		if(modified_values[0]%4==0){ // if it is a leap year...
			DAYS_IN_MONTH[2]=29;
		}
		
		int value_to_fix;
		for (int i = 5; i>0; i--){
			if (i==5){ // seconds
				
				// check if seconds value is not valid
				if (modified_values[5] > 59){
					modified_values[5] -=  60;
					modified_values[4] += 1; // minutes
				}
				else if(modified_values[5]<0){
					modified_values[5] += 60;
					modified_values[4] -= 1; // minutes
				}
			}
			
			if(i==4){
				// check if minutes value is not valid
				if (modified_values[4] > 59){
					modified_values[4] -= 60;
					modified_values[3] += 1;  // hour
				}
				else if(modified_values[4]<0){
					modified_values[4] += 60;
					modified_values[3] -= 1;  // hour
				}
			}
			
			if(i==3){
				// check if hours value is not valid
				if (modified_values[3] > 23){
					modified_values[3] -= 24;
					modified_values[2] += 1;  // day
				}
				else if(modified_values[3]<0){
					modified_values[3] += 24;
					modified_values[2] -= 1;  // day
				}
			}
			
			if(i==2){
				//check days. Its validity depend on the month
				int month = modified_values[1];
				int days_in_measure_month = DAYS_IN_MONTH[month];
				
				if(modified_values[2]>days_in_measure_month) {
					// i.e: January 32nd = February 1st(due to a change in the hour, due to a change in the minutes, due to a change in de seconds)
					modified_values[2] -= days_in_measure_month;
					modified_values[1] += 1; // month
				}
				else if(modified_values[2]<1){ 
					//i.e: November 0th =  October 31st
					modified_values[2] = DAYS_IN_MONTH[modified_values[2]-1];
					modified_values[1] -= 1;  // month
				}
			}
			if(i==1){
				// check if months value is not valid
				if (modified_values[1] > 12){
					modified_values[1] -= 12;
					modified_values[0] += 1;  // year
				}
				else if(modified_values[1]<0){
					modified_values[1] += 12;
					modified_values[0] -= 1;  // year
				}
			}
		}
		
		return modified_values;
	}
	
	public void setWeightMeasurement(Double f) {
		weightMeasurement = f;
	}	
	
	public void setHeartRateMeasurement(Double f) {
		hrMeasurement = f;
	}
	
	public void setSysMeasurement(Double f) {
		sysMeasurement = f;
	}
	
	public void setDiaMeasurement(Double f) {
		diaMeasurement = f;
	}
}

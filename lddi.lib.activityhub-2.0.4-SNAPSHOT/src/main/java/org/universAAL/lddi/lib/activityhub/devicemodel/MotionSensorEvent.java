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

package org.universAAL.lddi.lib.activityhub.devicemodel;

/**
 * sensor events of motion sensor ISO 11073-10471
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum MotionSensorEvent {
	MOTION_DETECTED(0),
	MOTION_DETECTED_DELAYED(1),
	TAMPER_DETECTED(2), 
	NO_CONDITION_DETECTED(3);
		
	private final int value;
		
	private MotionSensorEvent(int value) {
		this.value = value;
	}
		
	public int value() { return value; }
	
	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static MotionSensorEvent toMotionSensorEvent(String str)
    {
        try {
            return MotionSensorEvent.valueOf(str);
        } 
        catch (Exception ex) {
            //IllegalArgumentException - if the specified enum type has no constant with the specified name, or the specified class object does not represent an enum type
            //NullPointerException - if enumType or name is null
        	ex.printStackTrace();
            return null;
        }
    }
    
	/**
	 * convert int to enum item
	 * 
	 * @param int
	 * @return enum item
	 */
    public static MotionSensorEvent getMotionSensorEvent(int val) {
            for (MotionSensorEvent mse : MotionSensorEvent.values()) {
            	if ( mse.value == val ) return mse;
            }
            throw new AssertionError();
    }

}

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

//package org.universAAL.lddi.knx.exporter;
//
//import java.util.EnumSet;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 
// * @author Thomas Fuxreiter (foex@gmx.at)
// */
//public abstract class DeviceOntologyTypeUtil {
//
//	/**
//	 * convert String to enum item
//	 * 
//	 * @param str
//	 * @return enum item
//	 */
//    public static DeviceOntologyType toActivityHubDevice(String str)
//    {
//        try {
//            return DeviceOntologyType.valueOf(str);
//        } 
//        catch (Exception ex) {
//            return null;
//        }
//    }
//    
//    /**
//     * device types from uAAL device ontology
//     * according to org.universAAL.ontology.DeviceFactory
//     */
//    public enum DeviceOntologyType {
//    	SwitchActuator(5),
//    	SwitchController(18),
//    	SwitchSensor(21);
//
//    	private int typecode;
//    	
//    	private static final Map<Integer,DeviceOntologyType> lookup = 
//    		new HashMap<Integer,DeviceOntologyType>();
//        
//    	static {
//            for(DeviceOntologyType s : EnumSet.allOf(DeviceOntologyType.class))
//                 lookup.put(s.getTypeCode(), s);
//        }
//        
//    	private DeviceOntologyType(int typecode) {
//    		this.typecode = typecode;
//    	}
//    	
//    	public int getTypeCode() {
//    		return typecode;
//    	}
//    	
//        public static DeviceOntologyType get(int code) { 
//            return lookup.get(code); 
//        }
//    }    
//}

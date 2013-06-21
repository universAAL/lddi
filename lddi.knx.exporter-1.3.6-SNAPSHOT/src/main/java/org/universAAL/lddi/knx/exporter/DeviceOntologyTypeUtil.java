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

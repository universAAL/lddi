package org.universAAL.lddi.knx.devicecategory;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
 
 /**
  * Definition of main device categories in KNX.
  * According to spec: "KXN Datapoint Types v1.7.00 AS"
  * 
  * @author Thomas Fuxreiter (foex@gmx.at)
  */
public abstract class KnxDeviceCategoryUtil {

	/**
	 * convert String to enum item
	 * 
	 * @param str
	 * @return enum item
	 */
    public static KnxDeviceCategory toKnxDevice(String str)
    {
        try {
            return KnxDeviceCategory.valueOf(str);
        } 
        catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * device category format according to KNX spec.
     */
    public enum KnxDeviceCategory {
    	KNX_DPT_1(1),
    	KNX_DPT_2(2),
    	KNX_DPT_3(3),
    	KNX_DPT_4(4),
    	KNX_DPT_5(5),
    	KNX_DPT_6(6),
    	KNX_DPT_7(7),
    	KNX_DPT_8(8),
    	KNX_DPT_9(9),
    	KNX_DPT_10(10),
    	KNX_DPT_11(11),
    	KNX_DPT_12(12),
    	KNX_DPT_13(13),
    	KNX_DPT_14(14),
    	KNX_DPT_15(15),
    	KNX_DPT_16(16),
    	KNX_DPT_17(17),
    	KNX_DPT_18(18),
    	KNX_DPT_19(19),
    	KNX_DPT_20(20),
    	KNX_DPT_21(21),
    	KNX_DPT_22(22),
    	KNX_DPT_23(23),
    	KNX_DPT_24(24),
    	KNX_DPT_25(25),
    	KNX_DPT_26(26),
    	KNX_DPT_27(27),
    	KNX_DPT_28(28),
    	KNX_DPT_29(29),
    	KNX_DPT_30(30),
    	KNX_DPT_31(31),
    	KNX_DPT_200(200),
    	KNX_DPT_201(201),
    	KNX_DPT_202(202),
    	KNX_DPT_203(203),
    	KNX_DPT_204(204),
    	KNX_DPT_205(205),
    	KNX_DPT_206(206),
    	KNX_DPT_207(207),
    	KNX_DPT_209(209),
    	KNX_DPT_210(210),
    	KNX_DPT_211(211),
    	KNX_DPT_212(212),
    	KNX_DPT_213(213),
    	KNX_DPT_214(214),
    	KNX_DPT_215(215),
    	KNX_DPT_216(216),
    	KNX_DPT_217(217),
    	KNX_DPT_218(218),
    	KNX_DPT_219(219),
    	KNX_DPT_220(220),
    	KNX_DPT_221(221),
    	KNX_DPT_222(222),
    	KNX_DPT_223(223),
    	KNX_DPT_224(224),
    	KNX_DPT_225(225),
    	KNX_DPT_229(229),
    	KNX_DPT_230(230),
    	KNX_DPT_231(231),
    	KNX_DPT_232(232),
    	KNX_DPT_234(234),
    	KNX_DPT_235(235),
    	KNX_DPT_236(236),
    	KNX_DPT_237(237),
    	KNX_DPT_238(238),
    	KNX_DPT_239(239),
    	KNX_DPT_240(240),
    	KNX_DPT_241(241);


    	private int typecode;
    	
    	private static final Map<Integer,KnxDeviceCategory> lookup = 
    		new HashMap<Integer,KnxDeviceCategory>();
        
    	static {
            for(KnxDeviceCategory s : EnumSet.allOf(KnxDeviceCategory.class))
                 lookup.put(s.getTypeCode(), s);
        }
        
    	private KnxDeviceCategory(int typecode) {
    		this.typecode = typecode;
    	}
    	
    	public int getTypeCode() {
    		return typecode;
    	}
    	
        public static KnxDeviceCategory get(int code) { 
            return lookup.get(code); 
        }
    }
}

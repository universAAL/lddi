package org.universAAL.lddi.knx.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The priority of a KNX packet is sent within the control byte (first byte of a KNX telegram).
 * Octet from control byte:
 * 	D7 D6 D5 D4 D3 D2 D1 D0
 * 	1  0  W  1  P  P  0  0  
 * 	            0  0		System command (highest priority)
 * 				1  0		Alarm command
 * 				0  1		Normal (high priority)
 * 				1  1		Auto (low priority) DEFAULT !
 * 		  0					Repeat (0 if this telegram was sent again)
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum KnxPacketPriority {

	SYSTEM(0), //highest priority 
	NORMAL(1), //normal priority
	ALARM(2), //high priority
	AUTO(3); //low priority

	private int typecode;

	private KnxPacketPriority(int typecode) {
		this.typecode = typecode;
	}
	
	public int getTypeCode() {
		return typecode;
	}
	
	private static final Map<Integer,KnxPacketPriority> lookup = 
		new HashMap<Integer,KnxPacketPriority>();
    
	static {
        for(KnxPacketPriority s : EnumSet.allOf(KnxPacketPriority.class))
             lookup.put(s.getTypeCode(), s);
    }
	
    public static KnxPacketPriority get(int code) { 
        return lookup.get(code); 
    }
}

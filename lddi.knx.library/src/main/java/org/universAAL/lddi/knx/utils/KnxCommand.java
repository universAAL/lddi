package org.universAAL.lddi.knx.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The KNX command type is sent within the TCPI and ACPI bytes.
 * Octets:
 * 0 0 X X X X C C   C C D D D D D D	  B = Command (APCI)	D = Data
 * 			   0 0   0 0 					Value Read
 * 			   0 0   0 1 					Value Response	
 * 			   0 0   1 0 					Value Write
 * 			   1 0   1 0 					Memory Write
 * 0 0 0 0 0 0 0 0   1 0 0 0 0 0 0 0	0x80
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public enum KnxCommand {

	VALUE_READ(0), //highest priority
	VALUE_RESPONSE(1), //normal priority
	VALUE_WRITE(2), //high priority
	MEMORY_WRITE(10); //low priority

	private int typecode;

	private KnxCommand(int typecode) {
		this.typecode = typecode;
	}
	
	public int getTypeCode() {
		return typecode;
	}
	
	private static final Map<Integer,KnxCommand> lookup = 
		new HashMap<Integer,KnxCommand>();
    
	static {
        for(KnxCommand s : EnumSet.allOf(KnxCommand.class))
             lookup.put(s.getTypeCode(), s);
    }
	
    public static KnxCommand get(int code) { 
        return lookup.get(code); 
    }
}

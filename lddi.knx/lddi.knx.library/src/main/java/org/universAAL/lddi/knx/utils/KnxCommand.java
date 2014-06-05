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

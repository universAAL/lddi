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

package org.universAAL.lddi.knx.groupdevicemodel;


/**
 * Factory for KNX group devices for each data point type.
 * According to spec: "KXN Datapoint Types v1.7.00 AS"
 * 
 * @author Thomas Fuxreiter
 */
public abstract class KnxGroupDeviceFactory {

	/**
	 * @param dptMainNumber e.g.: 1
	 * @return KnxGroupDevice object or null if no implementation for the given datapoint number exists
	 */
	public static KnxGroupDevice getKnxGroupDevice(int dptMainNumber) {
	
    	switch (dptMainNumber) {
			case 1: return new KnxDpt1GroupDevice();
			case 2: return new KnxDpt2GroupDevice();
			case 3: return new KnxDpt3GroupDevice();
			case 4: return new KnxDpt4GroupDevice();
			case 5: return new KnxDpt5GroupDevice();
//			case 6: return new KnxDpt6Device();
//			case 7: return new KnxDpt7Device();
//			case 8: return new KnxDpt8Device();
			case 9: return new KnxDpt9GroupDevice();
//			case 10: return new KnxDpt10Device();
//			case 11: return new KnxDpt11Device();
//			case 12: return new KnxDpt12Device();
//			case 13: return new KnxDpt13Device();
//			case 14: return new KnxDpt14Device();
//			case 15: return new KnxDpt15Device();
//			case 16: return new KnxDpt16Device();
//			case 17: return new KnxDpt17Device();
//			case 18: return new KnxDpt18Device();
//			case 19: return new KnxDpt19Device();
//			case 20: return new KnxDpt20Device();
//			case 21: return new KnxDpt21Device();
//			case 22: return new KnxDpt22Device();
//			case 23: return new KnxDpt23Device();
//			case 24: return new KnxDpt24Device();
//			case 25: return new KnxDpt25Device();
//			case 26: return new KnxDpt26Device();
//			case 27: return new KnxDpt27Device();
//			case 28: return new KnxDpt28Device();
//			case 29: return new KnxDpt29Device();
//			case 30: return new KnxDpt30Device();
//			case 31: return new KnxDpt31Device();
//			case 200: return new KnxDpt200Device();
//			case 201: return new KnxDpt201Device();
//			case 202: return new KnxDpt202Device();
//			case 203: return new KnxDpt203Device();
//			case 204: return new KnxDpt204Device();
//			case 205: return new KnxDpt205Device();
//			case 206: return new KnxDpt206Device();
//			case 207: return new KnxDpt207Device();
//			
//			case 209: return new KnxDpt209Device();
//			case 210: return new KnxDpt210Device();
//			case 211: return new KnxDpt211Device();
//			case 212: return new KnxDpt212Device();
//			case 213: return new KnxDpt213Device();
//			case 214: return new KnxDpt214Device();
//			case 215: return new KnxDpt215Device();
//			case 216: return new KnxDpt216Device();
//			case 217: return new KnxDpt217Device();
//			case 218: return new KnxDpt218Device();
//			case 219: return new KnxDpt219Device();
//			case 220: return new KnxDpt220Device();
//			case 221: return new KnxDpt221Device();
//			case 222: return new KnxDpt222Device();
//			case 223: return new KnxDpt223Device();
//			case 224: return new KnxDpt224Device();
//			case 225: return new KnxDpt225Device();
//			
//			case 229: return new KnxDpt229Device();
//			case 230: return new KnxDpt230Device();
//			case 231: return new KnxDpt231Device();
//			case 232: return new KnxDpt232Device();
//
//			case 234: return new KnxDpt234Device();
//			case 235: return new KnxDpt235Device();
//			case 236: return new KnxDpt236Device();
//			case 237: return new KnxDpt237Device();
//			case 238: return new KnxDpt238Device();
//			case 239: return new KnxDpt239Device();
//			case 240: return new KnxDpt240Device();
//			case 241: return new KnxDpt241Device();

			
			default:  return null;
		}

    }
}

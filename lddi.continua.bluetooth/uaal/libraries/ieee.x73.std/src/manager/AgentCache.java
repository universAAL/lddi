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
package manager;

import java.util.Hashtable;

public class AgentCache {

	private Hashtable<byte[], String> cache; // to implement. don't sure about the arguments... now is prepared to admit a serial number and a type of agent
	
	public AgentCache(){
		cache = new Hashtable<byte[], String>();
	}
	
	public synchronized void addAgent(byte[] sys_id, String dev_id){
		cache.put(sys_id, dev_id);
	}
	
	public synchronized boolean isCached(byte[] sys_id){
		return cache.containsKey(sys_id);
	}
	
	
	
}

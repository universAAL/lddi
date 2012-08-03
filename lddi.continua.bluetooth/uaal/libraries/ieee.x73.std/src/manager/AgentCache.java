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

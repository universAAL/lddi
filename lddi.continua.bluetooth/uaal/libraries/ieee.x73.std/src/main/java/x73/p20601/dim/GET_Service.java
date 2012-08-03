package x73.p20601.dim;

public interface GET_Service {

	
	/**
	 * The manager may request the MDS object attributes of the agent in which case the manager shall send the
	Remote Operation Invoke | Get command (see roiv-cmip-get in A.10.2) with the reserved handle value of
	0. The agent shall respond by reporting its MDS object attributes to the manager using the Remote
	Operation Response | Get response (see rors-cmip-get in A.10.2). In the response to a Get MDS Object
	command, only attributes implemented by the agent are returned.
	 */
	public void GET();
	
}

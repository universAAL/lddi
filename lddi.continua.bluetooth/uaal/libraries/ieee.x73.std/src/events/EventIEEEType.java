package events;

public interface EventIEEEType {

	/*
	 * Events for managing FSM
	 */
	
	public final int TRANSPORT_ON = 0;
	public final int TRANSPORT_OFF = 1;
	public final int TIMEOUT_EVENT = 2;
	
	
	/*
	 * Events from received APDUs
	 */
	public final int APDU_RECEIVED = 10;
	public final int AARQ_RECEIVED = 11;
	public final int RLRQ_RECEIVED = 12;
	public final int ABRT_RECEIVED = 13;
	public final int DATA_RECEIVED = 14;
	
	/*
	 * Events from sent APDUs
	 */
	public final int APDU_SENT = 20;
}

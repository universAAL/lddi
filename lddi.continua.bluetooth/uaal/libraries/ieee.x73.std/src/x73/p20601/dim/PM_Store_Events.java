package x73.p20601.dim;

import x73.p20601.SegmentDataEvent;
import x73.p20601.SegmentDataResult;

public interface PM_Store_Events {

	
	/**
	 * This event sends data stored in the Fixed-Segment-Data of a PM-segment from the agent to the
		manager. The event is triggered by the manager by the Trig-Segment-Data-Xfer method. Once the data
		transfer is triggered, the agent sends Segment-Data-Event messages until the complete Fixed-Segment-
		Data is transferred or the transfer is aborted by the manager or agent. See Transfer PM-segment content
		in 8.9.3.4.2 for a full description.
		It is encouraged to place as many segment entries contained in a Segment-Data-Event as possible to
		reduce the number of messages required for the transfer of the segment.
		Support for the event by the agent is mandatory if the agent supports PM-store objects.
	 */
	public SegmentDataResult Segment_Data_Event(SegmentDataEvent segdataev);
	
	
}

package events;

public class Event{

	private int type;
	private int reason;
	
	public Event (int type){
		this.type = type;
	}
	
	public int getType(){
		return type;
	}
	
	public void setReason (int reason){
		this.reason = reason;
	}
	
	public int getReason()
	{
		return reason;
	}
	

	
}

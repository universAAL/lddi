package events;

import java.util.EventObject;


public class EventIEEE extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6892620464208010594L;
	Event event;
	
	public EventIEEE (Object source, Event event){
		super(source);
		this.event=event;
	}
	
	public Event getEvent(){
		return event;
	}
	
	

}

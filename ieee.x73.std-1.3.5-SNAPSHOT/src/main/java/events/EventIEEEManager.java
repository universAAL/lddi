package events;

import java.util.ArrayList;
import java.util.Iterator;


public class EventIEEEManager {
	
	private Event event;
	private ArrayList<EventIEEEListener> listeners = new ArrayList<EventIEEEListener>();
	
	public synchronized void addEventListener(EventIEEEListener listener)
	{
		listeners.add(listener);
	}
	public synchronized void removeEventListener(EventIEEEListener listener)
	{
		listeners.remove(listener);
	}
	
	public synchronized void receiveEvent(Event e){
		event = e;
		fireEvent();
	}
	
	private synchronized void fireEvent(){
		EventIEEE eventieee = new EventIEEE (this, event);
		Iterator<EventIEEEListener> listen = listeners.iterator();
		while(listen.hasNext()){
			((EventIEEEListener)listen.next()).eventReceived(eventieee);
		}
	}
	
	

}

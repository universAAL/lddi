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

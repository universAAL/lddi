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


import java.util.Timer;
import java.util.TimerTask;

import events.Event;
import events.EventIEEEManager;
import events.EventIEEEType;

import utils.Hour;
import utils.Logging;
import x73.nomenclature.StatusCodes;

/**
 * This class implements the timeouts defined on the IEEE 11073-20601 standard
 * @author lgigante
 *
 */
public class Timeout{
	
	/* Configuring	 */
	public static final int TO_CONFIG = 10000; // wait 10 seconds for a configuration to check
	
	/* Association Release Request	 */
	public static final int TO_RELEASE = 3000; // after sending a Association Release request, wait 3 seconds for a association release response
	
	/* Operating  */ 
	
	/*MDS and PMStore Objects*/
	public static final int TO_CONFIRMED_ACTION = 3000; // wait 3 seconds for a Confirmed Action response
	public static final int TO_GET = 3000; 				// wait 3 seconds for a Get response
	/*MDS, Scanner and PMStore Objects*/
	public static final int TO_CONFIRMED_SET = 3000; 	// wait 3 seconds for a Confirmed Set response
	
	/*MDS Object*/
	public static final int TO_SP_MDS = 3000; 			// After sending Confirmed Action 
														// Confirmed Action (MDC_ACT_DATA_REQUEST, start, time-period, time=0), 
	/*PMStore Object*/
	public static final int TO_SP_PMS = 3000; 			// After sending Confirmed Action (MDC_ACT_SEG_TRIG_XFER), wait 3 sec for a Confirmed Action Report
	public static final int TO_CLR_PMS = 3000; 			// After sending Confirmed Action (MDC_ACT_SEG_CLR), wait 3 sec for a Confirmed Action Report


	private int time; // timeout 
	private Timer timer;
	private String type;
	protected  EventIEEEManager eventmanager;
	TimeoutTask totask;
	
	public Timeout(EventIEEEManager evtmanager)
	{
		this.eventmanager = evtmanager;
	}
	
	public Timeout (int time, String reason, EventIEEEManager evtmanager){
		this.time = time;
		this.type = reason;
		this.eventmanager = evtmanager;
		this.timer = new Timer();
		
		this.timer.schedule(new TimeoutTask(), time);
	}
	
	public void  waitforConfig(){
		time = TO_CONFIG;
		type = "Configuration Waiting";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	public void  waitforReleaseResponse(){
		time = TO_RELEASE;
		type = "Release Response";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	public void waitforConfirmedAction(){
		time = TO_CONFIRMED_ACTION;
		type = "Confirmed Action";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	public void  waitforConfirmedSet(){
		time = TO_CONFIRMED_SET;
		type = "Confirmed Set";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	public void  waitforGetResponse(){
		time = TO_GET;
		type = "Get Response";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	public void  waitforMDS(){
				time = TO_SP_MDS;
				type = "MDS Request";
				timer = new Timer();
				totask = new TimeoutTask();
				timer.schedule(totask, time);
	}
	public void waitforPMStore(){
	
		time = TO_SP_PMS;
		type = "PMStore Request";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);

	}
	public void waitforClearPMStore(){
		
		time = TO_CLR_PMS;
		type = "PMStore Clear";
		timer = new Timer();
		totask = new TimeoutTask();
		timer.schedule(totask, time);
	}
	
	private String getTypeofTimeout(){
		return type;
	}
	
	
	public boolean cancel(){
		timer.purge();
		timer.cancel();
		
		return totask.cancel();
	}
	
	
	class TimeoutTask  extends TimerTask{
		@Override
		public void run() {
			
			Logging.logWarning("TIMEOUT EXPIRED: "+getTypeofTimeout());
			
			if (getTypeofTimeout().equals("Configuration Waiting"))
			{
				Event event = new Event(EventIEEEType.TIMEOUT_EVENT);
				event.setReason(StatusCodes.ABORT_REASON_RESPONSE_TIMEOUT);
				eventmanager.receiveEvent(event);
			}else{
				Event event = new Event(EventIEEEType.TIMEOUT_EVENT);
				event.setReason(StatusCodes.ABORT_REASON_RESPONSE_TIMEOUT);
				eventmanager.receiveEvent(event);
			}
			timer.cancel();
			System.out.println(Hour.getActualTime());
		}
		
	}
	
	// public boolean cancel() method inherited from TimerTask.
	/*
	 * true if this task is scheduled for one-time execution and has not yet run, 
	 * or this task is scheduled for repeated execution. Returns false if the task 
	 * was scheduled for one-time execution and has already run, or if the task was
	 *  never scheduled, or if the task was already cancelled. 
	 *  (Loosely speaking, this method returns true if it prevents one or more scheduled executions from taking place.)
	 */
}

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
package org.universAAL.lddi.zwave.exporter.Server;

import java.util.ArrayList;

import org.universAAL.lddi.zwave.exporter.MotionContact.MotionContactSensorPublisher;

public class MotionDecoder implements ISocketSeverProtocolDecoder {
	
	//private String message = "";
	private StringBuilder message = new StringBuilder();
	private MotionContactSensorPublisher motionPublisher;
	
	public MotionDecoder (MotionContactSensorPublisher mp){
		motionPublisher = mp;
	}
	
	public void decode(byte[] bBuffer) {
		// TODO Auto-generated method stub
		System.out.print("COLLECTING MESSAGE\n");
		for (final byte b : bBuffer) {
            // FSM
			if((char)b =='\n'){
				String msg = message.toString();
				System.out.println( msg);
				message = new StringBuilder();
				motionPublisher.publishMotionDetection(msg);
			}else{
				message.append((char)b);	
			}
		}
	}

	public void setListener(ISocketServerProtocolDecoderListener listener) {
		// TODO Auto-generated method stub

	}

}

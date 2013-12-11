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

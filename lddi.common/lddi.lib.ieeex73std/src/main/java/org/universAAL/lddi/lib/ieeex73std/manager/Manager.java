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
package org.universAAL.lddi.lib.ieeex73std.manager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.universAAL.lddi.lib.ieeex73std.events.EventIEEEManager;
import org.universAAL.lddi.lib.ieeex73std.manager.apdu.APDUGenerator;
import org.universAAL.lddi.lib.ieeex73std.manager.apdu.APDUProcessor;
import org.universAAL.lddi.lib.ieeex73std.org.bn.CoderFactory;
import org.universAAL.lddi.lib.ieeex73std.org.bn.IDecoder;
import org.universAAL.lddi.lib.ieeex73std.org.bn.IEncoder;
import org.universAAL.lddi.lib.ieeex73std.utils.ASNUtils;
import org.universAAL.lddi.lib.ieeex73std.utils.Testing;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.StatusCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ApduType;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.fsm.StateMachine20601;

public class Manager<T> {

	public static final byte[] protocol_version = { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00 };
	public static final byte[] nomenclature_version = { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	public static final byte[] system_id = "TSB_Mana".getBytes();
	public static final byte[] system_type_manager = { (byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // manager
	public static final byte[] system_type_agent = { (byte) 0x00, (byte) 0x80, (byte) 0x00, (byte) 0x00 }; // manager
	public static final byte[] functional_units = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }; // always
																											// 0
	public static final byte[] encode_mder = { (byte) 0x80, (byte) 0x00 }; // MDER

	public static final byte[] datareqmodeflags = { (byte) 0, (byte) 0 }; // always
																			// 0

	public static final int MAX_APDU_SIZE = 64512;// MAX APDU SIZE FROM AGENT TO
													// MANAGER = 63 KB (63*1024)
	public static final int MAX_APDU_TOAGENT_SIZE = 8192; // MAX APDU SIZE FROM
															// MANAGER TO AGENT
															// = 8 KB (8*1024)

	String manufacturer;
	String model;
	String version;
	public StateMachine20601 statemachine;
	IDecoder decoder = null;
	IEncoder<T> encoder = null;
	EventIEEEManager eventmanager;
	APDUGenerator msg_gen;
	APDUProcessor rmp;
	AgentCache agentcache;

	public Manager(EventIEEEManager eventmanager) {

		manufacturer = "tsb";
		model = "manager_universAAL";
		version = "1";

		agentcache = new AgentCache();

		initCoderDecoder();

		if (decoder != null && encoder != null) {
			initMessageManagers();
		}

		/**
		 * TEST THINGS
		 */

		statemachine.transportActivate();
		System.out.println(statemachine.getStringChannelState());

		/*
		 * Testing
		 */

		// Testing test = new Testing(encoder, decoder);
		//
		// test.decode10407AARQ(rmp);
		// test.decode10407Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
		// test.decode10415AARQ(rmp);
		// test.decode10415Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
		// test.decode10417AARQ(rmp);
		// test.decode10417Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
		// test.decodeUnknownCfg(rmp);
		// test.decodeExtended10404CfgReport(rmp);
		// test.decodeExtended10404Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
		// test.decodeUnknownCfg(rmp);
		// test.decodeExtended10408CfgReport(rmp);
		// test.decodeExtended10408Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
		// test.decodeUnknownCfg(rmp);
		// test.decodeExtended10415CfgReport(rmp);
		// test.decodeExtended10415Measure(rmp);
		// test.decodeRLRQ(rmp);
		//
	}

	private void initCoderDecoder() {
		try {
			decoder = CoderFactory.getInstance().newDecoder("MDER");
			encoder = CoderFactory.getInstance().newEncoder("MDER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initMessageManagers() {
		eventmanager = new EventIEEEManager(); // for managing timeouts
		statemachine = new StateMachine20601(this, eventmanager);
		msg_gen = new APDUGenerator(encoder);
		rmp = new APDUProcessor(decoder, statemachine, msg_gen, agentcache);

	}

	public byte[] getAPDU(byte[] received_apdu_bytes) {
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;

		ApduType received_apdu;
		try {

			bais = new ByteArrayInputStream(received_apdu_bytes);
			received_apdu = decoder.decode(bais, ApduType.class);

			// //test Activate for normal procedure
			ApduType response = rmp.processAPDU(received_apdu);
			byte[] apdu_bytes_resp = null;
			if (response != null)
				baos = new ByteArrayOutputStream();
			encoder.encode((T) response, baos);
			apdu_bytes_resp = baos.toByteArray();

			// test: association reject.
			// byte[] apdu_bytes_resp= rejectAssociation();

			// test: do not ack the measurements
			// byte[] apdu_bytes_resp = null;
			// System.out.println(statemachine.getStringChannelState() +" "+
			// ASNUtils.asHex(received_apdu_bytes[0]));
			// if( (received_apdu_bytes[0]==(byte)231) &&
			// (statemachine.getChannelState()==StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING)){
			// // EL ERROR ESTA AQUI
			//
			// apdu_bytes_resp = notACKMeasures(received_apdu);
			// }else{
			// System.out.println("PROCESSING");
			// ApduType response = rmp.processAPDU(received_apdu);
			// baos = new ByteArrayOutputStream();
			// encoder.encode((T) response, baos);
			// apdu_bytes_resp = baos.toByteArray();
			// ASNUtils.printAPDU(apdu_bytes_resp);
			// }
			// testing: control fsm transitions.
			System.out.println(statemachine.getStringTransportState());
			System.out.println(statemachine.getStringChannelState());

			if (apdu_bytes_resp != null)
				ASNUtils.printAPDU(apdu_bytes_resp);
			return apdu_bytes_resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return received_apdu_bytes;

	}

	private byte[] notACKMeasures(ApduType apdu) {

		ByteArrayInputStream bais;
		ByteArrayOutputStream baos;
		byte[] apdu_bytes_resp = null;

		if (statemachine.getChannelState() != StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING) {

			ApduType response;
			try {
				response = rmp.processAPDU(apdu);
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				apdu_bytes_resp = baos.toByteArray();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return apdu_bytes_resp;
	}

	private byte[] rejectAssociation() {
		ByteArrayInputStream bais;
		ByteArrayOutputStream baos;

		ApduType reject = msg_gen.AareApduGenerator(StatusCodes.REJECTED_UNAUTHORIZED);

		byte[] apdu_bytes_resp = null;
		baos = new ByteArrayOutputStream();
		try {
			encoder.encode((T) reject, baos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		apdu_bytes_resp = baos.toByteArray();
		return apdu_bytes_resp;
	}

	public StateMachine20601 getFSM() {
		return statemachine;
	}

	public APDUProcessor getMessageProcessor() {
		return rmp;
	}

	public APDUGenerator getMessageGenerator() {
		return msg_gen;
	}
}

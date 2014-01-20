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
 
package org.universAAL.lddi.lib.ieeex73std.manager.apdu;


import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;

import org.universAAL.lddi.lib.ieeex73std.manager.AgentCache;
import org.universAAL.lddi.lib.ieeex73std.manager.Manager;
import org.universAAL.lddi.lib.ieeex73std.org.bn.IDecoder;
import org.universAAL.lddi.lib.ieeex73std.org.bn.utils.BitArrayInputStream;
import org.universAAL.lddi.lib.ieeex73std.utils.ASNUtils;
import org.universAAL.lddi.lib.ieeex73std.utils.Logging;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.NomenclatureCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.nomenclature.StatusCodes;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.DeviceSpecialization;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.ExtendedConfiguration;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.p10404PulsiOximeter;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.p10407BloodPressure;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.p10408Thermometer;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.p10415WeighingScale;
import org.universAAL.lddi.lib.ieeex73std.x73.p104zz.p10417Glucometer;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AarqApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.AbrtApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ApduType;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReport;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ConfigReportRsp;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataProto;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataProtoList;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataReqId;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataReqModeCapab;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.EventReportArgumentSimple;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PhdAssociationInformation;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.PrstApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.RlreApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.RlrqApdu;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoFixed;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.ScanReportInfoVar;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataApdu.MessageChoiceType;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.fsm.StateMachine20601;




/**
 * This class processes the APDUs received from the Agent, and generate the proper response.
 * 
 * Also, it manages if the received APDU is acceptable in the current state of the state machine.
 * 
 * @author lgigante
 *
 */
public class APDUProcessor {

	IDecoder decoder = null;
	ByteArrayInputStream bais = null;
	StateMachine20601 statemachine;
	APDUGenerator msg_generator;
	AgentCache agentcache;		
	DeviceSpecialization agent;
	
	ApduType apdu = null; // apdu to return 

	public APDUProcessor(IDecoder decoder, StateMachine20601 fsm, APDUGenerator msg_gen, AgentCache acache){
		
		this.decoder = decoder;
		statemachine = fsm;
		msg_generator = msg_gen;
		agentcache = acache; 
	}
	
	
	
	public ApduType processAPDU (ApduType apdu) throws Exception
	{
		Logging.log("New APDU Received!");
		// Reception of an Association request
		if (apdu.isAarqSelected()){
			AarqApdu aarq = apdu.getAarq();
			return processAarq(aarq);
		}
		// Reception of an Asociation Response (won't get any of this. We are the manager and we are the only ones sending them)
		else if (apdu.isAareSelected()){
			statemachine.transitiontoUnassociated();
			throw new Exception ("Association Response received... and we shouldn't be receiving them."); // just in case
		}
		
		// Reception of a Presentation APDU (here is included the DataApdu)
		else if(apdu.isPrstSelected()){
			PrstApdu prst = apdu.getPrst();
			return processPrst(prst);
		}
		
		// Reception of a Release Request 
		else if(apdu.isRlrqSelected()){
			
			RlrqApdu rlrq = apdu.getRlrq();
			return processRlrq(rlrq);
		}
		
		//Reception of a Release Response
		else if(apdu.isRlreSelected()){
			RlreApdu rlre = apdu.getRlre();
			return processRlre(rlre);
		}
		
		// Reception of an Abort Resquest
		else if(apdu.isAbrtSelected()){
			AbrtApdu abrt = apdu.getAbrt();
			return processAbrt(abrt);
		}
		else{
			throw new Exception ("This received APDU is not from IEEE 11073"); // just in case
		}
		
		
	}

/*
 * ASSOCIATION REQUEST APDU PROCESSING
 */	
	/**
	 * Processing of an Association Request APDU.
	 * 
	 * Request is analised in this order:
	 * 1- The only state we can accept Association Request APDUs is when the Manager is in Unassociated state. Otherwise, we abort the current association and return to Unassociated.
	 * 2- If the Agent can use the IEEE 11073-20601 protocol it is a candidate to associate. Otherwise, the association is rejected
	 * and the Manager transitions to a Unassociated state.
	 * 3- Check if the PhdAssociationInformation given by the Agent is compatible with the Manager. Otherwise, the association is rejected.
	 * 		3.1- Protocol Version (1)
	 * 		3.2- Encoding Rules (MDER capable)
	 * 		3.3- Nomenclature Version (1)
	 * 		3.4- Functional Units (normal or with test capabilities, which won't be used)
	 * 4- The Agent has to use a standard configuration. In this case, Weighing scale and Blood Pressure Monitor are supported. Otherwise, 
	 * the association is rejected and the Manager transitions to a Unassociated state.
	 * 5- If the Agent is trying to establish its first connection, it will be cached. In any case, since we are using standard configurations,
	 * we will be accepting connections with KNOWN_CONFIG in the Association Response ADPU.
	 * 6- If all the previous is correct, the Manager send a AareAPDU accepting the association and both transition to Operating State.
	 * 
	 * 
	 * NOTE: since in this version we are only accepting Standard Configurations, there is no chance to get into the Waiting for and Checking Config states.
	 * @throws Exception 
	 */
	private ApduType processAarq(AarqApdu aarq) throws Exception {
		
		
			
		Logging.logAARQProcessing("Starting...");
		if(!checkStateforAarq()){
			Logging.logAARQProcessingError("AARQ received in bad time. The status is: " +statemachine.getStringChannelState());
			apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
			statemachine.transitiontoUnassociated();
			
		}else{
			// first of all, change the state to Associating
			statemachine.transitiontoAssociating();
			
			DataProtoList dpl =  aarq.getData_proto_list();
			DataProto dp;
			Iterator<DataProto> i = dpl.getValue().iterator();
			boolean found20601 = false;
			
			while (i.hasNext()){
				dp = i.next();
				
				//we look the IEEE 11073-20601 protocol. Otherwise, we reject association.
				if(dp.getData_proto_id().getValue().getValue() == StatusCodes.DATA_PROTO_ID_20601){
					Logging.logAARQProcessing("Agent capable of Protocol IEEE 11073-20601 found");
					found20601 = true;
					try {
						byte[] proto_info = dp.getData_proto_info();
						bais = new ByteArrayInputStream(proto_info);
						PhdAssociationInformation phd = decoder.decode(bais, PhdAssociationInformation.class);

						// is the PhdAssociationInformation valid for operating?
						if (isPhdValid(phd)){
							//process PHDAssociationInformation from the agent

							// get the configuration id in order to know if it is a recognizable standard device, or uses an extended configuration.
							Integer cfg_id = phd.getDev_config_id().getValue().getValue();
							Logging.logAARQProcessing("Device Configuration of the Agent: "+cfg_id);
							
							if (cfg_id >=400 && cfg_id <=499){							//it's a standard pulsioximeter
								agent = new p10404PulsiOximeter(decoder);
								Logging.logAARQProcessing("AARQ SUCCESSFUL");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED);
								statemachine.transitiontoOperating();
								break;
							
							}else if (cfg_id >=700 && cfg_id <=799){							//it's a standard blood pressure monitor
								agent = new p10407BloodPressure(decoder);
								Logging.logAARQProcessing("AARQ SUCCESSFUL");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED);
								statemachine.transitiontoOperating();
								break;
							}else if (cfg_id >=800 && cfg_id <=899){							//it's a Thermometer
								agent = new p10408Thermometer(decoder);
								Logging.logAARQProcessing("AARQ SUCCESSFUL");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED);
								statemachine.transitiontoOperating();
								break;
								
							}else if(cfg_id >=1500 && cfg_id <=1599)	{					// it's a weighing scale
								
								agent = new p10415WeighingScale(decoder);
								Logging.logAARQProcessing("AARQ SUCCESSFUL");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED);
								statemachine.transitiontoOperating();
								break;
								
								
							}else if (cfg_id >=1700 && cfg_id <=1799){							//it's a Glucose meter
								agent = new p10417Glucometer(decoder);
								Logging.logAARQProcessing("AARQ SUCCESSFUL");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED);
								statemachine.transitiontoOperating();
								break;
								
								/*
								 * The following devices does not have any standard configurations. Always use extended:
								 * - Cardiovascular fitness and Activity Monitor
								 * - Strengh fitness equipment
								 * - Activity Hub
								 */
								
							}else{
								// the agent is not standard, but uses IEEE 11073-20601. Demand its configuration.
								Logging.logAARQProcessing("Device using an EXTENDED configuration");
								apdu = msg_generator.AareApduGenerator(StatusCodes.ACCEPTED_UNKNOWN_CONFIG);
								statemachine.transitiontoWaitingConfig();

							}
						}else{  // phd not valid
							apdu = msg_generator.AareApduGenerator(StatusCodes.REJECTED_NO_COMMON_PARAMETER);
							statemachine.transitiontoUnassociated();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// the agent doesn't support IEEE 11073-20601, so a communication cannot be established.
			if (!found20601){
				apdu = msg_generator.AareApduGenerator(StatusCodes.REJECTED_NO_COMMON_PROTOCOL);
				statemachine.transitiontoUnassociated();
			}
		}
		return apdu;
	}
	/*
	 * AUXILIAR METHODS FOR ASSOCIATION REQUEST APDU PROCESSING
	 */
	
	// check if the Aarq APDU is received while the manager is Unassociated
	private boolean checkStateforAarq() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else
		if (statemachine.getChannelState()!=StateMachine20601.CHANNELSTATE_UNASSOCIATED){
			return false;
		}else{
			return true;
		}
	}
	
	private boolean isPhdValid(PhdAssociationInformation phd) {
		
		Logging.logAARQProcessing("Checking PhdAssociationInformation...");
		
		// Check if the agent uses the same Protocol Version, Encoding Rules, Functional Units and so on.
		byte[] protocol_version = phd.getProtocol_version().getValue().getValue().getValue();
		Logging.logAARQProcessing("Protocol Version: "+ASNUtils.asHexwithspaces(protocol_version));
		byte[] fora_prot_version = new byte[]{ (byte)0xc0, (byte)0x00, (byte)0x00, (byte)0x00};
		if((!Arrays.equals(protocol_version, Manager.protocol_version))&&(!Arrays.equals(protocol_version, fora_prot_version)))
		{
			Logging.logAARQProcessingError("Processing AARQ: Protocol Version not valid");
			return false;
		}
		
		// we have to check if the most significant bit of the first byte is 1 (Agent supports MDER only (0x80) or MDER and others (ex: 0xA0)
		byte[] enc_rules = phd.getEncoding_rules().getValue().getValue().getValue();
		byte enc_byte = enc_rules[0];	
		Logging.logAARQProcessing("Encoding Rules: "+ASNUtils.asHexwithspaces(enc_rules));
		if(!isSet(enc_byte, 7)){ // is the most significant bit 1?
			Logging.logAARQProcessingError("Processing AARQ: the Agent does not support MDER encoding rules");
			return false;
		}
		
		byte[] nomenclature_version = phd.getNomenclature_version().getValue().getValue().getValue();
		Logging.logAARQProcessing("Nomenclature Version: "+ASNUtils.asHexwithspaces(nomenclature_version));
		if(!Arrays.equals(nomenclature_version, Manager.nomenclature_version)){
			Logging.logAARQProcessingError("Processing AARQ: different nomenclature version");
			return false;
		}
		
		
		// Aware of the Test Association (0x40 0x00 0x00 0x00). It will be valid
		byte[] functional_units = phd.getFunctional_units().getValue().getValue().getValue();
		Logging.logAARQProcessing("Functional Units: "+ASNUtils.asHexwithspaces(functional_units));
		byte[] test_assoc = new byte[]{ (byte)0x40, (byte)0x00, (byte)0x00, (byte)0x00};
		if(!(Arrays.equals(functional_units, Manager.functional_units)	|| Arrays.equals(functional_units, test_assoc))){
			Logging.logAARQProcessingError("Processing AARQ: failure with Functional Units");
			return false;
		}
		
		//System Type
		byte[] sys_type = phd.getSystem_type().getValue().getValue().getValue();
		
		Logging.logAARQProcessing("System Type: "+ASNUtils.asHexwithspaces(sys_type));
		if(!Arrays.equals(sys_type, Manager.system_type_agent))
		{
			Logging.logAARQProcessingError("Processing AARQ: the AARQ was not sent by an Agent");
			return false;
		}
		
		//Data Request modes
		//TODO check data request modes? in this version we will be only managing agent initiated measurements
		
		// if all was correct, we can continue
		Logging.logAARQProcessing("PHD correct! ");
		return true;
	}
	
	
	// check if a bit in position "pos" of a byte is set to 1
	private static boolean isSet(byte value, int pos){
		   return (value&(1<<pos))!=0;
	} 
	/*
	 * END OF AUXILIAR METHODS FOR ASSOCIATION REQUEST APDU PROCESSING
	 */
/*
 * END OF ASSOCIATION REQUEST APDU PROCESSING
 */	

/*
 * RELEASE REQUEST  APDU PROCESSING
 */	

	/**
	 * In the case of receiving an Release Request, we have to be in any state but Unassociated.
	 * 
	 * If we are unassociated, we send an Abort APDU to force the Agent to restart.
	 * If not, we normally accept the Release request, send a Release Response with Normal reason and transition to Unassociated.
	 * 
	 * 	NOTE: exception while we are in Diassociating state, in which the Manager wants to start a disassociation. We sent previously our Rlrq, 
	 * so we have to wait for the Agent to send the Rlre response to that APDU prior to this Rlrq reception. In any case, we send another Rlre to avoid
	 * the Rlre timeout event in the Agent, which will be generate an Abort APDU.
	 * 
	 * @param rlrq
	 * @throws Exception 
	 */
	private ApduType processRlrq(RlrqApdu rlrq) throws Exception {
		if(!checkStateforRlrq()){
			Logging.logRLRQProcessingError("RLRQ APDU received while Unassociated. Sending Abort.");
			apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
			return apdu;
		}else{

			if(statemachine.getChannelState() != StateMachine20601.CHANNELSTATE_DISASSOCIATING){
				Logging.logRLRQProcessing("RLRQ APDU Received. Sending ACK and transitioning to Unassociated.");
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.RlreApduGenerator(StatusCodes.RELEASE_RESPONSE_REASON_NORMAL);
				return apdu;
			}else{
				Logging.logRLRQProcessing("RLRQ APDU Received while Disasociating. Sending ACK but " +
						"I wait for a ACK from the Agent to a RLRQ I have previoulsy sent.");
				apdu = msg_generator.RlreApduGenerator(StatusCodes.RELEASE_RESPONSE_REASON_NORMAL);
				return apdu;
			}
		}
	}

	/*
	 * AUXILIAR METHODS FOR ASSOCIATION REQUEST APDU PROCESSING
	 */
	// check if the Rlrq APDU is received while the manager is not Unassociated
	private boolean checkStateforRlrq() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
			case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
				return false;
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
			case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
				return true;
			default:
				return false;
			}
		}
	}
	/*
	 * END OF AUXILIAR METHODS FOR ASSOCIATION REQUEST APDU PROCESSING
	 */
/*
 * END  RELEASE  REQUEST APDU PROCESSING
 */	
	
/*
 * RELEASE REQUEST  APDU PROCESSING
 */	
	/**
	 * Processing of a received Release Response APDU.
	 * 
	 * The functionality of the Manager depends of the state of the state machine:
	 * 
	 * - Unassociated: ignore it.
	 * - Waiting for Config: should not happen. Send Abort APDU.
	 * - Checking Config:  should not happen. Send Abort APDU.
	 * - Operating:  should not happen. Send Abort APDU.
	 * - Disassociating: Release process completed. Exit to unassociated. Do not send anything.
	 * 
	 * @param rlre
	 * @return 
	 * @throws Exception 
	 */
	
	private ApduType processRlre(RlreApdu rlre) throws Exception {
		Logging.logRLREProcessing("Starting...");
		if(!checkStateforRlre()){
			statemachine.transitiontoUnassociated();
			apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
			return apdu;
		}else{
			if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
				statemachine.transitiontoUnassociated();
			}
			return null; // the Manager is in Unassociated state. Ignore the Rlre
		}
	}
	
	/*
	 * AUXILIAR METHODS FOR RELEASE RESPONSE APDU PROCESSING
	 */
	// check if the Rlre APDU is received in a proper moment
	private boolean checkStateforRlre() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
				case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
				case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
					return true;
					//
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
					return false;
				default:
					return false;
			}
		}
	}
	/*
	 * END OF AUXILIAR METHODS FOR RELEASE RESPONSE APDU PROCESSING
	 */
/*
 * END OF RELEASE RESPONSE  APDU PROCESSING
 */	
	
/*
 * ABORT APDU PROCESSING
 */	
	
	/**
	 * Processing of a received Abort APDU.
	 * Since there is no other option, we have to accept it because something went wrong at the Agent side.
	 * 
	 * Manager must return to unassociated state no matter in what status is now.
	 * 
	 * 
	 * @param abrt
	 * @return 
	 */
	private ApduType processAbrt(AbrtApdu abrt) {
		Logging.logAbrtProcessing("Abrt APDU received. Nothing is sent back to the Agent. Transitioning to Unassociated...");
		statemachine.transitiontoUnassociated();
		return null;
	}

/*
 * END OF ABORT APDU PROCESSING
 */	

/*
 * PRESENTATION APDU PROCESSING
 */	
	
	
	/**
	 * Processing of Presentation APDUs. This messages contains all data communication. 
	 * - Reports of measurements
	 * - Error messages
	 * - Reject messages 
	 * - Get and Set method invocations and responses.
	 * 
	 * Prst APDUs only can be received while the manager and agent are associated (operating or waiting/checking config)
	 * 
	 * @param prst
	 * @return response APDU to the message received (if needed)
	 * @throws Exception
	 */
	private ApduType processPrst(PrstApdu prst) throws Exception {
		
		if(!checkStateforPrst()){
			Logging.logPrstProcessingError("Data APDU received while not operating. Sending abort and transitioning to Unassociated...");
			statemachine.transitiontoUnassociated();

			apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
			return apdu;
		}else{
			// decode Data APDU in Presentation APDU
			
			byte[] prst_data = prst.getValue();
			bais = new ByteArrayInputStream(prst_data);
			DataApdu dataapdu = decoder.decode(bais, DataApdu.class);
			return getResponseToDataApdu(dataapdu);
		}
	}
	
	
	/*
	 * START OF AUXILIAR METHODS FOR PRESENTATION APDU PROCESSING
	 */
	
	
	// See table E.3	
	// Check if the Prst APDU is received in a proper moment
	private boolean checkStateforPrst() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
			
			// the procedure depends on the type of Prst APDU received.
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
			case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
			case StateMachine20601.CHANNELSTATE_DISASSOCIATING:  // this will be an special case. See further methods
				return true;
			case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
				return false;
			default:
				return false;
			}
		}
	}
	
	// What kind of message did the manager receive from the Agent?
	private ApduType getResponseToDataApdu(DataApdu dataapdu) throws Exception {
		
		MessageChoiceType choice = dataapdu.getMessage();
		
		// error
		if(choice.isRoerSelected()){
			if(!checkRoerState()){
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO reception of Rorj while Associated. No response will be sent (we are receiving a response to a query the manager sent previously) 
				return null; 
			}
		}
		
		if(choice.isRoiv_cmip_actionSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;
				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;
					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_actionSelected
					return null; // modify
				
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}

			}
		}
		
		if(choice.isRoiv_cmip_confirmed_actionSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_confirmed_actionSelected
					return null; // modify
				
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}
			}
		}
			
		// Used by the agent for sending measurements
		if(choice.isRoiv_cmip_confirmed_event_reportSelected()){
			
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
							
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// Event report containing configuration from agent
					processConfiguration_Roiv_cmip_confirmed_event_report(dataapdu);
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					//The agent is sending 	measurements before a configuration is agreed to.
					// if not a config report, send roer (no-such object-instance)
					// if config report, then send abrt
					
					int event_type = choice.getRoiv_cmip_confirmed_event_report().getEvent_type().getValue().getValue();
					
					if(event_type == NomenclatureCodes.MDC_NOTI_CONFIG){
						
						statemachine.transitiontoUnassociated();
						apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
						return apdu;
					}else{
						apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
						return apdu;
					}
					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// Normal processing of messages. This is the normal operating state
					// TODO: normal processing of messages: only is compatible with Numeric objects
					return processMeasure_Roiv_cmip_confirmed_event_reportSelected(dataapdu);

				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}
			}
		}
		
		if(choice.isRoiv_cmip_confirmed_setSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_confirmed_setSelected
					return null; // modify

				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}
			}
		}
		
		if(choice.isRoiv_cmip_event_reportSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_event_reportSelected
					return null; // modify

				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}
			}
		}
		
		if(choice.isRoiv_cmip_getSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_getSelected
					return null; // modify

				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_DISASSOCIATING){
					// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
					// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
					return null;
				}
			}
		}
		
		if(choice.isRoiv_cmip_setSelected()){
			if (!checkRoivState()){
				// Manager unassociated. Transmit abrt
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// different procedures depending of the state 
				if(statemachine.getChannelState()  == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG){
					// not allowed
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_OBJECT_INSTANCE);
					return apdu;				
				}else if(statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG){
					
					//The agent only sends event report messages. This should never happen
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.NO_SUCH_ACTION);
					return apdu;					
				}else if (statemachine.getChannelState() == StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING){
					// TODO processing of Roiv_cmip_setSelected
					return null; // modify
				}
			}
			// in disassociating state: The agent sent an invoke message as the manager sent an rlrq. 
			// The manager has 	transitioned out of the Operating state and therefore will not provide any response.
			return null;
		}
		
		
		if(choice.isRorjSelected()){
			if(!checkRorjState()){
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO reception of Rorj while Associated. No response will be sent (we are receiving a response to a query the manager sent previously) 
				return null; 
			}
		}
		
		if(choice.isRors_cmip_confirmed_actionSelected()){
			if(!checkRorsState()){
				// The manager is unassociated or disassociating. 
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO reception of Rors while Associated. No response will be sent (we are receiving a response to a query the manager sent previously) 
				return null; 
			}
		}
		
		if(choice.isRors_cmip_confirmed_event_reportSelected()){
			if(!checkRorsState()){
				// The manager is unassociated or disassociating. 
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO reception of Rors while Associated.
				return null;
			}
		}
		
		if(choice.isRors_cmip_confirmed_setSelected()){
			if(!checkRorsState()){
				// The manager is unassociated or disassociating. 
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO reception of Rors while Associated.
				return null;
			}
		}
		
		if(choice.isRors_cmip_getSelected()){
			
			if(!checkRorsState()){
				// The manager is unassociated or disassociating. 
				statemachine.transitiontoUnassociated();
				apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
				return apdu;
			}else{
				// WAITING / CHECKING CONFIG : Manager may have sent a roiv-cmip-get(handle=0). ee 6.3.2.6.1
				// OPERATING: Normal processing of messages. This is the normal operating state.
				// No State transition in this case.
				// TODO full processing of Get Response message.
				agent.getTimeGapofDevice(choice.getRors_cmip_get());
//				return null;
			}
		}
		return null;
		
	}
		

	// check if Remote Invocation APDU is received in a proper moment
	private boolean checkRoivState() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
				case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
					return true;
				case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
					return false;
				default:
					return false;
			}
		}
	}
	
	// check if Remote Error APDU is received in a proper moment
	private boolean checkRoerState() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
					return true;
				case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
				case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
					return false;
				default:
					return false;
				}
		}
	}
	
	// check if Remote Reject APDU is received in a proper moment
	private boolean checkRorjState() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
					return true;
				case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
				case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
					return false;
				default:
					return false;
				}
		}
	}

	// check if Remote Remote Operation invocation Response APDU is received in a proper moment
	private boolean checkRorsState() throws Exception {
		if(!statemachine.getTransportState()){
			throw new Exception("Transport state is off");
		}else{
			switch (statemachine.getChannelState()) {
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_OPERATING:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_WAITINGFORCONFIG:
				case StateMachine20601.CHANNELSTATE_ASSOCIATED_CONFIGURING_CHECKINGCONFIG:
					return true;
				case StateMachine20601.CHANNELSTATE_DISASSOCIATING:
				case StateMachine20601.CHANNELSTATE_UNASSOCIATED:
					return false;
				default:
					return false;
				}
		}
	}


	// process of a measurement sent by the Agent
	private ApduType processMeasure_Roiv_cmip_confirmed_event_reportSelected(DataApdu dataapdu) {
		
		EventReportArgumentSimple eras = dataapdu.getMessage().getRoiv_cmip_confirmed_event_report();
		
		// Get the Handle to know what is being transmitted. 0 represents the MDS. Other values, represent a Scanner or PM-Store
		int handle = eras.getObj_handle().getValue().getValue().intValue();
		if (handle == 0){

			int event_type = eras.getEvent_type().getValue().getValue().intValue();
			
			switch (event_type) {
			
//			// a fixed measurement is received
//			MDS-Dynamic-Data-Update-Fixed:
//				This event provides dynamic data (typically measurements) from the agent for some or all of the metric
//				objects or the MDS object that the agent supports. Data are reported in the fixed format defined by the
//				Attribute-Value-Map attribute for reported metric objects or the MDS object (see 7.4.5 for details on
//				event report formats). The event is triggered by an MDS-Data-Request from the manager system (i.e., a
//				manager-initiated measurement data transmission), or it is sent as an unsolicited message by the agent
//				(i.e., an agent-initiated measurement data transmission). For agents that support manager-initiated
//				measurement data transmission, refer to 8.9.3.3.3 for information on controlling the activation and/or
//				period of the data transmission. For agents that do not support manager-initiated measurement data
//				transmission, refer to 8.9.3.3.2 for information on the limited control a manager can assert.
			case NomenclatureCodes.MDC_NOTI_SCAN_REPORT_FIXED:
				try {
					processScanReportFixed(eras);
					apdu = msg_generator.MeasureACKGenerator(dataapdu, event_type);
					return apdu;
				} catch (Exception e) {
					e.printStackTrace();
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.INVALID_OBJECT_INSTANCE);
					return apdu;
				}

//			// a variable measurement is received
//				MDS-Dynamic-Data-Update-Var:
//					This event provides dynamic data (typically measurements) from the agent for some or all of the
//					objects that the agent supports. Data for reported objects are reported using a generic attribute list
//					variable format (see 7.4.5 for details on event report formats). The event is triggered by an MDS-Data-
//					Request from the manager system, or it is sent as an unsolicited message by the agent. For agents that
//					support manager-initiated measurement data transmission, refer to 8.9.3.3.3 for information on
//					controlling the activation and/or period of the data transmission. For agents that do not support
//					manager-initiated measurement
			case NomenclatureCodes.MDC_NOTI_SCAN_REPORT_VAR:
				try {
					processScanReportVar(eras);
					apdu = msg_generator.MeasureACKGenerator(dataapdu, event_type);
					return apdu;
				} catch (Exception e) {
					e.printStackTrace();
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.INVALID_OBJECT_INSTANCE);
					return apdu;				}
				
//				MDS-Dynamic-Data-Update-MP-Fixed:
//					This is the same as MDS-Dynamic-Data-Update-Fixed, but allows inclusion of data from multiple
//					persons.
			case NomenclatureCodes.MDC_NOTI_SCAN_REPORT_MP_FIXED:
				try {
					processScanReportFixed(eras);
					apdu = msg_generator.MeasureACKGenerator(dataapdu, event_type);
					return apdu;
				} catch (Exception e) {
					e.printStackTrace();
					apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.INVALID_OBJECT_INSTANCE);
					return apdu;
				}

//				MDS-Dynamic-Data-Update-MP-Var:
//					This is the same as MDS-Dynamic-Data-Update-Var, but allows inclusion of data from multiple
//					persons.
			case NomenclatureCodes.MDC_NOTI_SCAN_REPORT_MP_VAR:
			// TODO variable measurement report with multi-person details
				break;

			/*
			 * The next methods are for Scanner objets: For a simple example, in pulsioximeters. See IEEE 11073-10404, section H.6
			 */				

//				Unbuf-Scan-Report-Fixed:
//					This event style is used whenever data values change and the fixed message format of each object is
//					used to report data that changed.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_FIXED:
			// TODO unbuffered fixed report when a Scanner object is used. 
				break; 
				
//				Unbuf-Scan-Report-Var:
//					This event style reports summary data about any objects and attributes that the scanner monitors. The
//					event is triggered whenever data values change and the variable message format (type/length/value) is
//					used when reporting data that changed.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_VAR:
				// TODO unbuffered variable report when a Scanner object is used. 
				break;
				
//				Unbuf-Scan-Report-Grouped:
//					This style is used when the scanner object is used to send the data in its most compact format. The
//					Handle-Attr-Val-Map attribute describes the objects and attributes that are included and the format of
//					the message.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_GROUPED:
			// TODO unbuffered grouped report when a Scanner object is used. 
				break;
			
//				Unbuf-Scan-Report-MP-Fixed:
//					This is the same as Unbuf-Scan-Report-Fixed, but allows inclusion of data from multiple persons.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_MP_FIXED:
			// TODO unbuffered multi-person fixed report when a Scanner object is used. 
				break;
				
//				Unbuf-Scan-Report-MP-Var:
//					This is the same as Unbuf-Scan-Report-Var, but allows inclusion of data from multiple persons.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_MP_VAR:
			// TODO unbuffered multi-person variable report when a Scanner object is used. 
				break;
				
//				Unbuf-Scan-Report-MP-Grouped:
//					This is the same as Unbuf-Scan-Report-Grouped, but allows inclusion of data from multiple persons.
			case NomenclatureCodes.MDC_NOTI_UNBUF_SCAN_REPORT_MP_GROUPED:
			// TODO unbuffered multi-person grouped report when a Scanner object is used. 
				break;
			
//				All of the event report styles listed in Table 18 are buffered equivalents to their unbuffered counterparts in
//				6.3.9.4.5. One difference is that the scanner buffers data over the reporting interval and sends a single
//				message at the end of the interval. A second difference is that the same objects and attributes are included
//				in each report regardless of whether their values have changed.
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_FIXED:
			// TODO buffered fixed report when a Periodic Scanner object is used. 
				break; 
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_VAR:
				// TODO buffered variable report when a Periodic Scanner object is used. 
				break;
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_GROUPED:
			// TODO buffered grouped report when a Periodic Scanner object is used. 
				break;
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_MP_FIXED:
			// TODO buffered multi-person fixed report when a Periodic Scanner object is used. 
				break; 
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_MP_VAR:
				// TODO buffered multi-person variable report when a Periodic Scanner object is used. 
				break;
			case NomenclatureCodes.MDC_NOTI_BUF_SCAN_REPORT_MP_GROUPED:
			// TODO buffered multi-person grouped report when a Periodic Scanner object is used. 
				break;
				
				
				/*
				 * This method is related with PM-Store storage of information.
				 */
			case NomenclatureCodes.MDC_NOTI_SEGMENT_DATA:
				// TODO This event sends data stored in the Fixed-Segment-Data of a PM-segment from the agent to the
				//			manager. The event is triggered by the manager by the Trig-Segment-Data-Xfer method. Once the data
				//			transfer is triggered, the agent sends Segment-Data-Event messages until the complete Fixed-Segment-
				//			Data is transferred or the transfer is aborted by the manager or agent. See Transfer PM-segment content
				//			in 8.9.3.4.2 for a full description.
			default:
				apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.INVALID_OBJECT_INSTANCE);
				return apdu;			
			}
			
		// if it is not an acceptable report, send error response
		}else{
			
			apdu = msg_generator.RoerGenerator(dataapdu.getInvoke_id(), StatusCodes.INVALID_OBJECT_INSTANCE);
			return apdu;
		}
		// if it is not a data report, send abort (it won't be executed)
		statemachine.transitiontoUnassociated();
		apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
		return apdu; // never reached;		
	}
	
	

	// process fixed measure
	private void processScanReportFixed(EventReportArgumentSimple eras) throws Exception {
		
		byte[] eras_data = eras.getEvent_info();
		bais = new ByteArrayInputStream(eras_data);
		ScanReportInfoFixed srif = decoder.decode(bais, ScanReportInfoFixed.class);
		
		DataReqId dri = srif.getData_req_id();
		int dri_value = dri.getValue().getValue();
		
		// it's an agent-initiated measure report.
		if (dri_value==StatusCodes.DATA_REQ_ID_AGENT_INITIATED){
			try{
			agent.MDS_Dynamic_Data_Update_Fixed(srif);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
	
	// process variable measurement
	private void processScanReportVar(EventReportArgumentSimple eras) throws Exception {
		
		byte[] eras_data = eras.getEvent_info();
		bais = new ByteArrayInputStream(eras_data);
		ScanReportInfoVar sriv = decoder.decode(bais, ScanReportInfoVar.class);
		
		DataReqId dri = sriv.getData_req_id();
		int dri_value = dri.getValue().getValue();
		
		// it's an agent-initiated measure report.
		if (dri_value==StatusCodes.DATA_REQ_ID_AGENT_INITIATED){
			try{
			agent.MDS_Dynamic_Data_Update_Var(sriv);
			}catch (Exception e){
				throw new Exception();
			}
		}
	}

	
	// process configuration report from the agent (usually, an extended one).
	private ApduType processConfiguration_Roiv_cmip_confirmed_event_report(DataApdu dataapdu) throws Exception {

		EventReportArgumentSimple eras = dataapdu.getMessage().getRoiv_cmip_confirmed_event_report();
		
		// Get the Handle to know what is being transmitted. 0 represents the MDS. Other values, represent a Scanner or PM-Store
		int handle = eras.getObj_handle().getValue().getValue().intValue();
		if (handle == 0){
			int event_type = eras.getEvent_type().getValue().getValue().intValue();

//		// a configuration is received
//			 MDS-Configuration-Event:
//			This event is sent by the agent during the configuring state of startup if the manager does not already
//			know the agent�s configuration from past associations. The event provides static information about the
//			supported measurement capabilities of the agent.
			if(event_type == NomenclatureCodes.MDC_NOTI_CONFIG){
				statemachine.transitiontoCheckingConfig();
				agent = new ExtendedConfiguration(decoder);
				
				byte[] eras_data = eras.getEvent_info();
				bais = new ByteArrayInputStream(eras_data);
				ConfigReport cfgreport = decoder.decode(bais, ConfigReport.class);
				
				try{
					ConfigReportRsp resp = agent.MDS_Configuration_Event(cfgreport);
					
					// everything went correct. accept and start to operate.
					if(resp.getConfig_result().getValue().getValue()==StatusCodes.ACCEPTED_CONFIG){
						statemachine.transitiontoOperating();

					// something in this configuration is not supported. Ask for another one.
					}else if (resp.getConfig_result().getValue().getValue() == StatusCodes.UNSUPPORTED_CONFIG){
						statemachine.transitiontoWaitingConfig();
					}

					apdu = msg_generator.extConfigResponsegenerator(dataapdu.getInvoke_id(), resp);
					return apdu;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		apdu = msg_generator.AbrtApduGenerator(StatusCodes.ABORT_REASON_UNDEFINED);
		return apdu; // never reached;
	}
	
	
	/*
	 * END OF AUXILIAR METHODS FOR PRESENTATION APDU PROCESSING
	 */

/*
 * END OF PRESENTATION APDU PROCESSING
 */		
	
/* 
 * GENERAL AUXILIAR METHODS
 */
	
}



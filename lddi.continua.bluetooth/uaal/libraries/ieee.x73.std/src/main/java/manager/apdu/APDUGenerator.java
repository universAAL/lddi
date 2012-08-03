/**
 * In this class are generated the messages from the Manager that will be sent to the Agent.
 * 
 * The vast majority of them are responses to a previous message received from the Agent.
 * 
 */

package manager.apdu;


import java.io.ByteArrayOutputStream;

import manager.Manager;

import org.bn.IEncoder;
import org.bn.types.BitString;

import x73.nomenclature.NomenclatureCodes;
import x73.nomenclature.StatusCodes;
import x73.p20601.AareApdu;
import x73.p20601.Abort_reason;
import x73.p20601.AbrtApdu;
import x73.p20601.ApduType;
import x73.p20601.AssociateResult;
import x73.p20601.AttributeList;
import x73.p20601.BITS_16;
import x73.p20601.BITS_32;
import x73.p20601.ConfigId;
import x73.p20601.ConfigReportRsp;
import x73.p20601.DataApdu;
import x73.p20601.DataApdu.MessageChoiceType;
import x73.p20601.DataProto;
import x73.p20601.DataProtoId;
import x73.p20601.DataReqModeCapab;
import x73.p20601.DataReqModeFlags;
import x73.p20601.EncodingRules;
import x73.p20601.ErrorResult;
import x73.p20601.EventReportResultSimple;
import x73.p20601.FunctionalUnits;
import x73.p20601.HANDLE;
import x73.p20601.INT_U16;
import x73.p20601.INT_U32;
import x73.p20601.INT_U8;
import x73.p20601.InvokeIDType;
import x73.p20601.NomenclatureVersion;
import x73.p20601.OID_Type;
import x73.p20601.PhdAssociationInformation;
import x73.p20601.ProtocolVersion;
import x73.p20601.PrstApdu;
import x73.p20601.RejectResult;
import x73.p20601.RelativeTime;
import x73.p20601.ReleaseRequestReason;
import x73.p20601.ReleaseResponseReason;
import x73.p20601.RlreApdu;
import x73.p20601.RlrqApdu;
import x73.p20601.RoerErrorValue;
import x73.p20601.RorjProblem;
import x73.p20601.SystemType;


public class APDUGenerator<T> {

	IEncoder<T> encoder = null;
	ByteArrayOutputStream baos = null;
	
	public APDUGenerator(IEncoder<T> encoder) {
		this.encoder = encoder;
	}
	
	
	// Association request. Not implemented because Managers don't send this kind of APDU.
	public ApduType AarqApduGenerator() {
		return null;
	}	
	
	// Association response APDU
	public ApduType AareApduGenerator(int reason)
	{
		byte [] data={};
		
		ApduType apdu = new ApduType();
		AareApdu aare = new AareApdu();
		
		//if the association is accepted
		if(reason == StatusCodes.ACCEPTED || reason == StatusCodes.ACCEPTED_UNKNOWN_CONFIG){
			
			// data protocol to use
			DataProto dataprotocol = new DataProto();
	
			// Generate the Phd Association Information to inform the Agent what parameters are going to be used in this connection 
			PhdAssociationInformation phd = new PhdAssociationInformation();
			
			//protocol version
			ProtocolVersion proto_vers = new ProtocolVersion(new BITS_32(new BitString(Manager.protocol_version)));
			phd.setProtocol_version(proto_vers);
			
			//Encoding rules
			EncodingRules encoding_rules = new EncodingRules(new BITS_16(new BitString(Manager.encode_mder)));
			phd.setEncoding_rules(encoding_rules);
			
			// NomenclatureVersion
			NomenclatureVersion nomen_version = new NomenclatureVersion(new BITS_32(new BitString(Manager.nomenclature_version)));
			phd.setNomenclature_version(nomen_version);
			
			//Functional Units
			FunctionalUnits func_units = new FunctionalUnits(new BITS_32(new BitString(Manager.functional_units)));
			phd.setFunctional_units(func_units);
			
			// System type
			SystemType sys_type = new SystemType(new BITS_32(new BitString(Manager.system_type_manager)));
			phd.setSystem_type(sys_type);
			
			//System ID
			phd.setSystem_id(Manager.system_id);
			
			//Set Data Req mode capabilities (always zero in AareAPDUs)
			DataReqModeCapab datareqmodecapabilities = new DataReqModeCapab();
			DataReqModeFlags datareqmodeflags = new DataReqModeFlags();
			datareqmodeflags.setValue(new BITS_16(new BitString(Manager.datareqmodeflags)));
			datareqmodecapabilities.setData_req_mode_flags(datareqmodeflags);
			datareqmodecapabilities.setData_req_init_agent_count(new INT_U8(0));
			datareqmodecapabilities.setData_req_init_manager_count(new INT_U8(0));
			phd.setData_req_mode_capab(datareqmodecapabilities);
			
			//Config id (always 0)
			phd.setDev_config_id(new ConfigId(new INT_U16(0)));
			
			// Option List
			AttributeList attr_list = new AttributeList();
			attr_list.initValue();		
			phd.setOption_list(attr_list);
			
			
			// Encode the Phd Information of the Manager.
			baos  = new ByteArrayOutputStream();
			try {
				encoder.encode((T) phd, baos);
				data = baos.toByteArray();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			// set the IEEE 11073-20601 as data protocol
			dataprotocol.setData_proto_id(new DataProtoId(new INT_U16(StatusCodes.DATA_PROTO_ID_20601)));
			dataprotocol.setData_proto_info(data);
			aare.setSelected_data_proto(dataprotocol);
			
		//if the association is rejected
		}else{
			DataProto dataprotocol = new DataProto();
			dataprotocol.setData_proto_id(new DataProtoId(new INT_U16(0))); //rejected
			dataprotocol.setData_proto_info(new byte[]{(byte)0x00,(byte)0x00});
			aare.setSelected_data_proto(dataprotocol);
		}
		aare.setResult(new AssociateResult(new INT_U16(reason)));
		// encapsule the Aare into the Apdu.
		apdu.selectAare(aare);
		
		return apdu;
	}
	
	/*
	 * release request
	 */
	// The manager will only send the reason StatusCodes.REL_REQ_RE_NORMAL. No-more-configurations and configuration-changed reasons come from Agent.
	public ApduType createRlrqApduNormal()
	{
		ApduType apdu = new ApduType();
		RlrqApdu rlrq = new RlrqApdu();
		rlrq.setReason(new ReleaseRequestReason(new INT_U16(StatusCodes.REL_REQ_RE_NORMAL)));
		apdu.selectRlrq(rlrq);
		return apdu;
	}
	

	
	/*
	 * release response 
	 */
	
	// will get the reason from the request received from the Agent
	public ApduType RlreApduGenerator (int reason)
	{
		ApduType apdu = new ApduType();
		RlreApdu rlre = new RlreApdu();
		rlre.setReason(new ReleaseResponseReason(new INT_U16( reason)));
		apdu.selectRlre(rlre);
		return apdu;
	}
	
	
	/*
	 *  abort APDU
	 */
	public ApduType AbrtApduGenerator(int reason)
	{
		ApduType apdu = new ApduType();
		AbrtApdu abrt = new AbrtApdu();
		abrt.setReason(new Abort_reason(new INT_U16(reason)));
		apdu.selectAbrt(abrt);
		return apdu;
	}
		
	
	
	/*
	 * Prst APDUs: they include Data APDUs
	 */
	/**
	 * Generation of an acknowledgement for a measurement received from an Agent.
	 * 
	 * @param datareceived measurement received from the Agent
	 * @param typeofreport integer with the code of the report received. 
	 * @return
	 */
	public ApduType MeasureACKGenerator(DataApdu datareceived, int typeofreport){
		
		ApduType apdu = new ApduType();
		PrstApdu prst = new PrstApdu();
		
		// generate and encode the response data
		DataApdu data = new DataApdu();
		// copy the invoke id (sequence number)
		data.setInvoke_id(datareceived.getInvoke_id());
		// set the message information to send to the Agent
		MessageChoiceType mct = new MessageChoiceType();
		EventReportResultSimple eventreportresult = new EventReportResultSimple();
		// Handle = 0
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(0));
		eventreportresult.setObj_handle(handle);
		// Relative time = 0;
		RelativeTime relativetime = new RelativeTime();
		relativetime.setValue(new INT_U32(0L));
		eventreportresult.setCurrentTime(relativetime);
		// The type of report will be the same of the Apdu received from the Agent.
		OID_Type scanreportfixed = new OID_Type();
		scanreportfixed.setValue(new INT_U16(typeofreport));
		eventreportresult.setEvent_type(scanreportfixed);
		
		// save the information 
		byte[] replyinfo = new byte[]{};
		eventreportresult.setEvent_reply_info(replyinfo);
		
		mct.selectRors_cmip_confirmed_event_report(eventreportresult);
		data.setMessage(mct);
		
		byte[] data_encoded = null;
		
		baos  = new ByteArrayOutputStream();
		try {
			encoder.encode((T) data, baos);
			data_encoded = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		prst.setValue(data_encoded);
		apdu.selectPrst(prst);
		return apdu;
	}
	
	
	/**
	 * Something was wrong processing the Apdu from the Agent, and we will send an Error.
	 * @param invokeid sequence number
	 * @param err_code identificator of the error
	 * @return
	 */
	public ApduType RoerGenerator(InvokeIDType invokeid, int err_code){
		
		ApduType apdu = new ApduType();
		PrstApdu prst = new PrstApdu();
		
		// generate the data response
		DataApdu data = new DataApdu();
		// copy the invoke id (sequence number)
		data.setInvoke_id(invokeid);
		// create the message info object
		MessageChoiceType mct = new MessageChoiceType();
		// we will send an error
		ErrorResult roer = new ErrorResult();
		// set the error code
		RoerErrorValue roer_value = new RoerErrorValue(new INT_U16(err_code));
		
		roer.setError_value(roer_value);
		roer.setParameter(new byte[]{}); // no additional information will be sent
		mct.selectRoer(roer);
		data.setMessage(mct);
		byte[] data_encoded = null;
		
		baos  = new ByteArrayOutputStream();
		try {
			encoder.encode((T) data, baos);
			data_encoded = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		prst.setValue(data_encoded);
		apdu.selectPrst(prst);
		return apdu;
		
	}

	
	/**
	 * Some APDU is not acceptable and we send a reject message
	 * 
	 * @param invokeid sequence number
	 * @param err_code identificator of the error
	 * @return
	 */
	public ApduType RorjGenerator(InvokeIDType invokeid, int err_code){
		
		ApduType apdu = new ApduType();
		PrstApdu prst = new PrstApdu();
		
		// generate the data response
		DataApdu data = new DataApdu();
		// copy the invoke id (sequence number)
		data.setInvoke_id(invokeid);
		// create the message info object
		MessageChoiceType mct = new MessageChoiceType();
		// this is going to be a reject response
		RejectResult rorj = new RejectResult();
		RorjProblem rorj_value = new RorjProblem();
		rorj_value.setValue(new INT_U16(err_code));
		rorj.setProblem(rorj_value);

		mct.selectRorj(rorj);
		data.setMessage(mct);
		byte[] data_encoded = null;
		baos  = new ByteArrayOutputStream();
		try {
			encoder.encode((T) data, baos);
			data_encoded = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		prst.setValue(data_encoded);
		apdu.selectPrst(prst);
		
		return apdu;
	}

	
	
	/**
	 * Generate the response to a configuration report from the Agent. Usually, this will be an extended configuration
	 * @param invoke_id sequence number of DataApdu in which the configuration report is embedded
	 * @param resp object with the response (if it is valid and the manager can work with it, or not)
	 * @return the Apdu response
	 */	
	public ApduType extConfigResponsegenerator(InvokeIDType invoke_id, ConfigReportRsp resp){
		ApduType apdu = new ApduType();
		PrstApdu prst = new PrstApdu();
		
		// generate the data response
		DataApdu data = new DataApdu();
		// copy the invoke id (sequence number)
		data.setInvoke_id(invoke_id);
		// create the message info object
		MessageChoiceType mct = new MessageChoiceType();
		// create the event object that contains the result info 
		EventReportResultSimple errs = new EventReportResultSimple();
		
		// Handle = 0;
		HANDLE handle = new HANDLE();
		handle.setValue(new INT_U16(0));
		errs.setObj_handle(handle);
		
		// type of report (configuration_notification)
		OID_Type scanreportfixed = new OID_Type();
		scanreportfixed.setValue(new INT_U16(NomenclatureCodes.MDC_NOTI_CONFIG));
		errs.setEvent_type(scanreportfixed);

		// Relative time = 0
		RelativeTime rt = new RelativeTime();
		rt.setValue(new INT_U32(0L));
		errs.setCurrentTime(rt);
		
		// encode the object ConfigReportRsp
		byte[] info = null;
		
		baos  = new ByteArrayOutputStream();
		try {
			encoder.encode((T) resp, baos);
			info = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		errs.setEvent_reply_info(info);
		mct.selectRors_cmip_confirmed_event_report(errs);
		data.setMessage(mct);
		byte[] data_encoded = null;
		
		baos  = new ByteArrayOutputStream();
		try {
			encoder.encode((T) data, baos);
			data_encoded = baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		prst.setValue(data_encoded);
		apdu.selectPrst(prst);
		
		return apdu;
	}
	
	
}

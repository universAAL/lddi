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
package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.bn.metadata.ASN1TypeMetadata;

import events.Event;
import events.EventIEEEManager;
import events.EventIEEEType;

import manager.Manager;
import manager.apdu.APDUProcessor;
import testchannel20601.AareAPDUtest;
import testchannel20601.Aarq10407APDUtest;
import testchannel20601.Aarq10415APDUtest;
import testchannel20601.Aarq10417APDUtest;
import testchannel20601.AbrtAPDUtest;
import testchannel20601.ExtendedCfg10404;
import testchannel20601.ExtendedCfg10408;
import testchannel20601.ExtendedCfg10415;
import testchannel20601.ExtendedMeasure10404APDUtest;
import testchannel20601.ExtendedMeasure10408APDUtest;
import testchannel20601.ExtendedMeasure10415APDUtest;
import testchannel20601.Measure10407PrstAPDUtest;
import testchannel20601.Measure10415PrstAPDUtest;
import testchannel20601.Measure10417PrstAPDUtest;
import testchannel20601.RealMeasure10415PrstAPDUtest;
import testchannel20601.RlrqAPDUtest;
import testchannel20601.UnknownCfg;
import x73.p20601.ApduType;

public class Testing<T> {
	
	IDecoder decoder = null;
	IEncoder<T> encoder = null;
	
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	
	
	public Testing (IEncoder encoder, IDecoder decoder){
		this.encoder = encoder;
		this.decoder = decoder;
	}
	

	public void decodeAARE(APDUProcessor rmp) {
		AareAPDUtest aare = new AareAPDUtest(false);
		try {
			
			byte[] toDecode = aare.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}


	
	public void decode10415AARQ(APDUProcessor rmp) {
		Aarq10415APDUtest aarq = new Aarq10415APDUtest();
		try {
			
			byte[] toDecode = aarq.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	
	
	public void decodeUnknownCfg(APDUProcessor rmp) {
		UnknownCfg aarq = new UnknownCfg();
		try {
			
			byte[] toDecode = aarq.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	public void decodeExtended10415CfgReport(APDUProcessor rmp) {
		ExtendedCfg10415 cfg = new ExtendedCfg10415();
		try {
			
			byte[] toDecode = cfg.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	
	public void decodeExtended10404CfgReport(APDUProcessor rmp) {
		ExtendedCfg10404 cfg = new ExtendedCfg10404();
		try {
			
			byte[] toDecode = cfg.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	
	
	
	public void decodeExtended10408CfgReport(APDUProcessor rmp) {
		ExtendedCfg10408 cfg = new ExtendedCfg10408();
		try {
			
			byte[] toDecode = cfg.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	
	public void decodeExtended10415Measure(APDUProcessor rmp) {
		ExtendedMeasure10415APDUtest extmeasure = new ExtendedMeasure10415APDUtest();
		try {
			
			byte[] toDecode = extmeasure.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	public void decodeExtended10404Measure(APDUProcessor rmp) {
		ExtendedMeasure10404APDUtest extmeasure = new ExtendedMeasure10404APDUtest();
		try {
			
			byte[] toDecode = extmeasure.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	public void decodeExtended10408Measure(APDUProcessor rmp) {
		ExtendedMeasure10408APDUtest extmeasure = new ExtendedMeasure10408APDUtest();
		try {
			
			byte[] toDecode = extmeasure.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	public void decode10417AARQ(APDUProcessor rmp) {
		Aarq10417APDUtest aarq = new Aarq10417APDUtest();
		try {
			
			byte[] toDecode = aarq.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}
	
	public void decode10407AARQ(APDUProcessor rmp) {
		Aarq10407APDUtest aarq = new Aarq10407APDUtest();
		try {
			
			byte[] toDecode = aarq.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
		
	}


	public void decodeRLRQ(APDUProcessor rmp) {
		RlrqAPDUtest rlrq = new RlrqAPDUtest();
		try {
			
			byte[] toDecode = rlrq.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}	
	}

	public void decodeABRT(APDUProcessor rmp) {
		AbrtAPDUtest abrt = new AbrtAPDUtest();
		try {
			
			byte[] toDecode = abrt.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}		
	}
	
	public void decode10415Measure(APDUProcessor rmp)
	{
		Measure10415PrstAPDUtest masurement = new Measure10415PrstAPDUtest();

		try {
			
			byte[] toDecode = masurement.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	
	public void decodeRealWeightMeasure(APDUProcessor rmp)
	{
		RealMeasure10415PrstAPDUtest masurement = new RealMeasure10415PrstAPDUtest();

		try {
			
			byte[] toDecode = masurement.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	
	
	public void decode10407Measure(APDUProcessor rmp)
	{
		Measure10407PrstAPDUtest masurement = new Measure10407PrstAPDUtest();

		try {
			
			byte[] toDecode = masurement.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	
	public void decode10417Measure(APDUProcessor rmp)
	{
		Measure10417PrstAPDUtest masurement = new Measure10417PrstAPDUtest();

		try {
			
			byte[] toDecode = masurement.getByteArray();
			
			bais = new ByteArrayInputStream(toDecode);
			ApduType apdu = decoder.decode(bais, ApduType.class);
			ApduType response = rmp.processAPDU(apdu);

			// simulate sending response 
			
			if(response != null){
				baos = new ByteArrayOutputStream();
				encoder.encode((T) response, baos);
				
				byte[] apdu_bytes = baos.toByteArray();
				Logging.logSend(ASNUtils.asHexwithspaces(apdu_bytes));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.logError(e.toString());
		}
	}
	

	 public void waiting (int n){
	    
	    long t0, t1;

	    t0 = System.currentTimeMillis();

	    System.out.println("Waiting for "+n+" seconds");
	    
	    do{
	      t1 = System.currentTimeMillis();
	    }
	    while ((t1 - t0) < (n * 1000));
	  }

}

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
package org.universAAL.lddi.lib.ieeex73std.x73.p20601;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.universAAL.lddi.lib.ieeex73std.org.bn.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.constraints.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "ScanReportInfoMPFixed", isSet = false )
    public class ScanReportInfoMPFixed implements IASN1PreparedElement {
            
        @ASN1Element ( name = "data-req-id", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private DataReqId data_req_id = null;
                
  
        @ASN1Element ( name = "scan-report-no", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U16 scan_report_no = null;
                
  
@ASN1SequenceOf( name = "scan-per-fixed", isSetOf = false ) 

    
        @ASN1Element ( name = "scan-per-fixed", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private java.util.Collection<ScanReportPerFixed>  scan_per_fixed = null;
                
  
        
        public DataReqId getData_req_id () {
            return this.data_req_id;
        }

        

        public void setData_req_id (DataReqId value) {
            this.data_req_id = value;
        }
        
  
        
        public INT_U16 getScan_report_no () {
            return this.scan_report_no;
        }

        

        public void setScan_report_no (INT_U16 value) {
            this.scan_report_no = value;
        }
        
  
        
        public java.util.Collection<ScanReportPerFixed>  getScan_per_fixed () {
            return this.scan_per_fixed;
        }

        

        public void setScan_per_fixed (java.util.Collection<ScanReportPerFixed>  value) {
            this.scan_per_fixed = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ScanReportInfoMPFixed.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
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
    @ASN1Sequence ( name = "SegmentDataEvent", isSet = false )
    public class SegmentDataEvent implements IASN1PreparedElement {
            
        @ASN1Element ( name = "segm-data-event-descr", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SegmDataEventDescr segm_data_event_descr = null;
                
  @ASN1OctetString( name = "" )
    
        @ASN1Element ( name = "segm-data-event-entries", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private byte[] segm_data_event_entries = null;
                
  
        
        public SegmDataEventDescr getSegm_data_event_descr () {
            return this.segm_data_event_descr;
        }

        

        public void setSegm_data_event_descr (SegmDataEventDescr value) {
            this.segm_data_event_descr = value;
        }
        
  
        
        public byte[] getSegm_data_event_entries () {
            return this.segm_data_event_entries;
        }

        

        public void setSegm_data_event_entries (byte[] value) {
            this.segm_data_event_entries = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmentDataEvent.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
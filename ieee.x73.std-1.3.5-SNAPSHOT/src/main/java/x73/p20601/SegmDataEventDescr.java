
package x73.p20601;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "SegmDataEventDescr", isSet = false )
    public class SegmDataEventDescr implements IASN1PreparedElement {
            
        @ASN1Element ( name = "segm-instance", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private InstNumber segm_instance = null;
                
  
        @ASN1Element ( name = "segm-evt-entry-index", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U32 segm_evt_entry_index = null;
                
  
        @ASN1Element ( name = "segm-evt-entry-count", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U32 segm_evt_entry_count = null;
                
  
        @ASN1Element ( name = "segm-evt-status", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SegmEvtStatus segm_evt_status = null;
                
  
        
        public InstNumber getSegm_instance () {
            return this.segm_instance;
        }

        

        public void setSegm_instance (InstNumber value) {
            this.segm_instance = value;
        }
        
  
        
        public INT_U32 getSegm_evt_entry_index () {
            return this.segm_evt_entry_index;
        }

        

        public void setSegm_evt_entry_index (INT_U32 value) {
            this.segm_evt_entry_index = value;
        }
        
  
        
        public INT_U32 getSegm_evt_entry_count () {
            return this.segm_evt_entry_count;
        }

        

        public void setSegm_evt_entry_count (INT_U32 value) {
            this.segm_evt_entry_count = value;
        }
        
  
        
        public SegmEvtStatus getSegm_evt_status () {
            return this.segm_evt_status;
        }

        

        public void setSegm_evt_status (SegmEvtStatus value) {
            this.segm_evt_status = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmDataEventDescr.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            

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
    @ASN1Sequence ( name = "MdsTimeInfo", isSet = false )
    public class MdsTimeInfo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "mds-time-cap-state", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MdsTimeCapState mds_time_cap_state = null;
                
  
        @ASN1Element ( name = "time-sync-protocol", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private TimeProtocolId time_sync_protocol = null;
                
  
        @ASN1Element ( name = "time-sync-accuracy", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RelativeTime time_sync_accuracy = null;
                
  
        @ASN1Element ( name = "time-resolution-abs-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U16 time_resolution_abs_time = null;
                
  
        @ASN1Element ( name = "time-resolution-rel-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U16 time_resolution_rel_time = null;
                
  
        @ASN1Element ( name = "time-resolution-high-res-time", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U32 time_resolution_high_res_time = null;
                
  
        
        public MdsTimeCapState getMds_time_cap_state () {
            return this.mds_time_cap_state;
        }

        

        public void setMds_time_cap_state (MdsTimeCapState value) {
            this.mds_time_cap_state = value;
        }
        
  
        
        public TimeProtocolId getTime_sync_protocol () {
            return this.time_sync_protocol;
        }

        

        public void setTime_sync_protocol (TimeProtocolId value) {
            this.time_sync_protocol = value;
        }
        
  
        
        public RelativeTime getTime_sync_accuracy () {
            return this.time_sync_accuracy;
        }

        

        public void setTime_sync_accuracy (RelativeTime value) {
            this.time_sync_accuracy = value;
        }
        
  
        
        public INT_U16 getTime_resolution_abs_time () {
            return this.time_resolution_abs_time;
        }

        

        public void setTime_resolution_abs_time (INT_U16 value) {
            this.time_resolution_abs_time = value;
        }
        
  
        
        public INT_U16 getTime_resolution_rel_time () {
            return this.time_resolution_rel_time;
        }

        

        public void setTime_resolution_rel_time (INT_U16 value) {
            this.time_resolution_rel_time = value;
        }
        
  
        
        public INT_U32 getTime_resolution_high_res_time () {
            return this.time_resolution_high_res_time;
        }

        

        public void setTime_resolution_high_res_time (INT_U32 value) {
            this.time_resolution_high_res_time = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MdsTimeInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
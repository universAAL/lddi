
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
    @ASN1Sequence ( name = "TrigSegmDataXferReq", isSet = false )
    public class TrigSegmDataXferReq implements IASN1PreparedElement {
            
        @ASN1Element ( name = "seg-inst-no", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private InstNumber seg_inst_no = null;
                
  
        
        public InstNumber getSeg_inst_no () {
            return this.seg_inst_no;
        }

        

        public void setSeg_inst_no (InstNumber value) {
            this.seg_inst_no = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(TrigSegmDataXferReq.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
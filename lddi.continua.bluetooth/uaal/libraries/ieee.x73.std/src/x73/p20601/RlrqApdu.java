
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
    @ASN1Sequence ( name = "RlrqApdu", isSet = false )
    public class RlrqApdu implements IASN1PreparedElement {
            
        @ASN1Element ( name = "reason", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ReleaseRequestReason reason = null;
                
  
        
        public ReleaseRequestReason getReason () {
            return this.reason;
        }

        

        public void setReason (ReleaseRequestReason value) {
            this.reason = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RlrqApdu.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
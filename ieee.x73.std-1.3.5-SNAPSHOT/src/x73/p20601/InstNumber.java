
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
    @ASN1BoxedType ( name = "InstNumber" )
    public class InstNumber implements IASN1PreparedElement {
                
        
        @ASN1Element ( name = "InstNumber", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
        private INT_U16  value;        

        
        
        public InstNumber () {
        }
        
        
        
        public void setValue(INT_U16 value) {
            this.value = value;
        }
        
        
        
        public INT_U16 getValue() {
            return this.value;
        }            
        

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(InstNumber.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
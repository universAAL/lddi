
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
    @ASN1BoxedType ( name = "DataReqResult" )
    public class DataReqResult implements IASN1PreparedElement {
        @ASN1Element ( name = "DataReqResult", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

            private INT_U16 value;
            
            public DataReqResult() {
            }

            public DataReqResult(INT_U16 value) {
                this.value = value;
            }
            
            public void setValue(INT_U16 value) {
                this.value = value;
            }
            
            public INT_U16 getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(DataReqResult.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            
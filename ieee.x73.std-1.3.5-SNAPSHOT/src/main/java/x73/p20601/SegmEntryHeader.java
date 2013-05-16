
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
    @ASN1BoxedType ( name = "SegmEntryHeader" )
    public class SegmEntryHeader implements IASN1PreparedElement {
    
        @ASN1Element ( name = "SegmEntryHeader", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

            private BITS_16 value = null;
            
            public SegmEntryHeader() {
            }

            public SegmEntryHeader(BITS_16 value) {
                this.value = value;
            }
            
            public void setValue(BITS_16 value) {
                this.value = value;
            }
            
            public BITS_16 getValue() {
                return this.value;
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmEntryHeader.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

    }
            
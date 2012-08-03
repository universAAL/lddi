
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
    @ASN1Sequence ( name = "AarqApdu", isSet = false )
    public class AarqApdu implements IASN1PreparedElement {
            
        @ASN1Element ( name = "assoc-version", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private AssociationVersion assoc_version = null;
                
  
        @ASN1Element ( name = "data-proto-list", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private DataProtoList data_proto_list = null;
                
  
        
        public AssociationVersion getAssoc_version () {
            return this.assoc_version;
        }

        

        public void setAssoc_version (AssociationVersion value) {
            this.assoc_version = value;
        }
        
  
        
        public DataProtoList getData_proto_list () {
            return this.data_proto_list;
        }

        

        public void setData_proto_list (DataProtoList value) {
            this.data_proto_list = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AarqApdu.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
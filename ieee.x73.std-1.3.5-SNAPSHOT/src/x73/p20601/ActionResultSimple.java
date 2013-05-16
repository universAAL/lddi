
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
    @ASN1Sequence ( name = "ActionResultSimple", isSet = false )
    public class ActionResultSimple implements IASN1PreparedElement {
            
        @ASN1Element ( name = "obj-handle", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private HANDLE obj_handle = null;
                
  
        @ASN1Element ( name = "action-type", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private OID_Type action_type = null;
                
  @ASN1Any( name = "" )
    
        @ASN1Element ( name = "action-info-args", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private byte[] action_info_args = null;
                
  
        
        public HANDLE getObj_handle () {
            return this.obj_handle;
        }

        

        public void setObj_handle (HANDLE value) {
            this.obj_handle = value;
        }
        
  
        
        public OID_Type getAction_type () {
            return this.action_type;
        }

        

        public void setAction_type (OID_Type value) {
            this.action_type = value;
        }
        
  
        
        public byte[] getAction_info_args () {
            return this.action_info_args;
        }

        

        public void setAction_info_args (byte[] value) {
            this.action_info_args = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ActionResultSimple.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
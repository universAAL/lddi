
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
    @ASN1Sequence ( name = "SystemModel", isSet = false )
    public class SystemModel implements IASN1PreparedElement {
            @ASN1OctetString( name = "" )
    
        @ASN1Element ( name = "manufacturer", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private byte[] manufacturer = null;
                
  @ASN1OctetString( name = "" )
    
        @ASN1Element ( name = "model-number", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private byte[] model_number = null;
                
  
        
        public byte[] getManufacturer () {
            return this.manufacturer;
        }

        

        public void setManufacturer (byte[] value) {
            this.manufacturer = value;
        }
        
  
        
        public byte[] getModel_number () {
            return this.model_number;
        }

        

        public void setModel_number (byte[] value) {
            this.model_number = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SystemModel.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            

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
    @ASN1Sequence ( name = "ConfigReport", isSet = false )
    public class ConfigReport implements IASN1PreparedElement {
            
        @ASN1Element ( name = "config-report-id", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ConfigId config_report_id = null;
                
  
        @ASN1Element ( name = "config-obj-list", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private ConfigObjectList config_obj_list = null;
                
  
        
        public ConfigId getConfig_report_id () {
            return this.config_report_id;
        }

        

        public void setConfig_report_id (ConfigId value) {
            this.config_report_id = value;
        }
        
  
        
        public ConfigObjectList getConfig_obj_list () {
            return this.config_obj_list;
        }

        

        public void setConfig_obj_list (ConfigObjectList value) {
            this.config_obj_list = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(ConfigReport.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
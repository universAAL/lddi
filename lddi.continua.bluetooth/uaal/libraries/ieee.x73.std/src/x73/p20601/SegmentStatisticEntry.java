
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
    @ASN1Sequence ( name = "SegmentStatisticEntry", isSet = false )
    public class SegmentStatisticEntry implements IASN1PreparedElement {
            
        @ASN1Element ( name = "segm-stat-type", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private SegmStatType segm_stat_type = null;
                
  @ASN1OctetString( name = "" )
    
        @ASN1Element ( name = "segm-stat-entry", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private byte[] segm_stat_entry = null;
                
  
        
        public SegmStatType getSegm_stat_type () {
            return this.segm_stat_type;
        }

        

        public void setSegm_stat_type (SegmStatType value) {
            this.segm_stat_type = value;
        }
        
  
        
        public byte[] getSegm_stat_entry () {
            return this.segm_stat_entry;
        }

        

        public void setSegm_stat_entry (byte[] value) {
            this.segm_stat_entry = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmentStatisticEntry.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
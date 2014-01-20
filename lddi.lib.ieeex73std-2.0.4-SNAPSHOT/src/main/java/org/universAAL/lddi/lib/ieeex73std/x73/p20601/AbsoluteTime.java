/*
    Copyright 2007-2014 TSB, http://www.tsbtecnologias.es
    Technologies for Health and Well-being - Valencia, Spain

    See the NOTICE file distributed with this work for additional
    information regarding copyright ownership

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */
package org.universAAL.lddi.lib.ieeex73std.x73.p20601;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.universAAL.lddi.lib.ieeex73std.org.bn.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.constraints.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.*;
import org.universAAL.lddi.lib.ieeex73std.org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "AbsoluteTime", isSet = false )
    public class AbsoluteTime implements IASN1PreparedElement {
            
        @ASN1Element ( name = "century", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 century = null;
                
  
        @ASN1Element ( name = "year", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 year = null;
                
  
        @ASN1Element ( name = "month", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 month = null;
                
  
        @ASN1Element ( name = "day", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 day = null;
                
  
        @ASN1Element ( name = "hour", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 hour = null;
                
  
        @ASN1Element ( name = "minute", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 minute = null;
                
  
        @ASN1Element ( name = "second", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 second = null;
                
  
        @ASN1Element ( name = "sec-fractions", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private INT_U8 sec_fractions = null;
                
  
        
        public INT_U8 getCentury () {
            return this.century;
        }

        

        public void setCentury (INT_U8 value) {
            this.century = value;
        }
        
  
        
        public INT_U8 getYear () {
            return this.year;
        }

        

        public void setYear (INT_U8 value) {
            this.year = value;
        }
        
  
        
        public INT_U8 getMonth () {
            return this.month;
        }

        

        public void setMonth (INT_U8 value) {
            this.month = value;
        }
        
  
        
        public INT_U8 getDay () {
            return this.day;
        }

        

        public void setDay (INT_U8 value) {
            this.day = value;
        }
        
  
        
        public INT_U8 getHour () {
            return this.hour;
        }

        

        public void setHour (INT_U8 value) {
            this.hour = value;
        }
        
  
        
        public INT_U8 getMinute () {
            return this.minute;
        }

        

        public void setMinute (INT_U8 value) {
            this.minute = value;
        }
        
  
        
        public INT_U8 getSecond () {
            return this.second;
        }

        

        public void setSecond (INT_U8 value) {
            this.second = value;
        }
        
  
        
        public INT_U8 getSec_fractions () {
            return this.sec_fractions;
        }

        

        public void setSec_fractions (INT_U8 value) {
            this.sec_fractions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(AbsoluteTime.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            
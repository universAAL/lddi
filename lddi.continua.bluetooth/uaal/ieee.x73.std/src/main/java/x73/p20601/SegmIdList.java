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
    @ASN1BoxedType ( name = "SegmIdList" )
    public class SegmIdList implements IASN1PreparedElement {
                
            
            @ASN1SequenceOf( name = "SegmIdList" , isSetOf = false)
	    private java.util.Collection<InstNumber> value = null; 
    
            public SegmIdList () {
            }
        
            public SegmIdList ( java.util.Collection<InstNumber> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<InstNumber> value) {
                this.value = value;
            }
            
            public java.util.Collection<InstNumber> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<InstNumber>()); 
            }
            
            public void add(InstNumber item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(SegmIdList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            
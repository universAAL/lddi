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
    @ASN1BoxedType ( name = "MetricIdList" )
    public class MetricIdList implements IASN1PreparedElement {
                
            
            @ASN1SequenceOf( name = "MetricIdList" , isSetOf = false)
	    private java.util.Collection<OID_Type> value = null; 
    
            public MetricIdList () {
            }
        
            public MetricIdList ( java.util.Collection<OID_Type> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<OID_Type> value) {
                this.value = value;
            }
            
            public java.util.Collection<OID_Type> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<OID_Type>()); 
            }
            
            public void add(OID_Type item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MetricIdList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            
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
package mder;

import java.io.OutputStream;
import java.lang.reflect.Field;
import org.bn.coders.ElementInfo;
import org.bn.coders.Encoder;
import org.bn.metadata.ASN1SequenceOfMetadata;
import org.bn.metadata.constraints.ASN1SizeConstraintMetadata;
import org.bn.metadata.constraints.ASN1ValueRangeConstraintMetadata;
import org.bn.metadata.constraints.IASN1ConstraintMetadata;
import org.bn.types.BitString;
import org.bn.utils.ReverseByteArrayOutputStream;


public class EncoderMDER <T> extends Encoder<T> {


	public void encode (T object, OutputStream os) throws Exception
	{
		ReverseByteArrayOutputStream rbaos = new ReverseByteArrayOutputStream();
		super.encode(object, rbaos);
		rbaos.writeTo(os);		
	}


	@Override
	/*
	 * The encoding of an integer value is primitive, and the octets represent the value using a twos-complement
		binary representation for signed integers and the absolute value for unsigned.
		For the size-constrained integer values supported by MDER, Figure F.2 defines octet encodings.
		(non-Javadoc)
		F.3 Byte order
		Refer to Figure F.1, which shows how various binary strings are mapped between network and memory.
		Network byte order (NBO) representation is used in diagrams. The following rules are numbered for
		reference convenience:
		
		1) Representation in diagrams uses the NBO format shown in Figure F.1.
		
		2) No alignment is used in MDER. In other words, additional bytes are not added to byte
		strings, e.g., to obtain lengths that are divisible by two or four. However, variable-length
		data items, i.e., strings, should have an even length for performance reasons. For example,
		because most data elements are 16-bit, they are not misaligned if strings are even length.
		
		3) MDAP communicants are restricted to using the NBO (big-endian) convention.
		
		4) The association protocol shall use ISO MDER to provide for universal interoperability
		during negotiation of MDER conventions. All other PDUs exchanged in the life cycle of
		device-host communication will be based in MDER, e.g., CMIP* and ROSE* PDUs. The
		suffixed asterisk (*) indicates that MDER is used as an optimization of the ISO protocol
		that is based typically in binary encoding rules (BER).
		Multibyte structures are mapped between network and computer memory and ordered in computer memory
		in two basic ways, referred to as big endian and little endian. Big-endian format is consistent with NBO,
		but little endian is not. For example, in the last example in Figure F.1, the structure ABCD would be
		ordered DCBA. In this case, if big endian is the negotiated protocol, then a little-endian machine needs to
		swap components of these structures both to and from memory, as appropriate. Program language macros
		and machine-dependent byte-swapping instructions that typically facilitate normalization are
		implementation issues, but are facilitated by non-normative definitions in this and related standards.
	 * @see org.bn.coders.IASN1TypesDecoder#decodeInteger(org.bn.coders.DecodedObject, java.lang.Class, org.bn.coders.ElementInfo, java.io.InputStream)
	 */
	public int encodeInteger(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		
    	int resultSize = 0;
        long min = 0, max = 0;
        
		ASN1ValueRangeConstraintMetadata constraint = (ASN1ValueRangeConstraintMetadata) elementInfo.getPreparedInfo().getConstraint();
		min = constraint.getMin();
		max = constraint.getMax();     
		if(min == 0 && max == 0){
			throw new Exception("Constraint for Integer not defined");
		}
        
        if(object instanceof Integer) {
            Integer value = (Integer) object;
            resultSize = encodeIntegerValue(min,max,value,stream);
        }
        else if(object instanceof Long) {
            Long value = (Long) object;
            resultSize = encodeIntegerValue(min,max,value,stream);
        }
        else if(object instanceof Short) {
        	Short value = (Short) object;
        	resultSize = encodeIntegerValue(min,max,value,stream);
        }
        return resultSize;
    }

	// returns the size in bytes of the integer and move bits properly for correct storage MSB----LSB.
	// Please note that the order of bytes is the inverse of network byte order.
	private int encodeIntegerValue (long min, long max, long value, OutputStream os) throws Exception
	{

		int resultSize = 0;
		
		if((min == -128 && max == 127)||(min == 0 && max == 255)){
			resultSize = 1;
		}else if((min == -32768 && max == 32767)||(min == 0 && max == 65535)){
			resultSize = 2;
		}else if((min == -2147483687L && max == 2147483647)||(min == 0 && max == 4294967295L)){
			resultSize = 4;
		}
		
		if(resultSize!=0){
			for (int i = 0; i<resultSize; i++){
				os.write((byte)value);
				value = value >> 8;
			}			
			return resultSize;
		}else{
			throw new Exception("Bad constraint in integer");
		}
		
	}

	@Override
	/**
	 * Two types of OctetString: 
	 * - fixed-length (size constrained) 
	 * - variable length (has an extra U-16 Integer as a length field) and then the octets. 
	 */
	public int encodeOctetString(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		
        int resultSize = 0, sizeOfString = 0;
        byte[] buffer = (byte[])object;
		// process the octet string as a string of bytes
        stream.write( buffer );
        sizeOfString = buffer.length;
        resultSize += sizeOfString;
		
		IASN1ConstraintMetadata constraint = null;
		constraint = elementInfo.getPreparedInfo().getConstraint();
		if(constraint==null){ // is not a fixed octet string, we have to add the length field (INT_U16) to the result 
			resultSize += encodeIntegerValue(0,65535, sizeOfString, stream);

		}
		return resultSize;
	}
	
	@Override
	/**
	 * Three types of BitString:
	 * - 8 bits
	 * - 16 bits
	 * - 32 bits
	 * We only have to extract the length in bytes of the BitString
	 */
	public int encodeBitString(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {

		int resultSize = 0, sizeOfString = 0;
		BitString str = (BitString)object;
		 
		// check if the constraint of size is present
		ASN1SizeConstraintMetadata constraint = null;
		constraint = (ASN1SizeConstraintMetadata) elementInfo.getPreparedInfo().getConstraint();
		if(constraint==null){
			throw new Exception("Constraint for BitString not defined");
		}
		
		byte[] buffer = str.getValue();
        stream.write( buffer );
        sizeOfString = buffer.length;
		
        resultSize += sizeOfString;
		
		return resultSize;
	}

	@Override
	public int encodeSequence(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		
		int resultSize = 0;
		
		// get the fields of the sequence
		Field [] fields = elementInfo.getFields(object.getClass());
		// calculate the total size of the sequence through its fields
        for ( int i = 0;i<fields.length; i++) {
//        	Field field  = fields [ i];
//            totalSize+= encodeSequenceField(object, i, field,steam,elementInfo);
            Field field  = fields [ fields.length - 1 - i];
            resultSize+= encodeSequenceField(object, fields.length - 1 - i, field,stream,elementInfo);
		}
		return resultSize;
	}

	
	/**
	 * Group of elements of the same type. Contains:
	 * - A header with two fields: count of number of elements / length in octets of the encoded elements (not sums the header fields)
	 * - Encoded elements, one after another 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int encodeSequenceOf(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		
		int resultSize = 0;
        Object[] collection = ((java.util.Collection<Object>)object).toArray();
        int sizeOfCollection = 0;
		
        for(int i=0;i<collection.length;i++) {
        	// process each element of the array
        	// we have to know which class is it, and encode it separately.
        	 Object obj = collection[collection.length - 1 - i];
             ElementInfo info = new ElementInfo();
             info.setAnnotatedClass(obj.getClass());
             info.setParentAnnotated(elementInfo.getAnnotatedClass());
             if(elementInfo.hasPreparedInfo()) {
                 ASN1SequenceOfMetadata seqOfMeta = (ASN1SequenceOfMetadata)elementInfo.getPreparedInfo().getTypeMetadata();
                 info.setPreparedInfo( seqOfMeta.getItemClassMetadata() );
             }
             sizeOfCollection+=encodeClassType(obj,stream,info);
         }
		resultSize += sizeOfCollection;
		
		// and now, the headers
		// number of octets
		resultSize += encodeIntegerValue(0,65535, sizeOfCollection, stream);
		// number of elements
		resultSize += encodeIntegerValue(0,65535, collection.length, stream);
			
		return resultSize;
	}
	

	@Override
	/**
	 * Compound of a INT_U16 tag (can be implicit or explicit, so it couldn't be there) with marks what kind of choice is encoded.
	 * After that tag, comes a INT_U16 field with the octet value of the encoding chosen alternative
	 * And the chosen alternative itself.
	 * @throws Exception 
	 */
	public int encodeChoice (Object object, OutputStream stream, ElementInfo elementInfo) throws Exception
	{
        int resultSize = 0;
//        doAlign(stream);
        ElementInfo info = null;
//        int elementIndex = 0;
        int fieldIdx = 0;
        for ( Field field : object.getClass().getDeclaredFields() ) {
//            if(!field.isSynthetic()) {                                
//                elementIndex++;
                info = new ElementInfo();
                info.setAnnotatedClass(field);
                if(elementInfo.hasPreparedInfo()) {
                    info.setPreparedInfo(elementInfo.getPreparedInfo().getFieldMetadata(fieldIdx));
                }
                else {
                    info.setASN1ElementInfoForClass(field);
                }                
                if(isSelectedChoiceItem(field,object, info)) {
                    break;
                }
                else
                    info = null;
                fieldIdx++;
//            }
        }
		
		if (info==null)
			throw new Exception("The Choice does not have a item");
		int tag = -1;
		tag = info.getPreparedASN1ElementInfo().getTag();
		if(tag == -1){
			throw new Exception ("TAG without value");
		}
		
		Object invokeObjResult = invokeGetterMethodForField((Field)info.getAnnotatedClass(),object, info);
        int sizeofChoice=encodeClassType(invokeObjResult, stream, info);

        resultSize+=sizeofChoice;
        /* Number of octets */
        resultSize+=encodeIntegerValue(0,65535, sizeofChoice, stream);
        /* tag selected in choice type */
        resultSize+=encodeIntegerValue(0,65535, tag, stream);

        return resultSize;
	}

	@Override
	/**
	 * Two fields:
	 * - Length in octets of the next field (INT_U16)
	 * - Encoding of selected value (m octets)
	 */
	public int encodeAny(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		
        int resultSize = 0, sizeOfString = 0;
        byte[] buffer = (byte[])object;
        stream.write( buffer );
        sizeOfString = buffer.length;
		resultSize+=sizeOfString;
		// length field
		resultSize+=encodeIntegerValue(0,65535, sizeOfString, stream);
		
		return resultSize;
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public int encodeEnumItem(Object enumConstant, Class enumClass,
			OutputStream stream, ElementInfo elementInfo) throws Exception {
		throw new Exception("ENUMERATED not included in MDER");
	}
	@Override
	public int encodeBoolean(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		throw new Exception("BOOLEAN not included in MDER");
	}
	
	@Override
	public int encodeObjectIdentifier(Object object, OutputStream steam,
			ElementInfo elementInfo) throws Exception {
		throw new Exception("OBJECT IDENTIFIER not included in MDER");
	}
	@Override
	public int encodeString(Object object, OutputStream steam,
			ElementInfo elementInfo) throws Exception {
		throw new Exception("STRING not included in MDER");
	}
	
	@Override
	public int encodeNull(Object object, OutputStream stream,
			ElementInfo elementInfo) throws Exception {
		throw new Exception("NULL not included in MDER");
	}
	@Override
	public int encodeReal(Object object, OutputStream steam,
			ElementInfo elementInfo) throws Exception {
		throw new Exception("REAL not included in MDER");
	}
}



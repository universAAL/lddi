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
package org.universAAL.lddi.lib.ieeex73std.mder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;

import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.constraints.ASN1ValueRangeConstraint;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.CoderUtils;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.DecodedObject;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.Decoder;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.ElementInfo;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.IASN1PreparedElementData;
import org.universAAL.lddi.lib.ieeex73std.org.bn.metadata.ASN1SequenceOfMetadata;
import org.universAAL.lddi.lib.ieeex73std.org.bn.metadata.constraints.ASN1SizeConstraintMetadata;
import org.universAAL.lddi.lib.ieeex73std.org.bn.metadata.constraints.ASN1ValueRangeConstraintMetadata;
import org.universAAL.lddi.lib.ieeex73std.org.bn.metadata.constraints.IASN1ConstraintMetadata;
import org.universAAL.lddi.lib.ieeex73std.org.bn.types.BitString;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.DataProtoId;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U16;
import org.universAAL.lddi.lib.ieeex73std.x73.p20601.INT_U8;



@SuppressWarnings("rawtypes")

public class DecoderMDER extends Decoder {

	public static final int INTU16SIZE=2;
	
	/*
	 * The encoding of an integer value is primitive, and the octets represent the value using a twos-complement
		binary representation for signed integers and the absolute value for unsigned.
		For the size-constrained integer values supported by MDER, Figure F.2 defines octet encodings.
		(non-Javadoc)
	 * @see org.bn.coders.IASN1TypesDecoder#decodeInteger(org.bn.coders.DecodedObject, java.lang.Class, org.bn.coders.ElementInfo, java.io.InputStream)
	 */

	public DecodedObject decodeInteger(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		
		
		long max =0;
		long min =0;
		boolean signed = false;
		ASN1ValueRangeConstraintMetadata constraint = (ASN1ValueRangeConstraintMetadata) elementInfo.getPreparedInfo().getConstraint();
		min = constraint.getMin();
		max = constraint.getMax();
		
//		System.out.println(min +" "+max);
		
		if(min == 0 && max == 0){
			throw new Exception("Constraint for Integer not defined");
		}
		
		// get the length of the integer with its bounds
		int intlength = 0;
		
		if((min == -128 && max == 127)||(min == 0 && max == 255)){
			intlength = 1;
		}else if((min == -32768 && max == 32767)||(min == 0 && max == 65535)){
			intlength = 2;
		}else if((min == -2147483687L && max == 2147483647)||(min == 0 && max == 4294967295L)){
			intlength = 4;
		}
//		System.err.println(intlength);
		if(intlength!=0){

			//is it signed?
			if (min<0)
				signed = true;
			
			// get its value and then return the decoded object
			long longValue = decodeIntegerValueAsBytes(intlength, stream, signed);
			if(objectClass.equals(Integer.class)){
				DecodedObject<Integer> result = new DecodedObject<Integer>((int)longValue,2);
				return result;
			}else if(objectClass.equals(Short.class)){
				DecodedObject<Short> result = new DecodedObject<Short>((short)longValue,1);
				return result;
			}else
			{
				DecodedObject<Long> result = new DecodedObject<Long>(longValue,4);
				return result;
			}
		}else{
			throw new Exception("Bad integer constraint");
		}
	}
	
   protected long decodeIntegerValueAsBytes(int intLen, InputStream stream, boolean signed) throws Exception {
        /*long result = 0;
        for (int i = intLen-1 ; i >= 0; i--) {
            result|= (stream.read()) << 8*i;
        }        
        return result;*/
//	   System.out.println(intLen);
         long value =0;
         for(int i=0;i<intLen;i++) {
             int bt = stream.read();
             if (bt == -1 ) {
                 throw new IllegalArgumentException("Unexpected EOF when decoding!");
             }
             
             if( signed && i == 0 && (bt & (byte)0x80)!=0) {
                 bt = bt - 256;
             }
             
             value = (value << 8) | bt ;
         }
         return value;
    }
   
   protected long decodeIntegerValueAsBytes(int intLen, InputStream stream) throws Exception {
       /*long result = 0;
       for (int i = intLen-1 ; i >= 0; i--) {
           result|= (stream.read()) << 8*i;
       }        
       return result;*/
        long value =0;
        for(int i=0;i<intLen;i++) {
            int bt = stream.read();
            if (bt == -1 ) {
                throw new IllegalArgumentException("Unexpected EOF when decoding!");
            }
            
//            if( i == 0 && (bt & (byte)0x80)!=0) {
//                bt = bt - 256;
//            }
            
            value = (value << 8) | bt ;
        }
        return value;
   }

	
    /*
	 * The encoding of a bit string value is primitive, and the contents octets simply represent the bits set in the bit
		string. Bit string lengths are constrained to 8-, 16-, or 32-bit lengths.
		Bit 0 in the encoding is represented by the most significant bit (MSB), bit 1 is represented by the next bit in
		the octet, etc.
		For the size-constrained bit string values supported by MDER, Figure F.3 defines octet encodings.
		(non-Javadoc)
	 * @see org.bn.coders.IASN1TypesDecoder#decodeBitString(org.bn.coders.DecodedObject, java.lang.Class, org.bn.coders.ElementInfo, java.io.InputStream)
	 */
	public DecodedObject decodeBitString(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {

		// get its size and check if its correct (has to be 1, 2 or 4 bytes)
		int size = 0; // the size will be returned in bits.
		boolean sizeCorrect = false;
		ASN1SizeConstraintMetadata constraint = (ASN1SizeConstraintMetadata) elementInfo.getPreparedInfo().getConstraint();
		size = (int) constraint.getMax();
//		System.out.println(size);
		size /=8; // convert to bytes

		if (size == 2 | size == 4){
			sizeCorrect= true;
		}

		if(sizeCorrect){
			byte[] buf = new byte[size]; // in bytes
			stream.read(buf);
			DecodedObject<BitString> result = new DecodedObject<BitString>();
			result.setValue(new BitString(buf));
			result.setSize(size);
			return result;
		}else{
			throw new Exception("BitString size not correct: "+size);
		}
	}
	
	/*
	 * The encoding of an octet string value is primitive, and the contents octets simply represent the elements of
		the string. The encoding of the octets is inherent to the definition of the type of the string
		
		The octets may contain ASCII printable characters or may contain encapsulated binary data. OCTET
		STRINGs containing ASCII printable characters shall be even length using a NULL character as padding.
		Note that strings that are naturally even length may not be NULL terminated.
		
		Fixed OCTET STRING types are encoded without a length field and have only the content octets.
		Variable-length OCTET STRING types are encoded with a 16-bit length field (unsigned integer, twoscomplement),
		followed by the specific number of content octets.
		
		MDER distinguishes between the fixed-length (size-constrained) OCTET STRING and the variable-length
		OCTET STRING as shown in Figure F.4:
		
		(non-Javadoc)
	 * @see org.bn.coders.IASN1TypesDecoder#decodeOctetString(org.bn.coders.DecodedObject, java.lang.Class, org.bn.coders.ElementInfo, java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	public DecodedObject decodeOctetString(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {

		int constraint_value = 0;
		boolean fixed = false;
		
		if(elementInfo.getPreparedInfo().hasConstraint()){
			ASN1SizeConstraintMetadata constraint = (ASN1SizeConstraintMetadata) elementInfo.getPreparedInfo().getConstraint();
//			System.out.println(constraint.getMax());
			constraint_value = (int) constraint.getMax();
		}
		
		if(constraint_value>0){
			fixed = true;
		}
		
		if(fixed) // size fixed! it won't have a length field
		{
			DecodedObject<Integer> length = new DecodedObject<Integer>(constraint_value,INTU16SIZE);
			byte[] buffer = new byte[length.getValue()];
			stream.read(buffer);
			return new DecodedObject(buffer, length.getValue());

		}else{
			// decode the Length field
			DecodedObject<Integer> intObj = new DecodedObject<Integer>(INTU16SIZE,INTU16SIZE);
			long longValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);

			DecodedObject<Integer> length  = new DecodedObject<Integer>((int) longValue,intObj.getSize());
			// read [value of length] bytes
			byte[] buffer = new byte[length.getValue()];
			stream.read(buffer);
			return new DecodedObject(buffer, length.getValue()+length.getSize());

		}
	}

	
	
	/*
	 * A SEQUENCE is encoded by encoding each element of the SEQUENCE in the order in which it is defined
		in the ASN.1 SEQUENCE. No alignment is performed.
		(non-Javadoc)
	 * @see org.bn.coders.Decoder#decodeSequence(org.bn.coders.DecodedObject, java.lang.Class, org.bn.coders.ElementInfo, java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	public DecodedObject decodeSequence (DecodedObject decodedTag, 
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception	{
				
		int totalSize = 0;
		Object sequence = createInstanceForElement(objectClass, elementInfo);
		initDefaultValues(sequence, elementInfo);
        Field[] fields = null;
        if(!CoderUtils.isSequenceSet(elementInfo) || elementInfo.hasPreparedInfo()) {
           fields = elementInfo.getFields(objectClass);
        }	
		
//		System.out.println(elementInfo.getPreparedInfo().getFields().getClass());
        ElementInfo info = new ElementInfo();
		// iterate all fields
        if(fields != null){
			for (int i =0 ; i<fields.length; i++)
			{
	            if(elementInfo.hasPreparedInfo()) {
	                info.setPreparedInfo(elementInfo.getPreparedInfo().getFieldMetadata(i));
	            }
	//			System.out.println(field.getType());
				Field field = fields[i];
	//			System.out.println(field.getDeclaringClass());
	//			System.out.println(field.getModifiers());
	//			Annotation[] ann = field.getAnnotations();
	//			for (int j =0 ; j<ann.length; j++){
	//				System.out.println(ann[j].annotationType());
	//			}
	//			System.out.println(field.getGenericType());
	//			System.out.println(field.getName());
				DecodedObject objectInSequence = decodeSequenceField(null, sequence, i, field, stream, elementInfo, true);
				totalSize += objectInSequence.getSize();
			}
        }else
        	throw new Exception ("Fields not found");
		return new DecodedObject(sequence, totalSize);
	}
	
	
	
	 /*
     * SEQUENCE OF is encoded by a header of a count field to specify the number of encoded elements, n, that
		follow and a length field to specify the total number of octets, m, that follow. The length, m, shall be equal
		to n size, where size is the length of each encoded element. Note that length does not include the size of
		the count and length elements. The header is followed by the encoded elements in order. See Figure F.6.
     * 
     * A count and length field with contents 0 indicates an empty list data structure and is an allowed value.
     * 
     * Has 3 fields: 
     * - INT_U16 : count of elements encoded
     * - INT_U16 : length,  octets of the elements encoded
     * - The encoded elements one after another.
     */
	@SuppressWarnings("unchecked")
	public DecodedObject decodeSequenceOf(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		
		// array of encoded elements
		Collection result = new LinkedList();
		
		// count object
		DecodedObject<Integer> intObj = new DecodedObject<Integer>(INTU16SIZE,INTU16SIZE);
		long longValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);
		int countOfElements = (int) longValue;
		
		//length object
		longValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);
		DecodedObject<Integer> length = new DecodedObject<Integer>((int) longValue,intObj.getSize());
		
		int numberofoctets = length.getValue();
		if (countOfElements>0)
		{
			Class paramType = CoderUtils.getCollectionType(elementInfo);

			for(int i=0;i<countOfElements;i++) {
				ElementInfo info = new ElementInfo();
				info.setAnnotatedClass(paramType);
				info.setParentAnnotated(elementInfo.getAnnotatedClass());
				if(elementInfo.hasPreparedInfo()) {
					ASN1SequenceOfMetadata seqOfMeta = (ASN1SequenceOfMetadata)elementInfo.getPreparedInfo().getTypeMetadata();
					info.setPreparedInfo( seqOfMeta.getItemClassMetadata() );
				}
	    
				DecodedObject item=decodeClassType(null,paramType,info,stream);
	  	     if(item!=null) {
	  	    	 result.add(item.getValue());
	  	     }
		   };
		}
		
		//total size: INT_U16(count)+INT_U16(length)+encodeditems 
		return new DecodedObject(result, 
				(intObj.getSize()+length.getSize()+numberofoctets));
	}
	
	/*
	 * 
	 * CHOICE is encoded by a header of a tag field to specify the encoding of the chosen alternative and a length
		field to specify the number of octets in the encoding of the chosen alternative that follows. See Figure F.7
		The rules for tag values are defined as follows:
		-	Tags are implicit or explicit.
		-	The abstract syntax for implicit tags does not include an explicit choice number and, therefore, requires
			a rule for assigning choice_id field values. For implicit tags, choice_id field values shall start with the
			value 1 and are sequential in order of the abstract syntax choices. In the example above, the choice_id
			field values for one_type_chosen and two_type_chosen fields are 1 and 2, respectively.
		- 	The abstract syntax for explicit tags includes an explicit choice number, which is mapped directly to
			the choice_id field in the encoding rule just defined. In this case, choices are sequential, but disjoint,
			depending on the application, as in the following example:

	 * Three fields:
	 * - INT_U16 Tag
	 * - INT_U16 Length
	 * - Encoding of the choice
	 */
	
	public DecodedObject decodeChoice (DecodedObject decodedTag, 
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception	{

		// tag (apdu type)
		DecodedObject<Integer> intObj = new DecodedObject<Integer>(INTU16SIZE,INTU16SIZE);
		
		long tagValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);

		DecodedObject<Integer> tag = new DecodedObject<Integer>((int) tagValue,intObj.getSize());
		
		//length
		
		long lengthValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);

		DecodedObject<Integer> lengthEncoded = new DecodedObject<Integer>((int) lengthValue,intObj.getSize());
		
		Object choice = createInstanceForElement(objectClass, elementInfo);

        Field[] fields = elementInfo.getFields(objectClass);

		DecodedObject value = null;
		int size=0;
		if(fields!=null){
			for (int i=0 ; i<fields.length;i++) { 
				Field field = fields[i];
				ElementInfo info = new ElementInfo();
				info.setAnnotatedClass(field);
				if(elementInfo.hasPreparedInfo()) {
					info.setPreparedInfo(elementInfo.getPreparedInfo().getFieldMetadata(i));
				}
				else
					info.setASN1ElementInfoForClass(field);
				info.setGenericInfo(field.getGenericType());   
//				System.out.println(field.getType());
//				System.out.println(info.getPreparedASN1ElementInfo().getTag());
//				System.out.println(tag.getValue());
//				System.out.println("------------");
				if(info.getPreparedASN1ElementInfo().getTag() == tag.getValue()){
					value = decodeClassType(decodedTag, field.getType(),info,stream);
					size += value.getSize();
					invokeSelectMethodForField(field, choice, value.getValue(), info);
					break;
				}
			}
		}
		
		return new  DecodedObject<Object>(choice,size+tag.getSize()+lengthEncoded.getSize());
	}
	
	
	@SuppressWarnings("unchecked")
	
	/*
	 * The ANY DEFINED BY type (ASN.1 1988/90) or the instance-of type (ASN.1 1994) is encoded by a
		header of a length field to specify the number of octets in the encoding of the selected value that follows.
		See Figure F.8.
		The type specified refers to embedded syntaxes that are specified using a registered OID. Refer to Annex H
		of ISO/IEEE 11073-20101:2004 [B14] for compatibility cases.
		
		This example shows the byte encoding of the SEQUENCE containing a context-sensitive OID and the
		value of an ANY DEFINED BY.
		
		In the preceding mapping, the type-id field is a context-free OID. An application uses the ID field to cast
		the any_data field to the right data type. The character data type for the any_data field is essentially
		meaningless and provides the address of the field only. Note that length can be 0, which means the
		any_data field does not exist.
		The instance-of type encodes the ASN.1 TYPE-IDENTIFIER construct and is identical to the ANY
		DEFINED BY encoding for the purpose of backwards-compatibility.
		
	 * INT_U16 - Length
	 * Encoding of the selected value
	 */
	public DecodedObject decodeAny(DecodedObject decodedTag, Class objectClass,
			ElementInfo elementInfo, InputStream stream) throws Exception {
		
//		System.out.println(elementInfo.getMaxAvailableLen());

		// obtain the Length field and its value
		DecodedObject<Integer> intObj = new DecodedObject<Integer>(INTU16SIZE,INTU16SIZE);
		
		
		long lengthValue = decodeIntegerValueAsBytes(intObj.getSize(), stream);

		
		DecodedObject<Integer> length = new DecodedObject<Integer>((int) lengthValue,intObj.getSize());
				
		int lengthofAny = length.getValue();
		
//		System.out.println(lengthofAny);
		ByteArrayOutputStream anyStream = new ByteArrayOutputStream(8192);
		if(lengthofAny>0){
			
			byte[] buf = new byte[lengthofAny];
			int readed = stream.read(buf);
	//		System.out.println(readed);
			anyStream.write(buf,0,readed);
			// return the byte array with the data and its length (2 from length field and the size of data)
			return new DecodedObject(anyStream.toByteArray(),readed+length.getSize());
		}
		// if the Any is empty, return a empty object with size 2 (length)
		return new DecodedObject(anyStream, length.getSize()); 
	}
	
	
	public DecodedObject decodeEnumItem(DecodedObject decodedTag,
			Class objectClass, Class enumClass, ElementInfo elementInfo,
			InputStream stream) throws Exception {
		throw new Exception("ENUMERATED not included in MDER");
	}
	
	public DecodedObject decodeObjectIdentifier(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		throw new Exception("OBJECT IDENTIFIER not included in MDER");

	}
	
	public DecodedObject decodeString(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		throw new Exception("STRING not included in MDER");

	}
	
	public DecodedObject decodeBoolean(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		throw new Exception("BOOLEAN not included in MDER");
	}
	
	public DecodedObject decodeReal(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		throw new Exception("REAL not included in MDER");
	}
	
	public DecodedObject decodeNull(DecodedObject decodedTag,
			Class objectClass, ElementInfo elementInfo, InputStream stream)
			throws Exception {
		throw new Exception("NULL not included in MDER");
	}

    protected DecodedObject decodeSet(DecodedObject decodedTag,Class objectClass, 
            ElementInfo elementInfo, Integer len,InputStream stream) throws Exception {
    	throw new Exception ("SET is not included in MDER");
	}  

	
	public DecodedObject decodeTag(InputStream stream) throws Exception {
		return null;
	}

}
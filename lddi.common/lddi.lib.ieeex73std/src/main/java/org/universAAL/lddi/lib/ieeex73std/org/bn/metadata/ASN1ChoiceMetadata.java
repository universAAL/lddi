/*
 Copyright 2006-2011 Abdulla Abdurakhmanov (abdulla@latestbit.com)
 Original sources are available at www.latestbit.com

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

package org.universAAL.lddi.lib.ieeex73std.org.bn.metadata;

import java.io.InputStream;
import java.io.OutputStream;

import org.universAAL.lddi.lib.ieeex73std.org.bn.annotations.ASN1Choice;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.DecodedObject;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.ElementInfo;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.IASN1TypesDecoder;
import org.universAAL.lddi.lib.ieeex73std.org.bn.coders.IASN1TypesEncoder;

/**
 * @author jcfinley@users.sourceforge.net
 */
public class ASN1ChoiceMetadata
    extends ASN1TypeMetadata
{
    public ASN1ChoiceMetadata(String name)
    {
        super(name);
    }
    
    public  ASN1ChoiceMetadata(ASN1Choice annotation) {
        this(annotation.name());
    }
    
    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream, 
               ElementInfo elementInfo) throws Exception {
        return encoder.encodeChoice(object, stream, elementInfo);
    }    
    
    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {
        return decoder.decodeChoice(decodedTag,objectClass,elementInfo,stream);
    }

}

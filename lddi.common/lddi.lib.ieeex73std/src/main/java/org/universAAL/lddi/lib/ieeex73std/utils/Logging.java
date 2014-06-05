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
package org.universAAL.lddi.lib.ieeex73std.utils;

import java.io.IOException;

import org.universAAL.lddi.lib.ieeex73std.mder.DecoderMDER;




public class Logging {
	
	public static void log (String phrase)
	{
		System.out.println("LOG: "+phrase);
	}
	
	public static void logError (String phrase)
	{
		System.err.println("ERROR: "+ phrase);
	}
	public static void logWarning (String phrase)
	{
		System.out.println("WARN: "+ phrase);
	}
	public static void logMeasurement (String phrase)
	{
		System.out.println("MEASUREMENT: "+ phrase);
	}


	public static void logError(DecoderMDER decoderMDER, String string,
			IOException e) {
		System.out.println("ERROR: "+ decoderMDER + " ---- " + string + " ---- " + e);
		
	}

	public static void logWarning(String name, String string) {
		System.out.println("WARN: Class"+ name + " ---- " + string);
		
	}

	public static void logWarning(
			Object obj, String string) {
		
		System.out.println("WARN: Class"+ obj.getClass().getName() + " ---- " + string);
		
	}

	public static void xml(String attribute) {
		System.out.println("XMLPARSER OUTPUT: "+attribute);
		
	}

	public static void logSend(String string) {
		
		System.out.println("SENDING APDU: "+string);
		blankLine();
		
	}

	public static void logAARQProcessing(String string) {
		System.out.println("LOG: AARQ Processing: "+string);
	}
	public static void logAARQProcessingError(String string) {
		System.err.println("LOG: AARQ Processing: "+string);
	}

	public static void logRLRQProcessing(String string) {
		System.out.println("LOG: RLRQ Processing: "+string);
	}
	public static void logRLRQProcessingError(String string) {
		System.err.println("LOG: RLRQ Processing: "+string);
	}

	public static void logAbrtProcessing(String string) {
		System.out.println("LOG: ABRT Processing: "+string);
	}
	public static void logAbrtProcessingError(String string) {
		System.err.println("LOG: ABRT Processing: "+string);
	}
	
	public static void logDataExtractor(String str){
		System.out.println("LOG: Extracting data: "+str);
	}
	public static void logDataExtractorError(String str){
		System.err.println("LOG: Extracting data: "+str);
	}

	public static void logRLREProcessing(String string) {
		System.out.println("LOG: RLRE Processing: "+string);
	}
	
	public static void logRLREProcessingError(String string) {
		System.err.println("LOG: RLRE Processing: "+string);
	}

	public static void logPrstProcessing(String string) {
		System.out.println("LOG: PRST Processing: "+string);		
	}
	public static void logPrstProcessingError(String string) {
		System.err.println("LOG: PRST Processing: "+string);		
	}
	
	public static void Separator(){
		System.out.println();  // blank lines for easy lecture
		System.out.println("---------------------------------------------");  // blank lines for easy lecture
		System.out.println();  // blank lines for easy lecture
	}

	public static void blankLine() {
		System.out.println();
		
	}
	
}

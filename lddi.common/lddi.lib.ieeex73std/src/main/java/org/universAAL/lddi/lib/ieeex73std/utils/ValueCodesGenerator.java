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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * Creation of the Status codes from the ISO/IEEE 11073-20601 standard
 */

public class ValueCodesGenerator {

	public ValueCodesGenerator() {
		System.out.println("Alla voy");

		FileWriter fw = null;
		String outputfile = "StatusCodes.java";

		try {
			fw = new FileWriter(outputfile);
			String fileName = "ieeesources/statuscodes.txt";
			BufferedReader in = new BufferedReader(new FileReader(fileName));
			String line;
			line = in.readLine();
			while (line != null) {
				StringTokenizer tokens = new StringTokenizer(line, " ");
				if (tokens.countTokens() > 1) {
					String name = tokens.nextToken();
					String value = tokens.nextToken();
					generateDef(fw, name, value);
					System.out.println(name + " " + value);
				}
				line = in.readLine();
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void generateDef(FileWriter fw, String name, String value) {
		try {
			fw.write("\t\tpublic static final int " + name + "\t\t\t\t\t\t = " + value + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

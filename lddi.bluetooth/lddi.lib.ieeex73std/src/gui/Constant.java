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

/** 
 *  
 *  @author Angel Martinez & Luis Gigante  
 *  
 *  NOTE 1: OMRON is a register trademark  
 *  
 *  */

/** Package */
package gui;

// Imports
import java.util.Calendar;

// Class
public final class Constant {
	
	// Attributes	
	
	/**Connection*/
	//IP and Port
	public static final String dirIP="192.168.230.105"; //change as needed (i.e. dirIP="192.168.1.1"
	public static final String port ="9278"; // change as needed (9278 is the default port for R-OSGi)
				
	/** GUI */
	// Main frame GUI dimensions
	public static final String mainFrameName = "Continua Health Alliance Digital Glucometer Client";
	public static final int mainFrameHeight = 650;
	public static final int mainFrameWidth = 550;		
	
	// Buttons dimensions
	public static final int buttonHeight = 75;
	public static final int buttonWidth = 150;	
	
	// Glucometer memory values 
	public static int glycaemicValue = -1;
	public static Calendar latestGlycaemicTestDate = null;
	
	// Image name and extension weighing scale
	public static final String imageName = "resources/img/weighing";
	public static final String imageExtension = ".png";
	
	// Methods   
    /** Show logs */
    public static void showLog(String str) {    	
    	System.out.println("<Log> " + str + " </Log>");
    }
    
    /** Show errors or exceptions */
    public static void showException(String str) {    	
    	System.out.println("<Exception> " + str + " </Exception>");    	
    }    
}
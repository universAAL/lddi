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
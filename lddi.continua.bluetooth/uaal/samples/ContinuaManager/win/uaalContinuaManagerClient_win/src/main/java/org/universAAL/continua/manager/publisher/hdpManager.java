/**

 Name        : hdpManager.java
 Author      : Angel Martinez (amartinez@tsbtecnologias.es)
 Version     : 0
 Copyright   : TSB
 Description : HDP manager manages Bluetooth native methods and implements an interface between 
 			   the medical device and the ISO/IEEE 11073 manager. 
 Important   : Any change at this file implies necessary generate the DLL file again.

 */

// Package
package org.universAAL.continua.manager.publisher;

//Imports
import org.universAAL.continua.manager.gui.GUI;

// Class
public class hdpManager implements hdpManagerListener {

	/** Attributes */	

	// Valid continua Devices	
	private static String CONTINUA_DEVICE = null;
	private static String dataTypeValue = null;

	/** Native libs (M$ dlls)*/
	private static final String dll_library_name_win_32 = "hdpNativeLib_win_x86_32.dll";	
	
	/** Available data types in Toshiba stack */
	public static final String HDP_DEVICE_BLOOD_PRESSURE = "BloodPressureMonitor";
	public static final String HDP_DEVICE_WEIGHING_SCALE = "WeightingScale";	
	
	// Remote device type
	private String remoteDeviceType = null;
	
	private static boolean timeoutLaunched = false;

	/** Native functions */
	// Init dll
	public native boolean initLibrary();	
	// Launch bluetooth settings
	public native boolean BtHdpLaunchBtSettings();		
	/** Link management */
	// Open a connection to HDP and wait for connection from a remote HDP device
	public native boolean BtHdpOpen(String dataTypeDevice);
	// Close a connection (previously opened) to an HDP device
	public native boolean BtHdpClose();	
	// Wait for HDP data frames
	public native void waitHDPDataFrames();	 
	
	/** Constructor */
	public hdpManager(String str) {	
		remoteDeviceType = str;
		if(remoteDeviceType.equals("HDP_DEVICE_BLOOD_PRESSURE")) {
			CONTINUA_DEVICE = "BloodPressureMonitor";						
		} else {			
			CONTINUA_DEVICE = "WeightingScale";					
		}
		dataTypeValue = CONTINUA_DEVICE;		
	}

	/** Java methods */	
	// Init method
	public void init() {		
		if(initLibrary()) {
			if(BtHdpOpen(CONTINUA_DEVICE)) {			
				waitHDPDataFrames();					
			} else {
				System.out.println("Unable to open HDP channel");
			}
		} else {
			System.out.println("Ups. Bad news, the monkey who has developed this application ran out of nuts. Try again later...");
		}		
	}	
	
	/** Shorten number of decimals */
	public double shortDecimalNumber(double d) {
		return Math.round(d*Math.pow(10,2))/Math.pow(10,2); 
	}		
	
	/** Data received from Weighing scale */
	public void onWeightDataReceived(String str) {		
		if(str != null) {								
			GUI.finalMeasuredWeightData = Double.parseDouble(str);
			GUI.uaalPublisherWeightValueTextfield.setText(str);					
			GUI.uaalPublisherWeightUnitTextfield.setText("kg");	
			GUI.mainPanel.repaint();				
		}
	}
	
	
	public void onDiastolicDataReceived(String str) {		
		if(str != null) {			
			GUI.finalDiaBloodPressureData = Double.parseDouble(str);								
			GUI.uaalPublisherBloodPressureDiaValueTextfield.setText(str);			
			GUI.mainPanel.repaint();
		}
	}
	
	
	public void onSystolicDataReceived(String str) {		
		if(str != null) {			
			GUI.finalSysBloodPressureData = Double.parseDouble(str);							
			GUI.uaalPublisherBloodPressureSysValueTextfield.setText(str);			
			GUI.mainPanel.repaint();
		}
	}
	
	
	public void onHeartRateDataReceived(String str) {		
		if(str != null) {			
			GUI.finalHrBloodPressureData = Double.parseDouble(str);			
			GUI.uaalPublisherBloodPressurePulValueTextfield.setText(str);			
			GUI.mainPanel.repaint();
		}
	}
	
	/** */
	public void onMessage(String str) {	
		if(str.equals("timeout")) {
			System.out.println("timeout desde java");
			timeoutLaunched = true;
		} else {
			System.out.println(str);	
		}				
	}
	
	/** Reset components */
	public void resetComponentsStatus() {
		System.out.println("reset compnents desde java");
		CONTINUA_DEVICE = null;
		dataTypeValue = null;
		timeoutLaunched = false;
	}
	
	/** Close HDP channel and free resources */
	public void exit() {	
		if(!timeoutLaunched) {
			System.out.println("bt chapado desde java");	
			//BtHdpClose();
		}
		resetComponentsStatus();			
	}

	// Load dynamic library
	static {		
		try {	
			// OS = Windows 7
			if(System.getProperty("os.name").equals("Windows 7")) {								
					System.loadLibrary(dll_library_name_win_32);				
			}						
		}catch(Exception ex) {
			System.out.println("Unable to load native library. Please, check your path and OSGi manifest settings...");
		}
	}

	public void onChannelConnected() {
		
		
	}
	public void onChannelDisconnected() {		
		
	}	
}
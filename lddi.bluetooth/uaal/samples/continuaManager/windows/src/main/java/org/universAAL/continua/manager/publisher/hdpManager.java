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
import java.math.BigDecimal;

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
	
	public static boolean timeoutLaunched = false;
	public static boolean readyToCloseWindow = false;

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
		if(Activator.dllReadyLatch) {
			if(initLibrary()) {
				if(BtHdpOpen(CONTINUA_DEVICE)) {
					Activator.dllReadyLatch = false;
					waitHDPDataFrames();
				} else {
					System.out.println("Unable to open HDP channel");
				}
			} else {
				System.out.println("Ups. Bad news, the monkey who has developed this application ran out of nuts. Try again later...");
			}
		}		
	}	
	
	/** Shorten number of decimals */	
	public String shortDecimalNumber(double unrounded) {
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(2,BigDecimal.ROUND_HALF_UP);	    
//	    return rounded.doubleValue();
	    return rounded.toString();
	}
	
	/** Data received from Weighing scale */
	public void onWeightDataReceived(String str) {		
		if(str != null) {								
			GUI.finalMeasuredWeightData = Double.parseDouble(str);
			GUI.uaalPublisherWeightValueTextfield.setText(shortDecimalNumber(Double.parseDouble(str)));					
			GUI.uaalPublisherWeightUnitTextfield.setText("kg");	
			GUI.mainPanel.repaint();
			//TODO cambio para la review
			GUI.publishDataToContextBus();
		}
	}
	
	
	public void onDiastolicDataReceived(String str) {		
		if(str != null) {			
			GUI.finalDiaBloodPressureData = Double.parseDouble(str);								
			GUI.uaalPublisherBloodPressureDiaValueTextfield.setText(shortDecimalNumber(Double.parseDouble(str)));			
			GUI.mainPanel.repaint();
		}
	}
	
	
	public void onSystolicDataReceived(String str) {		
		if(str != null) {			
			GUI.finalSysBloodPressureData = Double.parseDouble(str);							
			GUI.uaalPublisherBloodPressureSysValueTextfield.setText(shortDecimalNumber(Double.parseDouble(str)));			
			GUI.mainPanel.repaint();
		}
	}
	
	
	public void onHeartRateDataReceived(String str) {		
		if(str != null) {			
			GUI.finalHrBloodPressureData = Double.parseDouble(str);			
			GUI.uaalPublisherBloodPressurePulValueTextfield.setText(shortDecimalNumber(Double.parseDouble(str)));			
			GUI.mainPanel.repaint();
			//TODO cambio para la review
			GUI.publishDataToContextBus();
		}
	}
	
	/** */
	public void onMessage(String str) {	
		if(str.equals("timeout")) {			
			timeoutLaunched = true;
		} else if(str.equals("Waiting for HDP frames...")) {
			readyToCloseWindow = true;
			System.out.println(str);
		} else {
			System.out.println(str);	
		}
	}
	
	/** Reset components */
	public void resetComponentsStatus() {		
		CONTINUA_DEVICE = null;
		dataTypeValue = null;
		timeoutLaunched = false;
	}
	
	/** Close HDP channel and free resources */
	public void exit() {	
		//if(!timeoutLaunched) {				
			//BtHdpClose();
		//}
		resetComponentsStatus();			
	}

	// Load dynamic library
	static {		
		try {	
			// OS = Windows 7
			if(System.getProperty("os.name").equals("Windows 7")) {	
				if(Activator.dllReadyLatch) {
					System.loadLibrary(dll_library_name_win_32);					
				}	
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
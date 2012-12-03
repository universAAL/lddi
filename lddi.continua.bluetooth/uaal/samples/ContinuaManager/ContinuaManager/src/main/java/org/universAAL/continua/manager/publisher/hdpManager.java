/**

 Name        : hdpManager.java
 Author      : Angel Martinez (amartinez@tsbtecnologias.es) & Luis Gigante (lgigante@tsbtecnologias.es)
 Version     : 0
 Copyright   : TSB
 Description : HDP manager manages Bluetooth native methods and implements an interface between 
 			   the medical device and the ISO/IEEE 11073 manager. 
 Important   : Any change at this file implies necessary generate the C header file again.

 */

// Package
package org.universAAL.continua.manager.publisher;

//Imports
import manager.Manager;
import manager.apdu.APDUGenerator;
import manager.apdu.APDUProcessor;
import measurements.Measurement;
import x73.p20601.fsm.StateMachine20601;
import events.EventIEEEManager;
import org.universAAL.continua.manager.gui.GUI;

// Class
public class hdpManager implements hdpManagerListener {

	/** Attributes */	

	// Valid continua Devices	
	private static String CONTINUA_DEVICE = null;

	// Dinamic library name and path where it can be found (IMPORTANT: without "lib" prefix)
	//TODO Please, check the name and location of the native library (/lib folder)
	private static String libNameWithoutExtension_unix_32 = "HDPnative_unix_32";
	private static String libNameWithoutExtension_unix_64 = "HDPnative_unix_64";
	private static String libNameWithoutExtension_windows_32 = "";
	private static String libNameWithoutExtension_windows_64 = "";

	// MAC address remote device
	private static String macAddressRemoteDevice = null;	

	// Trusted property of remote device (Property options: Trusted[boolean], Blocked[boolean], Alias[string])
	private static String trustedProperty = "Trusted";
	
	// HDP channel properties. Options:
	// - dataTypeValue = depends on the type/profile of the remote device. You'll find the right value at x073 specs (weight scale = 0x100F, pulse
	//   oximeter = 0x1004, blood pressure monitor = 0x1007, thermometer = 0x1008, glucose meter = 0x1011, etc)
	// - roleValue = "Source" or "Sink" (which role will play our application?). 
	// - shorDescriptionValue = some text (optional)
	// - channelTypeValue = "Reliable" or "Streaming" (just for "Sources", optional otherwise)
	private static String dataTypeValue = null;	
	private static String roleValue = "Sink";
	private static String shortDescriptionValue = "TSB test (sink mode)";
	private static String channelTypeValue = "Reliable";	

	// Remote device path (real sample: /org/bluez/1134/hci0/dev_00_09_1F_80_0A_E0)
	private static String remoteDevicePath = null;

	// Data channel path (real sample: /org/bluez/1092/hci0/dev_00_09_1F_80_0A_E0/chan65279)
	private static String hdpDataChannelPath = null;

	// HDP data channel file descriptor (needed to send and receive data frames)
	private static int hdpDataChannelFileDescriptor = -1;

	// HDP received data frame
	private static byte[] hdpReceivedDataFrame = {};	

	// x73 object manager
	private static Manager x73manager = null;

	// Objects for controlling the ISO/IEEE 11073 Manager
	private static StateMachine20601 fsm = null;
	private static APDUGenerator msg_generator = null;
	private static APDUProcessor rmp = null;
	private static EventIEEEManager eventmanager = null;
	private static Measurement outputData = null;
	
	// Remote device type
	private String remoteDeviceType = null;

	/** Native functions */

	/**
	 *  Start dbus platform for message interchanging within processes in Linux OS.
	 *  
	 * @return if the Dbus platform is correctly initialized.
	 */
	private native boolean startDbusPlatform();

	/**
	 * Get the path for the local Bluetooth adapter which is in use at the moment.
	 * 
	 * @return local bluetooth adapter path
	 */
	private native String getLocalBluetoothAdapterPath();

	/**
	 * Show path of all local bluetooth adapters available at PC (may be more than one).
	 */
	private native void showLocalBluetoothAdaptersPath();

	/**
	 * Show all properties of the DEFAULT local bluetooth adapter. In case a computer has more than one Bluetooth adapter, 
	 * there will be one in use by default.
	 */
	private native void showDefaultLocalBluetoothAdaptersProperties();

	/**
	 * Get the object path of a remote device given a MAC address (format:XX:XX:XX:XX:XX:XX).
	 * 
	 * @param macAddressRemoteDevice the MAC address of the desired remote device (the format of the MAC is XX:XX:XX:XX:XX:XX).
	 * @return the path for the remote Bluetooth device in the local machine.
	 */
	private native String getRemoteBluetoothAdapterPath(String macAddressRemoteDevice);

	/**
	 * Show all properties of a remote device (it should be first created with getRemoteBluetoothAdapterPath method).
	 * 
	 * @param remoteDevicePath the path for the remote Bluetooth device in the local machine.
	 * @see showDefaultLocalBluetoothAdaptersProperties()
	 */
	private native void showRemoteBluetoothAdapterProperties(String remoteDevicePath);

	/**
	 * Modify a property of a remote device.
	 * This will be used for enabling the "Trusted" property in a remote Health Device Profile capable device for being establish a connection.
	 * 
	 * @param remoteDevicePath the path for the remote Bluetooth device in the local machine.
	 * @param propertyKey the name of the property to be modified.
	 * @param propertyValue the new value for the property.
	 */
	private native void setPropertyRemoteDevice(String remoteDevicePath,String propertyKey,boolean propertyValue);

	/**
	 * Create a new HDP application and gets the object path of the new created application.
	 * 
	 * @param dataTypeValue which kind of a HDP capable device will be used.
	 * @param roleValue set the role of the HDP role that the local machine will be playing. Usually, "Sink"
	 * @param shortDescriptionValue custom string for helping the remote devices to identify the local machine they are connected to.
	 * @param channelTypeValue set whether the channel is working with trusted or streaming mode.
	 * @return the path of the new Health Device Profile application newly created in the local machine. 
	 */
	private native String createHDPApplication(String dataTypeValue,String roleValue,String shortDescriptionValue,String channelTypeValue);

	/**
	 * Destroy an HDP application.
	 * 
	 * @param objectPathHDPApplication the path of the Health Device Profile application to be destroyed. 
	 */
	private native void destroyHDPApplication(String objectPathHDPApplication);	

	/**
	 * Checks DBUS system availability.
	 * 
	 * @return true if the Dbus system is available, or false if not.
	 */
	private native boolean getDbusSystemAvailability();

	/**
	 * Add a specific rule to follow HDP messages inside DBUS. 
	 * 
	 * @return true if the local application is subscribed to HDP messages in DBus.
	 */
	private native boolean enableHDPListener();

	/**
	 * 	Wait for new incoming HDP connections.
	 */
	private native void waitHDPConnections();

	/**
	 * Get the object path of the HDP channel ready to send/receive data to/from Continua devices.
	 * 
	 * @param remoteDevicePath the path for the remote Bluetooth device in the local machine.  
	 * @return the path of the data channel from a Health Device Profile application.  
	 */
	private native String getHDPDataChannelPath(String remoteDevicePath);

	/**
	 * Show the properties of a HDP data channel previously created.
	 * 
	 * @param hdpDataChannelPath the path of the data channel from a Health Device Profile application.  
	 */
	private native void showHDPDataChannelProperties(String hdpDataChannelPath);

	/**
	 * Get the file descriptor associated to a HDP data channel
	 * 
	 * @param hdpDataChannelPath the path of the data channel from a Health Device Profile application. 
	 * @return the number that identifies the file descriptor in a Linux OS.
	 */
	private native int getHDPDataChannelFileDescriptor(String hdpDataChannelPath);

	/**
	 * Release the file descriptor associated to this HDP data channel (HDP application will be closed).
	 * 
	 * @param hdpDataChannelPath the path of the data channel from a Health Device Profile application. 
	 */
	private native void releaseHDPDataChannelFileDescriptor(String hdpDataChannelPath);

	/**
	 * Wait for HDP data frames from Continua Health devices. These frames will be compliant with ISO/IEEE 11073 standard.
	 * 
	 * @param hdpDataChannelFileDescriptor the file descriptor that identifies a HDP data channel.
	 */
	private native void waitHDPDataFrames(int hdpDataChannelFileDescriptor);

	/**
	 * Get the input data frame from a Continua Health device. The data will be retrieved in raw format (byte array), and will be processed by the
	 * ISO/IEEE 11073 library included.
	 * 
	 * @param hdpDataChannelFileDescriptor the file descriptor that identifies a HDP data channel.
	 * @return the data a remote device sent, in raw format.
	 */
	private native byte[] getHDPDataFrame(int hdpDataChannelFileDescriptor);

	/**
	 * Get the number of bytes read from input data frames.
	 * 
	 * @param hdpDataChannelFileDescriptor the file descriptor that identifies a HDP data channel.
	 * @return the size of the data frame received.
	 */
	private native int getSizeHDPDataFrame(int hdpDataChannelFileDescriptor);

	/**
	 * Show HDP data frame received. 
	 *  
	 * @param hdpDataChannelFileDescriptor the file descriptor that identifies a HDP data channel.
	 */
	private native void showHDPDataFrame(int hdpDataChannelFileDescriptor);

	/**
	 * Send HDP data frames to Continua Health devices after processing the ones previously received and generate the proper response to them.
	 * 
	 * @param hdpDataChannelFileDescriptor the file descriptor that identifies a HDP data channel.
	 * @param hdpDataFrame the raw byte array of the data that will be sent back to a Continua device.
	 */
	private native void sendHDPDataToDevice(int hdpDataChannelFileDescriptor,byte[] hdpDataFrame);

	/**
	 * Free all employed resources and end HDP manager in a well-known status
	 * 
	 */
	private native void closeHDPManager(); 
	
	/** Constructor */
	public hdpManager(String str) {	
		remoteDeviceType = str;
		if(remoteDeviceType.equals("BloodPressureMonitor")) {
			CONTINUA_DEVICE = "BloodPressureMonitor";
			//TODO Please, put here your MAC address device
			macAddressRemoteDevice = "00:09:1F:80:04:D6";			
		} else {			
			CONTINUA_DEVICE = "WeightingScale";
			//TODO Please, put here your MAC address device
			macAddressRemoteDevice = "00:09:1F:80:0A:E0";		
		}
		dataTypeValue = CONTINUA_DEVICE;		
	}

	/** Java methods */	
	// Init method
	public void init() {		
		// Create the objects related to ISO/IEEE 11073 Manager interpreter
		eventmanager = new EventIEEEManager();
		x73manager = new Manager(eventmanager);
		fsm = x73manager.getFSM();
		rmp = x73manager.getMessageProcessor();		
		// First (mandatory) step: DBUS platform should be successfully initialized (otherwise JVM will return fatal errors)
		if(startDbusPlatform()) {			
			// Get and show the object path of the DEFAULT adapter (in GNU/Linux environments should be 'hci0')
			String defaultDevicePathTemp = getLocalBluetoothAdapterPath();
			System.out.println("Default bluetooth adapter path: "+defaultDevicePathTemp);	
			if(defaultDevicePathTemp == null) {				
				System.out.println("Unable to detect bluetooth adapters");
				System.exit(0);
			}	
			// Show the object path of ALL the local bluetooth adapters available at our PC
			System.out.println("List of local bluetooth adapters:");
			showLocalBluetoothAdaptersPath();
			// Show some properties of the DEFAULT adapter: address, name, class, powered, discoverable, pairable, discoverable timeout, pairable timeout,
			// discovering, devices and UUIDs. Check bluetooth manuals to understand the meaning of each
			System.out.println("Local bluetooth properties:");
			showDefaultLocalBluetoothAdaptersProperties();
			// Object path of a remote device
			remoteDevicePath = getRemoteBluetoothAdapterPath(macAddressRemoteDevice);
			System.out.println("Remote bluetooth adapter path: "+remoteDevicePath);	
			// Remote device properties (from its own path)
			System.out.println("Remote device properties (BEFORE):");
			showRemoteBluetoothAdapterProperties(remoteDevicePath);
			// Prior to start any communication process, we need to ensure that the remote device appears as a 'Trusted' (true value) device (by default  
			// it should appear as false (not trusted) so we need to change this property			
			setPropertyRemoteDevice(remoteDevicePath,trustedProperty,true);			
			// Show the properties of the remote device to verify that the 'Trusted' value has been successfully changed (false -> true)
			System.out.println("Remote device properties (AFTER):");			
			showRemoteBluetoothAdapterProperties(remoteDevicePath);
			// We need to create an HDP application before any HDP data frames exchange between agents/sources and managers/sinks. IMPORTANT: the app 
			// created will be only valid for the data types and roles passed as arguments. For instance, this HDP channel will be created only if the 
			// remote device is a weight scale acting as a source. So, here we go...
			String hdpApplicationIdTemp = createHDPApplication(dataTypeValue,roleValue,shortDescriptionValue,channelTypeValue);
			System.out.println("HDP application identifier: "+hdpApplicationIdTemp);
			// HDP app successfully created
			if(hdpApplicationIdTemp != null) {
				// We have the option to destroy/close any HDP application. For security reason, only the owner of this process will be able to destroy it
				destroyHDPApplication(hdpApplicationIdTemp);
				// New HDP application (again)
				hdpApplicationIdTemp = createHDPApplication(dataTypeValue,roleValue,shortDescriptionValue,channelTypeValue);
				System.out.println("HDP application identifier: "+hdpApplicationIdTemp);				
				// A good practice should be check the availability of the DBUS system (is it still alive?)
				System.out.println("DBUS sytem status: "+(getDbusSystemAvailability()?"Up":"Down"));
				// Remember that those PHD devices with compatible data type and role (weight scales and sources) are able to find our applicattion through 
				// bluetooth connections. At this case: we are a sink so we need to wait for input connections (handle signals). First step: we need to
				// subscribe our application to HDP events in the DBUS system.
				if(!enableHDPListener()) {
					System.out.println("Unable to listen HDP messages inside DBUS");
					System.exit(0);
				} else {
					System.out.println(fsm.getStringTransportState() + " " +fsm.getStringChannelState());					
					waitHDPConnections();
				}				
			}
		} else {
			System.out.println("Ups. Bad news, the monkey who has developed this application ran out of nuts. Try again later...");
		}
	}	

	/* HDP channel available. x073 state machine at connected status */
	public void onChannelConnected() {		
		// Get the object path of the just created HDP channel
		if(remoteDevicePath != null) {
			hdpDataChannelPath = getHDPDataChannelPath(remoteDevicePath);
			System.out.println("HDP channel path: "+hdpDataChannelPath);			
			if(hdpDataChannelPath != null) {				
				// Show HDP main properties
				System.out.println("HDP data channel properties:");
				showHDPDataChannelProperties(hdpDataChannelPath);
				// We need a valid file descriptor prior sending or receiving data bytes between managers and agents. If the file descriptor getted is not
				// valid a -1 value will be returned
				hdpDataChannelFileDescriptor = getHDPDataChannelFileDescriptor(hdpDataChannelPath);
				System.out.println("Number of file descriptor: "+hdpDataChannelFileDescriptor);
				// Notify the connection to the FSM of the Manager
				fsm = x73manager.getFSM();
				fsm.transportActivate();
				System.out.println(fsm.getStringTransportState());
				// Wait until right HDP data frame will be available in our assigned file descriptor
				waitHDPDataFrames(hdpDataChannelFileDescriptor);
			} else 
				System.out.println("HDP channel not available");
		}
	}

	/* HDP data received */
	public void onDataReceived() {
		// Show the received data frame (each byte in hex mode)
		showHDPDataFrame(hdpDataChannelFileDescriptor);
		// Get the number of bytes read (size of the buffer array with VALID data)
		int numberBytes = getSizeHDPDataFrame(hdpDataChannelFileDescriptor);
		System.out.println("Number of bytes received (call from native code): "+numberBytes);
		// Get the input data frame as a whole
		hdpReceivedDataFrame = getHDPDataFrame(hdpDataChannelFileDescriptor);
		System.out.println("Number of bytes received (call from JAVA): "+hdpReceivedDataFrame.length);
		System.out.println("Answering agent data frames");		
		rmp = x73manager.getMessageProcessor();		
		byte[] response = x73manager.getAPDU(hdpReceivedDataFrame);
		sendHDPDataToDevice(hdpDataChannelFileDescriptor,response);
		if(fsm.getStringChannelState().equals("ASSOCIATED - OPERATING")) {					
			if(remoteDeviceType.equals("BloodPressureMonitor")) {
				if((Measurement.hrMeasurement != null)&&(Measurement.sysMeasurement != null)&&(Measurement.diaMeasurement != null)) {
					Double temp_0 = Measurement.hrMeasurement;
					Double temp_1 = Measurement.sysMeasurement;
					Double temp_2 = Measurement.diaMeasurement;
					GUI.finalHrBloodPressureData = temp_0;
					GUI.finalSysBloodPressureData = temp_1;
					GUI.finalDiaBloodPressureData = temp_2;
					GUI.uaalPublisherBloodPressurePulValueTextfield.setText(Double.toString(temp_0));					
					GUI.uaalPublisherBloodPressureSysValueTextfield.setText(Double.toString(temp_1));					
					GUI.uaalPublisherBloodPressureDiaValueTextfield.setText(Double.toString(temp_2));
					GUI.mainPanel.repaint();
				}
			} else {	
				if(Measurement.weightMeasurement != null) {
					Double temp = Measurement.weightMeasurement;					
					GUI.finalMeasuredWeightData = temp;
					GUI.uaalPublisherWeightValueTextfield.setText(Double.toString(shortDecimalNumber(temp)));					
					GUI.uaalPublisherWeightUnitTextfield.setText("kg");	
					GUI.mainPanel.repaint();				
				}				
			}							
		}
	}
	
	/** Shorten number of decimals */
	public double shortDecimalNumber(double d) {
		return Math.round(d*Math.pow(10,2))/Math.pow(10,2); 
	}

	/** HDP channel unavailable. x073 state machine at disconnected status */
	public void onChannelDisconnected() {
		fsm.transportDeactivate();
		System.out.println("HDP channel disconnected");
	}	
	
	/** */
	public void onMessage(String str) {		
		System.out.println(str);		
	}
	
	/** Reset components */
	public void resetComponentsStatus() {
		CONTINUA_DEVICE = null;
		macAddressRemoteDevice = null;
		remoteDevicePath = null;
		hdpDataChannelPath = null;		
		hdpDataChannelFileDescriptor = -1;
		x73manager = null;
	}
	
	/** Close HDP channel and free resources */
	public void exit() {
		if(fsm != null)
			fsm.transportDeactivate();
		resetComponentsStatus();
		closeHDPManager();
	}

	// Load dinamic library (.so in GNU/Linux or .dll in M$ platforms)
	static {
		try {	
			// OS = GNU/Linux
			if(System.getProperty("os.name").equals("Linux")) {
				if(System.getProperty("os.arch").equals("i386")) {
					// Arch = 32 bits					
					System.loadLibrary(libNameWithoutExtension_unix_32);
				} else {
					// Arch = 64 bits					
					System.loadLibrary(libNameWithoutExtension_unix_64);
				}
			}						
		}catch(Exception ex) {
			System.out.println("Unable to load native library. Please, check your path and OSGi manifest settings...");
		}
	}	
}
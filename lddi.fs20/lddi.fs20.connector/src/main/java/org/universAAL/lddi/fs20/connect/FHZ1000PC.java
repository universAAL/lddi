/*
 * Copyright (C) 2009 Stefan Strömberg
 * History:
 * 2008-12-07	Added removeEventListener when closing down serial port.
 * 2008-09-07	Added support for Linux
 * 2007-01-08	Corrected COMMAND_DIM_LOOP and COMMAND_TOGGLE
 * 2007 jan 5	Created

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

package org.universAAL.lddi.fs20.connect;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Logger;

import jd2xx.JD2XX;
import jd2xx.JD2XXEvent;
import jd2xx.JD2XXEventListener;

/**
 * This class encapsulates an interface towards the FHZ1000PC device. It works
 * on both Windows and Linux, with different internal access methods but the
 * external interface is the same.
 *
 * FHZ1000PC is part of the FS20-system which is a home automation system with
 * devices like dimmers, switches, rain sensors and so on. The devices talk with
 * each other via radio messages. The FHZ1000PC is a USB-based interface that
 * allows a computer to send and receive these radio messages and thereby
 * control and monitor the devices. Unfortunatly only a german control program
 * without any external API accompany the device, so I had to write this
 * interface to communicate with it.
 *
 * The FHZ1000PC hardware is originally built for serial communication, but an
 * FTDI-chip (http://www.ftdichip.com) is used internally to convert the serial
 * communication to USB. In Windows The FTDI-drivers are installed when the
 * FHZ1000PC software is installed, and API software for those drivers are
 * provided free by FTDI.
 *
 * Those API:s are C-based, so some interface class is needed to access it from
 * Java. Such an interface is provided by https://jd2xx.dev.java.net. This
 * interface is released with a Berkeley Software Distribution (BSD) License.
 * This interface is implemented in two files which are needed to use this
 * interface: - jd2xx.dll - jd2xx.jar
 *
 * The http://fhz4linux.info/tiki-index.php site provided me with information on
 * the serial protocol that FHZ1000PC uses.
 *
 * I Linux we access the device directly via the serial interface. We only have
 * to persuade Linux that it really is a serial device. This is described on:
 * http://fhz4linux.info/tiki-index.php?page=Driver+installation
 *
 *
 * @author Stefan Strömberg
 *
 */
public class FHZ1000PC {

	/**
	 * An interface to the "raw" FHZ1000 device where you can read and write
	 * data to it.
	 *
	 * @author Stefan Strömberg
	 */
	public interface FHZ1000Device {
		public int write(byte data[]) throws IOException;

		public byte[] read(int length) throws IOException;

		public void activate() throws IOException;

		public void deactivate() throws IOException;
	}

	/**
	 * When running on Windows, we access the USB-device via FTDI-specific
	 * drivers to access the device. This is done via this class.
	 *
	 * @author Stefan Strömberg
	 */
	public class JD2XXFHZ1000Device implements JD2XXEventListener, FHZ1000Device {
		protected JD2XX m_Device;
		protected final String DEVICE_DESCRIPTION = "ELV FHZ 1000 PC";

		public JD2XXFHZ1000Device() throws IOException {
			m_Device = new JD2XX();
			m_Device.openByDescription(DEVICE_DESCRIPTION);

			// Configure the serial parameters of the JD2XX-Chip so it can
			// communicate
			// with the PIC-Processor of the FHZ1000PC
			m_Device.setBaudRate(9600);
			m_Device.setDataCharacteristics(8, JD2XX.STOP_BITS_1, JD2XX.PARITY_NONE);
			m_Device.setFlowControl(JD2XX.FLOW_NONE, 0, 0);
			m_Device.setTimeouts(2000, 2000);

			try {
				m_Device.addEventListener(this);
				m_Device.notifyOnEvent(JD2XX.EVENT_RXCHAR, true);
			} catch (TooManyListenersException a) {
				// NYI - handle this
				throw new IOException();
			}
		}

		public void activate() throws IOException {
			// NYI
		}

		public int write(byte data[]) throws IOException {
			return m_Device.write(data);
		}

		public byte[] read(int length) throws IOException {
			byte[] rd;
			rd = m_Device.read(length);
			return rd;
		}

		public void jd2xxEvent(JD2XXEvent ev) {
			int eventType = ev.getEventType();
			if ((eventType & JD2XX.EVENT_RXCHAR) != 0) {
				deviceEvent();
			}
		}

		public void deactivate() throws IOException {
			m_Device.notifyOnEvent(~0, false);
		}

	}

	/**
	 * When running on Linux, we access the USB-device via generic serial
	 * drivers for FTDI-chips. This is done via this class.
	 *
	 * @author Stefan Strömberg
	 */
	public class FHZ1000SerialDevice implements FHZ1000Device, SerialPortEventListener {
		protected final int MAX_WAIT_TIME_MS = 2000;
		protected Enumeration<CommPortIdentifier> portList;
		protected InputStream inputStream;
		protected OutputStream outputStream;
		protected SerialPort serialPort;
		protected CommPortIdentifier portId = null;
		protected boolean m_CallbackActive = false;

		public void activate() throws IOException {
			m_CallbackActive = true;

		}

		public FHZ1000SerialDevice(String m_ComPort) throws IOException {
			portList = CommPortIdentifier.getPortIdentifiers();

			boolean foundPort = false;

			/* Find the configured serial port */
			while (portList.hasMoreElements()) {
				portId = portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					if (portId.getName().equals(m_ComPort)) {
						// Ok, found
						foundPort = true;
						break;
					}
				}
			}
			if (!foundPort) {
				logger.warning("Failed to find COM Port: " + m_ComPort);
				throw new IOException("Failed to find COM Port: " + m_ComPort);
			}

			/* Try to open the serial port */
			try {
				serialPort = (SerialPort) portId.open("SNAPPort", 2000);
			} catch (PortInUseException e) {
				logger.warning("COM Port " + m_ComPort + " is already in use");
				throw new IOException("COM Port " + m_ComPort + " is already in use");
			}
			try {
				inputStream = serialPort.getInputStream();
				outputStream = serialPort.getOutputStream();
			} catch (IOException e) {
				logger.warning("COM Port " + m_ComPort + " could not be read " + e);
				throw new IOException("COM Port " + m_ComPort + " could not be read " + e);
			}
			try {
				serialPort.addEventListener(this);
			} catch (TooManyListenersException e) {
				logger.warning("COM Port " + m_ComPort + " has too many listeners" + e);
				throw new IOException("COM Port " + m_ComPort + " has too many listeners" + e);
			}
			serialPort.notifyOnDataAvailable(true);

			/* Configure serial port parameters */
			try {
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				serialPort.enableReceiveTimeout(2000);
			} catch (UnsupportedCommOperationException e) {
				logger.warning("Could not set parameters on " + m_ComPort + " " + e);
				throw new IOException("Could not set parameters on " + m_ComPort + " " + e);
			}
		}

		public void serialEvent(SerialPortEvent event) {
			switch (event.getEventType()) {
			case SerialPortEvent.BI:
			case SerialPortEvent.OE:
			case SerialPortEvent.FE:
			case SerialPortEvent.PE:
			case SerialPortEvent.CD:
			case SerialPortEvent.CTS:
			case SerialPortEvent.DSR:
			case SerialPortEvent.RI:
			case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
				break;
			case SerialPortEvent.DATA_AVAILABLE:
				if (m_CallbackActive) {
					deviceEvent();
				}
				break;
			}
		}

		public int write(byte data[]) throws IOException {
			outputStream.write(data);
			return data.length;
		}

		public byte[] read(int length) throws IOException {
			byte result[] = new byte[length];
			byte tempResult[] = new byte[1];
			int read;
			int i = 0;
			int waitCount = 0;
			long start = System.currentTimeMillis(); // starting time

			// The Linux drivers for the FTDI serial chip does not appear to
			// support waiting for more
			// than one character at the time. Therefore we have to read the
			// characters one at the time
			// and then assemble the result.
			while (i < length) {
				read = inputStream.read(tempResult);
				if (read == 1) {
					result[i] = tempResult[0];
					i++;
				} else {
					// If we did not get any character, we wait approximately
					// one character "time"
					// and try again, but max for MAX_WAIT_TIME_MS milliseconds.
					try {
						Thread.sleep(1);
						waitCount++;
						long current = System.currentTimeMillis();
						if (current - start > MAX_WAIT_TIME_MS)
							break;
					} catch (InterruptedException e) {
						// Do Dinada
					}
				}
			}
			read = i;
			logger.info("Read " + Integer.toString(read) + " bytes with " + Integer.toString(waitCount) + " waits");
			if (read == 0)
				return null;

			// If we got less data than we asked for, we have to create a new
			// shorter result array
			// and return that.
			if (read < result.length) {
				byte[] newarray = new byte[read];
				System.arraycopy(result, 0, newarray, 0, read);
				result = newarray;
			}
			return result;
		}

		public void deactivate() throws IOException {
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.close();
			}
		}
	}

	static final byte HELLO_MESSAGE[] = { 0x02, 0x01, 0x1f, 0x60 };
	static final byte FS20INIT[] = { (byte) 0xc9, 0x01, (byte) 0x96 };
	static final byte REQUEST_SERIAL[] = { (byte) 0xc9, 0x01, (byte) 0x84, 0x57, 0x02, (byte) 0x080 };
	static final byte TELEGRAM_TYPE1 = (byte) 0x04;
	static final byte TELEGRAM_TYPE2 = (byte) 0xc9;
	final static int HEADER_LENGTH = 4;
	final static byte START_BYTE = (byte) 0x81;

	public final static byte COMMAND_OFF = 0x00;
	public final static byte COMMAND_DIM1 = 0x01;
	public final static byte COMMAND_ON = 0x11;
	public final static byte COMMAND_DIM_LOOP = 0x15;
	public final static byte COMMAND_DIM_DOWN = 0x14;
	public final static byte COMMAND_DIM_UP = 0x13;
	public final static byte COMMAND_TOGGLE = 0x12;
	public final static byte COMMAND_TIMER_PROG = 0x16;
	public final static byte COMMAND_DELIVERY_STATE = 0x1b;

	private static Logger logger = Logger.getLogger(FHZ1000PC.class.getName());
	protected FHZ1000Device m_Device;
	protected FS20EventListener m_EventListener = null;

	protected String m_PortName = "/dev/ttyUSB1";

	public FHZ1000PC(String portName) throws IOException {
		m_PortName = portName;

		// This part is operating system dependent. In Windows, there are
		// specific drivers available for
		// the FTDI-chips, so we use these drivers to access the FHZ1000PS.
		// In Linux no specific drivers are available, but instead it is
		// possible to get Linux to accept
		// the device as a generic serial port and we access it as a serial
		// port.
		if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
			m_Device = new JD2XXFHZ1000Device();
		} else {
			m_Device = new FHZ1000SerialDevice(m_PortName);
		}

		// Send Hello message
		write(HELLO_MESSAGE, TELEGRAM_TYPE2);
		byte reply[] = read();
		// Do a simple sanity check of the reply
		if ((reply == null) || (reply.length < 2) || (reply[2] != 0x01)) {
			String sReply = "null";
			if (reply != null) {
				sReply = "{";
				for (int i = 0; i < reply.length; i++) {
					sReply += Byte.toString(reply[i]) + ",";
				}
				sReply += "}";
			}
			throw new IOException("Bad reply from device to Hello message: " + sReply);
		}
	}

	/**
	 * Send FS20Init command to the FHZ1000PC device.
	 */
	public void fs20Init() throws IOException {
		write(FS20INIT, TELEGRAM_TYPE1);
	}

	/**
	 * Send a command (for example COMMAND_DIM_DOWN) to a FS20 device (for
	 * example a dimmer) via the FHZ1000PC. The FS20 device is addressed via
	 * house code and button id.
	 *
	 * @param houseCode
	 *            16 bit House Code of the FS20 device in binary form, not the
	 *            1111 2222-form.
	 * @param button
	 *            8 bit button id
	 * @param command
	 *            FS20 Command (for example COMMAND_DIM_DOWN)
	 * @throws IOException
	 */
	public void sendFS20Command(int houseCode, byte button, byte command) throws IOException {
		byte commandData[] = { 2, 1, 1, 0, 0, 0, 0 };
		commandData[3] = (byte) (houseCode >> 8);
		commandData[4] = (byte) (houseCode & 0xff);
		commandData[5] = button;
		commandData[6] = command;
		write(commandData, TELEGRAM_TYPE1);
	}

	public void registerEventListener(FS20EventListener ev) {
		m_EventListener = ev;
		try {
			m_Device.activate();
		} catch (IOException e) {
			logger.info("Could not activate fs20-device");
		}
	}

	public void unregisterEventListener() throws IOException {
		m_Device.deactivate();
		m_EventListener = null;
	}

	public void deviceEvent() {
		try {
			byte message[] = read();
			// System.println("Erhalten");
			// Verify that this seems to be a legitimate message
			// System.println(message.length);
			// System.print("Message is:");
			// for (byte m : message)
			// System.print(" "+m);
			if ((message.length >= 11) && (message[0] == 4) && (message[2] == 1)) {
				// System.println("in if");
				// Ok, extract the data and create an event
				int houseCode = (message[6] << 8) + message[7];
				byte function = message[10];
				byte button = message[8];
				FS20Event event = new FS20Event(houseCode, function, button);
				// System.println("event erzeugt");
				// Notify the event listener
				if (m_EventListener != null) {
					m_EventListener.fs20Event(event);
					// System.println("notified");
				}
			}
		} catch (IOException e) {
			// NYI - handle read faliure
		}
	}

	/**
	 * Sends an string of bytes to the FHZ1000PC device. The method will add the
	 * required header information including checksum.
	 *
	 * @param data
	 *            Array of bytes to send to device
	 * @param telegramType
	 *            Type of message
	 * @return Number of bytes actually written to device
	 * @throws IOException
	 */
	protected int write(byte data[], byte telegramType) throws IOException {
		byte dataPacket[] = new byte[data.length + HEADER_LENGTH];
		dataPacket[0] = START_BYTE;
		dataPacket[1] = (byte) (data.length + 2);
		dataPacket[2] = telegramType;

		int checksum = 0;
		for (int i = 0; i < data.length; i++) {
			checksum += data[i];
			dataPacket[i + HEADER_LENGTH] = data[i];
		}
		dataPacket[3] = (byte) checksum;

		return m_Device.write(dataPacket);
	}

	/**
	 * Reads a complete message from the FHZ1000PC device and returns the
	 * payload of the message including telegram type and checksum. The actual
	 * message begins at index 2 in the array, index 0 is the telegram type and
	 * index 1 is the checksum. If no data was available before timeout an empty
	 * array is returned.
	 *
	 * @return bytes read including telegram type and checksum
	 * @throws IOException
	 */
	protected byte[] read() throws IOException {
		// First read start byte and message length
		byte[] header = m_Device.read(2);
		byte[] rd;
		// If we got a correct start byte and a length - go on
		if ((header != null) && (header.length == 2) && (header[0] == START_BYTE)) {
			// Read the actual message
			rd = m_Device.read(header[1]);
		} else {
			rd = new byte[0];
		}
		return rd;
	}

	/**
	 * Converts an integer address to the "base 4 + 1" format used by FS20 to
	 * denote the addresses for buttons and devices.
	 *
	 * @param value
	 *            The binary value to convert
	 * @param bits
	 *            Number of bits to convert, usually 8 or 16.
	 * @return the address in the "11224411"-format.
	 */
	public static String binFS20ByteToString(int value, int bits) {
		String result = "";
		for (int i = 0; i < bits; i += 2) {
			int bitPair = (value >> (bits - 2 - i)) & 0x03;
			result += (char) (bitPair + '1');
		}
		return result;
	}

	/**
	 * Converts an address string in the "base 4 + 1" format used by FS20 to a
	 * binary byte format actually used on the protocol.
	 *
	 * @param value
	 *            The address string to convert
	 * @return the corresponding binary value
	 */
	public static int StringFS20ToInt(String value) {
		int result = 0;
		for (int i = 0; i < value.length(); i++) {
			result = result << 2;
			result += (value.charAt(i) - '1') & 0x03;
		}
		return result;
	}
}

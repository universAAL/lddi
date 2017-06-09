/*
	 Copyright (c) 2012 Itsaso Aranburu <itsasoaranburu@gmail.com>, Signove Tecnologia S/A

	 Modified by Patrick Stern, AIT Austrian Institute of Technology GmbH
	 http://www.ait.ac.at
 	 2012-09-26

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

package org.universAAL.lddi.exporter.x73;

import java.io.*;
import org.freedesktop.dbus.*;
import org.freedesktop.dbus.exceptions.DBusException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import com.signove.health.*;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class MyAgent implements agent {
	private DBusConnection conn = null;
	private ISO11073ContextProvider contextProvider = null;

	// unit of measurement
	private String unit = null;
	private String unitCode = null;
	// compoundBasicNuObservedValue
	private String measuredValue_18949 = null;
	private String measuredValue_18950 = null;
	private String measuredValue_18951 = null;
	// basicNuObservedValue
	private String basicNuObservedValue = null;
	// absoluteTimeStamp
	private int century = -1;
	private int year = -1;
	private int month = -1;
	private int day = -1;
	private int hour = -1;
	private int minute = -1;
	private int second = -1;
	private int sec_fractions = -1;
	// systemId
	private String systemId = null;
	// systemTypeSpecList
	private String systemTypeSpecList = null;
	// systemModel
	private String manufacturer = null;
	private String modelNumber = null;

	public MyAgent(DBusConnection t_conn, ISO11073ContextProvider t_contextProvider) {
		conn = t_conn;
	}

	private void writeFile(String fname, String data) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(fname);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(data);
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this! ... for XPath
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}

	public boolean isRemote() {
		return false;
	}

	public void Connected(String dev, String addr) {
		System.out.println("Connected dev " + dev);
		System.out.println("Connect addr " + addr);
	}

	public void Associated(String dev, String data) {
		System.out.println("Associated dev " + dev);
		// System.out.println("Associated data " + data);
		writeFile("Associated.xml", data);
		Document xmlData = null;

		device remoteObject;
		try {
			remoteObject = (device) conn.getRemoteObject("com.signove.health", dev, device.class);
			remoteObject.RequestDeviceAttributes();
		} catch (DBusException e) {
			System.out.println("Exception while RequestDeviceAttributes");
		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);

		return nValue.getNodeValue();
	}

	public void MeasurementData(String dev, String data) {
		System.out.println("MeasurementData dev " + dev);
		// System.out.println("MeasurementData data " + data);
		writeFile("MeasurementData.xml", data);

		Document xmlData = null;
		try {
			xmlData = loadXMLFromString(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (xmlData != null) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			try {
				// Type
				XPathExpression expr = xpath.compile("//entries/entry[1]/simple/name/text()");
				NodeList nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				String value_type = nodes.item(0).getNodeValue();
				// two different ways to find the intended nodes. type with the
				// first entry - value with fixed name.

				if (value_type.compareTo("Simple-Nu-Observed-Value") == 0
						|| value_type.compareTo("Basic-Nu-Observed-Value") == 0) {
					// Measured Value
					expr = xpath.compile(
							"//entry/simple[name='Simple-Nu-Observed-Value' or name='Basic-Nu-Observed-Value']/value/text()");
					nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
					basicNuObservedValue = nodes.item(0).getNodeValue();
				} else {
					// compoundBasicNuObservedValue
				}

				// Unit
				expr = xpath.compile(
						"//entry[simple/name='Simple-Nu-Observed-Value' or simple/name='Basic-Nu-Observed-Value']/meta-data/meta[@name='unit']/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				unit = nodes.item(0).getNodeValue();
				// UnitCode
				expr = xpath.compile("//entries/entry[1]/meta-data/meta[@name='unit-code']/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				unitCode = nodes.item(0).getNodeValue();

				// Timestamp
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='century']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				century = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='year']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				year = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='month']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				month = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='day']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				day = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='hour']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				hour = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='minute']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				minute = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='second']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				second = Integer.parseInt(nodes.item(0).getNodeValue());
				expr = xpath.compile(
						"//entries/entry/compound[name='Absolute-Time-Stamp']/entries/entry/simple[name='sec_fractions']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				sec_fractions = Integer.parseInt(nodes.item(0).getNodeValue());

				// for (int i = 0; i < nodes.getLength(); i++) {
				// System.out.println("Value: " + nodes.item(i).getNodeValue());
				// }
			} catch (XPathExpressionException e) {
				System.out.println("XPathExpressionException - MeasurementData.xml");
			}
		}
	}

	public void DeviceAttributes(String dev, String data) {
		System.out.println("DeviceAttributes dev " + dev);
		// System.out.println("DeviceAttributes data " + data);
		writeFile("DeviceAttributes.xml", data);

		Document xmlData = null;
		try {
			xmlData = loadXMLFromString(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (xmlData != null) {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			try {
				// systemId
				XPathExpression expr = xpath.compile("//entries/entry/simple[name='System-Id']/value/text()");
				NodeList nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				systemId = nodes.item(0).getNodeValue();

				// systemTypeSpecList
				expr = xpath.compile(
						"//entries/entry/compound[name='System-Type-Spec-List']/entries/entry/compound/entries/entry/simple[name='type']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				systemTypeSpecList = nodes.item(0).getNodeValue();

				// systemModel
				expr = xpath.compile("//entry/simple[name='manufacturer']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				manufacturer = nodes.item(0).getNodeValue();
				expr = xpath.compile("//entry/simple[name='model-number']/value/text()");
				nodes = (NodeList) expr.evaluate(xmlData, XPathConstants.NODESET);
				modelNumber = nodes.item(0).getNodeValue();

			} catch (XPathExpressionException e) {
				System.out.println("XPathExpressionException - DeviceAttributes.xml");
			}
		}
	}

	public void Disassociated(String dev) {
		System.out.println("Disassociated dev " + dev);

		/*
		 * Debug Output:
		 * 
		 * System.out.println("Extracted data: "); System.out.println("unit: " +
		 * unit + " - " +unitCode); System.out.println("compoundObservedValue: "
		 * + measuredValue_18949 + measuredValue_18950 + measuredValue_18951);
		 * System.out.println("basicNuObservedValue: " + basicNuObservedValue);
		 * System.out.println("TimeStamp: " + century + year + "-" + month + "-"
		 * + day); System.out.println("SystemID: " + systemId);
		 * System.out.println("systemTypeSpecList: " + systemTypeSpecList);
		 * System.out.println("System-Model: " + manufacturer + " - " +
		 * modelNumber);
		 */

		// depending on the device model different methods with different
		// parameters have to be called
		if (modelNumber.compareTo("UC-321PBT-C") == 0) {
			contextProvider.publishWeight(dev, basicNuObservedValue, unitCode, century, year, month, day, hour, minute,
					second, sec_fractions, manufacturer, modelNumber, systemId, systemTypeSpecList);
			System.out.println("measureWeight finished");
		}
		// the blood pressure monitor measures only the pulse
		else if (modelNumber.compareTo("UA-767PBT-C") == 0) {
			contextProvider.publishPulse(dev, basicNuObservedValue, unitCode, century, year, month, day, hour, minute,
					second, sec_fractions, manufacturer, modelNumber, systemId, systemTypeSpecList);
			System.out.println("measurePulse finished");
		} else
			System.out.println("This device is not supported.");
	}

	public void Disconnected(String dev) {
		System.out.println("Disconnected " + dev);
	}

	public void setContextProvider(ISO11073ContextProvider t_contextProvider) {
		this.contextProvider = t_contextProvider;
		System.out.println("contextProvider set in MyAgent " + contextProvider.toString());
	}
}
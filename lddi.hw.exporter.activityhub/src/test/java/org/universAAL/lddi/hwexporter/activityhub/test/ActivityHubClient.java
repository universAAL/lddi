package org.universAAL.lddi.hwexporter.activityhub.test;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.WindowConstants;

import org.universAAL.lddi.iso11073.activityhub.devicecategory.ActivityHubDeviceCategoryUtil.ActivityHubDeviceCategory;
import org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubFactory;
import org.universAAL.lddi.iso11073.activityhub.devicemodel.ActivityHubSensor;

/**
 * This is the connector class to the uAAL related ontology classes, mainly MyActivityHubServiceConsumer.java
 * This class knows nothing about Ontologies!
 * It could be part of a Java application.
 * Here, it is a single class application with a simple GUI.
 * 2 Buttons are provided for 'getAllSensors' and 'getSensorInfo'
 * Furthermore, 2 textAreas for incoming context events and log messages
 * 
 * @author Thomas Fuxreiter (foex@gmx.at)
 */
public class ActivityHubClient extends javax.swing.JPanel {

	private static final long serialVersionUID = 885696167607678920L;

	/** 
	 * key is the instanceURI of the device, from the ontology, but is just a String
	 * value is an ActivityHubSensor object from the iso11073 library; not from the ontology model!
	 */
	private Map<String,ActivityHubSensor> activityHubSensors = new LinkedHashMap<String,ActivityHubSensor>();
	
	private ArtifactIntegrationTest myParent;
	private static JFrame frame;
	static private JList jList1;
	private static JTextArea deviceArea = new JTextArea();
	private static JScrollPane jsp1 = new JScrollPane(deviceArea);
	private static JTextArea contextArea = new JTextArea();
	private static JScrollPane jsp2 = new JScrollPane(contextArea);
	private static JTextArea logArea = new JTextArea();
	private static JScrollPane jsp3 = new JScrollPane(logArea);
	private static JLabel label1;
	private static JLabel label2;
	private static JLabel label3;
	private static JLabel label4;
	private JButton infoButton;
	private JButton sensorButton;
	private AbstractAction Info1;
	private AbstractAction Info2;

	/**
	 * Constructor
	 * @param link to Activator
	 */
	public ActivityHubClient(ArtifactIntegrationTest parent) {
		super();
		this.myParent = parent; 
		initGUI();
		start();
	}

	private void initGUI() {
		try {
			setPreferredSize(new Dimension(1000, 800));
			this.setLayout(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// create the GUI 
	public void start(){

		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // doesn't stop the whole framework on close
		frame.pack();
		frame.setSize(1000, 800);
		frame.setVisible(true);
		frame.getContentPane().setLayout(null);
		frame.setTitle("ActivityHub Client Example");
		initComponents();
		frame.setEnabled(true);
	}
	
	private void initComponents() {
		ListModel jListModel = new DefaultComboBoxModel(new Object[] { "Init..." });
		{
			label1 = new JLabel("Devices");
			frame.getContentPane().add(label1);
			label1.setBounds(20, 20, 200, 20);
			
			jList1 = new JList();
			frame.getContentPane().add(jList1);
			jList1.setModel(jListModel);
			jList1.setBounds(20, 40, 680, 100);
		}

		{
			sensorButton = new JButton();
			frame.getContentPane().add(sensorButton);
			sensorButton.setText("Sensors");
			sensorButton.setBounds(720, 40, 250, 35);
			sensorButton.setAction(getSensors());
		}

		{
			infoButton = new JButton();
			frame.getContentPane().add(infoButton);
			infoButton.setText("Device Info");
			infoButton.setBounds(720, 105, 250, 35);
			infoButton.setAction(getInfo());
		}

		{
			label2 = new JLabel("Device Info");
			frame.getContentPane().add(label2);
			label2.setBounds(20, 150, 200, 20);

			frame.getContentPane().add(jsp1);
			jsp1.setBounds(20, 170, 900, 150);
		}

		{
			label3 = new JLabel("Context Events");
			frame.getContentPane().add(label3);
			label3.setBounds(20, 330, 200, 20);
			
			frame.getContentPane().add(jsp2);
			jsp2.setBounds(20, 350, 900, 180);
		}

		{
			label4 = new JLabel("Log");
			frame.getContentPane().add(label4);
			label4.setBounds(20, 540, 200, 20);

			frame.getContentPane().add(jsp3);
			jsp3.setBounds(20, 560, 900, 180);
		}
	}
	
	/**
	 * AbstractAction for button getSensors
	 * initiates service call to parent
	 * @return
	 */
	private AbstractAction getSensors() {
		if(Info1 == null) {
			Info1 = new AbstractAction("get all ActivityHubSensors", null) {
				public void actionPerformed(ActionEvent evt) {
					myParent.serviceConsumer.getSensors();
				}
			};
		}
		return Info1;
	}
	
	/**
	 * AbstractAction for button getInfo
	 * initiates service call to parent
	 * @return
	 */
	private AbstractAction getInfo() {
		if(Info2 == null) {

			Info2 = new AbstractAction("get sensor details", null) {
				
				public void actionPerformed(ActionEvent evt) {
					
					int type = 0;
					if (activityHubSensors.get((String)jList1.getSelectedValue()) != null) {
						type = activityHubSensors.get((String)jList1.getSelectedValue()).getDeviceCategory().getTypeCode();
					}
					
					myParent.serviceConsumer.getDeviceInfo((String)jList1.getSelectedValue(),type
							);
				}
			};
		}
		return Info2;
	}
	

	/** 
	 * create new activityhubsensors from iso11073 library, not from ontology!
	 * store them in map this.activityHubSensors
	 * 
	 * @param resourceURI instanceId
	 * @param sensorType must be identical from the ontology model to the iso11073 library model !!
	 */
	public void addActivityHubSensor(String resourceURI, int sensorType) {
		String deviceId = resourceURI.substring(resourceURI.indexOf('#')+1); // e.g. controlledActivityHubDevice1/1/1
		ActivityHubSensor ahs = (ActivityHubSensor) ActivityHubFactory.createInstance(
				ActivityHubDeviceCategory.get(sensorType), null, 
				deviceId, null);
		
		this.activityHubSensors.put(resourceURI, ahs);
		
		addTextToLogArea("Added new ActivityHubSensor. Category: " + ahs.getDeviceCategory().toString() +
				" deviceId: " + ahs.getDeviceId());
	}

	/**
	 * just display the sensors that are already in the datastore this.activityHubSensors
	 * in the first list on the GUI
	 * Should be called after all sensors are stored through multiple calls of addActivityHubSensor
	 */
	public void showSensorList() {
		jList1.setListData(this.activityHubSensors.keySet().toArray());
	}

	
	/**
	 * send all text lines from the given array to addTextToDeviceArea
	 * @param multiple text lines for 1 device info response  
	 */
	public void showDeviceInfo(String[] listData) {
		for (String line : listData) {
			addTextToDeviceArea(line);
		}
	}

	/**
	 * Helper class that displays the given text on the deviceArea on the GUI
	 * adding a line break
	 * and set focus always to the bottom of the textArea
	 * @param text
	 */
	public void addTextToDeviceArea(String text) {
		deviceArea.setText(deviceArea.getText() + text + "\n");
	    deviceArea.setCaretPosition(deviceArea.getDocument().getLength());
	}
	
	
	/**
	 * send all text lines from the given array to addTextToContextArea
	 * @param multiple text lines for 1 context event  
	 */
	public void showContextEvent(String[] listData) {
		for (String line : listData) {
			addTextToContextArea(line);
		}
	}

	/**
	 * Helper class that displays the given text on the contextArea on the GUI
	 * adding a line break
	 * and set focus always to the bottom of the textArea
	 * @param text
	 */
	public void addTextToContextArea(String text) {
		contextArea.setText(contextArea.getText() + text + "\n");
	    contextArea.setCaretPosition(contextArea.getDocument().getLength());
	}
	
	/**
	 * Helper class that displays the given text on the logArea on the GUI
	 * adding a line break
	 * @param text
	 */
	public void addTextToLogArea(String text) {
		logArea.setText(logArea.getText() + text + "\n");
	}

	
	/**
	 * 
	 */
	public void deleteGui() {
		frame.dispose();
	}

}

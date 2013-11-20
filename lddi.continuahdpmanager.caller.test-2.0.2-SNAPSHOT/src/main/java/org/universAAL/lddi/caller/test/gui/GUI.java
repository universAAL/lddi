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
 * Continua Manager caller test GUI
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package 
package org.universAAL.lddi.caller.test.gui;

// Imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.osgi.framework.BundleContext;
import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.service.DefaultServiceCaller;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.ontology.continua.ContinuaHealthManagerOntology;
import org.universAAL.ontology.continua.ContinuaHealthDevice;
import org.universAAL.ontology.continua.ContinuaHealthManager;

//Main class
public class GUI extends JDialog implements ActionListener {
	
// Attributes
	
	/** Serializable object */
	private static final long serialVersionUID = 1L;
	
	/** Main and secondary panels */
	public static JPanel mainPanel = null;
	private JPanel radiobuttonPanel = null;
	private JPanel mainPublisherPanel = null;
	
	/** Components */
	private JRadioButton realMeasurementButton = null;		
	private JRadioButton simulatedMeasurementButton = null;
	private ButtonGroup radioButtonsGroup = null;
	private JButton bloodPressureButton = null;
	private JButton weighingScaleButton = null;
	private JLabel uaalLogoLabel = null;	
	public static JTextField uaalPublisherWeightValueTextfield = null;	
	public static JTextField uaalPublisherWeightUnitTextfield = null;	
	public static JTextField uaalPublisherBloodPressureSysValueTextfield = null;	
	public static JTextField uaalPublisherBloodPressureDiaValueTextfield = null;	
	public static JTextField uaalPublisherBloodPressurePulValueTextfield = null;
	
	/** Constants */
	private static final String weighingScaleImage = "ws.png";
	private static final String bloodPressureImage = "bp.png";
	private static final String uaalLogoImage = "uaal_logo_resized.jpg";
	
	/** Bundle context object */
	private BundleContext ctx = null;
	private ModuleContext mcx = null;
	
	/** Publisher object to send events */
	private Publisher uaalX73Publisher = null;	
	
	/** Expected remote device parameters */
	private String remoteDeviceType = null;
	private String remoteMacAddress = null;
	
	/** Real or simulated measurement */
	public static boolean realMeasurement = false;
	
	/** Service caller */
	private static ServiceCaller caller = null;
	
	// Constructor	
	public GUI(BundleContext context,ModuleContext mc) {
		ctx = context;
		mcx = mc;
		init();		
	}
	
	// Methods
	/** Create the main frame */
	public void init() {		
		// Main dialog
		setResizable(false);
		setBounds(100,100,450,325);
		setTitle("uAAL Continua manager publisher");
		getContentPane().setLayout(new BorderLayout());
		// Main panel (content pane)
		mainPanel = createJPanel();
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		// Create and add components
		createComponents();	
		// Show		
		setVisible(true);
		// Publish data to uAAL context bus. Ensure that values are not NULL
		uaalX73Publisher = new Publisher(ctx);		
	}	
	
	/** Create components */
	public void createComponents() {
		// Label (uaal image icon)
		uaalLogoLabel = new JLabel("");
		uaalLogoLabel.setIcon(new ImageIcon(ctx.getBundle().getResource(uaalLogoImage)));
		uaalLogoLabel.setBounds(75,10,300,81);
		mainPanel.add(uaalLogoLabel);
		// Radio buttons group
		radiobuttonPanel = new JPanel();	
		radiobuttonPanel.setLayout(new GridLayout(1,0));
		radiobuttonPanel.setBounds(20,100,400,50);		
		radioButtonsGroup = new ButtonGroup();
		realMeasurementButton = createJRadioButton("Real measurement");	
		realMeasurementButton.setToolTipText("Continua devices should be paired first");
		simulatedMeasurementButton = createJRadioButton("Simulated measurement");
		simulatedMeasurementButton.setToolTipText("Random values will be published to uAAL context bus (Continua devices not required)");
		radiobuttonPanel.add(realMeasurementButton);
		radiobuttonPanel.add(simulatedMeasurementButton);
		mainPanel.add(radiobuttonPanel);
		// Buttons
		bloodPressureButton = createJButton(bloodPressureImage,214,160,218,145,"bloodPressure");		
		mainPanel.add(bloodPressureButton);		
		weighingScaleButton = createJButton(weighingScaleImage,18,154,178,157,"weighingScale");			
		mainPanel.add(weighingScaleButton);		
	}	
	
	/** Create JPanel */
	public JPanel createJPanel() {
		JPanel output = null;
		output = new JPanel();	
		output.setLayout(null);
		output.setBorder(new EmptyBorder(5,5,5,5));	
		output.setBackground(Color.WHITE);
		return output;
	}
	
	/** Create JButtons */
	public JButton createJButton(String image,int x,int y,int weight,int height,String name) {
		JButton output = null;
		output = new JButton("");
		output.setBackground(Color.WHITE);
		if(image != null)
			output.setIcon(new ImageIcon(ctx.getBundle().getResource(image)));
		output.setBounds(x,y,weight,height);
		output.setActionCommand(name);
		output.addActionListener(this);
		return output;
	}	
	
	/** Create JRadioButtons */
	public JRadioButton createJRadioButton(String name) {
		JRadioButton output = null;
		output = new JRadioButton(name,false);
		output.setActionCommand(name);
		output.setBackground(Color.WHITE);
		output.addActionListener(this);
		radioButtonsGroup.add(output);
		return output;
	}
    
	/** Close GUI frame */
    public void closeGUI(){	     	
    	dispose();		
    }

    /** Action listener for radio buttons and jbuttons */
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("bloodPressure")) {
			// Blood pressure device
			if(realMeasurementButton.isSelected()) {
				new Thread(){
					public void run() {
						realMeasurement = true;
						remoteDeviceType = "BloodPressureMonitor";
						remoteMacAddress = "00:09:1F:80:04:D6";
						weighingScaleButton.setEnabled(false);
						simulatedMeasurementButton.setEnabled(false);
						System.out.println("Continua Health Manager service request...");
						caller = new DefaultServiceCaller(mcx);
						ServiceRequest addReq = new ServiceRequest(new ContinuaHealthManager(null),null);
						addReq.addAddEffect(new String[] { ContinuaHealthManager.PROP_HAS_CONTINUA_DEVICE },
								//TODO Put here the right MAC address of your Continua device
								new ContinuaHealthDevice(ContinuaHealthManagerOntology.NAMESPACE + "new Continua Sink",remoteMacAddress,remoteDeviceType));
						System.out.println(addReq.toString());
						caller.call(addReq);
					}
				}.start();
			} else if(simulatedMeasurementButton.isSelected()) {
				realMeasurement = false;
				if(uaalX73Publisher != null)
					uaalX73Publisher.publishBloodPressureEvent(getRandomValue(90,119),getRandomValue(60,79),getRandomValue(49,198));				
			}	
		} else if(e.getActionCommand().equals("weighingScale")) {
			// Weighing scale device
			if(realMeasurementButton.isSelected()) {
				new Thread(){
					public void run() {
						realMeasurement = true;
						bloodPressureButton.setEnabled(false);
						simulatedMeasurementButton.setEnabled(false);
						remoteDeviceType = "WeightingScale";
						remoteMacAddress = "00:09:1F:80:0A:E0";
						System.out.println("Continua Health Manager service request...");
						caller = new DefaultServiceCaller(mcx);						
						ServiceRequest addReq = new ServiceRequest(new ContinuaHealthManager(null),null);						
						addReq.addAddEffect(new String[] { ContinuaHealthManager.PROP_HAS_CONTINUA_DEVICE },
								//TODO Put here the right MAC address of your Continua device
								new ContinuaHealthDevice(ContinuaHealthManagerOntology.NAMESPACE + "new Continua Sink",remoteMacAddress,remoteDeviceType));
						System.out.println(addReq.toString());
						caller.call(addReq);						
					}
				}.start();				
			} else if(simulatedMeasurementButton.isSelected()) {
				realMeasurement = false;
				if(uaalX73Publisher != null)
					uaalX73Publisher.publishWeightEvent(getRandomValue(50,110));				
			}		
		}
	}
	
	/** Create JLabel */
	public JLabel createJLabel(String name,int x,int y,int weight,int height,int fontType,int fontSize) {
		JLabel output = null;
		output = new JLabel(name);	
		output.setBounds(x,y,weight,height);
		output.setFont(new Font("Courier",fontType,fontSize));	
		output.setHorizontalTextPosition(SwingConstants.CENTER);
		return output;
	}
	
	/** Create JTextfield */
	public JTextField createJTextfield(int x,int y,int weight,int height,int fontType,int fontSize) {
		JTextField output = null;
		output = new JTextField(10);	
		output.setBounds(x,y,weight,height);
		output.setEditable(false);
		output.setColumns(10);
		output.setHorizontalAlignment(JTextField.CENTER);
		output.setFont(new Font("Courier",fontType,fontSize));		
		return output;
	}
	
	/** Add components to panel */
	public void addJLabelComponent(JComponent component) {
		if(component != null)
			mainPublisherPanel.add(component);
	}
	
	/** Reset components */
	public void resetComponentsStatus() {		
		uaalPublisherWeightValueTextfield = null;		
		uaalPublisherWeightUnitTextfield = null;		
		uaalPublisherBloodPressureSysValueTextfield = null;		
		uaalPublisherBloodPressureDiaValueTextfield = null;		
		uaalPublisherBloodPressurePulValueTextfield = null;
		remoteDeviceType = null;
		remoteMacAddress = null;
		realMeasurement = false;		
	}
	
	/** Shorten number of decimals */
	public double shortDecimalNumber(double d) {
		return Math.round(d*Math.pow(10,2))/Math.pow(10,2); 
	}
	
	/** Get randomized value */
	public int getRandomValue(int min,int max) {
		int output = -1;
		Random r = new Random();
		int minValue = min;
		int maxValue = max;
		output = r.nextInt(maxValue - minValue + 1) + minValue;
		return output;
	}	
	
	public void switchOffManagerService() {
		if((realMeasurement)&&(remoteDeviceType != null)&&(remoteMacAddress != null)) {			
			System.out.println("Continua Health Manager service request...");					
			caller = new DefaultServiceCaller(mcx);						
			ServiceRequest addReq = new ServiceRequest(new ContinuaHealthManager(null),null);						
			addReq.addChangeEffect(new String[] { ContinuaHealthManager.PROP_HAS_NOT_CONTINUA_DEVICE },					
					new ContinuaHealthDevice(ContinuaHealthManagerOntology.NAMESPACE + "new Continua Sink",remoteMacAddress,remoteDeviceType));
			System.out.println(addReq.toString());
			caller.call(addReq);			
		}
		resetComponentsStatus();
	}
	
	/** Exit all */
	public void stopGUI() {		
		if((realMeasurement)&&(remoteDeviceType != null)&&(remoteMacAddress != null)) {			
			switchOffManagerService();
//			System.out.println("Continua Health Manager service request...");					
//			caller = new DefaultServiceCaller(mcx);						
//			ServiceRequest addReq = new ServiceRequest(new ContinuaHealthManager(null),null);						
//			addReq.addChangeEffect(new String[] { ContinuaHealthManager.PROP_HAS_NOT_CONTINUA_DEVICE },					
//					new ContinuaHealthDevice(ContinuaHealthManagerOntology.NAMESPACE + "new Continua Sink",remoteMacAddress,remoteDeviceType));
//			System.out.println(addReq.toString());
//			caller.call(addReq);			
		}			
		resetComponentsStatus();		
		dispose();		
	}
}
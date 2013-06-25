/**
 * x073 Continua agent publisher (agent events will be published over uAAL bus)
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package 
package org.universAAL.continua.manager.gui;

// Imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.universAAL.continua.manager.publisher.Publisher;
import org.universAAL.continua.manager.publisher.hdpManager;

//Main class
public class GUI extends JDialog implements ActionListener {
	
// Attributes
	
	/** Serializable object */
	private static final long serialVersionUID = 1L;	
	
	/** uAAL publisher window */
	private JDialog uaalPublisher = null;
	
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
	private JLabel uaalPublisherMainLabel = null;
	private JLabel uaalPublisherLogoLabel = null;	
	private JLabel uaalPublisherWeightValueLabel = null;
	public static JTextField uaalPublisherWeightValueTextfield = null;
	private JLabel uaalPublisherWeightUnitLabel = null;
	public static JTextField uaalPublisherWeightUnitTextfield = null;
	private JLabel uaalPublisherBloodPressureSysValueLabel = null;
	public static JTextField uaalPublisherBloodPressureSysValueTextfield = null;
	private JLabel uaalPublisherBloodPressureDiaValueLabel = null;
	public static JTextField uaalPublisherBloodPressureDiaValueTextfield = null;
	private JLabel uaalPublisherBloodPressurePulValueLabel = null;
	public static JTextField uaalPublisherBloodPressurePulValueTextfield = null;
	private JButton uaalPublisherButton = null;
	
	/** Constants */
	private static final String weighingScaleImage = "ws.png";
	private static final String bloodPressureImage = "bp.png";
	private static final String uaalLogoImage = "uaal_logo_resized.jpg";
	
	/** Bundle context object */
	private BundleContext ctx = null;
	
	/** Publisher object to send events */
	private Publisher uaalX73Publisher = null;
	
	/** HDP manager object */
	private hdpManager manager = null;
	
	/** Expected remote device type */
	private String remoteDeviceType = null;
	
	/** Final data to be published */
	public static double finalMeasuredWeightData = -1.0;
	public static double finalSysBloodPressureData = -1;
	public static double finalDiaBloodPressureData = -1;
	public static double finalHrBloodPressureData = -1;
	
	/** Real or simulated measurement */
	public static boolean realMeasurement = false;
	
	// Constructor	
	public GUI(BundleContext context) {
		ctx = context;
		init();		
	}
	
	// Methods
	/** Create the main frame */
	public void init() {		
		// Main dialog
		setResizable(false);
		setBounds(100,100,450,325);
		setTitle("uAAL Continua manager client");
		getContentPane().setLayout(new BorderLayout());
		// Main panel (content pane)
		mainPanel = createJPanel();
		getContentPane().add(mainPanel,BorderLayout.CENTER);
		// Create and add components
		createComponents();	
		// Show		
		setVisible(true);
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
				realMeasurement = true;
				createUaalPublisher("bloodPressure","real");				
			} else if(simulatedMeasurementButton.isSelected()) {
				realMeasurement = false;
				createUaalPublisher("bloodPressure","simulated");				
			}	
		} else if(e.getActionCommand().equals("weighingScale")) {
			// Weighing scale device
			if(realMeasurementButton.isSelected()) {
				realMeasurement = true;
				createUaalPublisher("weighingScale","real");				
			} else if(simulatedMeasurementButton.isSelected()) {
				realMeasurement = false;
				createUaalPublisher("weighingScale","simulated");				
			}
		} else if(e.getActionCommand().equals("publishData")) {
			// Publish data to uAAL context bus. Ensure that values are not NULL
			uaalX73Publisher = new Publisher(ctx);			
			if(realMeasurement) {				
				// Real values
				if(remoteDeviceType.equals("WeightingScale")) {					
					if(finalMeasuredWeightData != -1.0) {						
						double temp_1 = shortDecimalNumber(finalMeasuredWeightData)*1000;
						int temp = (int) temp_1;						
						uaalX73Publisher.publishWeightEvent(temp);	
						//stopPublisherGUI();
					}	
				} else {
					if((finalDiaBloodPressureData != -1)&&(finalHrBloodPressureData != -1)&&(finalSysBloodPressureData != -1)) {						
						int temp_0 = (int) finalSysBloodPressureData;
						int temp_1 = (int) finalDiaBloodPressureData;
						int temp_2 = (int) finalHrBloodPressureData;						
						uaalX73Publisher.publishBloodPressureEvent(temp_0,temp_1,temp_2);	
						//stopPublisherGUI();
					}
				}				
			} else {				
				// Random values
				if(remoteDeviceType.equals("WeightingScale")) {
					uaalX73Publisher.publishWeightEvent(Integer.parseInt(uaalPublisherWeightValueTextfield.getText()));
					//stopPublisherGUI();
				} else {					
					uaalX73Publisher.publishBloodPressureEvent(Integer.parseInt(uaalPublisherBloodPressureSysValueTextfield.getText()),
							   Integer.parseInt(uaalPublisherBloodPressureDiaValueTextfield.getText()),
							   Integer.parseInt(uaalPublisherBloodPressurePulValueTextfield.getText()));
					//stopPublisherGUI();
				}
			}			
		}
	}    
	
	/** Create and show uAAL publisher frame */
	public void createUaalPublisher(String device,String type) {
		// Hide main GUI
		setVisible(false);
		// Create dialog frame
		uaalPublisher = new JDialog(this,"uAAL publisher",true);
		uaalPublisher.setResizable(false);
		uaalPublisher.setBounds(100,100,650,325);
		uaalPublisher.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);			
		uaalPublisher.addWindowListener(new WindowAdapter() {				
		    public void windowClosing(WindowEvent e) {
		    	//@TODO do something
		    	setVisible(true);
		    	stopPublisherGUI();
		    }
		});		
		// Main panel
		mainPublisherPanel = createJPanel();
		uaalPublisher.getContentPane().add(mainPublisherPanel,BorderLayout.CENTER);
		// Main text label 		
		uaalPublisherMainLabel = createJLabel("",200,5,350,50,Font.BOLD,24);					
		// Image icon		
		uaalPublisherLogoLabel = new JLabel("");		
		uaalPublisherLogoLabel.setBounds(25,75,218,145);		
		// Check Continua agent selected
		if(device.equals("bloodPressure")) {
			uaalPublisherMainLabel.setText("Blood pressure monitor");		
			uaalPublisherLogoLabel.setIcon(new ImageIcon(ctx.getBundle().getResource(bloodPressureImage)));			
			uaalPublisherBloodPressureSysValueLabel = createJLabel("SYS(mmHg)",250,100,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressureSysValueTextfield = createJTextfield(400,100,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressureDiaValueLabel = createJLabel("DIA(mmHg)",250,150,100,50,Font.PLAIN,16);				
			uaalPublisherBloodPressureDiaValueTextfield = createJTextfield(400,150,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressurePulValueLabel = createJLabel("     BPM",250,200,100,50,Font.PLAIN,16);
			uaalPublisherBloodPressurePulValueTextfield = createJTextfield(400,200,100,50,Font.PLAIN,16);
		} else {
			uaalPublisherMainLabel.setText("Weighing scale");
			uaalPublisherLogoLabel.setIcon(new ImageIcon(ctx.getBundle().getResource(weighingScaleImage)));
			uaalPublisherWeightValueLabel = createJLabel("Weight value",250,100,150,50,Font.PLAIN,16);			
			uaalPublisherWeightValueTextfield = createJTextfield(400,100,100,50,Font.PLAIN,16);			
			uaalPublisherWeightUnitLabel = createJLabel(" Weight unit",250,150,150,50,Font.PLAIN,16);				
			uaalPublisherWeightUnitTextfield = createJTextfield(400,150,100,50,Font.PLAIN,16);
		}
		uaalPublisherButton = createJButton(null,250,275,200,25,"publishData");
		uaalPublisherButton.setText("Publish to uAAL");	
		uaalPublisherButton.setToolTipText("Public measured data to uAAL context bus");
		// Check type of measurement
		if(type.equals("real")) {
			// Run hdp manager and wait for agent values			
			if(device.equals("bloodPressure")) {
				remoteDeviceType = "BloodPressureMonitor";
				instantiateHdpManager();				
			} else {		
				remoteDeviceType = "WeightingScale";
				instantiateHdpManager();				
			}
		} else {
			// Generate random values
			if(device.equals("bloodPressure")) {
				remoteDeviceType = "BloodPressureMonitor";
				uaalPublisherBloodPressureSysValueTextfield.setText(""+getRandomValue(90,119));
				uaalPublisherBloodPressureDiaValueTextfield.setText(""+getRandomValue(60,79));
				uaalPublisherBloodPressurePulValueTextfield.setText(""+getRandomValue(49,198));
			} else {
				remoteDeviceType = "WeightingScale";
				uaalPublisherWeightValueTextfield.setText(""+getRandomValue(50,110));
				uaalPublisherWeightUnitTextfield.setText("kg");
			}
		}
		// Add components to the panel
		mainPublisherPanel.add(uaalPublisherMainLabel);
		mainPublisherPanel.add(uaalPublisherLogoLabel);	
		addJLabelComponent(uaalPublisherBloodPressureSysValueLabel);
		addJLabelComponent(uaalPublisherBloodPressureSysValueTextfield);
		addJLabelComponent(uaalPublisherBloodPressureDiaValueLabel);
		addJLabelComponent(uaalPublisherBloodPressureDiaValueTextfield);
		addJLabelComponent(uaalPublisherBloodPressurePulValueLabel);
		addJLabelComponent(uaalPublisherBloodPressurePulValueTextfield);
		addJLabelComponent(uaalPublisherWeightValueLabel);
		addJLabelComponent(uaalPublisherWeightValueTextfield);
		addJLabelComponent(uaalPublisherWeightUnitLabel);
		addJLabelComponent(uaalPublisherWeightUnitTextfield);
		mainPublisherPanel.add(uaalPublisherButton);
		// Show
		uaalPublisher.setVisible(true);
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
		uaalPublisherWeightValueLabel = null;
		uaalPublisherWeightValueTextfield = null;
		uaalPublisherWeightUnitLabel = null;
		uaalPublisherWeightUnitTextfield = null;
		uaalPublisherBloodPressureSysValueLabel = null;
		uaalPublisherBloodPressureSysValueTextfield = null;
		uaalPublisherBloodPressureDiaValueLabel = null;
		uaalPublisherBloodPressureDiaValueTextfield = null;
		uaalPublisherBloodPressurePulValueLabel = null;
		uaalPublisherBloodPressurePulValueTextfield = null;
		remoteDeviceType = null;
		realMeasurement = false;
		finalMeasuredWeightData = -1.0;
		finalSysBloodPressureData = -1;
		finalDiaBloodPressureData = -1;
		finalHrBloodPressureData = -1;
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
	
	/** Create a new HDP manager object */
	public void instantiateHdpManager() {		
		new Thread(){
			public void run() {
				manager = new hdpManager(remoteDeviceType);
				manager.init();										
			}			
		}.start();
	}	
	
	/** */
	public void stopPublisherGUI() {
		if(manager != null) {
			manager.exit();
			manager = null;
		}	
		resetComponentsStatus();
		uaalPublisher.dispose();
		setVisible(true);
	}
	
	/** Exit all */
	public void stopGUI() {
		if(manager != null) {
			manager.exit();
			manager = null;
		}	
		resetComponentsStatus();
		if(uaalPublisher != null)
			uaalPublisher.dispose();	
		dispose();
	}
}
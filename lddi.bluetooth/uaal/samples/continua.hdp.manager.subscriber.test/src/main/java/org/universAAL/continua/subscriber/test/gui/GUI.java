/**
 * x073 Continua agent subscriber (agent events will be received from uAAL bus)
 * 
 * @author Angel Martinez-Cavero
 * @version 0
 *  
 * TSB Technologies for Health and Well-being
 */

// Package 
package org.universAAL.continua.subscriber.test.gui;

// Imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.osgi.framework.BundleContext;
import org.universAAL.continua.subscriber.test.gui.Subscriber;

//Main class
public class GUI extends JDialog {
	
// Attributes
	
	/** Serializable object */
	private static final long serialVersionUID = 1L;		
	
	/** Main and secondary panels */
	public static JPanel mainPanel = null;
		
	/** Components */	
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
	
	/** Constants */
	private static final String weighingScaleImage = "ws.png";
	private static final String bloodPressureImage = "bp.png";	
	
	/** Bundle context object */
	private BundleContext ctx = null;
	
	/** Subscriber object */
	private Subscriber sb = null;
	
	/** Expected remote device type */
	private String remoteDeviceType = null;	
	
	// Constructor	
	public GUI(BundleContext context,String str,Subscriber s) {
		ctx = context;
		remoteDeviceType = str;
		sb = s;
		init();		
	}
	
	// Methods
	/** Create the main frame */
	public void init() {		
		// Main dialog
		setResizable(false);
		setBounds(100,100,550,275);
		setTitle("uAAL Continua manager subscriber");
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
		// Main text label 		
		uaalPublisherMainLabel = createJLabel("",200,5,350,50,Font.BOLD,24);
		// Image icon		
		uaalPublisherLogoLabel = new JLabel("");		
		uaalPublisherLogoLabel.setBounds(25,75,218,145);		
		// Check Continua agent selected
		if(remoteDeviceType.equals("bloodPressure")) {			
			uaalPublisherMainLabel.setText("Blood pressure monitor");		
			uaalPublisherLogoLabel.setIcon(new ImageIcon(ctx.getBundle().getResource(bloodPressureImage)));			
			uaalPublisherBloodPressureSysValueLabel = createJLabel("SYS(mmHg)",250,100,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressureSysValueTextfield = createJTextfield(400,100,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressureDiaValueLabel = createJLabel("DIA(mmHg)",250,150,100,50,Font.PLAIN,16);				
			uaalPublisherBloodPressureDiaValueTextfield = createJTextfield(400,150,100,50,Font.PLAIN,16);			
			uaalPublisherBloodPressurePulValueLabel = createJLabel("     BPM",250,200,100,50,Font.PLAIN,16);
			uaalPublisherBloodPressurePulValueTextfield = createJTextfield(400,200,100,50,Font.PLAIN,16);
			// Add components
			addJLabelComponent(uaalPublisherBloodPressureSysValueLabel);
			addJLabelComponent(uaalPublisherBloodPressureSysValueTextfield);
			addJLabelComponent(uaalPublisherBloodPressureDiaValueLabel);
			addJLabelComponent(uaalPublisherBloodPressureDiaValueTextfield);
			addJLabelComponent(uaalPublisherBloodPressurePulValueLabel);
			addJLabelComponent(uaalPublisherBloodPressurePulValueTextfield);
			// Show data
			uaalPublisherBloodPressureSysValueTextfield.setText(""+sb.getSysMeasuredValue());
			uaalPublisherBloodPressureDiaValueTextfield.setText(""+sb.getDiaMeasuredValue());
			uaalPublisherBloodPressurePulValueTextfield.setText(""+sb.getHrMeasuredValue());
		} else {
			uaalPublisherMainLabel.setText("Weighing scale");
			uaalPublisherLogoLabel.setIcon(new ImageIcon(ctx.getBundle().getResource(weighingScaleImage)));
			uaalPublisherWeightValueLabel = createJLabel("Weight value",250,100,150,50,Font.PLAIN,16);			
			uaalPublisherWeightValueTextfield = createJTextfield(400,100,100,50,Font.PLAIN,16);			
			uaalPublisherWeightUnitLabel = createJLabel(" Weight unit",250,150,150,50,Font.PLAIN,16);				
			uaalPublisherWeightUnitTextfield = createJTextfield(400,150,100,50,Font.PLAIN,16);
			addJLabelComponent(uaalPublisherWeightValueLabel);
			addJLabelComponent(uaalPublisherWeightValueTextfield);
			addJLabelComponent(uaalPublisherWeightUnitLabel);
			addJLabelComponent(uaalPublisherWeightUnitTextfield);
			// Show data
			uaalPublisherWeightValueTextfield.setText(""+sb.getWeightMeasuredValue());
			uaalPublisherWeightUnitTextfield.setText("kg");
		}		
		// Add components to the panel
		mainPanel.add(uaalPublisherMainLabel);
		mainPanel.add(uaalPublisherLogoLabel);							
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
			mainPanel.add(component);
	}
    
	/** Close GUI frame */
    public void closeGUI(){	     	
    	resetComponentsStatus();			
		dispose();	
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
	}	
}
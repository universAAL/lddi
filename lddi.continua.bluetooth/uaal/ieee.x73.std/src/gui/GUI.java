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

// Package
package gui;

// Import
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import manager.Manager;

// Main class
public class GUI extends WindowAdapter implements ActionListener{	
	
	// Components
	private JFrame mainFrame;
	
	private JPanel mainPanel;
	
	private JPanel rightPanel;
	private JPanel leftPanel;
	
	private JPanel imagePanel;
	private JPanel deviceInfoPanel;
	private JPanel fsmStatusPanel;
	private JPanel measurePanel;
	
	private JLabel deviceConnectedLabel;
	private JLabel deviceInfoLabel;
	
	private JLabel fsmStatusLabel;
	private JLabel fsmInfoLabel;
	
	private JLabel measureLabel;
	private JLabel measureInfoLabel;
	
	private JLabel devicePicture;
	private BufferedImage picture;
	private JTextArea outputTextJTextArea;	
	
	/** Frame configuration */
	public void initGUI() {		
		// Main panel
//		setTitle("");
		mainFrame = initFrame("IEEE/ISO 11073", 800, 600 );
		
		createComponents();
		insertComponents();
		
        mainFrame.pack();        
//        mainFrame.setContentPane(mainPanel);  
        mainFrame.setResizable(false);                
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        mainFrame.repaint();
        
	}	
	
    private void insertComponents() {
    	
    	imagePanel.add(devicePicture);
    	deviceInfoPanel.add(deviceConnectedLabel);
    	deviceInfoPanel.add(deviceInfoLabel);
    	
    	fsmStatusPanel.add(fsmStatusLabel);
    	fsmStatusPanel.add(fsmInfoLabel);
    	
    	measurePanel.add(measureLabel);
    	measurePanel.add(measureInfoLabel);
    	
    	leftPanel.add(imagePanel);
    	
    	rightPanel.add(deviceInfoPanel);

    	rightPanel.add(fsmStatusPanel);
    	rightPanel.add(measurePanel);

		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);

		
		mainFrame.add(mainPanel);
	}

	private void createComponents() {
		
    	mainPanel = initPanel(BoxLayout.X_AXIS);
    	mainPanel.setLayout(new GridLayout(1,2)); 
    	leftPanel = initPanel(BoxLayout.X_AXIS);
    	leftPanel.setLayout(new GridLayout(1,1)); 
    	
    	rightPanel = initPanel(BoxLayout.X_AXIS);
    	rightPanel.setLayout(new GridLayout(3,1)); 
    	
    	imagePanel = initPanel(BoxLayout.X_AXIS);
    	imagePanel.setLayout(new GridLayout(1,1));
    	
    	deviceInfoPanel = initPanel(BoxLayout.X_AXIS);
    	deviceInfoPanel.setLayout(new GridLayout(2,1));
    	
    	fsmStatusPanel = initPanel(BoxLayout.X_AXIS);
    	fsmStatusPanel.setLayout(new GridLayout(2,1));
    	
    	measurePanel = initPanel(BoxLayout.X_AXIS);
    	measurePanel.setLayout(new GridLayout(2,1));
    	
    	deviceConnectedLabel = initLabel("Device Connected:", 16);
    	deviceInfoLabel = initLabel("-", 16);
    	fsmStatusLabel = initLabel("Status of Manager:", 16);
    	fsmInfoLabel = initLabel("-", 16);
    	measureLabel = initLabel("Measurement", 16);
    	measureInfoLabel = initLabel("-", 16);
    	
    	devicePicture = initImage("resources/img/blood.jpg");
    	
	}

	/** Create frames */
    public JFrame initFrame(String name,int width,int height) {    	
    	JFrame jf;
    	jf = new JFrame(name);
        jf.setLayout(new BorderLayout());
        jf.setResizable(true);
        jf.setMinimumSize(new Dimension(width,height));
        jf.setPreferredSize(new Dimension(width,height));
        jf.setSize(new Dimension(width,height));        
        jf.setResizable(false);
        jf.setLocationRelativeTo(null);        
        jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jf.addWindowListener(this);
    	return jf;
    }
	
	private JPanel initPanel(int axis) {
    	JPanel jp;
    	jp = new JPanel();
    	jp.setBackground(Color.WHITE);
    	jp.setLayout(new BoxLayout(jp,axis));        
    	return jp;
	}
    
    /** Create button component */
    public JButton initButton(String name) {    	
    	JButton jb;    	
    	jb = new JButton(name);    	
    	jb.setActionCommand(name);    	
    	jb.setPreferredSize(new Dimension(Constant.buttonWidth,Constant.buttonHeight));    	 
    	jb.setToolTipText(name);
    	jb.addActionListener(this);
    	return jb;
    }      
    
    /** Create static text component */    
    public JLabel initLabel(String name,int fontSize) {
    	JLabel jl;  
    	jl = new JLabel(name);    	
		jl.setForeground(Color.BLACK);			
		jl.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,fontSize));		
		jl.setHorizontalTextPosition(SwingConstants.CENTER);
    	jl.setVerticalTextPosition(SwingConstants.CENTER);    	
    	return jl;    	
    } 
    
    /** Create image viewer */    
    public JLabel initImage(String path) {
    	try {
			picture = ImageIO.read(new File(path));
	    	JLabel jl;  
	    	jl = new JLabel(new ImageIcon(picture));    	
	    	return jl; 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return deviceConnectedLabel;
   	
    } 
    
    /** Create text field component */
    public JTextField initTextField(int size) {    	
    	JTextField jtf;  
    	jtf = new JTextField();    	
    	jtf.setEnabled(true);
    	jtf.setEditable(false);
    	jtf.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,size)); 	
    	jtf.setHorizontalAlignment(JTextField.CENTER);  
    	jtf.setColumns(12);  
    	jtf.setBackground(Color.WHITE);
    	jtf.setBorder(null);
    	jtf.addActionListener(this);
    	return jtf;
    }
    
    /** Create text field component */
    public JTextArea initTextArea(int size, int lines, int cols) {    	
    	JTextArea jta;  
    	jta = new JTextArea(lines,cols);    	
    	jta.setEnabled(true);
    	jta.setEditable(false);
    	jta.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,size)); 	
       	jta.setBackground(Color.WHITE);
    	jta.setBorder(null);
    	return jta;
    }
    

    public void setDeviceName(String type, String name){
    	deviceInfoLabel.setText("["+type+"]  " + name );
    }
    
    public void setFSMLabelInfo(String status){
    	deviceInfoLabel.setText(status);
    
    }
    
    public void setMeasurementLabel(String measure){
    	deviceInfoLabel.setText(measure);
    }
    public void setPicture(String path){
    	devicePicture.setText(path);
    	mainFrame.repaint();
    }
    
    
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}
	
    /** Bundle stop */
    public void endGUI() {    	
    	mainFrame.dispose();
    }
	
	
	/** Init bluetooth manager */
//	public void initBluetoothManager() {					
//		manager = new Manager(this);
//		mana.init();
//	}
}

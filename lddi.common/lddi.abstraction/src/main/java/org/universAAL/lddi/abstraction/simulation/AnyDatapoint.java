/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JTextField;

import org.universAAL.lddi.abstraction.ExternalDatapoint;

/**
 * @author mtazari
 *
 */
public class AnyDatapoint extends SimulatedDatapoint implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4590775353114048184L;
	
	private JTextField valueText;

	/**
	 * 
	 */
	public AnyDatapoint(ExternalDatapoint dp, URL iconURL, Object initialVal) {
		super(dp, iconURL, initialVal);
		valueText = new JTextField(dp.getComponent().internalValueAsString(dp.getProperty(), initialVal));
		valueText.setColumns(15);
		valueText.addActionListener(this);
		GridBagConstraints gbc_valueText = new GridBagConstraints();
		gbc_valueText.insets = new Insets(1, 1, 1, 1);
		gbc_valueText.gridx = 0;
		gbc_valueText.gridy = 0;
		add(valueText, gbc_valueText);
	}

	@Override
	void setValue(Object o) {
		valueText.setText(dp.getComponent().internalValueAsString(dp.getProperty(), o));
		value = o;
	}

	public void actionPerformed(ActionEvent e) {
		pushValue(dp.getComponent().internalValueOf(dp.getProperty(), valueText.getText()));
	}

}

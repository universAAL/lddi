/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.universAAL.lddi.abstraction.ExternalDatapoint;

/**
 * @author mtazari
 *
 */
public class PercentageDatapoint extends SimulatedDatapoint implements ChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6110070657096292557L;
	
	private JSlider theSlider = new JSlider(0, 100);

	/**
	 * 
	 */
	public PercentageDatapoint(ExternalDatapoint dp, URL iconURL, Object initialVal) {
		super(dp, iconURL, initialVal);
		
		if (initialVal != null)
			theSlider.setValue(Math.round(Float.valueOf(
					dp.getComponent().internalValueAsString(dp.getProperty(), initialVal))));
		theSlider.setMajorTickSpacing(25);
		theSlider.setMinorTickSpacing(5);
		theSlider.addChangeListener(this);
		theSlider.setPaintLabels(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.insets = new Insets(1, 1, 1, 1);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 0;
		add(theSlider, gbc_slider);
	}

	@Override
	void setValue(Object o) {
		if (o != null)
			theSlider.setValue(Math.round(Float.valueOf(dp.getComponent().internalValueAsString(dp.getProperty(), o))));
		value = o;
	}

	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == theSlider  &&  !theSlider.getValueIsAdjusting()) {
			pushValue(dp.getComponent().internalValueOf(dp.getProperty(), Integer.toString(theSlider.getValue())));
		}
	}

}

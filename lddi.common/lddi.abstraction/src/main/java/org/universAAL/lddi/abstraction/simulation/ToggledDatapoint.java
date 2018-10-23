/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.universAAL.lddi.abstraction.ExternalDatapoint;

/**
 * @author mtazari
 *
 */
class ToggledDatapoint extends SimulatedDatapoint {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4732316777737436800L;
	
	private int noOfButtons = 0;
    private JToolBar buttonBar = new JToolBar();

	/**
	 * 
	 */
	public ToggledDatapoint(ExternalDatapoint dp, Hashtable<Object, URL> altValues, Object initialVal) {
		super(dp, altValues.get(initialVal), initialVal);
		
		for (Object o : altValues.keySet()) {
			buttonBar.add(new ThumbnailButton(altValues.get(o), o));
			noOfButtons++;
		}

		GridBagConstraints gbc_bBar = new GridBagConstraints();
		gbc_bBar.insets = new Insets(1, 1, 1, 1);
		gbc_bBar.gridx = 0;
		gbc_bBar.gridy = 0;
		add(buttonBar, gbc_bBar);
	}
	
	

	@Override
	void setValue(Object o) {
		for (int i=0;  i<noOfButtons;  i++) {
			ThumbnailButton jb = (ThumbnailButton) buttonBar.getComponent(i);
			if (o == jb.value  ||  (o != null  &&  o.equals(jb.value)))
				jb.actionPerformed(null);
		}
	}

    
    private class ThumbnailButton extends JButton implements ActionListener {
        
        private static final long serialVersionUID = 7982389349522263108L;
		private Icon displayPhoto;
        Object value;
        
        ThumbnailButton(URL url, Object value) {
            displayPhoto = getScaledImage(url, 80);
            this.value = value;
            
            setIcon(getScaledImage(url, 40));
            addActionListener(this);
        }
        
        public void actionPerformed(ActionEvent e) {
            dpIcon.setIcon(displayPhoto);
            ToggledDatapoint.this.pushValue(value);
        }
    }
}

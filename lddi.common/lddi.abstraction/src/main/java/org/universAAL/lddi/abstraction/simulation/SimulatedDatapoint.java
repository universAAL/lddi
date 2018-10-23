/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.middleware.container.utils.StringUtils;

/**
 * @author mtazari
 *
 */
abstract class SimulatedDatapoint extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 687342480826786080L;
	private SimulationTool parent = null;
	protected ExternalDatapoint dp = null;
	protected Object value = null;
	protected JLabel dpIcon = null;
	
	protected SimulatedDatapoint(ExternalDatapoint dp, URL iconURL, Object initialVal) {
		super(new GridBagLayout());
		setSize(130, 130);
		
		this.dp = dp;
		value = initialVal;
		
		setLabel();
		setImage(iconURL);
	}
	
	private void setLabel() {
		String prop = dp.getProperty();
		if (StringUtils.isQualifiedName(prop))
			prop = prop.substring(prop.lastIndexOf('#') + 1);
		
		String id = dp.getComponent().getOntResource().getLocalName();
		
		JLabel l = new JLabel(prop+"\n@"+id);

		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.insets = new Insets(1, 1, 1, 1);
		gbc_label.gridx = 0;
		gbc_label.gridy = 2;
		add(l, gbc_label);
	}

	ExternalDatapoint getDatapoint() {
		return dp;
	}
	
	Object getValue() {
		return value;
	}
	
	abstract void setValue(Object o);
	
	protected final void pushValue(Object value) {
		this.value = value;
		if (parent == null) {
			Component c = this.getParent();
			while (c != null  &&  !(c instanceof SimulationTool))
				c = c.getParent();
			if (c == null)
				throw new RuntimeException("Cannot find the simulation tool as parent of a simulated datapoint!");
			parent = (SimulationTool) c;
		}
		
		parent.pushValue(dp, value);
	}
	
	private void setImage(URL url) {
		dpIcon = new JLabel(getScaledImage(url, 80));
		GridBagConstraints gbc_image = new GridBagConstraints();
		gbc_image.insets = new Insets(1, 1, 1, 1);
		gbc_image.gridx = 0;
		gbc_image.gridy = 1;
		add(dpIcon, gbc_image);
	}
    
    /**
     * Resizes an image to a square with a given size using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param size - target width and height of the image
     * @return - the new resized image
     */
    protected ImageIcon getScaledImage(URL url, int size){
        BufferedImage resizedImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(new ImageIcon(url).getImage(), 0, 0, size, size, null);
        g2.dispose();
        return new ImageIcon(resizedImg);
    }
}

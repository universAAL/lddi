/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.ontology.location.Location;

/**
 * @author mtazari
 *
 */
public class SimulationTool extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 590113997647987251L;
	private static final String TAB_TITLE_NOWHERE = "Global";
	private static final int TAB_MAX_COLUMN_NUMBER = 5;
	private static final int TAB_MAX_ROW_NUMBER = 3;
	private static final Hashtable<ExternalDatapoint, SimulatedDatapoint> renderedDatapoints = new Hashtable<ExternalDatapoint, SimulatedDatapoint>();
	
	private JTabbedPane contentTabs;
	private CommunicationGateway cgw;

	public SimulationTool(CommunicationGateway cgw) {
		this.cgw = cgw;
		contentTabs = new JTabbedPane();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(20, 20, 800, 600);
		contentTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentTabs);
	}
	
	private void addDatapointToTab(ExternalDatapoint dp, SimulatedDatapoint dpPane) throws ArrayIndexOutOfBoundsException {
		Location l = dp.getComponent().getLocation();
		String tabTitle = (l == null)?  TAB_TITLE_NOWHERE : l.getLocalName();
		
		JPanel contentPane = null;
		int count = contentTabs.getTabCount();
		for (int i=0; i < count; i++)
			if (contentTabs.getTitleAt(i).equals(tabTitle)) {
				contentPane = (JPanel) contentTabs.getComponentAt(i);
				break;
			}
		
		if (contentPane == null) {
			contentPane = new JPanel();
			GridBagLayout gbl_contentPane = new GridBagLayout();
			gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
			gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
			gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
			gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
			contentPane.setLayout(gbl_contentPane);
			contentPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			contentTabs.addTab(tabTitle, contentPane);
		}
		
		count = contentPane.getComponentCount();
		if (count < (TAB_MAX_COLUMN_NUMBER * TAB_MAX_ROW_NUMBER)) {
			int row = count / TAB_MAX_COLUMN_NUMBER;
			int col = count - (row * TAB_MAX_COLUMN_NUMBER);
			GridBagConstraints gbc_dpPane = new GridBagConstraints();
			gbc_dpPane.insets = new Insets(5, 5, 5, 5);
			gbc_dpPane.gridx = col;
			gbc_dpPane.gridy = row;
			dpPane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
			contentPane.add(dpPane, gbc_dpPane);
		} else
			throw new ArrayIndexOutOfBoundsException();

		renderedDatapoints.put(dp,  dpPane);
	}
	
	public void addDataPoint(ExternalDatapoint dp, Hashtable<Object, URL> altValues, Object initialVal) {
		if (!renderedDatapoints.contains(dp))
			addDatapointToTab(dp, new ToggledDatapoint(dp, altValues, initialVal));
	}
	
	public void addDataPoint(ExternalDatapoint dp, boolean isPercentage, URL iconURL, Object initialVal) {
		if (!renderedDatapoints.contains(dp)) {
			SimulatedDatapoint sDP = isPercentage?
					new PercentageDatapoint(dp, iconURL, initialVal)
					:  new AnyDatapoint(dp, iconURL, initialVal);
			addDatapointToTab(dp, sDP);
		}
	}
	
	public Object getDatapointValue(ExternalDatapoint dp) {
		return renderedDatapoints.get(dp).getValue();
	}
	
	public void setDatapointValue(ExternalDatapoint dp, Object value) {
		renderedDatapoints.get(dp).setValue(value);
	}
	
	void pushValue(ExternalDatapoint dp, Object value) {
		if (dp.getPushAddress() != null)
			cgw.handleSimulatedEvent(this, dp, value);
	}
}

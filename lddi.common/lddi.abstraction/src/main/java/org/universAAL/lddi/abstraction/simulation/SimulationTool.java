/**
 * 
 */
package org.universAAL.lddi.abstraction.simulation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.universAAL.lddi.abstraction.Activator;
import org.universAAL.lddi.abstraction.CommunicationGateway;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.lddi.abstraction.config.tool.DatapointConfigTool;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurableEntityEditor;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.ConfigurationParameterEditor;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.ApplicationPartPattern;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.ApplicationPattern;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.EntityPattern;
import org.universAAL.middleware.interfaces.configuration.configurationEditionTypes.pattern.IdPattern;
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

	public SimulationTool() {
		contentTabs = new JTabbedPane();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(20, 20, 800, 600);
		contentTabs.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentTabs);
		
		JPanel contentPane = createTab(TAB_TITLE_NOWHERE);
		
		JButton btnSwitch2Simulation = new JButton("To Address Test Mode");
		GridBagConstraints gbc_btnSwitch2Simulation = new GridBagConstraints();
		gbc_btnSwitch2Simulation.insets = new Insets(5, 5, 5, 5);
		gbc_btnSwitch2Simulation.gridx = 1;
		gbc_btnSwitch2Simulation.gridy = 3;
		contentPane.add(btnSwitch2Simulation, gbc_btnSwitch2Simulation);
		btnSwitch2Simulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOperationMode(CommunicationGateway.OPERATION_MODE_ADDRESS_TEST);
			}
		});
		
		JButton btnSwitch2Production = new JButton("To Production Mode");
		GridBagConstraints gbc_btnSwitch2Production = new GridBagConstraints();
		gbc_btnSwitch2Production.insets = new Insets(5, 5, 5, 5);
		gbc_btnSwitch2Production.gridx = 3;
		gbc_btnSwitch2Production.gridy = 3;
		contentPane.add(btnSwitch2Production, gbc_btnSwitch2Production);
		btnSwitch2Production.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeOperationMode(CommunicationGateway.OPERATION_MODE_IN_PRODUCTION);
			}
		});
	}
	
	private JPanel createTab(String tabTitle) {
		JPanel contentPane = new JPanel();
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		contentPane.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		contentTabs.addTab(tabTitle, contentPane);
		return contentPane;
	}
	
	private void changeOperationMode(int mode) {
		List<EntityPattern> patterns = new ArrayList<EntityPattern>();
		patterns.add(new ApplicationPattern(CommunicationGateway.class.getSimpleName()));
		patterns.add(new ApplicationPartPattern(CommunicationGateway.CGW_CONF_APP_PART_PROTOCOL_ID));
		patterns.add(new IdPattern(CommunicationGateway.CONF_PARAM_CGW_PROTOCOL_OPERATION_MODE));
		List<ConfigurableEntityEditor> configs = Activator.getConfigEditor().getMatchingConfigurationEditors(patterns, Locale.ENGLISH);
		try {
			ConfigurationParameterEditor configParam = (ConfigurationParameterEditor) configs.get(0);
			configParam.setValue(mode);
		} catch (Exception e) {
			LogUtils.logError(Activator.getMC(), DatapointConfigTool.class, "changeOperationMode()", e.getMessage());
		}
	}
	
	private void addDatapointToTab(ExternalDatapoint dp, SimulatedDatapoint dpPane) throws ArrayIndexOutOfBoundsException {
		Location l = dp.getComponent().getLocation();
		String tabTitle = (l == null)?  TAB_TITLE_NOWHERE : l.getLocalName();
		String tabContinued = tabTitle + " 2";
		
		JPanel contentPane = null;
		int count = contentTabs.getTabCount();
		for (int n=0; n < count; n++)
			if (contentTabs.getTitleAt(n).equals(tabTitle))
				contentPane = (JPanel) contentTabs.getComponentAt(n);
			else if (contentTabs.getTitleAt(n).equals(tabContinued)) {
				contentPane = (JPanel) contentTabs.getComponentAt(n);
				break;
			}
		
		if (contentPane == null) {
			count = 0;
			contentPane = createTab(tabTitle);
		} else {
			count = contentPane.getComponentCount();
			if (count >= (TAB_MAX_COLUMN_NUMBER * TAB_MAX_ROW_NUMBER)) {
				count = 0;
				contentPane = createTab(tabContinued);
			}
		}
		
		int row = count / TAB_MAX_COLUMN_NUMBER;
		int col = count - (row * TAB_MAX_COLUMN_NUMBER);
		GridBagConstraints gbc_dpPane = new GridBagConstraints();
		gbc_dpPane.insets = new Insets(5, 5, 5, 5);
		gbc_dpPane.gridx = col;
		gbc_dpPane.gridy = row;
		dpPane.setBorder(BorderFactory.createLineBorder(Color.darkGray));
		contentPane.add(dpPane, gbc_dpPane);

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
		if (dp != null) {
			SimulatedDatapoint sdp = renderedDatapoints.get(dp);
			return (sdp == null)? null : sdp.getValue();
		}
		return null;
	}
	
	public void setDatapointValue(ExternalDatapoint dp, Object value) {
		renderedDatapoints.get(dp).setValue(value);
	}
	
	void pushValue(ExternalDatapoint dp, Object value) {
		if (dp.getPushAddress() != null)
			CommunicationGateway.handleSimulatedEvent(this, dp, value);
	}
}

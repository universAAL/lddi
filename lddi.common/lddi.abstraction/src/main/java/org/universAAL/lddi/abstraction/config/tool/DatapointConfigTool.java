package org.universAAL.lddi.abstraction.config.tool;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.universAAL.lddi.abstraction.ComponentIntegrator;
import org.universAAL.lddi.abstraction.ExternalComponent;
import org.universAAL.lddi.abstraction.ExternalDatapoint;
import org.universAAL.lddi.abstraction.config.data.ConfiguredDatapoint;
import org.universAAL.middleware.container.utils.StringUtils;
import org.universAAL.middleware.owl.ManagedIndividual;
import org.universAAL.ontology.location.Location;

import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import java.awt.Insets;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DatapointConfigTool extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7698455054350741666L;
	private static final String PROP_MY_EVENT_MODEL = "urn:lddi.abstraction/DatapointConfigTool#eventsModel";

	private class PropertiesModel implements ComboBoxModel {
		ArrayList<String> propChoices = new ArrayList<String>();
		ArrayList<ListDataListener> myListeners = new ArrayList<ListDataListener>(1);
		int selection = 0;
		
		PropertiesModel() {
			propChoices.add("");
		}

		public int getSize() {
			return propChoices.size();
		}

		public String getElementAt(int index) {
			return propChoices.get(index);
		}

		public void addListDataListener(ListDataListener l) {
			if (l != null)
				myListeners.add(l);
		}

		public void removeListDataListener(ListDataListener l) {
			myListeners.remove(l);
		}

		public void setSelectedItem(Object anItem) {
			int oldSelection = selection;
			for (int i=0;  i<propChoices.size();  i++)
				if (propChoices.get(i).equals(anItem)) {
					selection = i;
					break;
				}
			if (selection != oldSelection)
				DatapointConfigTool.propertySelected((String) anItem);
		}

		public String getSelectedItem() {
			return propChoices.get(selection);
		}
		
		void clear() {
			int oldSelection = selection;
			int s = propChoices.size();
			if (s > 1) {
				propChoices.clear();
				propChoices.add("");
				selection = 0;
				ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 1, s-1);
				for (ListDataListener l : myListeners)
					l.intervalRemoved(e);
				if (oldSelection != 0)
					DatapointConfigTool.propertySelected("");
			}
		}
		
		void add(String prop) {
			if (prop != null) {
				int s = propChoices.size();
				propChoices.add(prop);
				ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, s, s);
				for (ListDataListener l : myListeners)
					l.intervalRemoved(e);
			}
				
		}
		
	}

	private class EventsModel extends AbstractTableModel {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3652207261256135835L;
		private ArrayList<String> events = new ArrayList<String>();

		public int getRowCount() {
			return events.size();
		}

		public int getColumnCount() {
			return 2;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return (columnIndex == 0)?  events.get(rowIndex)  :  Integer.toString(rowIndex+1);
		}
		
		public String getColumnName(int col) {
			return (col == 0)?  "Value pushed"  :  "#";
		}
		
		void addValue(String val) {
			int ind = events.size();
			events.add(val);
			fireTableRowsInserted(ind, ind);
		}
		
		void clear() {
			if (!events.isEmpty()) {
				int n = events.size() - 1;
				events.clear();
				fireTableRowsDeleted(0, n);
			}
		}
	}
	
	private JPanel contentPane;
	private static JTextField valPullAddress;
	private static JTextField valPushAddress;
	private static JTextField valSetAddress;
	private static JComboBox<?> properties;
	private static JLabel lblCompID, lblType, lblLocation;
	
	private static JTextField indexValue;
	private static ArrayList<ExternalComponent> components = new ArrayList<ExternalComponent>();
	private static int indexSelectedComponent = -1;
	private static PropertiesModel propertiesModel;
	private static JLabel lblValueFetched;
	private static JTable notifications;
	private static EventsModel eventsModel;
	private static JTextField valueToWrite;
	
	private static DatapointConfigTool self;
	
	private ArrayList<ExternalComponent[]> newComponents;
	private Hashtable<String, ExternalComponent> receivedEvents;
	private final Lock lock;
	private final Condition publishCond;
	private final Condition componentsReplacedCond;

	/**
	 * Create the frame.
	 */
	public DatapointConfigTool(ArrayList<ExternalComponent[]> news, Hashtable<String, ExternalComponent> events, Lock lock, Condition publishCond, Condition componentsReplacedCond) {
		newComponents = news;
		receivedEvents = events;
		this.lock = lock;
		this.publishCond = publishCond;
		this.componentsReplacedCond = componentsReplacedCond;
		
		self = this;
		
		propertiesModel = new PropertiesModel();
		eventsModel = new EventsModel();
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JButton btnPrevComp = new JButton("Previous");
		GridBagConstraints gbc_btnPrevComp = new GridBagConstraints();
		gbc_btnPrevComp.insets = new Insets(5, 5, 5, 5);
		gbc_btnPrevComp.gridx = 0;
		gbc_btnPrevComp.gridy = 0;
		contentPane.add(btnPrevComp, gbc_btnPrevComp);
		btnPrevComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (indexSelectedComponent > -1) {
					indexValue.setText(Integer.toString(indexSelectedComponent));
					DatapointConfigTool.componentSelected(indexSelectedComponent);
				}
			}
		});
		
		indexValue = new JTextField();
		indexValue.setColumns(3);
		indexValue.setText("0");
		GridBagConstraints gbc_indexValue = new GridBagConstraints();
		gbc_indexValue.anchor = GridBagConstraints.WEST;
		gbc_indexValue.insets = new Insets(5, 5, 5, 5);
		gbc_indexValue.gridx = 1;
		gbc_indexValue.gridy = 0;
		contentPane.add(indexValue, gbc_indexValue);

		indexValue.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					int i = Integer.parseInt(indexValue.getText());
					if (i < 0  ||  i > components.size())
						indexValue.grabFocus();
					else
						DatapointConfigTool.componentSelected(i);
				} catch (NumberFormatException e1) {
					// TODO Auto-generated catch block
					indexValue.grabFocus();
				}
			}
		});
		
		btnPrevComp = new JButton("Next");
		gbc_btnPrevComp = new GridBagConstraints();
		gbc_btnPrevComp.insets = new Insets(5, 5, 5, 5);
		gbc_btnPrevComp.gridx = 2;
		gbc_btnPrevComp.gridy = 0;
		contentPane.add(btnPrevComp, gbc_btnPrevComp);
		btnPrevComp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (indexSelectedComponent < components.size()-1) {
					indexValue.setText(Integer.toString(indexSelectedComponent+2));
					DatapointConfigTool.componentSelected(indexSelectedComponent+2);
				}
			}
		});
		
		lblCompID = new JLabel("None");
		GridBagConstraints gbc_lblCompID = new GridBagConstraints();
		gbc_lblCompID.anchor = GridBagConstraints.WEST;
		gbc_lblCompID.gridwidth = 2;
		gbc_lblCompID.insets = new Insets(5, 5, 5, 5);
		gbc_lblCompID.gridx = 3;
		gbc_lblCompID.gridy = 0;
		contentPane.add(lblCompID, gbc_lblCompID);
		
		lblType = new JLabel("Type");
		GridBagConstraints gbc_lblType = new GridBagConstraints();
		gbc_lblType.gridwidth = 2;
		gbc_lblType.insets = new Insets(5, 5, 5, 5);
		gbc_lblType.anchor = GridBagConstraints.WEST;
		gbc_lblType.gridx = 0;
		gbc_lblType.gridy = 1;
		contentPane.add(lblType, gbc_lblType);
		
		lblLocation = new JLabel("Location");
		GridBagConstraints gbc_lblLocation = new GridBagConstraints();
		gbc_lblLocation.gridwidth = 2;
		gbc_lblLocation.insets = new Insets(5, 5, 5, 5);
		gbc_lblLocation.anchor = GridBagConstraints.WEST;
		gbc_lblLocation.gridx = 3;
		gbc_lblLocation.gridy = 1;
		contentPane.add(lblLocation, gbc_lblLocation);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.gridwidth = 5;
		gbc_separator_1.insets = new Insets(0, 0, 5, 5);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 2;
		contentPane.add(separator_1, gbc_separator_1);
		
		properties = new JComboBox();
		properties.setModel(propertiesModel);
		GridBagConstraints gbc_properties = new GridBagConstraints();
		gbc_properties.gridwidth = 5;
		gbc_properties.insets = new Insets(5, 5, 5, 5);
		gbc_properties.fill = GridBagConstraints.HORIZONTAL;
		gbc_properties.gridx = 0;
		gbc_properties.gridy = 3;
		contentPane.add(properties, gbc_properties);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 5;
		gbc_separator.insets = new Insets(5, 5, 5, 5);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 4;
		contentPane.add(separator, gbc_separator);
		
		JLabel lblPullAddress = new JLabel("Read Address");
		lblPullAddress.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblPullAddress = new GridBagConstraints();
		gbc_lblPullAddress.insets = new Insets(5, 5, 5, 5);
		gbc_lblPullAddress.anchor = GridBagConstraints.WEST;
		gbc_lblPullAddress.gridx = 0;
		gbc_lblPullAddress.gridy = 5;
		contentPane.add(lblPullAddress, gbc_lblPullAddress);
		
		valPullAddress = new JTextField();
		valPullAddress.setColumns(30);
		lblPullAddress.setLabelFor(valPullAddress);
		valPullAddress.setText("the pull address...");
		GridBagConstraints gbc_valPullAddress = new GridBagConstraints();
		gbc_valPullAddress.insets = new Insets(5, 5, 5, 5);
		gbc_valPullAddress.anchor = GridBagConstraints.WEST;
		gbc_valPullAddress.gridx = 1;
		gbc_valPullAddress.gridy = 5;
		contentPane.add(valPullAddress, gbc_valPullAddress);
		
		JButton btnGetValue = new JButton("Get Value");
		GridBagConstraints gbc_btnGetValue = new GridBagConstraints();
		gbc_btnGetValue.insets = new Insets(5, 5, 5, 5);
		gbc_btnGetValue.anchor = GridBagConstraints.WEST;
		gbc_btnGetValue.gridx = 2;
		gbc_btnGetValue.gridy = 5;
		contentPane.add(btnGetValue, gbc_btnGetValue);
		
		lblValueFetched = new JLabel("value fetched...");
		GridBagConstraints gbc_lblValueFetched = new GridBagConstraints();
		gbc_lblValueFetched.insets = new Insets(5, 5, 5, 5);
		gbc_lblValueFetched.gridwidth = 2;
		gbc_lblValueFetched.anchor = GridBagConstraints.WEST;
		gbc_lblValueFetched.gridx = 3;
		gbc_lblValueFetched.gridy = 5;
		contentPane.add(lblValueFetched, gbc_lblValueFetched);
		btnGetValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!"the pull address...".equals(valPullAddress.getText())
						&&  indexSelectedComponent > -1
						&&  indexSelectedComponent < components.size()) {
					String s = components.get(indexSelectedComponent).getDatapointValue(
							propertiesModel.getSelectedItem(), valPullAddress.getText());
					lblValueFetched.setText((s == null)? "value fetched..."  :  s);
				}
			}
		});
		
		JLabel lblSetAddress = new JLabel("Write Address");
		lblSetAddress.setHorizontalAlignment(SwingConstants.TRAILING);
		GridBagConstraints gbc_lblSetAddress = new GridBagConstraints();
		gbc_lblSetAddress.anchor = GridBagConstraints.WEST;
		gbc_lblSetAddress.insets = new Insets(5, 5, 5, 5);
		gbc_lblSetAddress.gridx = 0;
		gbc_lblSetAddress.gridy = 6;
		contentPane.add(lblSetAddress, gbc_lblSetAddress);
		
		valSetAddress = new JTextField();
		valSetAddress.setColumns(30);
		lblSetAddress.setLabelFor(valSetAddress);
		valSetAddress.setText("the set address...");
		GridBagConstraints gbc_valSetAddress = new GridBagConstraints();
		gbc_valSetAddress.anchor = GridBagConstraints.WEST;
		gbc_valSetAddress.insets = new Insets(5, 5, 5, 5);
		gbc_valSetAddress.gridx = 1;
		gbc_valSetAddress.gridy = 6;
		contentPane.add(valSetAddress, gbc_valSetAddress);
		
		valueToWrite = new JTextField("value to write...");
		valueToWrite.setColumns(30);
		GridBagConstraints gbc_lblValueToWrite = new GridBagConstraints();
		gbc_lblValueToWrite.anchor = GridBagConstraints.WEST;
		gbc_lblValueToWrite.gridwidth = 2;
		gbc_lblValueToWrite.insets = new Insets(5, 5, 5, 5);
		gbc_lblValueToWrite.gridx = 2;
		gbc_lblValueToWrite.gridy = 6;
		contentPane.add(valueToWrite, gbc_lblValueToWrite);
		
		JButton btnSetValue = new JButton("Set Value");
		GridBagConstraints gbc_btnSetValue = new GridBagConstraints();
		gbc_btnSetValue.insets = new Insets(5, 5, 5, 5);
		gbc_btnSetValue.gridx = 4;
		gbc_btnSetValue.gridy = 6;
		contentPane.add(btnSetValue, gbc_btnSetValue);
		btnSetValue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!"the set address...".equals(valSetAddress.getText())
						&&  indexSelectedComponent > -1
						&&  indexSelectedComponent < components.size()) {
					lblValueFetched.setText("value fetched...");
					components.get(indexSelectedComponent).setDatapointValue(
							propertiesModel.getSelectedItem(), 
							valSetAddress.getText(),
							valueToWrite.getText());
				}
			}
		});
		
		JLabel lblNotifications = new JLabel("Notifications");
		GridBagConstraints gbc_lblNotifications = new GridBagConstraints();
		gbc_lblNotifications.anchor = GridBagConstraints.NORTHWEST;
		gbc_lblNotifications.insets = new Insets(5, 5, 5, 5);
		gbc_lblNotifications.gridx = 0;
		gbc_lblNotifications.gridy = 7;
		contentPane.add(lblNotifications, gbc_lblNotifications);
		
		valPushAddress = new JTextField();
		valPushAddress.setColumns(30);
		lblNotifications.setLabelFor(valPushAddress);
		valPushAddress.setText("the push address...");
		GridBagConstraints gbc_valPushAddress = new GridBagConstraints();
		gbc_valPushAddress.insets = new Insets(5, 5, 5, 5);
		gbc_valPushAddress.anchor = GridBagConstraints.WEST;
		gbc_valPushAddress.gridx = 1;
		gbc_valPushAddress.gridy = 7;
		contentPane.add(valPushAddress, gbc_valPushAddress);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 8;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		notifications = new JTable(eventsModel);
		scrollPane.setViewportView(notifications);
		notifications.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		notifications.setColumnSelectionAllowed(false);
		notifications.setRowSelectionAllowed(true);
		notifications.setFillsViewportHeight(true);
	}
	
	private Thread eventsThread = null;
	private Thread componentsThread = null;
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			if (eventsThread == null) {
				eventsThread = new Thread() {
					public void run() {
						while (true) {
							lock.lock();
							try {
								while (receivedEvents.isEmpty())
									publishCond.await();
								for (String key : receivedEvents.keySet()) {
										ExternalComponent ec = receivedEvents.remove(key);
										DatapointConfigTool.this.newNotification(ec, key, ec.valueAsString(key));
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								lock.unlock();
							}
						}
					}
				};
				try {
					eventsThread.start();
				} catch (Exception e) {}
			}
			
			if (componentsThread == null) {
				componentsThread = new Thread() {
					public void run() {
						while (true) {
							lock.lock();
							try {
								while (newComponents.isEmpty())
									componentsReplacedCond.await();
								ExternalComponent[] ecs = newComponents.remove(0);
								if (ecs == null)
									DatapointConfigTool.this.setVisible(false);
								else
									DatapointConfigTool.this.componentsReplaced(ecs);
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								lock.unlock();
							}
						}
					}
				};
				try {
					componentsThread.start();
				} catch (Exception e) {}
			}
		} else {
			if (eventsThread != null  &&  eventsThread.isAlive())
				eventsThread.interrupt();
			if (componentsThread != null  &&  componentsThread.isAlive())
				componentsThread.interrupt();
			this.dispose();
		}
	}

	public void newNotification(ExternalComponent ec, String propURI, String newValue) {
		EventsModel em = null;
		if (ec != null  &&  propURI != null) {
			ExternalDatapoint ed = ec.getDatapoint(propURI);
			if (ed instanceof ConfiguredDatapoint) {
				em = (EventsModel) ((ConfiguredDatapoint) ed).getProperty(PROP_MY_EVENT_MODEL);
				if (em == null) {
					em = self.new EventsModel();
					((ConfiguredDatapoint) ed).setProperty(PROP_MY_EVENT_MODEL, em);
				}
				em.addValue((newValue == null)? "Null value" : newValue);
			}
		}
		if (em == null
				&&  indexSelectedComponent > -1  &&  indexSelectedComponent < components.size()
				&&  components.get(indexSelectedComponent) == ec
				&&  propURI != null  &&  propURI.equals(propertiesModel.getSelectedItem()))
			eventsModel.addValue((newValue == null)? "Null value" : newValue);
	}
	
	public void componentsReplaced(ExternalComponent[] news) {
		if (news != null)
			for (int i=0;  i<news.length;  i++)
				if (news[i] != null)
					components.add(news[i]);
	}
	
	private static void componentSelected(int index) {
		indexSelectedComponent = index - 1;
		if (index > 0  &&  indexSelectedComponent < components.size()) {
			ExternalComponent ec = components.get(indexSelectedComponent);
			String type = ec.getTypeURI();
			Location loc = ec.getLocation();
			lblCompID.setText(ec.getOntResource().getLocalName());
			lblType.setText(type.substring(type.lastIndexOf('/')+1));
			lblLocation.setText((loc == null)? "No location" : loc.getLocalName());
			
			propertiesModel.clear();
			for (Enumeration<String> e = ec.enumerateProperties();  e.hasMoreElements(); )
				propertiesModel.add(e.nextElement());
		} else {
			lblCompID.setText("None");
			lblType.setText("Type");
			lblLocation.setText("Location");
			propertiesModel.clear();
		}
	}
	
	private static void propertySelected(String prop) {
		eventsModel.clear();
		notifications.setModel(eventsModel);
		
		if (StringUtils.isNullOrEmpty(prop)) {
			valPullAddress.setText("the pull address...");
			valPushAddress.setText("the push address...");
			valSetAddress.setText("the set address...");
		} else {
			ExternalComponent ec = components.get(indexSelectedComponent);
			ExternalDatapoint ed = ec.getDatapoint(prop);
			valPullAddress.setText(String.valueOf(ed.getPullAddress()));
			valPushAddress.setText(String.valueOf(ed.getPushAddress()));
			valSetAddress.setText(String.valueOf(ed.getSetAddress()));

			EventsModel em = null;
			if (ed instanceof ConfiguredDatapoint) {
				em = (EventsModel) ((ConfiguredDatapoint) ed).getProperty(PROP_MY_EVENT_MODEL);
				if (em == null) {
					em = self.new EventsModel();
					((ConfiguredDatapoint) ed).setProperty(PROP_MY_EVENT_MODEL, em);
				}
				notifications.setModel(em);
			}
		}
		lblValueFetched.setText("value fetched...");
		valueToWrite.setText("value to write...");
	}
}

package edu.uci.ics.archtrace.gui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.uci.ics.archtrace.connectors.ArchTraceConnector;
import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.RootElement;
import edu.uci.ics.archtrace.model.RootElementCollection;
import edu.uci.ics.archtrace.policies.PolicyManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * A panel that allows creation or removal of Architectures/Repositories
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 30, 2004
 */
public abstract class ConfigListPanel extends JPanel {

	/**
	 * List of root elements
	 */
	private JList elementsList;
	
	/**
	 * Model of root elements
	 */
	private ConfigListModel elementListModel;

    /**
     * Name of the selected root element
     */
    private JTextField nameField;
    
    /**
     * Url of the selected root element
     */
	private JTextField urlField;
	
	/**
	 * Connector of the selected root element
	 */
	private JComboBox connectorComboBox;

	/**
	 * Creates the panel
	 * @param sample
	 */
	public ConfigListPanel(final RootElementCollection element, Object[] connectors) {
		super(new BorderLayout(5, 5));
		
		elementListModel = new ConfigListModel(element);
		elementsList = new JList(elementListModel);
		JScrollPane scrollPane = new JScrollPane(elementsList);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Configured " + element.getName()));
		
		// Configures the list
		elementsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		elementsList.setLayoutOrientation(JList.VERTICAL);
		elementsList.setVisibleRowCount(-1);
		
		// Creates the name panel
		JPanel namePanel = new JPanel(new BorderLayout(5, 5));
		namePanel.add(new JLabel("Name: "), BorderLayout.WEST);
		nameField = new JTextField();
		namePanel.add(nameField, BorderLayout.CENTER);
		
		// Creates the URL panel
		JPanel urlPanel = new JPanel(new BorderLayout(5, 5));
		urlPanel.add(new JLabel("URL: "), BorderLayout.WEST);
		urlField = new JTextField();
		urlPanel.add(urlField, BorderLayout.CENTER);

		// Creates the Connector panel
		JPanel connectorPanel = new JPanel(new BorderLayout(5, 5));
		connectorPanel.add(new JLabel("Connector: "), BorderLayout.WEST);
		connectorComboBox = new JComboBox(connectors);
		connectorPanel.add(connectorComboBox, BorderLayout.CENTER);
		
		// Creates the edit panel (name + url + connector)
		JPanel editPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		editPanel.add(namePanel);
		editPanel.add(urlPanel);
		editPanel.add(connectorPanel);
		
		// Create the buttons panel
		JButton addButton = new JButton("Add");
		JButton removeButton = new JButton("Remove");
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		buttonsPanel.add(addButton);
		buttonsPanel.add(removeButton);
						
		// Sets listeners to the Add button		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				GUIManager.getInstance().run(new Runnable() {
					public void run() {
						synchronized(element) {
							try {
								//TODO: implement update - If there is an element selected, remove and add this new one
								String connectorName = (String)connectorComboBox.getSelectedItem();
								// TODO: Connector is stateless. Use only one of each type for all elements
								ArchTraceConnector connector = ConnectorManager.getInstance().createConnector(connectorName);
	
								PolicyManager.getInstance().setEnabled(false);
								RootElement newElement = (RootElement)connector.get(nameField.getText(), urlField.getText());

								addElement(newElement);
								addTraces(newElement);								

								resetPanel();
							} catch (Exception e) {
								GUIManager.getInstance().showErrorDialog(e);
							} catch (OutOfMemoryError error) {
								Exception e = new Exception("Out of memory (please, set a higher -Xmx argument)", error);
								GUIManager.getInstance().showErrorDialog(e);
							} finally {
								PolicyManager.getInstance().setEnabled(true);
							}
						}
					}
				});
			}
		});
		
		// Sets listeners to the Remove button
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				synchronized(element) {
					RootElement selectedElement = (RootElement)elementsList.getSelectedValue();
					if (selectedElement != null) {
						TraceManager traceManager = TraceManager.getInstance();
						Set<Trace> traces = traceManager.getAllTraces(selectedElement);
						traceManager.removeTraces(traces);
						
						removeElement(selectedElement);
						
						resetPanel();
					}
				}
			}
		});
		
		// Sets listeners to the list
		ToolTipManager.sharedInstance().registerComponent(elementsList);
		elementsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					RootElement selectedElement = (RootElement)elementsList.getSelectedValue();
					if (selectedElement != null) {
						nameField.setText(selectedElement.getName());
					    urlField.setText(selectedElement.getUrl());
						String connectorName = ConnectorManager.getInstance().getConnectorName(selectedElement.getConnector());
						connectorComboBox.setSelectedItem(connectorName);
					}
				}
			}
		});
		
		
		// Create the top panel
		JPanel topPanel = new JPanel(new BorderLayout(5, 5));
		topPanel.setBorder(BorderFactory.createTitledBorder("New/Selected " + element.getName()));
		topPanel.add(editPanel, BorderLayout.CENTER);
		topPanel.add(buttonsPanel, BorderLayout.SOUTH);
		
		// Configure the panel
		this.add(topPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
	}
	
	/**
	 * Clear all fields of the panel
	 */
	private void resetPanel() {
	    nameField.setText("");
		urlField.setText("");
		connectorComboBox.setSelectedIndex(0);
		elementsList.clearSelection();
		elementListModel.fireContentsChanged();
	}
	
	/**
	 * Adds an element to the collection
	 * (needed to avoid unchecked warnings)
	 */
	protected abstract void addElement(RootElement element);
	
	/**
	 * Removes an element from the collection
	 * (needed to avoid unchecked warnings)
	 */
	protected abstract void removeElement(RootElement element);
	
	/**
	 * Add traces to a root element
	 */
	protected abstract void addTraces(RootElement element);
}
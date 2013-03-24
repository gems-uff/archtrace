package edu.uci.ics.archtrace.gui.config;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import edu.uci.ics.archtrace.ArchTrace;

/**
 * Configuration dialog of ArchTrace
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 30, 2004
 */
public class ConfigDialog extends JDialog {

	/**
	 * Creates the dialog
	 */
	public ConfigDialog(Frame owner) {
		super(owner, "ArchTrace Configuration", true);
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		
		// Creates the preferences panel
		tabbedPane.addTab("Preferences", new JScrollPane(new ConfigPreferencesPanel()));
		
		// Creates the arch panel
		if (ArchTrace.getInstance().getRunningMode() != ArchTrace.ARCHSTUDIO_MODE)
			tabbedPane.addTab("Architectures", new ConfigArchitecturesPanel());
		
		// Creates the cm panel
		tabbedPane.addTab("Repositories", new ConfigRepositoriesPanel());
		
		// Creates the policies panel 
		tabbedPane.addTab("Policies", new JScrollPane(new ConfigPoliciesPanel()));
		
		// Close button
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		// Botton panel
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		bottomPanel.add(closeButton);
		
		// Main panel
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		
		this.setContentPane(mainPanel);
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});
	}

	/**
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		this.setLocationRelativeTo(this.getOwner());
		super.setVisible(visible);
	}
}
package edu.uci.ics.archtrace.gui.config;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.persistence.Preferences;

/**
 * Panel that allows configuration of user preferences
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 7, 2004
 */
public class ConfigPreferencesPanel extends JPanel implements Scrollable {

	/**
	 * Creates the panel
	 */
	public ConfigPreferencesPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
		final Preferences preferences = PersistenceManager.getInstance().getPreferences();
		StringBuffer text;
		Box box;

		// Show config check-box
		text = new StringBuffer();
		text.append("<html>")
		    .append("Always show this window when ArchTrace starts.")
			.append("<br>(access to this window is also available in the ArchTrace main menu)")
			.append("</html>");		
		final JCheckBox showConfigCheckbox = new JCheckBox(text.toString(), preferences.isShownAutomatically());		
		showConfigCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preferences.setShownAutomatically(showConfigCheckbox.isSelected());
			}
		});
		box = Box.createHorizontalBox();
		box.add(showConfigCheckbox);
		this.add(box);
		this.add(Box.createVerticalStrut(5));
		
		// Save check-box
		text = new StringBuffer();
		text.append("<html>")
			.append("<p>Automatically save when exiting ArchTrace.</p>")
			.append("</html>");		
		final JCheckBox saveCheckbox = new JCheckBox(text.toString(), preferences.isSaveAutomatically());		
		saveCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preferences.setSaveAutomatically(saveCheckbox.isSelected());
			}
		});
		box = Box.createHorizontalBox();
		box.add(saveCheckbox);
		this.add(box);
		this.add(Box.createVerticalStrut(5));
		
		// User Workspace
		final JTextField workspaceField = new JTextField(preferences.getUserWorkspace());
		workspaceField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                preferences.setUserWorkspace(workspaceField.getText());
            }
            
            public void insertUpdate(DocumentEvent e) {
                preferences.setUserWorkspace(workspaceField.getText());
            }
            
            public void removeUpdate(DocumentEvent e) {
                preferences.setUserWorkspace(workspaceField.getText());
            }
		});
		box = Box.createHorizontalBox();
		box.add(new JLabel("User's workspace:"));
		box.add(workspaceField);
		this.add(box);
		this.add(Box.createVerticalStrut(5));
		
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}
	
	/**
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	/**
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL)
			return visibleRect.height;
		else
			return visibleRect.width;
	}

	/**
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL)
			return visibleRect.height / 10;
		else
			return visibleRect.width / 10;
	}
}

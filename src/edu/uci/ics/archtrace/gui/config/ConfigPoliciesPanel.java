package edu.uci.ics.archtrace.gui.config;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.ArchitecturalElementEvolutionPolicy;
import edu.uci.ics.archtrace.policies.ConfigurationItemEvolutionPolicy;
import edu.uci.ics.archtrace.policies.Policies;
import edu.uci.ics.archtrace.policies.PostTracePolicy;
import edu.uci.ics.archtrace.policies.PreTracePolicy;

/**
 * Allows enabling/disabling policies in ArchTrace
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 31, 2004
 */
public class ConfigPoliciesPanel extends JPanel implements Scrollable {

	/**
	 * Creates the config policies panel
	 */
	public ConfigPoliciesPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Policies policies = PersistenceManager.getInstance().getPolicies();
		addPolicies("Pre-trace policies", policies, PreTracePolicy.class);
		addPolicies("Post-trace policies", policies, PostTracePolicy.class);
		addPolicies("Architectural element evolution policies", policies, ArchitecturalElementEvolutionPolicy.class);
		addPolicies("Configuration item evolution policies", policies, ConfigurationItemEvolutionPolicy.class);
		
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Add a set of policies of a given type in the panel
     */
    private void addPolicies(String typeName, final Policies policies, Class<? extends ArchTracePolicy> type) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(typeName));
        
        for (final ArchTracePolicy policy : policies.get(type)) {
			final JCheckBox checkbox = new JCheckBox("<html>" + policy.getDescription() + "</html>");
			checkbox.setSelected(policies.isEnabled(policy));
			checkbox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					policies.setEnabled(policy, checkbox.isSelected());
				}
			});
			
			JButton rationaleButton = new JButton("Rationale...");
			rationaleButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().showMessageDialog("Policy Rationale", policy.getDescription(), "Explanation", policy.getRationale(), true, JOptionPane.INFORMATION_MESSAGE);
				}
			});
			
			JPanel policyPanel = new JPanel();
			policyPanel.setLayout(new BoxLayout(policyPanel, BoxLayout.X_AXIS));
			policyPanel.add(checkbox);
			policyPanel.add(rationaleButton);

			panel.add(policyPanel);
			panel.add(Box.createVerticalStrut(5));
		}
        
        this.add(panel);
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

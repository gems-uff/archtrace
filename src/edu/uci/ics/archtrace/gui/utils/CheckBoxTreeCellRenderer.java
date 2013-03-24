package edu.uci.ics.archtrace.gui.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;


/**
 * Render checkboxes in the tree
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 4, 2004
 */
public abstract class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer {

	/**
	 * Real tree cell renderer
	 */
	private DefaultTreeCellRenderer renderer;
	
	/**
	 * Check box
	 */
	private JCheckBox checkbox;
	
	/**
	 * Invisible panel to complement the checkbox
	 */
	private JPanel panel;
	
	/**
	 * Constructs the renderer
	 */
	public CheckBoxTreeCellRenderer() {
		super(new BorderLayout());

		checkbox = new JCheckBox();
		this.add(checkbox, BorderLayout.WEST);
		checkbox.setForeground(UIManager.getColor("Tree.textForeground"));
		checkbox.setBackground(UIManager.getColor("Tree.textBackground"));

		renderer = new DefaultTreeCellRenderer();
		this.add(renderer, BorderLayout.CENTER);

		panel = new JPanel();
		this.add(panel, BorderLayout.EAST);
		panel.setForeground(UIManager.getColor("Tree.textForeground"));
		panel.setBackground(UIManager.getColor("Tree.textBackground"));
		panel.setPreferredSize(checkbox.getPreferredSize());

		this.setForeground(UIManager.getColor("Tree.textForeground"));
		this.setBackground(UIManager.getColor("Tree.textBackground"));
	}

	/**
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		// Process the renderer
		renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		
		// Call specific treatment for the checkbox and label
		prepareForRendering(value, checkbox, renderer);
				
		// If there is no checkbox, put the panel to reserve space.
		panel.setVisible(!checkbox.isVisible());

		// Revalidade this component to recalculate the prefered size (the text has been changed)
		this.revalidate();
		
		return this;
	}
	
	/**
	 * Inform if a click in a given point means the component should be selected
	 */
	public boolean shouldSelect(Point point) {
		Point componentLocation = this.getBounds().getLocation();
		point.translate(-componentLocation.x, -componentLocation.y);
		return renderer.getBounds().contains(point);
	}

	/**
	 * Add an action listener to the checkbox
	 */
	public void addActionListener(ActionListener listener) {
		checkbox.addActionListener(listener);
	}
	
	/**
	 * Prepare the checkBox and Label for rendering.
	 * Should be implemented by sub-classes.
	 */
	protected abstract void prepareForRendering(Object value, JCheckBox checkbox, JLabel label);
}

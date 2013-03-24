package edu.uci.ics.archtrace.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;

/**
 * Allows editing checkboxes in the tree
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 3, 2004
 */
public abstract class CheckBoxTreeCellEditor extends CheckBoxTreeCellRenderer implements TreeCellEditor {
	
	/**
	 * Selected value
	 */
	private Object selectedValue;
	
	/**
	 * Renderer of editable components
	 */
	private CheckBoxTreeCellRenderer renderer;
	
	/**
	 * Cell editor listeners
	 */
	private List<CellEditorListener> cellEditorListeners;
	
	/**
	 * Constructs the editor
	 */
	public CheckBoxTreeCellEditor() {
		cellEditorListeners = new ArrayList<CellEditorListener>();
		
		renderer = new CheckBoxTreeCellRenderer() {
			protected void prepareForRendering(Object value, JCheckBox checkbox, JLabel label) {
				CheckBoxTreeCellEditor.this.prepareForRendering(value, checkbox, label);
			}
		};
		
		renderer.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				// Close the editing mode ASAP
				fireEditingCanceled();
			}
		});
		
		renderer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Process the click
				processCheckBoxClick(selectedValue);
				
				// Close the editing mode ASAP
				fireEditingCanceled();	
			}
		});
	}

	/**
	 * Every cell is editable (even it does not have a checkbox to edit...)
	 * It does not worth computing if it has or not a checkbox...
	 * 
	 * @see javax.swing.CellEditor#isCellEditable(java.util.EventObject)
	 */
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}
	
	/**
	 * Only select the cell if the it the click was over the label of TreeComponent
	 * 
	 * @see javax.swing.CellEditor#shouldSelectCell(java.util.EventObject)
	 */
	public boolean shouldSelectCell(EventObject anEvent) {
		try {
			MouseEvent mouseEvent = (MouseEvent)anEvent;
			return renderer.shouldSelect(mouseEvent.getPoint());
		} catch (Exception e) { 
			return false;
		}	
	}

	/**
	 * @see javax.swing.tree.TreeCellEditor#getTreeCellEditorComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int)
	 */
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		selectedValue = value;
		return renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
	}

	/**
	 * @see javax.swing.CellEditor#cancelCellEditing()
	 */
	public void cancelCellEditing() {
		// Ignore!
	}

	/**
	 * @see javax.swing.CellEditor#stopCellEditing()
	 */
	public boolean stopCellEditing() {
		return false;
	}

	/**
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return null;
	}

	/**
	 * @see javax.swing.CellEditor#addCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void addCellEditorListener(CellEditorListener l) {
		cellEditorListeners.add(l);
	}

	/**
	 * @see javax.swing.CellEditor#removeCellEditorListener(javax.swing.event.CellEditorListener)
	 */
	public void removeCellEditorListener(CellEditorListener l) {
		cellEditorListeners.remove(l);
	}
	
	/**
	 * Notify that a connection just started
	 */
	public void fireEditingCanceled() {
		ChangeEvent event = new ChangeEvent(this);
		for (CellEditorListener listener : cellEditorListeners) {
			listener.editingCanceled(event);
		}
	}

	/**
	 * Process the click on a checkbox.
	 * Should be implemented by sub-classes.
	 * @param value Node selected in the tree
	 */
	protected abstract void processCheckBoxClick(Object value);
}

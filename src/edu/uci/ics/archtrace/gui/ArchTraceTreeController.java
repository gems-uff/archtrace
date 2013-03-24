package edu.uci.ics.archtrace.gui;

import java.awt.Cursor;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import edu.uci.ics.archtrace.gui.utils.CheckBoxTreeCellEditor;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.gui.utils.IconManager;
import edu.uci.ics.archtrace.model.ArchTraceElement;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Class responsible to control the rendering and click on elements at CM Tree
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 16, 2004
 */
public class ArchTraceTreeController extends CheckBoxTreeCellEditor {
	
	/**
	 * Opposite tree
	 */
	private JTree oppositeTree;
	
	/**
	 * Construct the controller with references to both trees
	 */
	public ArchTraceTreeController(JTree oppositeTree) {
		this.oppositeTree = oppositeTree;
	}

	/**
	 * @see edu.uci.ics.archtrace.gui.utils.CheckBoxTreeCellRenderer#prepareForRendering(java.lang.Object, javax.swing.JCheckBox, javax.swing.JLabel)
	 */
	protected void prepareForRendering(Object value, JCheckBox checkbox, JLabel label) {
		ArchTraceElement element = (ArchTraceElement)value;

		label.setIcon(IconManager.getInstance().getIcon(element.getType()));		
				
		// Process the checkbox
		TreePath path = oppositeTree.getSelectionPath();
		if (path != null) {
			ArchTraceElement oppositeElement = (ArchTraceElement)path.getLastPathComponent();  
			Set oppositeTraces = TraceManager.getInstance().getTraces(oppositeElement);
			Set<Trace> traces = TraceManager.getInstance().getTraces(element);

			traces.retainAll(oppositeTraces);
			if (!traces.isEmpty())
				checkbox.setIcon(IconManager.getInstance().getIcon(IconManager.CHECKBOX_SELECTED));
			else {
				oppositeTraces = TraceManager.getInstance().getAllTraces(oppositeElement);
				traces = TraceManager.getInstance().getAllTraces(element);
				
				traces.retainAll(oppositeTraces);
				if (!traces.isEmpty())
					checkbox.setIcon(IconManager.getInstance().getIcon(IconManager.CHECKBOX_SEMISELECTED));
				else
					checkbox.setIcon(IconManager.getInstance().getIcon(IconManager.CHECKBOX_UNSELECTED));
			}

			//checkbox.setEnabled(element.getAllowsTrace() && oppositeElement.getAllowsTrace());
			checkbox.setVisible(true);
		} else
			checkbox.setVisible(false);
	}	
		
	/**
	 * @see edu.uci.ics.archtrace.gui.utils.CheckBoxTreeCellEditor#processCheckBoxClick(java.lang.Object)
	 */
	protected void processCheckBoxClick(Object value) {
		GUIManager.getInstance().setMainWindowCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try {
			TreePath path = oppositeTree.getSelectionPath();
			if (path != null) {
				ArchitecturalElement archElement;
				ConfigurationItem cmElement;
				
				try {
					archElement = (ArchitecturalElement)value;
					cmElement = (ConfigurationItem)path.getLastPathComponent();
				} catch (Exception e) {
					archElement = (ArchitecturalElement)path.getLastPathComponent();
					cmElement = (ConfigurationItem)value;
				}
				
				Trace trace = new Trace(archElement, cmElement); 
				if (TraceManager.getInstance().hasTrace(trace))
					archElement.getArchitecture().removeTrace(trace);
				else
					archElement.getArchitecture().addTrace(trace);
			}			
		} catch (Exception e) {
			GUIManager.getInstance().addPolicyMessage("Traces can be created only among architectural elements and configuration items. Trace aborted!");
		} finally {
			GUIManager.getInstance().setMainWindowCursor(Cursor.getDefaultCursor());
		}
	}
}
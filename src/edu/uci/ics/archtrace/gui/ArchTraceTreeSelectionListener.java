package edu.uci.ics.archtrace.gui;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

/**
 * This class is responsible for processing selection on JTrees containing ArchTraceElement objects
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public class ArchTraceTreeSelectionListener implements TreeSelectionListener {

	/**
	 * Opposite tree to repaint after processing selection
	 */
	private JTree oppositeTree;
	
	/**
	 * @param editor
	 * 
	 */
	public ArchTraceTreeSelectionListener(JTree oppositeTree) {
		this.oppositeTree = oppositeTree;
	}

	/**
	 * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
	 */
	public void valueChanged(TreeSelectionEvent e) {
		TreePath path = e.getNewLeadSelectionPath();
		
		if (path != null) {
			//ArchTraceElement element = (ArchTraceElement)path.getLastPathComponent();

			
			// Repaint the opposite tree
			oppositeTree.repaint();
		}
	}
}

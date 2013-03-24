package edu.uci.ics.archtrace.gui;

import java.awt.Cursor;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeWillExpandListener;

import edu.uci.ics.archtrace.gui.utils.GUIManager;

/**
 * This class is a listener of tree expansion/collapsion events
 * It changes the icon to prevent dead-air
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 27, 2004
 */
public class ArchTraceTreeExpansionListener implements TreeWillExpandListener, TreeExpansionListener {

	/**
	 * Singleton instance
	 */
	private static ArchTraceTreeExpansionListener instance;
	
	/**
	 * Private singleton constructor
	 */
	private ArchTraceTreeExpansionListener() {
		// Singleton Constructor
	}
	
	/**
	 * Creates the singleton instance
	 */
	public static synchronized ArchTraceTreeExpansionListener getInstance() {
		if (instance == null)
			instance = new ArchTraceTreeExpansionListener();
		return instance;
	}
	
	/**
	 * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillExpand(TreeExpansionEvent event) {
		GUIManager.getInstance().setMainWindowCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}	
	
	/**
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeExpanded(TreeExpansionEvent event) {
		GUIManager.getInstance().setMainWindowCursor(Cursor.getDefaultCursor());
	}

	/**
	 * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillCollapse(TreeExpansionEvent event) {
		GUIManager.getInstance().setMainWindowCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));	
	}
	
	/**
	 * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeCollapsed(TreeExpansionEvent event) {
		GUIManager.getInstance().setMainWindowCursor(Cursor.getDefaultCursor());
	}
}
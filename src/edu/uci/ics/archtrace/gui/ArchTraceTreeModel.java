package edu.uci.ics.archtrace.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import edu.uci.ics.archtrace.model.ArchTraceElement;
import edu.uci.ics.archtrace.model.RootElementCollection;

/**
 *  A tree model that uses ArchTraceElement objects.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 13, 2004
 */
public class ArchTraceTreeModel implements TreeModel {

	/**
	 * Root element of the model
	 */
	private RootElementCollection root;
	
	/**
	 * Model Listeners
	 */
	private Collection<TreeModelListener> listeners;
	
	/**
	 * Creates a JList model aware of ArchTrace elements
	 */
	public ArchTraceTreeModel(RootElementCollection root) {
		this.root = root;
		listeners = new ArrayList<TreeModelListener>();
	}

	/**
	 * @see javax.swing.tree.TreeModel#getRoot()
	 */
	public Object getRoot() {
		return root;
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent) {
		ArchTraceElement element = (ArchTraceElement)parent;
		return element.getChildCount();
	}

	/**
	 * @see javax.swing.tree.TreeModel#isLeaf(java.lang.Object)
	 */
	public boolean isLeaf(Object node) {
		ArchTraceElement element = (ArchTraceElement)node;
		return !element.mayHaveChildren();
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index) {
		ArchTraceElement element = (ArchTraceElement)parent;
		return element.getChild(index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getIndexOfChild(java.lang.Object, java.lang.Object)
	 */
	public int getIndexOfChild(Object parent, Object child) {
		ArchTraceElement parentElement = (ArchTraceElement)parent;
		ArchTraceElement element = (ArchTraceElement)child;
		return parentElement.getChildIndex(element);
	}

	/**
	 * @see javax.swing.tree.TreeModel#valueForPathChanged(javax.swing.tree.TreePath, java.lang.Object)
	 */
	public void valueForPathChanged(TreePath path, Object newValue) {
		// Ignored
	}
	
	/**
	 * @see javax.swing.tree.TreeModel#addTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);		
	}

	/**
	 * @see javax.swing.tree.TreeModel#removeTreeModelListener(javax.swing.event.TreeModelListener)
	 */
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	/**
	 * Notify change in an ArchTrace element 
	 */
	public void fireElementChanged(Object[] path, int position, ArchTraceElement element) {
		final TreeModelEvent event = new TreeModelEvent(this, path, new int[] { position }, new Object[] { element });

		run(new Runnable() {
			public void run() {
				for (TreeModelListener listener : listeners) {
					listener.treeNodesChanged(event);
				}
			}
		});
	}
	
	/**
	 * Notify insertion of an ArchTrace element 
	 */
	public void fireElementInserted(Object[] path, int position, ArchTraceElement element) {
		final TreeModelEvent event = new TreeModelEvent(this, path, new int[] { position }, new Object[] { element });
		
		run(new Runnable() {
			public void run() {
				for (TreeModelListener listener : listeners) {
					listener.treeNodesInserted(event);
				}
			}
		});
	}
	
	/**
	 * Notify removal of an ArchTrace element 
	 */
	public void fireElementRemoved(Object[] path, int position, ArchTraceElement element) {
		final TreeModelEvent event = new TreeModelEvent(this, path, new int[] { position }, new Object[] { element });

		run(new Runnable() {
		    public void run() {
				for (TreeModelListener listener : listeners) {
					listener.treeNodesRemoved(event);
				}
			}
		});
	}
	
	/**
	 * Run a specific code in the swing thread
	 */
	private void run(Runnable code) {
	    if (SwingUtilities.isEventDispatchThread()) {
	        code.run();
	    } else {
	        SwingUtilities.invokeLater(code);
	    }
	}
}

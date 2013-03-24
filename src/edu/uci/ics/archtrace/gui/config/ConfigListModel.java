package edu.uci.ics.archtrace.gui.config;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import edu.uci.ics.archtrace.model.ArchTraceElement;

/**
 *  A list model that uses an ArchTraceElement object to show its subelements.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public class ConfigListModel implements ListModel {

	/**
	 * Element being processed by the model
	 * The subelements of this element will be shown in the list 
	 */
	private ArchTraceElement element;
	
	/**
	 * Model Listeners
	 */
	private Collection<ListDataListener> listeners;
	
	/**
	 * Creates a JList model aware of ArchTrace elements
	 */
	public ConfigListModel(ArchTraceElement element) {
		listeners = new ArrayList<ListDataListener>();
		setElement(element);
	}

	/**
	 * Clear the list
	 */
	public void clear() {
		this.setElement(null);
	}	

	/**
	 * Set the current element
	 */
	public void setElement(ArchTraceElement element) {
		this.element = element;
		fireContentsChanged();
	}
	
	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		int size = 0;
		
		if (element != null) {
			size = element.getChildCount();
		}
			
		return size;
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		Object child = null;
		
		if (element != null) {
			child = element.getChild(index);
		}
			
		return child;
	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	/**
	 * Notify a COMPLETE change in the list.
	 */
	public void fireContentsChanged() {
		ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, Integer.MAX_VALUE);
		for (ListDataListener listener : listeners) {
			listener.contentsChanged(event);
		}
	}
}

package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.gui.ArchTraceTreeModel;

/**
 * TODO Document Repositories
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 1, 2004
 */
public class RootElementCollection<E extends RootElement> extends ArchTraceCollection<E> {

	/**
	 * External visible model for ArchTrace trees
	 */
	private ArchTraceTreeModel model;
	
	/**
	 * Create a collection of root elements
	 */
	public RootElementCollection(String name) {
		super(name);
		model = new ArchTraceTreeModel(this);
	}

	/**
	 * @return Returns the model.
	 */
	public ArchTraceTreeModel getModel() {
		return model;
	}
	
	/**
	 * Restore all root elements
	 */
	public synchronized void restore() {
		for (E element : getChildren()) {
			try {
				element.restore();
			} catch (ConnectionException e) {
				Logger.global.info("Could not restore url " + element.getUrl());
			}
		}
	}
	
	/**
	 * Update all root elements
	 */
	public synchronized void update() {
		for (E element : getChildren()) {
			try {
				element.update();
			} catch (ConnectionException e) {
				Logger.global.info("Could not update url " + element.getUrl());
			}
		}
	}
	
	/**
	 * Get the first element that has an specific text in the name or the url ends with this text
	 */
	public synchronized E get(String searchText) {
	    for (E element : getChildren()) {
	        if ((element.getName().equalsIgnoreCase(searchText)) ||
	            (element.getUrl().toLowerCase().endsWith(searchText.toLowerCase()))) {
	            return element;
	        }
	    }
	    return null;
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#fireElementChanged(java.util.List)
	 */
	public void fireElementChanged(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		model.fireElementChanged(newPath.toArray(), position, element);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#fireElementInserted(java.util.List, int, edu.uci.ics.archtrace.model.ArchTraceElement)
	 */
	public void fireElementInserted(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		model.fireElementInserted(newPath.toArray(), position, element);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#fireElementRemoved(java.util.List, int, edu.uci.ics.archtrace.model.ArchTraceElement)
	 */
	public void fireElementRemoved(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		model.fireElementRemoved(newPath.toArray(), position, element);
	}
}

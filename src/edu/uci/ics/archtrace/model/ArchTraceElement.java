package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a generic element. Should be extended by each specific ArchTrace element.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 21, 2004
 */
public abstract class ArchTraceElement {
	
	/**
	 * Element name.
	 */
	private String name;

	/**
	 * Set of parent elements of this element
	 * A tree node has only one parent element, but this structure is a DAG (Directed Acyclic Graph).
	 */
	private transient Collection<ArchTraceElement> parents;
	
	/**
	 * List of children elements of this element (lazy loaded)
	 */
	private transient List<? extends ArchTraceElement> children;

	/**
	 * Creates a new ArchTraceElement.
	 */
	public ArchTraceElement(String name) {
	    this.name = name;
		parents = new HashSet<ArchTraceElement>();
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
		this.fireElementChanged();
	}
	
	/**
	 * Provides the iterator over  parents
	 */
	public Collection<ArchTraceElement> getParents() {
		return Collections.unmodifiableCollection(parents);
	}
	
	/**
	 * Adds a new parent to this element
	 */
	public void addParent(ArchTraceElement parent) {
		parents.add(parent);
	}
	
	/**
	 * Removes a specific parent from this element
	 */
	public void removeParent(ArchTraceElement parent) {
		parents.remove(parent);		
	}

	/**
	 * Gets all subelements of this element.
	 * Should be implemented by sub-elements.
	 */
	protected abstract List<? extends ArchTraceElement> getChildren();
	
	/**
	 * Informs the type of this element
	 */
	public abstract int getType();
	
// ----------------------------------------------------------------------------
//						Model update notification methods
// ----------------------------------------------------------------------------

	/**
	 * Notify the parent objects that an ArchTrace Element has been added
	 */
	protected void fireElementInserted(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		for (ArchTraceElement parent : getParents()) {
			parent.fireElementInserted(newPath, position, element);
		}
	}
	
	/**
	 * Notify the parent objects that an ArchTrace Element has been removed
	 */
	protected void fireElementRemoved(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		for (ArchTraceElement parent : getParents()) {
			parent.fireElementRemoved(newPath, position, element);
		}
	}
	
	/**
	 * Notify the parent objects that this ArchTrace Element has been changed
	 */
	public void fireElementChanged() {
		for (ArchTraceElement parent : getParents()) {
			List<ArchTraceElement> path = Collections.emptyList();
			parent.fireElementChanged(path, parent.getChildIndex(this), this);
		}
	}
	
	/**
	 * Notify the parent objects that a sub ArchTrace Element has been changed
	 */
	protected void fireElementChanged(List<ArchTraceElement> path, int position, ArchTraceElement element) {
		List<ArchTraceElement> newPath = new ArrayList<ArchTraceElement>(path);
		newPath.add(0, this);
		for (ArchTraceElement parent : getParents()) {
			parent.fireElementChanged(newPath, position, element);
		}
	}

// ----------------------------------------------------------------------------
//				Access methods for lazy loaded children information
// ----------------------------------------------------------------------------

	/**
	 * Informs if the element may have
	 * This is an optimization provide by TreeModel
	 */
	public abstract boolean mayHaveChildren();
	
	/**
	 * Provides the number of children
	 */
	public int getChildCount() {
		if (children == null)
			children = getChildren();
		
		return children.size();
	}
	
	/**
	 * Provides the children at a specified position
	 */
	public ArchTraceElement getChild(int index) {
		if (children == null)
			children = getChildren();
		
        return children.get(index);
	}

	/**
	 * Provides the index of a specified child
	 */
	public int getChildIndex(ArchTraceElement child) {
		if (children == null)
			children = getChildren();
		
		return children.indexOf(child);
	}

// ----------------------------------------------------------------------------
//							Overriden from Object
// ----------------------------------------------------------------------------
	
	/**
	 * Provides the element name as its textual representation
	 */
	public String toString() {
		return name;
	}
}

package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uci.ics.archtrace.utils.ArchTraceComparator;

/**
 * Represents a list of ArchTrace elements
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 27, 2004
 */
public class ArchTraceCollection<E extends ArchTraceElement> extends ArchTraceElement {

	/**
	 * Type of the collection
	 */
	private int type;
	
	/**
	 * Sub-elements
	 */
	private List<E> elements;
	
	/**
	 * Creates a new collection
	 */
	public ArchTraceCollection(String name) {
		this(name, Types.COLLECTION, null);
	}

	/**
	 * Creates a new collection with a specific parent
	 */
	public ArchTraceCollection(String name, int type, ArchTraceElement parent) {
		super(name);
		this.type = type;
		
		if (parent != null)
			this.addParent(parent);
		
		this.elements = new ArrayList<E>();
	}
	
	/**
	 * Clear the collection
	 */
	public synchronized void clear() {
		for (ArchTraceElement element : elements) {
			element.removeParent(this);
		}
		elements.clear();
	}
	
	/**
	 * Adds a new element to the collection
	 * The element should not exist in the collection
	 */
	public synchronized void add(E element) {
		element.addParent(this);
		int position = Collections.binarySearch(elements, element, ArchTraceComparator.getInstance());
		if (position < 0)
		    position = -position - 1;
		elements.add(position, element);
		List<ArchTraceElement> path = Collections.emptyList();
		fireElementInserted(path, position, element);
	}
	
	/**
	 * Remove a specific element from the collection
	 * The element should exist in the collection
	 */
	public synchronized void remove(E element) {
		element.removeParent(this);
		int position = Collections.binarySearch(elements, element, ArchTraceComparator.getInstance());
		elements.remove(position);
		List<ArchTraceElement> path = Collections.emptyList();
		fireElementRemoved(path, position, element);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getChildren()
	 */
	protected synchronized List<E> getChildren() {
		return elements;
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return true;
	}

	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		return type;
	}
}

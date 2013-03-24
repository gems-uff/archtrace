package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents a generic architectural element
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 27, 2004
 */
public class ArchitecturalElement extends ArchTraceElement {
	
	/**
	 * Type of the element
	 */
	private int type;
	
	/**
	 * Version of the element
	 */
	private String version;
	
	/**
	 * Immutable state of the element
	 */
	private boolean immutable;
	
	/**
	 * Id of the element
	 */
	private String id;
	
	/**
	 * Collection of Ids of the ancestry elements
	 * Collection [String]
	 */
	private Collection<String> ancestryIds;
	
	/**
	 * Creates an architectural element
	 */
	public ArchitecturalElement(String name, int type, String version, boolean immutable, String id, Collection<String> ancestryIds) {
		super(name);
		this.type = type;
		this.version = version;
		this.immutable = immutable;
		this.id = id;
		this.ancestryIds = ancestryIds;
	}
	
	/**
	 * Provides the architecture that contains this architectural element
	 */
	public Architecture getArchitecture() {
		// TODO ugly!!!
		return (Architecture)((ArchTraceCollection)getParents().iterator().next()).getParents().iterator().next();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getChildren()
	 */
	protected List<ArchTraceElement> getChildren() {
		return Collections.emptyList();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return false;
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @return Returns the immutable.
	 */
	public boolean isImmutable() {
		return immutable;
	}
	
	/**
	 * @param immutable The immutable to set.
	 */
	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
		this.fireElementChanged();
	}
	
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
		this.fireElementChanged();
	}
	
	/**
	 * Provides the collection of ancestry version of this architectural element
	 * All these versions are in the same level (direct ancestries)
	 */
	public Collection<ArchitecturalElement> getAncestries() {
		Collection<ArchitecturalElement> ancestries = new ArrayList<ArchitecturalElement>();
		
		for (String ancestryId : ancestryIds) {
			ancestries.add(this.getArchitecture().getArchitecturalElement(ancestryId));
		}
		
		return ancestries;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(this.getName());
		buffer.append(" (");
		if (version == null)
			buffer.append("not versioned");
		else
			buffer.append("version ").append(version);
		buffer.append(", ");
		if (immutable)
			buffer.append("immutable");
		else
			buffer.append("mutable");
		buffer.append(")");
		
		return buffer.toString();
	}
}

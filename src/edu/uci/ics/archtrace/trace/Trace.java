package edu.uci.ics.archtrace.trace;

import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;

/**
 * Represents a trace between Arch and CM
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - 05/08/2004
 */
public class Trace {

	// Elements in the trace
	private ArchitecturalElement architecturalElement;
	private ConfigurationItem configurationItem;	
	
	/**
	 * Create a new trace
	 */
	public Trace(ArchitecturalElement architecturalElement, ConfigurationItem configurationItem) {
		this.architecturalElement = architecturalElement;
		this.configurationItem = configurationItem;
	}
		
	/**
	 * @return Returns the architecturalElement.
	 */
	public ArchitecturalElement getArchitecturalElement() {
		return architecturalElement;
	}
	
	/**
	 * @return Returns the configurationItem.
	 */
	public ConfigurationItem getConfigurationItem() {
		return configurationItem;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (architecturalElement.hashCode() + configurationItem.hashCode());
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		try {
			Trace otherTrace = (Trace)obj;

			return (architecturalElement.equals(otherTrace.getArchitecturalElement()) &&
					configurationItem.equals(otherTrace.getConfigurationItem()));			
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer("Trace from ");
		buffer.append(architecturalElement);
		buffer.append(" to ");
		buffer.append(configurationItem);
		return buffer.toString();
	}
}
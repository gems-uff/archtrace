package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uci.ics.archtrace.connectors.ArchConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Represents a xADL architecture
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 21, 2004
 */
public class Architecture extends RootElement {
	
	/**
	 * Components of this architecture
	 */
	private transient ArchTraceCollection<ArchitecturalElement> components;
	
	/**
	 * Interfaces of this architecture
	 */
	private transient ArchTraceCollection<ArchitecturalElement> interfaces;
	
	/**
	 * Connectors of this architecture
	 */
	private transient ArchTraceCollection<ArchitecturalElement> connectors;
	
	/**
	 * Creates a new Architecture
	 */
	public Architecture(String name, String url, ArchConnector connector) {
		super(name, url, connector);

		components = new ArchTraceCollection<ArchitecturalElement>("Components", Types.COMPONENTS_VIEW, this);
		interfaces = new ArchTraceCollection<ArchitecturalElement>("Interfaces", Types.INTERFACES_VIEW, this);
		connectors = new ArchTraceCollection<ArchitecturalElement>("Connectors", Types.CONNECTORS_VIEW, this);
	}
	
	/**
	 * Provides the components of this architecture
	 */
	public ArchTraceCollection<ArchitecturalElement> getComponents() {
		return components;
	}
	
	/**
	 * Provides the interfaces of this architecture
	 */
	public ArchTraceCollection<ArchitecturalElement> getInterfaces() {
		return interfaces;
	}
	
	/**
	 * Provides the connectors of this architecture
	 */
	public ArchTraceCollection<ArchitecturalElement> getConnectors() {
		return connectors;
	}
	
	/**
	 * Provides the architectural element that has a given id
	 * Linear search -> O(n)
	 */
	public ArchitecturalElement getArchitecturalElement(String id) {
		// Search in the components collection
		for (ArchitecturalElement ae : components.getChildren()) {
			if (ae.getId().equals(id))
				return ae;
		}
		
		// Search in the interfaces collection
		for (ArchitecturalElement ae : interfaces.getChildren()) {
			if (ae.getId().equals(id))
				return ae;
		}
		
		// Search in the connectors collection
		for (ArchitecturalElement ae : connectors.getChildren()) {
			if (ae.getId().equals(id))
				return ae;
		}

		return null;
	}
	
	/**
	 * Provides all traces from any architectural element of this 
	 * architecture to configuration items in the given repositories
	 */
	public Collection<Trace> getTraces(Repositories repositories) {
		return ((ArchConnector)getConnector()).getTraces(this, repositories);
	}
	
	/**
	 * Provides all traces from any architectural element of this 
	 * architecture to configuration items in the given repository
	 */
	public Collection<Trace> getTraces(Repository repository) {
		return ((ArchConnector)getConnector()).getTraces(this, repository);
	}
	
	/**
	 * Adds a trace to the architecture
	 * @return informs if the execution was successful
	 */
	public boolean addTrace(Trace trace) {
		return ((ArchConnector)getConnector()).addTrace(this, trace);
	}
	
	/**
	 * Removes a trace from the architecture
	 * @return informs if the execution was successful
	 */
	public boolean removeTrace(Trace trace) {
		return ((ArchConnector)getConnector()).removeTrace(trace);
	}

 	/**
	 * Save all trace changes related to this architecture 
 	 * @throws ConnectionException
	 */
	public void save() throws ConnectionException {
		((ArchConnector)getConnector()).save(this);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getChildren()
	 */
	protected List<ArchTraceCollection> getChildren() {
		List<ArchTraceCollection> archElements = new ArrayList<ArchTraceCollection>();
			
		archElements.add(components);
		archElements.add(interfaces);
		archElements.add(connectors);
			
		return archElements;
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		return Types.ARCHITECTURE;
	}
}

package edu.uci.ics.archtrace.trace;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.archtrace.model.ArchTraceElement;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;

/**
 * Manage all information related to trace among architectural elements and 
 * configuration items
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 5, 2004
 */
public class TraceManager {

	/**
	 * Singleton instance
	 */
	private static TraceManager instance;
	
	/**
	 * Map from all involved ArchTraceElements (ArchitecturalElement,
	 * and ConfigurationItem) to a Set of Trace objects
	 */
	private Map<ArchTraceElement, Set<Trace>> directTraces;
	
	/**
	 * Indirect traces (traces of sub-elements)
	 */
	private Map<ArchTraceElement, Set<Trace>> indirectTraces;
	
	
	/**
	 * Constructs the manager
	 */
	private TraceManager() {
		directTraces = new HashMap<ArchTraceElement, Set<Trace>>();
		indirectTraces = new HashMap<ArchTraceElement, Set<Trace>>();
	}
	
	/**
	 * Provides the singleton instance
	 */
	public static synchronized TraceManager getInstance() {
		if (instance == null)
			instance = new TraceManager();
		return instance;
	}
	
	/**
	 * Resets the singleton instance
	 */
	public static synchronized void resetInstance() {
		instance = null;
	}
	
	/**
	 * Verify if a trace exist
	 */
	public boolean hasTrace(Trace trace) {
		// Could verify through architectural element or configuration item...
		return getDirectTraces(trace.getArchitecturalElement()).contains(trace);
	}
	
	/**
	 * Add a collection of traces
	 */
	public void addTraces(Collection<Trace> traces) {
		for (Trace trace : traces) {
			this.addTrace(trace);			
		}
	}

	/**
	 * Add a trace
	 */
	public synchronized void addTrace(Trace trace) {
		ArchitecturalElement archElement = trace.getArchitecturalElement();
		getDirectTraces(archElement).add(trace);
		addIndirectTrace(archElement, trace);
	
		ConfigurationItem cmElement = trace.getConfigurationItem();
		getDirectTraces(cmElement).add(trace);
		addIndirectTrace(cmElement, trace);
	}
	
	/**
	 * Recursivelly add indirect trace in parent elements of a given elements
	 */
	private void addIndirectTrace(ArchTraceElement element, Trace trace) {
		for (ArchTraceElement parent : element.getParents()) {
			getIndirectTraces(parent).add(trace);
			addIndirectTrace(parent, trace);			
		}
		element.fireElementChanged();
	}
	
	/**
	 * Remove a collection of traces
	 */
	public void removeTraces(Collection<Trace> traces) {
		for (Trace trace : traces) {
			this.removeTrace(trace);			
		}
	}

	/**
	 * Remove a trace
	 */
	public synchronized void removeTrace(Trace trace) {
		ArchitecturalElement archElement = trace.getArchitecturalElement();
		getDirectTraces(archElement).remove(trace);
		removeIndirectTrace(archElement, trace);
		
		ConfigurationItem cmElement = trace.getConfigurationItem();
		getDirectTraces(cmElement).remove(trace);
		removeIndirectTrace(cmElement, trace);
	}
	
	/**
	 * Recursivelly remove indirect trace in parent elements of a given elements
	 */
	private void removeIndirectTrace(ArchTraceElement element, Trace trace) {
		for (ArchTraceElement parent : element.getParents()) {
			getIndirectTraces(parent).remove(trace);
			removeIndirectTrace(parent, trace);			
		}
		element.fireElementChanged();
	}

	/**
	 * Provides all direct traces of a given element
	 */
	public Set<Trace> getTraces(ArchTraceElement element) {
		return new HashSet<Trace>(getDirectTraces(element));
	}
	
	/**
	 * Provides all indirect traces of a given element
	 */
	public Set<Trace> getAllTraces(ArchTraceElement element) {
		Set<Trace> result = getTraces(element);
		result.addAll(getIndirectTraces(element));
		return result;
	}
	
	/**
	 * Get (create if necessary) the set of direct traces of a given element
	 */
	private synchronized Set<Trace> getDirectTraces(ArchTraceElement element) {
		Set<Trace> result = directTraces.get(element);
		
		if (result == null) {
			result = new HashSet<Trace>();
			directTraces.put(element, result);	
		}
		
		return result;
	}
	
	/**
	 * Get (create if necessary) the set of indirect traces of a given element
	 */
	private synchronized Set<Trace> getIndirectTraces(ArchTraceElement element) {
		Set<Trace> result = indirectTraces.get(element);
		
		if (result == null) {
			result = new HashSet<Trace>();
			indirectTraces.put(element, result);	
		}

		return result;
	}

	/**
	 * Update all indirect traces of a given configuration
	 */
	public void updateIndirectTraces(Configuration configuration) {
		updateIndirectTraces(configuration, configuration);
	}
	
	/**
	 * Update all indirect traces of a given configuration.
	 * @param configuration Configuration to be updated.
	 * @param version Information used to optimize the update process.
	 * Only update configuration item in that version.
	 * @return Updated indirect traces.
	 */
	private Set<Trace> updateIndirectTraces(Configuration configuration, Configuration version) {
		Set<Trace> indirectTraces = getIndirectTraces(configuration);
		boolean wasEmpty = indirectTraces.isEmpty();
		indirectTraces.clear();
		
		for (int i = 0; i < configuration.getChildCount(); i++) {
			ConfigurationItem child = (ConfigurationItem)configuration.getChild(i);
			indirectTraces.addAll(getDirectTraces(child));
			
			// Optimization to avoid recursion in elements that were not changed
			if (child.getVersion() == version)
				indirectTraces.addAll(updateIndirectTraces(child, version));
			else
				indirectTraces.addAll(getIndirectTraces(child));
		}
		
		if (wasEmpty ^ indirectTraces.isEmpty())
			configuration.fireElementChanged();
		
		return indirectTraces;
	}	
}
package edu.uci.ics.archtrace.connectors;

import java.util.Collection;

import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Represents a connector that is able to load architectures
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public abstract class ArchConnector extends ArchTraceConnector {

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#get(java.lang.String)
	 */
	public RootElement get(String name, String url) throws ConnectionException {
		Architecture architecture = new Architecture(name, url, this);
		update(architecture);
		return architecture;
	}
	
	/**
	 * Adds a new trace in the architecture
	 * @return informs if the execution was successful
	 */
	public abstract boolean addTrace(Architecture architecture, Trace trace);

	/**
	 * Remove an existing trace from the architecture
	 * @return informs if the execution was successful
	 */
	public abstract boolean removeTrace(Trace trace);

	/**
	 * Get all traces from an architecture to a collection of repositories
	 */
	public abstract Collection<Trace> getTraces(Architecture architecture, Repositories repositories);

	/**
	 * Get all traces from an architecture to a repository
	 */
	public abstract Collection<Trace> getTraces(Architecture architecture, Repository repository);
	
	/**
	 * Save all modifications to traces on a given architecture
	 */
	public abstract void save(Architecture architecture) throws ConnectionException;
}
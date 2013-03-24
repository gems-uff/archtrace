package edu.uci.ics.archtrace.connectors;

import edu.uci.ics.archtrace.model.RootElement;

/**
 * Generic connector with listening support
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public abstract class ArchTraceConnector {

	/**
	 * Provides an RootElement loaded from a give url
	 * @throws ConnectionException
	 */
	public abstract RootElement get(String name, String url) throws ConnectionException;
	
	/**
	 * Restore a root element to the last running state
	 */
	public abstract void restore(RootElement element) throws ConnectionException;
	
	/**
	 * Update a given root element
	 */
	public abstract void update(RootElement element) throws ConnectionException;
}

package edu.uci.ics.archtrace.connectors;


import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;

/**
 * Generic interface to connect to subversion
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public abstract class CMConnector extends ArchTraceConnector {

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#get(java.lang.String)
	 */
	public RootElement get(String name, String url) throws ConnectionException {
		Repository repository = new Repository(name, url, this);
		update(repository);
		return repository;
	}

    /**
     * Checks-out a specific configuration from a given repository into a specific workspace (directory)
     */
    public abstract void checkout(Repository repository, Configuration configuration, String workspace) throws ConnectionException;

    /**
     * Checks-in a workspace into the repository. A check-in message, informing the changes,
     * should be passed to the CM system.
     * @return The revision number that has been checked-in
     */
    public abstract long checkin(Repository repository, String workspace, String message) throws ConnectionException;

    /**
     * Add a path in the workspace
     * The path should be present inside a previously checked-out workspace
     * (This add is only a notification to the version control system)
     */
    public abstract void add(String path) throws ConnectionException;
    
    /**
     * Remove a specific path from the workspace
     * The path should be present inside a previously checked-out workspace
     */
    public abstract void remove(String path) throws ConnectionException;
}
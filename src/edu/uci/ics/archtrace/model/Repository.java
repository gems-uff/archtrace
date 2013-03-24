package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import edu.uci.ics.archtrace.connectors.CMConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;

/**
 * Represents a CM repository
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 26, 2004
 */
public class Repository extends RootElement {
	
	/**
	 * Existing configurations indexed by their names
	 */
	private transient List<Configuration> configurations;
	
	/**
	 * Number of the configuration that was the last to be processed by trace policies.
	 */
	private int lastProcessedConfigurationNumber;
	
	/**
	 * Construct a repository that have alread been processed in the past
	 */
	public Repository(String name, String url, int lastProcessedConfigurationNumber, CMConnector connector) {
		this(name, url, connector);
		this.lastProcessedConfigurationNumber = lastProcessedConfigurationNumber;
	}
	
	/**
	 * Constructs a new repository
	 */
	public Repository(String name, String url, CMConnector connector) {
		super(name, url, connector);
		this.configurations = new ArrayList<Configuration>();
	}
	
	/**
	 * Provides the number of the last processed configuration
	 */
	public int getLastProcessedConfigurationNumber() {
		return lastProcessedConfigurationNumber;
	}
	
	/**
	 * Get a specific configuration
	 */
	public synchronized Configuration getConfiguration(String name) {
		try {
			return configurations.get(configurations.size() - Integer.parseInt(name));
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * @return Returns the lastConfiguration.
	 */
	public synchronized Configuration getLastConfiguration() {
		if (configurations.isEmpty())
			return null;
		else
			return configurations.get(0);
	}
	
	/**
	 * Add a new configuration to the repository
	 */
	public synchronized void add(Configuration configuration) {
		configuration.addParent(this);
		configurations.add(0, configuration);
		lastProcessedConfigurationNumber = Math.max(lastProcessedConfigurationNumber, configuration.getNumber());
		List<ArchTraceElement> path = Collections.emptyList();
		fireElementInserted(path, 0, configuration);
	}
	
	/**
	 * Get a specific configuration item
	 */
	public ConfigurationItem getConfigurationItem(String path, String version) {
		Configuration configuration = getConfiguration(version);
		if (configuration == null)
			return null;
		else {
			StringTokenizer parser = new StringTokenizer(path, "/");		
			while (parser.hasMoreTokens()) {
				String ciName = parser.nextToken();
				configuration = configuration.get(ciName);
			}
			
			return (ConfigurationItem)configuration;
		}
	}
	
    /**
     * Checks-out a specific configuration into a specific directory
     */
    public void checkout(Configuration configuration, String workspace) throws ConnectionException {
        ((CMConnector)getConnector()).checkout(this, configuration, workspace);
    }

    /**
     * Checks-in a workspace into the repository. A check-in message, informing the changes,
     * should be passed to the CM system.
     * @return The revision number that has been checked-in
     */
    public long checkin(String workspace, String message) throws ConnectionException {
        return ((CMConnector)getConnector()).checkin(this, workspace, message);
    }
    
    /**
     * Add a path in the workspace
     * The path should be present inside a previously checked-out workspace
     * (This add is only a notification to the version control system)
     */
    public void add(String path) throws ConnectionException {
        ((CMConnector)getConnector()).add(path);
    }
    
    /**
     * Remove a specific path from the workspace
     * The path should be present inside a previously checked-out workspace
     */
    public void remove(String path) throws ConnectionException {
        ((CMConnector)getConnector()).remove(path);
    }
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getChildren()
	 */
	protected List<Configuration> getChildren() {
		return configurations;
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		return Types.REPOSITORY;
	}

	/**
	 * Lists all configuration item version of a given path
	 */
	public List<ConfigurationItem> getConfigurationItems(String path) {
		// TODO Auto-generated method stub
		return null;
	}
}
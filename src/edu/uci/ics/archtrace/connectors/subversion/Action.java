package edu.uci.ics.archtrace.connectors.subversion;

import java.util.StringTokenizer;

import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.PolicyManager;

/**
 * Represents an action to be processed over a configuration
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 24, 2004
 */
public abstract class Action {
	
	/**
	 * Configuration to be processed
	 */
	private Configuration configuration;
	
	/**
	 * Path of the element
	 */
	private String path;

	/**
	 * Create the Action
	 */
	public Action(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * Set the path to be processed
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Provides the path that the action will process
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Provides the configuration that this action will manipulate
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Provides the last configuration in the path. 
	 * Clones all configurations in the path if necessary.
	 * After this call, the version of the returned configuration and its parent (recursivelly)
	 * will be the current configuration being processed.
	 */
	protected Configuration getClonedConfiguration(String path) {
		Configuration configuration = getConfiguration();
		
		StringTokenizer parser = new StringTokenizer(path, "/");		
		while (parser.hasMoreTokens()) {
			String ciName = parser.nextToken();

			ConfigurationItem configurationItem = configuration.get(ciName);
			if (configurationItem.getVersion() != getConfiguration()) {
				configuration.remove(ciName);
				configurationItem = addConfigurationItem(configuration, ciName, configurationItem);
			}
			configuration = configurationItem;
		}
		
		return configuration;
	}
	
	/**
	 * Add a configuration item inside a specific configuration
	 */
	protected ConfigurationItem addConfigurationItem(Configuration parent, String name, ConfigurationItem ancestry) {
		ConfigurationItem configurationItem = new ConfigurationItem(name, getConfiguration(), ancestry);
		parent.add(configurationItem);
		PolicyManager.getInstance().executeConfigurationItemEvolutionPolicies(configurationItem, ArchTracePolicy.ADD_ACTION);
		return configurationItem;
	}
	
	/**
	 * Run the action
	 */
	public abstract void run();
}

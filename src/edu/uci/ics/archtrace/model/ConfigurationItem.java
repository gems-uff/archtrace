package edu.uci.ics.archtrace.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a configuration item
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 26, 2004
 */
public class ConfigurationItem extends Configuration {
	
	/**
	 * Version of this configuration item
	 */
	private Configuration version;
	
	/**
	 * Constructs a new configuration item 
	 */
	public ConfigurationItem(String name, Configuration version, ConfigurationItem ancestry) {
		super(name, ancestry);
		this.version = version;
	}

	/**
	 * @return Returns the version.
	 */
	public Configuration getVersion() {
		return version;
	}
	
	/**
	 * Provides the path of this configuration item
	 * The path is calculated using all elements in the same version
	 */
	public String getPath() {
		StringBuffer path = new StringBuffer();
		
		// Try to get the path of the parent in the same version
		for (ArchTraceElement parent : getParents()) {
			try {
				ConfigurationItem parentCI = (ConfigurationItem)parent;
				if (this.getVersion() == parentCI.getVersion()) {
					path.append(parentCI.getPath());
					break;
				}
			} catch (ClassCastException e) {}
		}

		path.append("/").append(getName());
		return path.toString();
	}
	
	/**
	 * Provides the repository that this configuration item is stored
	 */
	public Repository getRepository() {
		return version.getRepository(); 
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return (getType() == Types.DIRECTORY);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(this.getName());
		buffer.append(" (version ").append(version.getName()).append(")");
		return buffer.toString();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		if (this.getChildCount() == 0)
			return Types.FILE;
		else
			return Types.DIRECTORY;
	}
	
	/**
	 * Provide the configurations that have this configuration item
	 */
	public Set<Configuration> getRootConfigurations() {
	    Set<Configuration> configurations = new HashSet<Configuration>();
	    
	    for (ArchTraceElement parent : getParents()) {
	        Configuration parentConfiguration = (Configuration)parent;
            configurations.addAll(parentConfiguration.getRootConfigurations());
	    }
	    
	    return configurations;
	}

	/**
	 * Provides the latest version of this configuration item
	 */
	public Configuration getLatestVersion() {
		// TODO Auto-generated method stub
		return null;
	}
}

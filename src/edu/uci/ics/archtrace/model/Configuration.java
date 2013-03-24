package edu.uci.ics.archtrace.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.utils.ArchTraceComparator;


/**
 * Represents a configuration. A configuration is a version of the whole repository.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 11, 2004
 */
public class Configuration extends ArchTraceElement {
		
	/**
	 * Author
	 */
	private String author;
	
	/**
	 * Date
	 */
	private Date date;
	
	/**
	 * Ancestry of this configuration
	 */
	private Configuration ancestry;
	
	/**
	 * Posterities of this configuration
	 */
	private Collection<Configuration> posterities;

	/**
	 * Existing configuration items indexed by their names
	 */
	private Map<String, ConfigurationItem> configurationItems;

	/**
	 * Creates a new configuration
	 */
	public Configuration(String name, Configuration ancestry) {
		super(name);
		
		configurationItems = new HashMap<String, ConfigurationItem>();

		if (ancestry != null) {
			ancestry.posterities.add(this);
			this.ancestry = ancestry;
			for (ConfigurationItem ci : ancestry.getConfigurationItems()) {
				add(ci);
			}			
		}
		
		posterities = new ArrayList<Configuration>();
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * @param author The author to set.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
	
	/**
	 * @return Returns the date.
	 */
	public Date getDate() {
		return date;
	}
	
    /**
	 * @param date The date to set.
     */
    public void setDate(Date date) {
        this.date = date;
    }
	
	/**
	 * @param date The date to set.
	 */
	public void setDate(String date) throws ParseException {
	    // I do not know the meaning of 'T' and 'Z'
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
	    this.date = formater.parse(date);
	}
	
	/**
	 * @return Returns the ancestry.
	 */
	public Configuration getAncestry() {
		return ancestry;
	}
	
	/**
	 * @return Returns the posterities.
	 */
	public Collection<Configuration> getPosterities() {
		return Collections.unmodifiableCollection(posterities);
	}
	
	/**
	 * @return Returns an iterator over the configurationItems.
	 */
	private Collection<ConfigurationItem> getConfigurationItems() {
		return Collections.unmodifiableCollection(configurationItems.values());
	}
	
	/**
	 * Provides an specific configuration item
	 */
	public ConfigurationItem get(String configurationItemName) {
		return configurationItems.get(configurationItemName);
	}
	
	/**
	 * Add a configuration item to this configuration
	 */
	public void add(ConfigurationItem configurationItem) {
		configurationItems.put(configurationItem.getName(), configurationItem);
		configurationItem.addParent(this);
	}
	
	/**
	 * Remove a specific configuration item from this configuration
	 * @return The configuration item that has been removed
	 */
	public void remove(String configurationItemName) {
		ConfigurationItem configurationItem = configurationItems.remove(configurationItemName);
		configurationItem.removeParent(this);
	}
	
	/**
	 * The number of the configuration
	 */
	public int getNumber() {
		return Integer.parseInt(getName());
	}
	
	/**
	 * Provides the repository that this configuration is stored
	 */
	public Repository getRepository() {
		return (Repository)getParents().iterator().next();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return true;
	}

	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getChildren()
	 */
	protected List<ArchTraceElement> getChildren() {
		List<ArchTraceElement> elements = new ArrayList<ArchTraceElement>(configurationItems.values());
		Collections.sort(elements, ArchTraceComparator.getInstance());

		return elements;
	}
	
	/**
	 * Return this configuration
	 */
	public Set<Configuration> getRootConfigurations() {
	    return Collections.singleton(this);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Configuration " + getName();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#getType()
	 */
	public int getType() {
		return Types.CONFIGURATION;
	}

    /**
     * Checks-out this configuration in a specific directory
     */
    public void checkout(String workspace) throws ConnectionException {
        getRepository().checkout(this, workspace);
    }

    /**
     * Informs if the element is a branch
     */
	public boolean isBranch() {
		// TODO Auto-generated method stub
		return false;
	}
}
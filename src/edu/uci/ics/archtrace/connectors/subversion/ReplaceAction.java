package edu.uci.ics.archtrace.connectors.subversion;

import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;

/**
 * Action to replace an existing configuration item in the configuration
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 30, 2004
 */
public class ReplaceAction extends Action {

	/**
	 * Base (ancestry) element for the action
	 */
	private ConfigurationItem copyFrom;
	
	/**
	 * Create the replace action
	 */
	public ReplaceAction(Configuration configuration, ConfigurationItem copyFrom) {
		super(configuration);
		this.copyFrom = copyFrom;
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.subversion.Action#run()
	 */
	public void run() {
		int separatorIndex = getPath().lastIndexOf('/');
		String path = getPath().substring(0, separatorIndex);
		String ciName = getPath().substring(separatorIndex + 1, getPath().length());
		Configuration configuration = getClonedConfiguration(path);
		configuration.remove(ciName);
		addConfigurationItem(configuration, ciName, copyFrom);
	}
}

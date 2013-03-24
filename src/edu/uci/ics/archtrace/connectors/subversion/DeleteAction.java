package edu.uci.ics.archtrace.connectors.subversion;

import edu.uci.ics.archtrace.model.Configuration;

/**
 * Action to delete a configuration item from the configuration
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 30, 2004
 */
public class DeleteAction extends Action {

	/**
	 * Create the delete action
	 */
	public DeleteAction(Configuration configuration) {
		super(configuration);
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
	}
}

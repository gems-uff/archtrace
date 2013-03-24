package edu.uci.ics.archtrace.connectors.subversion;

import edu.uci.ics.archtrace.model.Configuration;

/**
 * Action to modify a configuration item in the configuration
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 30, 2004
 */
public class ModifyAction extends Action {

	/**
	 * Create the add action
	 */
	public ModifyAction(Configuration configuration) {
		super(configuration);
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.subversion.Action#run()
	 */
	public void run() {
		getClonedConfiguration(getPath());
	}
}

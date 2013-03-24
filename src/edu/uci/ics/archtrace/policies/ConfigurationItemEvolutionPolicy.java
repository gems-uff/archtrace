package edu.uci.ics.archtrace.policies;

import edu.uci.ics.archtrace.model.ConfigurationItem;

/**
 * This interface should be implemented by all policies that need to be
 * executed after a configuration item has evolved.
 * 
 * This policy CANNOT abort the action.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public interface ConfigurationItemEvolutionPolicy extends ArchTracePolicy {

	public void execute(ConfigurationItem configurationItem, byte action);
}
package edu.uci.ics.archtrace.policies.builtin;

import java.util.List;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ConfigurationItemEvolutionPolicy;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * Automatically update old traces when configuration items are reinserted.
 *
 * @author Leo Murta (murta@cos.ufrj.br) - Jan 25, 2007
 */
public class UpdateOldTracesPolicy implements ConfigurationItemEvolutionPolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Automatically update old traces when configuration items are reinserted.";
	
	/**
	 * @see edu.uci.ics.archtrace.policies.ConfigurationItemEvolutionPolicy#execute(edu.uci.ics.archtrace.model.ConfigurationItem, byte)
	 */
	public void execute(ConfigurationItem ci, byte action) {
		if (action == ADD_ACTION) {
			List<ConfigurationItem> existingCIs = ci.getRepository().getConfigurationItems(ci.getPath());
			for (ConfigurationItem existingCI : existingCIs) {
				if (existingCI != ci) {
					for (Trace oldTrace : TraceManager.getInstance().getTraces(existingCI)) {
				        ArchitecturalElement ae = oldTrace.getArchitecturalElement();
				        Trace newTrace = new Trace(ae, ci);
						if (ae.getArchitecture().addTrace(newTrace)) {
							GUIManager.getInstance().addPolicyMessage(oldTrace + " updated to " + ci + ".");
						}
					}
				}
			}
		}
	}

	/**
	 * @see edu.uci.ics.archtrace.policies.ArchTracePolicy#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * @see edu.uci.ics.archtrace.policies.ArchTracePolicy#getRationale()
	 */
	public String getRationale() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Configuration items inadvertently removed and reinserted in the repository should have their old traces reestablished.")
			  .append("\n(this policy is non-interactive)");
		return buffer.toString();
	}

}

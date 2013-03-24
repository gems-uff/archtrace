package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.ConfigurationItemEvolutionPolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Automatically update traces when new configuration items are added.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class UpdateTracesPolicy implements ConfigurationItemEvolutionPolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Automatically update traces when new configuration items are added.";
	
	/**
	 * @see edu.uci.ics.archtrace.policies.ConfigurationItemEvolutionPolicy#execute(edu.uci.ics.archtrace.model.ConfigurationItem, byte)
	 */
	public void execute(ConfigurationItem ci, byte action) {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ConfigurationItem ancestry = (ConfigurationItem)ci.getAncestry();
			while (ancestry != null) {
				for (Trace trace : TraceManager.getInstance().getTraces(ancestry)) {
					ArchitecturalElement ae = trace.getArchitecturalElement();
					Trace newTrace = new Trace(ae, ci);
					if (ae.getArchitecture().addTrace(newTrace))
						GUIManager.getInstance().addPolicyMessage(trace + " updated to " + ci + ".");
				}
				ancestry = (ConfigurationItem)ancestry.getAncestry();
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
		buffer.append("Architectural elements that has traces to a specific configuration item version can be benefited by improvements provided by newer versions.")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(DenyImmutableAETracePolicy.DESCRIPTION)
			  .append("\n -> ").append(DenyMultipleCIVersionsPolicy.DESCRIPTION)
			  .append("\n -> ").append(RemoveAncestryTracesPolicy.DESCRIPTION);
		return buffer.toString();
	}

}

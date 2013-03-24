package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.PostTracePolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Remove traces to ancestry versions of the configuration item when traces are created to new versions
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class RemoveAncestryTracesPolicy implements PostTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Remove traces to ancestry versions of the configuration item when traces are created to new versions.";
	
	/**
	 * @see edu.uci.ics.archtrace.policies.PostTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ConfigurationItem ci = trace.getConfigurationItem();
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ancestry = (ConfigurationItem)ci.getAncestry();
			while (ancestry != null) {
				Trace oldTrace = new Trace(ae, ancestry);
				if ((TraceManager.getInstance().hasTrace(oldTrace)) &&
					(ae.getArchitecture().removeTrace(oldTrace)))
					GUIManager.getInstance().addPolicyMessage(oldTrace + " removed.");
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
		buffer.append("Usually it is not expected to have two or more versions of the same configuration item related to a specific version of an architectural element.")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(DenyMultipleCIVersionsPolicy.DESCRIPTION)
			  .append("\n -> ").append(UpdateTracesPolicy.DESCRIPTION);
		return buffer.toString();
	}

}

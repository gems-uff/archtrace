package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PreTracePolicy;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Denies removal of traceability links to source code in the trunk when a commit is performed in a branch.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class DenyRemovalFromBranch implements PreTracePolicy {
	
	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Denies removal of traceability links to source code in the trunk when a commit is performed in a branch.";
	
	/**
	 * @see edu.uci.ics.archtrace.policies.PreTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) throws TraceAbortedException {
		if (action == ArchTracePolicy.REMOVE_ACTION) {
			ConfigurationItem ci = trace.getConfigurationItem();
			if (ci.getLatestVersion().isBranch()) {
				throw new TraceAbortedException("Traces to configuration items in the trunk should persist after branching.");
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
		buffer.append("Commits in branches should not interfere in the main line of development.")
			  .append("\n(this policy is non-interactive)");
		return buffer.toString();
	}
}

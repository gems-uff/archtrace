package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PreTracePolicy;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Deny traces change on immutable architectural elements.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class DenyImmutableAETracePolicy implements PreTracePolicy {
	
	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Deny traces change on immutable architectural elements.";
	
	/**
	 * @see edu.uci.ics.archtrace.policies.PreTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) throws TraceAbortedException {
		if ((action == ArchTracePolicy.ADD_ACTION) || (action == ArchTracePolicy.REMOVE_ACTION)) {
			if (trace.getArchitecturalElement().isImmutable())
				throw new TraceAbortedException("Immutable architectural elements cannot have their traces changed.");
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
		buffer.append("In some circumstances, it is not desirable to evolve the traces of architectural elements that are marked as \"immutable\".")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(UpdateTracesPolicy.DESCRIPTION);
		return buffer.toString();
	}
}

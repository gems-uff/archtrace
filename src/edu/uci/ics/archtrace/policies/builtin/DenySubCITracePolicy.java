package edu.uci.ics.archtrace.policies.builtin;

import java.util.ArrayList;
import java.util.List;

import edu.uci.ics.archtrace.model.ArchTraceElement;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PreTracePolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Deny traces to the same architectural element version when a super configuration item already has the traces.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class DenySubCITracePolicy implements PreTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Deny traces to the same architectural element version when a super configuration item already has the traces.";
		
	/**
	 * @see edu.uci.ics.archtrace.policies.PreTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) throws TraceAbortedException {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			List<ArchTraceElement> ancestors = new ArrayList<ArchTraceElement>(ci.getParents());
			while (!ancestors.isEmpty()) {
				ArchTraceElement ancestor = ancestors.remove(0);
				for (Trace oldTrace : TraceManager.getInstance().getTraces(ancestor)) {
					if (oldTrace.getArchitecturalElement() == ae)
						throw new TraceAbortedException(ae + " alread has traces to a super configuration item: " + ancestor + ".");
				}
				
				ancestors.addAll(ancestor.getParents());
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
		buffer.append("If the composite configuration item has a trace, it is redundant to create the same trace to its parts.")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(RemoveSubCITracesPolicy.DESCRIPTION);
		return buffer.toString();
	}

}

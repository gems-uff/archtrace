package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.PostTracePolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Remove traces among sub configuration items and the architectural element when the super configuration item already has a trace to the architectural element
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class RemoveSubCITracesPolicy implements PostTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Remove traces among sub configuration items and the architectural element when the super configuration item already has a trace to the architectural element.";
		
	/**
	 * @see edu.uci.ics.archtrace.policies.PostTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			
			for (Trace oldTrace : TraceManager.getInstance().getAllTraces(ci)) {
				if ((oldTrace.getArchitecturalElement() == ae) && 
					(oldTrace.getConfigurationItem() != ci) &&
					(ae.getArchitecture().removeTrace(oldTrace)))
					GUIManager.getInstance().addPolicyMessage(trace + " removed.");
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
		buffer.append("When there is a trace to a composite configuration item (directory, for example), is redundant to keep traces to its sub configuration items (sub-directories and files, for example).")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(DenySubCITracePolicy.DESCRIPTION);
		return buffer.toString();
	}

}

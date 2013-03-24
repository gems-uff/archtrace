package edu.uci.ics.archtrace.policies.builtin;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.ArchitecturalElementEvolutionPolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Copy all traces from the ancestry version of the architectural element when a new version is added.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class CopyAncestryAETracesPolicy implements ArchitecturalElementEvolutionPolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Copy all traces from the ancestry version of the architectural element when a new version is added.";

	/**
	 * @see edu.uci.ics.archtrace.policies.ArchitecturalElementEvolutionPolicy#execute(edu.uci.ics.archtrace.model.ArchitecturalElement, byte)
	 */
	public void execute(ArchitecturalElement ae, byte action) {
		if (action == ArchTracePolicy.ADD_ACTION) {
			for (ArchitecturalElement ancestry : ae.getAncestries()) {
				for (Trace trace : TraceManager.getInstance().getTraces(ancestry)) {
					ConfigurationItem ci = trace.getConfigurationItem();
					Trace newTrace = new Trace(ae, ci);
					if (ae.getArchitecture().addTrace(newTrace))
						GUIManager.getInstance().addPolicyMessage(newTrace + " added.");
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
		buffer.append("Typically, new architectural element versions have the same traces of the version they have been originated from.")
			  .append("\n(this policy is non-interactive)");
		return buffer.toString();
	}

}

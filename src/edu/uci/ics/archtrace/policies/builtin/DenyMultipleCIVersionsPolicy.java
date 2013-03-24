package edu.uci.ics.archtrace.policies.builtin;

import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PreTracePolicy;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Deny trace to different versions of the same configuration item in the same architecture.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class DenyMultipleCIVersionsPolicy implements PreTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Deny trace to different versions of the same configuration item in the same architecture.";
		
	/**
	 * @see edu.uci.ics.archtrace.policies.PreTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) throws TraceAbortedException {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			Architecture arch = ae.getArchitecture();
			Set otherVersions = getOtherVersions(ci);

			for (Trace oldTrace : TraceManager.getInstance().getAllTraces(arch)) {
				ConfigurationItem oldCi = oldTrace.getConfigurationItem();
				if (otherVersions.contains(oldCi))
					throw new TraceAbortedException(oldCi + " and " + ci + " are versions of the same configuration item and " + oldCi + " already has traces in the architecture.");
			}
			
		}
	}

	/**
	 * Get all the other versions of a given configuration item
	 */
	private Set getOtherVersions(ConfigurationItem ci) {
		ConfigurationItem currentVersion = ci;
		
		// Get the first version
		ConfigurationItem ancestry = (ConfigurationItem)ci.getAncestry();
		while (ancestry != null) {
			ci = ancestry;
			ancestry = (ConfigurationItem)ci.getAncestry();
		}

		Set<ConfigurationItem> result = getAllPosterities(ci);
		result.add(ci);
		result.remove(currentVersion);
		return result;
	}

	/**
	 * Provide all posterities of a given configuration item
	 */
	private Set<ConfigurationItem> getAllPosterities(ConfigurationItem ci) {
		Set<ConfigurationItem> result = new HashSet<ConfigurationItem>();
		
		for (Configuration posterity : ci.getPosterities()) {
			result.add((ConfigurationItem)posterity);
			result.addAll(getAllPosterities((ConfigurationItem)posterity));
		}
		
		return result;
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
		buffer.append("Some programming languages do not support more than one version of the same configuration item in the same runtime environment.")
			  .append("\n(this policy is non-interactive)")
			  .append("\n\nSee also:")
			  .append("\n -> ").append(RemoveAncestryTracesPolicy.DESCRIPTION)
			  .append("\n -> ").append(UpdateTracesPolicy.DESCRIPTION);
		return buffer.toString();
	}

}

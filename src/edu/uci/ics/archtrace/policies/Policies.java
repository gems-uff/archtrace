package edu.uci.ics.archtrace.policies;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.archtrace.policies.builtin.CopyAncestryAETracesPolicy;
import edu.uci.ics.archtrace.policies.builtin.DenyImmutableAETracePolicy;
import edu.uci.ics.archtrace.policies.builtin.DenyMultipleCIVersionsPolicy;
import edu.uci.ics.archtrace.policies.builtin.DenyRemovalFromBranch;
import edu.uci.ics.archtrace.policies.builtin.DenySubCITracePolicy;
import edu.uci.ics.archtrace.policies.builtin.RemoveAncestryTracesPolicy;
import edu.uci.ics.archtrace.policies.builtin.RemoveSubCITracesPolicy;
import edu.uci.ics.archtrace.policies.builtin.SuggestNewerCIPolicy;
import edu.uci.ics.archtrace.policies.builtin.SuggestRelatedTracesPolicy;
import edu.uci.ics.archtrace.policies.builtin.UpdateTracesPolicy;

/**
 * Collection of all user defined policies
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 1, 2004
 */
public class Policies {

	/**
	 * Map from policies to the enabled state
	 */
	private Map<ArchTracePolicy, Boolean> policies;
	
	/**
	 * Construct a new collection of policies
	 */
	public Policies() {
		policies = new LinkedHashMap<ArchTracePolicy, Boolean>();		
	}
	
	/**
	 * Add a new policy to ArchTrace
	 */
	public synchronized void add(ArchTracePolicy policy, boolean enabled) {
		policies.put(policy, Boolean.valueOf(enabled));
	}
	
	/**
	 * Provides all policies of a given type
	 */
	public synchronized <T extends ArchTracePolicy> List<T> get(Class<T> type) {
		List<T> result = new ArrayList<T>();
		
		for (ArchTracePolicy policy : policies.keySet()) {
			try {
				result.add(type.cast(policy));
			} catch (Exception e) {}
		}
		
		return result;
	}
		
	/**
	 * Informs if a policy is enabled
	 */
	public synchronized boolean isEnabled(ArchTracePolicy policy) {
		try {
			return policies.get(policy).booleanValue();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Change the enabled state of a policy
	 */
	public synchronized void setEnabled(ArchTracePolicy policy, boolean enabled) {
		policies.put(policy, Boolean.valueOf(enabled));
	}

	/**
	 * Provide the collection of built-in policies.
	 * Each policy has a specific enabled state.
	 */
	public static Policies createBuiltInPolicies() {
		Policies builtInPolicies = new Policies();
		
		// Pre-trace policies
		builtInPolicies.add(new DenyImmutableAETracePolicy(), true);
		builtInPolicies.add(new DenySubCITracePolicy(), true);
		builtInPolicies.add(new DenyMultipleCIVersionsPolicy(), false);
		builtInPolicies.add(new SuggestNewerCIPolicy(), true);
		builtInPolicies.add(new DenyRemovalFromBranch(), false);

		// Post-trace policies
		builtInPolicies.add(new RemoveAncestryTracesPolicy(), true);
		builtInPolicies.add(new RemoveSubCITracesPolicy(), true);
		builtInPolicies.add(new SuggestRelatedTracesPolicy(), true);
		
		// Architectural element evolution policies
		builtInPolicies.add(new CopyAncestryAETracesPolicy(), true);
		
		// Configuration item evolution policies
		builtInPolicies.add(new UpdateTracesPolicy(), true);
		
		return builtInPolicies;
	}
}
package edu.uci.ics.archtrace.policies;

import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Singleton class responsible to manage policies in ArchTrace
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public class PolicyManager {
	
	/**
	 * Singleton instance
	 */
	private static PolicyManager instance;
	
	/**
	 * Informs if the policy manager is enabled
	 */
	private boolean enabled;
	
	/**
	 * Construct the policy manager
	 */
	private PolicyManager() {
		enabled = false;
	}
	
	/**
	 * Gets the singleton instance
	 */
	public synchronized static PolicyManager getInstance() {
		if (instance == null)
			instance = new PolicyManager();
		return instance;
	}
	
	/**
	 * Resets the singleton instance
	 */
	public static synchronized void resetInstance() {
		instance = null;
	}
	
	/**
	 * @param enabled The enabled to set.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Execute all pre-trace policies
	 */
	public void executePreTracePolicies(Trace trace, byte action) throws TraceAbortedException {
		if (enabled) {
			Policies policies = PersistenceManager.getInstance().getPolicies();
			for (PreTracePolicy policy : policies.get(PreTracePolicy.class)) {
				if (policies.isEnabled(policy))
					policy.execute(trace, action);			
			}
		}
	}
	
	/**
	 * Execute all post-trace policies
	 */
	public void executePostTracePolicies(Trace trace, byte action) {
		if (enabled) {
			Policies policies = PersistenceManager.getInstance().getPolicies();
			for (PostTracePolicy policy : policies.get(PostTracePolicy.class)) {
				if (policies.isEnabled(policy))
					policy.execute(trace, action);			
			}
		}
	}
	
	/**
	 * Execute all architectural element evolution policies
	 */
	public void executeArchitecturalElementEvolutionPolicies(ArchitecturalElement architecturalElement, byte action) {
		if (enabled) {
			Policies policies = PersistenceManager.getInstance().getPolicies();
			for (ArchitecturalElementEvolutionPolicy policy : policies.get(ArchitecturalElementEvolutionPolicy.class)) {
				if (policies.isEnabled(policy))
					policy.execute(architecturalElement, action);			
			}
		}
	}
	
	/**
	 * Execute all architectural element evolution policies
	 */
	public void executeConfigurationItemEvolutionPolicies(ConfigurationItem configurationItem, byte action) {
		if (enabled) {
			Policies policies = PersistenceManager.getInstance().getPolicies();
			for (ConfigurationItemEvolutionPolicy policy : policies.get(ConfigurationItemEvolutionPolicy.class)) {
				if (policies.isEnabled(policy))
					policy.execute(configurationItem, action);			
			}
		}
	}
}
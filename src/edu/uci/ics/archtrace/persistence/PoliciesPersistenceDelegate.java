package edu.uci.ics.archtrace.persistence;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.Policies;

/**
 * This class provides support for storing RootElementCollection objects using XMLEncoder
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public class PoliciesPersistenceDelegate extends DefaultPersistenceDelegate {
	 
	/**
	 * @see java.beans.PersistenceDelegate#initialize(java.lang.Class, java.lang.Object, java.lang.Object, java.beans.Encoder)
	 */
	protected void initialize(Class type, Object oldInstance, Object newInstance, Encoder out) {
		// Needed for properties
		super.initialize(type, oldInstance, newInstance, out);
		
		Policies policies = (Policies)oldInstance;
		
		for (ArchTracePolicy policy : policies.get(ArchTracePolicy.class)) {
			boolean enabled = policies.isEnabled(policy);
			out.writeStatement(new Statement(policies, "add", new Object[] { policy, Boolean.valueOf(enabled) }));
		}
	}
}

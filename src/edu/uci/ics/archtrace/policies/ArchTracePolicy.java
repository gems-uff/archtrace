package edu.uci.ics.archtrace.policies;

/**
 * TODO Document ArchTracePolicy
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public interface ArchTracePolicy {
	
	/**
	 * Action of adding the element
	 */
	public static final byte ADD_ACTION = 1;
	
	/**
	 * Action of removing the element
	 */
	public static final byte REMOVE_ACTION = 2;
	
	/**
	 * Action of changing the element
	 */
	public static final byte CHANGE_ACTION = 4;
	
	/**
	 * Provides a short description of the policy.
	 * This description will be used as the enable/disable checkbox text
	 */
	public String getDescription();
	
	/**
	 * Provides a more detailed explanation of why this policy is important
	 */
	public String getRationale();
}

package edu.uci.ics.archtrace.policies;

import edu.uci.ics.archtrace.trace.Trace;

/**
 * This interface should be implemented by all policies that need to be
 * executed before a trace is added or removed.
 * 
 * This policy can abort the action.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public interface PreTracePolicy extends ArchTracePolicy {

	public void execute(Trace trace, byte action) throws TraceAbortedException;
}

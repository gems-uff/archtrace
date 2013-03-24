package edu.uci.ics.archtrace.policies;

import edu.uci.ics.archtrace.ArchTraceException;

/**
 * This exception can be thrown by PreTracePolicy to inform ArchTrace that the
 * trace should be aborted.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public class TraceAbortedException extends ArchTraceException {

	/**
	 * Create an exception explaning why the trace should be aborted
	 */
	public TraceAbortedException(String reason) {
		super(reason);
	}
}

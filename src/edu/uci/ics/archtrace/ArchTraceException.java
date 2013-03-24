package edu.uci.ics.archtrace;

/**
 * Represents a generic exception in ArchTrace.
 * The message should be redable by users!
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 13, 2004
 */
public class ArchTraceException extends Exception {

	/**
	 * Create an exception with a specific message and cause
	 */
	public ArchTraceException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * Create an exception with a specific message
	 */
	public ArchTraceException(String message) {
		super(message);
	}
}

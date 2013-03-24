package edu.uci.ics.archcm;

/**
 * Represents a generic exception in ArchCM.
 * The message should be redable by users!
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 13, 2004
 */
public class ArchCMException extends Exception {

	/**
	 * Create an exception with a specific message and cause
	 */
	public ArchCMException(String message, Throwable cause) {
		super(message, cause);

	}

	/**
	 * Create an exception with a specific message
	 */
	public ArchCMException(String message) {
		super(message);
	}
}

package edu.uci.ics.archtrace.connectors;

/**
 * Encapsulates exceptions created by connection mechanisms
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public class ConnectionException extends Exception {

	/**
	 * Create this exception with a USER FRENDLY message
	 */
	public ConnectionException(String message) {
		super(message);
	}
	
	/**
	 * Create this exception with a USER FRENDLY message and the real exception
	 */
	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}
}

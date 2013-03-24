package edu.uci.ics.archtrace.model;

/**
 * Stores all existing types of ArchTraceElement. This is necessary to allow
 * detection of Icon, among other crosscuting characteristics.
 * 
 * It is not possible to use class as type because some elements change
 * class during their live (eg.: File and Directory).
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - 18/08/2004
 */
public interface Types {
	public static final int COLLECTION = 0;
	
	public static final int ARCHITECTURE = 1001;
	public static final int COMPONENTS_VIEW = 1002;
	public static final int INTERFACES_VIEW = 1003;
	public static final int CONNECTORS_VIEW = 1004;
	public static final int COMPONENT = 1005;
	public static final int INTERFACE = 1006;
	public static final int CONNECTOR = 1007;
	
	public static final int REPOSITORY = 2001;
	public static final int CONFIGURATION = 2002;
	public static final int DIRECTORY = 2003;
	public static final int FILE = 2004;
}
package edu.uci.ics.archtrace.utils;

import java.util.Comparator;

import edu.uci.ics.archtrace.model.ArchTraceElement;

/**
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - 18/08/2004
 */
public class ArchTraceComparator implements Comparator<ArchTraceElement> {

	/**
	 * Singleton instance
	 */
	private static ArchTraceComparator instance;
	
	/**
	 * Singleton constructor
	 */
	private ArchTraceComparator() {}
	
	/**
	 * Provides the singleton instance
	 */
	public static ArchTraceComparator getInstance() {
		if (instance == null)
			instance = new ArchTraceComparator();
		return instance;
	}
	
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ArchTraceElement element1, ArchTraceElement element2) {

		if (element1.equals(element2))
			return 0;
		else {
			int typeResult = element1.getType() - element2.getType();
			if (typeResult != 0)
				return typeResult;
			else
				return element1.toString().compareToIgnoreCase(element2.toString());
		}
	}

}

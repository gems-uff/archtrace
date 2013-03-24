package edu.uci.ics.archtrace.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author murta
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AllTests extends TestSuite {

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AllTests.suite());
	}

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ArchTrace");

		// Include tests here
		// Tests removed to avoid long nightly builds (tests should be rewritten to access local repository)
		//suite.addTest(new TestSuite(SubversionConnectorTest.class));
		//suite.addTest(new TestSuite(XArchADTConnectorTest.class));
		
		return suite;
	}
}
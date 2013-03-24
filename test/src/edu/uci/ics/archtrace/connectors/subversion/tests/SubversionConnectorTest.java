package edu.uci.ics.archtrace.connectors.subversion.tests;

import junit.framework.TestCase;
import edu.uci.ics.archtrace.connectors.CMConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.subversion.SubversionCLIConnector;
import edu.uci.ics.archtrace.model.Repository;


/**
 * Test case fo Subversion connector
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 28, 2004
 */
public class SubversionConnectorTest extends TestCase {

	/**
	 * The CLI connector to subversion
	 */
	CMConnector subversion;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		subversion = new SubversionCLIConnector();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Gets versions of ./tags/ in repository "http://svn.collab.net/repos/svn" using both connectors
	 * and comparing the results
	 * @throws ConnectionException
	 */
	public final void testGetRepository() throws ConnectionException {		
//		// Get "http://svn.collab.net/repos/svn"
//		String url = "http://svn.collab.net/repos/svn";
	    
	    // Get "file:///c:/murta/svnrep"
		String url = "file:///c:/murta/svnrep";
		
		Repository repository = (Repository)subversion.get("Test", url);

		assertNotNull(repository);
		assertTrue(repository.getChildCount() > 10);
	}

	/**
	 * Run this test case
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(SubversionConnectorTest.class);
	}
}

package edu.uci.ics.archtrace.connectors.xarchadt.tests;

import junit.framework.TestCase;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.subversion.SubversionCLIConnector;
import edu.uci.ics.archtrace.connectors.xarchadt.XArchADTConnector;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * TODO Document XArchADTConnectorTest
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 2, 2004
 */
public class XArchADTConnectorTest extends TestCase {

	/**
	 * The xArchADT connector
	 */
	XArchADTConnector xArchADT;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		xArchADT = new XArchADTConnector();
	}
	
	public void testTrace() throws ConnectionException {
		// Get "file:///c:/murta/entertainmentSystem.xml"
		String url = "file:///c:/murta/entertainmentSystem.xml";
		Architecture architecture = (Architecture)xArchADT.get("Test", url);
		assertNotNull(architecture);
		
		ArchitecturalElement ae = (ArchitecturalElement)architecture.getComponents().getChild(0);
		assertNotNull(ae);
		System.out.println(ae);
		
		Repository rep = new Repository("Test", "file:///c:/murta/svnrep", new SubversionCLIConnector());
		Configuration version = new Configuration("1", null);
		ConfigurationItem ci = new ConfigurationItem("testCI", version, null);
		version.add(ci);
		rep.add(version);
		
		Trace trace = new Trace(ae, ci);
		xArchADT.addTrace(architecture, trace);
		System.out.println(xArchADT.getTraces(architecture, rep));
		xArchADT.save(architecture);
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(XArchADTConnectorTest.class);
	}
}

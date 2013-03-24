package edu.uci.ics.archtrace.connectors.subversion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.xml.SVNXMLLogHandler;
import org.tmatesoft.svn.core.wc.xml.SVNXMLSerializer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.archtrace.connectors.CMConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;



/**
 * Encapsulates the Subversion connection/parsing mechanism
 * This connector uses SVN Kit Library.
 *
 * @author Joao Gustavo (gustavo@cos.ufrj.br) - Ago 22, 2007
 */
public class SVNKitConnector extends CMConnector {
	
	/**
	 * SVNRepository from SVNKit Library
	 */
	private SVNRepository svnRepository;
	
	public synchronized void update(RootElement element) throws ConnectionException {
		Repository repository = (Repository)element;
		
		DAVRepositoryFactory.setup();

		SVNRepository svnRepository = null;
		try {
			// SVNRepository driver
			svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(repository.getUrl()));

		} catch (SVNException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		int firstConfiguration = repository.getLastProcessedConfigurationNumber() + 1;
		int lastConfiguration;
		try {
			lastConfiguration = (int) svnRepository.getLatestRevision();
			//lastConfiguration = getLastConfiguration(repository.getUrl());
			System.out.println("last: ");
			System.out.println(lastConfiguration);
			load(repository, firstConfiguration, lastConfiguration);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a set of configurations from the repository
	 * (from firstRevision up to lastRevision, inclusive on both ends)
	 * @throws SVNException 
	 * @throws IOException 
	 */
	private void load(Repository repository, int firstRevision, int lastRevision) throws ConnectionException, SVNException, IOException {		
		if (firstRevision <= lastRevision) {
			
			String url = repository.getUrl();
			
			// Codigo XML output
			
			SVNLogClient logClient = SVNClientManager.newInstance().getLogClient();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			SVNXMLSerializer xmlSerializer = new SVNXMLSerializer(output);
			LogHandler logHandler = new LogHandler(repository, lastRevision - firstRevision + 1);
			SVNXMLLogHandler xmlLogHandler = new SVNXMLLogHandler(logHandler);
			
			// xmlLogHandler.startDocument();
			
			// Mesmo efeito do comando:
			// "svn log --revision <firstRevision>:<firstRevision> --quiet --verbose --stop-on-copy --xml --non-interactive <url>"
			logClient.doLog(SVNURL.parseURIEncoded(repository.getUrl()), new String[] {""}, 
					SVNRevision.UNDEFINED, SVNRevision.create(firstRevision), SVNRevision.create(lastRevision), 
					true, true, -1, xmlLogHandler);
			
			// xmlLogHandler.endDocument();
			
//			try {
//				// Gravando arquivo XML de saída em output
//				xmlSerializer.flush();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//			LogHandler logHandler = new LogHandler(repository, lastRevision - firstRevision + 1);
//			
//			run(url, output, logHandler);
		}
	}

	private void run(String url, ByteArrayOutputStream output, DefaultHandler handler) throws IOException {
		
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + url);
		
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(new ByteArrayInputStream(output.toByteArray()), handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		
		ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + url);
	}

	@Override
	public void add(String path) throws ConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long checkin(Repository repository, String workspace, String message)
			throws ConnectionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void checkout(Repository repository, Configuration configuration,
			String workspace) throws ConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String path) throws ConnectionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restore(RootElement element) throws ConnectionException {
		// TODO Auto-generated method stub
		
	}
}

package edu.uci.ics.archtrace.connectors.subversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.archtrace.connectors.CMConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;

/**
 * Encapsulates the Subversion connection/parsing mechanism
 * This connector uses cache with reference counting mechanism.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 26, 2004
 */
public class SubversionCLIConnector extends CMConnector {

	/**
	 * Subversion program
	 */
	private static final String SVN = "svn";
	
	/**
	 * Log command
	 */
	private static final String LOG = "log";
	
	/**
	 * Check-out command
	 */
	private static final String CHECKOUT = "checkout";
	
	/**
	 * Check-in command
	 */
	private static final String COMMIT = "commit";

	/**
	 * Remove command
	 */
	private static final String ADD = "add";
	
	/**
	 * Remove command
	 */
	private static final String REMOVE = "remove";
	
	/**
	 * Message switch
	 */
	private static final String MESSAGE = "--message";
	
	/**
	 * Revision switch
	 */
	private static final String REVISION = "--revision";
	
	/**
	 * Revision separator
	 */
	private static final String UP_TO = ":";
	
	/**
	 * Last revision
	 */
	private static final String HEAD = "HEAD";
	
	/**
	 * Quiet switch
	 */
	private static final String QUIET = "--quiet";
	
	/**
	 * Verbose switch
	 */
	private static final String VERBOSE = "--verbose";
	
	/**
	 * Stop on copy switch
	 */
	private static final String STOP_ON_COPY = "--stop-on-copy";
	
	/**
	 * XML switch
	 */
	private static final String XML = "--xml";
	
	/**
	 * Non interactive switch
	 */
	private static final String NON_INTERACTIVE = "--non-interactive";
	
	/**
	 * Non recursive switch
	 */
	private static final String NON_RECURSIVE = "--non-recursive";
	
	/**
	 * Force switch
	 */
	private static final String FORCE = "--force";
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#restore(edu.uci.ics.archtrace.model.RootElement)
	 */
	public synchronized void restore(RootElement element) throws ConnectionException {
		Repository repository = (Repository)element;
		
		load(repository, 1, repository.getLastProcessedConfigurationNumber());
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#update(edu.uci.ics.archtrace.model.RootElement)
	 */
	public synchronized void update(RootElement element) throws ConnectionException {
		Repository repository = (Repository)element;
		int firstConfiguration = repository.getLastProcessedConfigurationNumber() + 1;
		int lastConfiguration = getLastConfiguration(repository.getUrl());
		load(repository, firstConfiguration, lastConfiguration);
	}
	
	/**
	 * Provides the last existing revision in the repository
	 */
	private int getLastConfiguration(String url) throws ConnectionException {
		// command: "svn log --revision HEAD --quiet --stop-on-copy --xml --non-interactive <url>"
		String[] command = { SVN, LOG, REVISION, HEAD, QUIET, STOP_ON_COPY, XML, NON_INTERACTIVE, url }; 

		final StringBuffer buffer = new StringBuffer();
		DefaultHandler handler = new DefaultHandler() {
			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				if ("logentry".equals(qName)) {
					buffer.append(attributes.getValue("revision"));
				}
			}	
		};		
		run(command, handler, url);
		
		return Integer.parseInt(buffer.toString());
	}

	/**
	 * Load a set of configurations from the repository
	 * (from firstRevision up to lastRevision, inclusive on both ends)
	 */
	private void load(Repository repository, int firstRevision, int lastRevision) throws ConnectionException {		
		if (firstRevision <= lastRevision) {
			String url = repository.getUrl();
	
			// command: "svn log --revision <firstRevision>:<firstRevision> --quiet --verbose --stop-on-copy --xml --non-interactive <url>"
			String[] command = { SVN, LOG, REVISION, firstRevision + UP_TO + lastRevision, QUIET, VERBOSE, STOP_ON_COPY, XML, NON_INTERACTIVE, url }; 
			
			run(command, new LogHandler(repository, lastRevision - firstRevision + 1), url);
		}
	}
	
    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#checkout(edu.uci.ics.archtrace.model.Repository, edu.uci.ics.archtrace.model.Configuration, String)
     */
    public void checkout(Repository repository, Configuration configuration, String workspace) throws ConnectionException {
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + repository);
		try {
		    ConnectorManager.getInstance().fireConnectionProgress("Checking-out " + configuration + " from " + repository);
			String url = repository.getUrl();
			
			// command: "svn checkout --revision <configuration> --non-interactive <url> <workspace>"
			String[] command = { SVN, CHECKOUT, REVISION, configuration.getName(), NON_INTERACTIVE, url, workspace }; 
			run(command, url);

        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to check-out ").append(configuration).append(" from repository ").append(repository);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + repository);
		}
    }
    
    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#checkin(edu.uci.ics.archtrace.model.Repository, java.io.File, java.lang.String)
     */
    public long checkin(Repository repository, String workspace, String message) throws ConnectionException {
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + repository);
		try {
		    ConnectorManager.getInstance().fireConnectionProgress("Verifying existing configuration in " + repository);
		    long oldConfiguration = getLastConfiguration(repository.getUrl());
		    
		    ConnectorManager.getInstance().fireConnectionProgress("Checking-in to " + repository);
			String url = repository.getUrl();
			
			// command: "svn commit <workspace> --message <message> --non-interactive"
			String[] command = { SVN, COMMIT, workspace, MESSAGE, message, NON_INTERACTIVE }; 
			run(command, url);
			
			ConnectorManager.getInstance().fireConnectionProgress("Verifying new configuration in " + repository);
		    long newConfiguration = getLastConfiguration(repository.getUrl());
		    
		    if (oldConfiguration == newConfiguration)
		        return -1;
		    else
		        return newConfiguration;

        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to check-in to repository ").append(repository);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + repository);
		}
    }
    
    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#add(java.lang.String)
     */
    public void add(String path) throws ConnectionException {
		try {
			// command: "svn add <path> --non-recursive"
			String[] command = { SVN, ADD, path, NON_RECURSIVE }; 
			run(command);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to remove file ").append(path);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		}
    }
    
    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#remove(java.lang.String)
     */
    public void remove(String path) throws ConnectionException {
		try {
			// command: "svn rm <path> --force --non-interactive"
			String[] command = { SVN, REMOVE, path, FORCE, NON_INTERACTIVE }; 
			run(command);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to remove file ").append(path);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		}
    }
	
	/**
	 * Run a specific command and process the results of this command using SAX
	 */
	private void run(String[] command, DefaultHandler handler, String url) throws ConnectionException {
		Process process = null;
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + url);
		try {
			process = Runtime.getRuntime().exec(command);
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(process.getInputStream(), handler);
		} catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to access repository ").append(url).append(":");

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String processError = reader.readLine();
				if (processError != null)
					errorMessage.append(" (").append(processError).append(")");
			} catch (Exception e1) {
				Logger.global.info("Could not read external process error.");
			}

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + url);
		}
	}
	
	/**
	 * Run a specific command and inform the results to listeners
	 */
	private void run(String[] command, String url) throws ConnectionException {
		Process process = null;
//		if (url != null)
//		    ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + url);

		try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String message = reader.readLine();
            while (message != null) {
//                ConnectorManager.getInstance().fireConnectionProgress(message);
                message = reader.readLine();
            }
            
            reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            message = reader.readLine();
            if (message != null) {
                StringBuffer errorMessage = new StringBuffer("Not able to run command ").append(command);
                do {
            	    errorMessage.append("\n").append(message);
            	    message = reader.readLine();
                } while (message != null);
                throw new ConnectionException(errorMessage.toString());
            }
        } catch (IOException e) {
            Logger.global.info("Could not read external process.");
        } finally {
//            if (url != null)
//                ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + url);
		}
	}
	
	/**
	 * Run a specific command
	 */
	private void run(String[] command) throws ConnectionException {
	    run(command, null);
	}
}

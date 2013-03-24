package edu.uci.ics.archtrace.connectors.subversion;

import java.text.ParseException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repository;

/**
 * SAX handler for XML resulting from "svn log ..." command
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 28, 2004
 */
public class LogHandler extends DefaultHandler {

	/**
	 * Repository that owns parsed versions
	 */
	private Repository repository;
	
	/**
	 * The configuration being processed
	 */
	private Configuration configuration;
	
	/**
	 * Value of the processing element
	 */
	private StringBuffer value;
	
	/**
	 * Action being processed
	 */
	private Action action;
	
	/**
	 * List of all action to be ordered and processed
	 * The processing of these action need to be postponed because svn log does not privide result
	 * in the correct execution order in the transaction (it informs a delete action 
	 * over an element that has not been added/copied yet)
	 */
	private SortedSet<Action> actions;
	
	/**
	 * Total number of configuration to be processed
	 */
	private int totalConfigurations;
	
	/**
	 * Progress counter (number of processed configurations)
	 */
	private int processedConfigurations;
	
	/**
	 * Creates the log handler
	 * @param lastRevision
	 */
	public LogHandler(Repository repository, int totalConfigurations) {
		this.repository = repository;
		this.totalConfigurations = totalConfigurations;
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		value = new StringBuffer();
		if ("path".equals(qName)) {
			switch (attributes.getValue("action").charAt(0)) {
			case 'M':
				action = new ModifyAction(configuration);
				break;
			case 'A':
				action = new AddAction(configuration, repository.getConfigurationItem(attributes.getValue("copyfrom-path"), attributes.getValue("copyfrom-rev")));
				break;
			case 'R':
				action = new ReplaceAction(configuration, repository.getConfigurationItem(attributes.getValue("copyfrom-path"), attributes.getValue("copyfrom-rev")));
				break;
			case 'D':
				action = new DeleteAction(configuration);
				break;
			}
		} else if ("logentry".equals(qName)) {
			String revision = attributes.getValue("revision");
			configuration = new Configuration(revision, repository.getLastConfiguration());
			actions = new TreeSet<Action>(new Comparator<Action>() {
				public int compare(Action a1, Action a2) {
					return a1.getPath().compareTo(a2.getPath());
				}
			});
		}
	}	
	
	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) {
		value.append(ch, start, length);
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) {
		try {
			if ("path".equals(qName)) {
				action.setPath(value.toString());
				actions.add(action);
			} else if ("author".equals(qName)) {
				configuration.setAuthor(value.toString());
			} else if ("date".equals(qName)) {
				try {
                    configuration.setDate(value.toString());
                } catch (ParseException e) {
                    Logger.global.warning("Could not parse date " + value.toString());
                }
			} else if ("logentry".equals(qName)) {
				// The configuration should be plugged into the repository prior to the items loading
				// Reason: some policies need to update traces, and the repository information is
				// needed to add a new trace into the architecture
				repository.add(configuration);
				
				for (Action action : actions) {
					action.run();
				}

				ConnectorManager.getInstance().fireConnectionProgress(++processedConfigurations + " of " + totalConfigurations + " configurations loaded from " + repository.getUrl());
			}
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
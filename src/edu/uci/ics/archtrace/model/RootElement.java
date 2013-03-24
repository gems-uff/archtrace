package edu.uci.ics.archtrace.model;

import java.io.File;

import edu.uci.ics.archtrace.connectors.ArchTraceConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;

/**
 * Represents a root ArchTrace element (Architecture or Repository)
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - 14/08/2004
 */
public abstract class RootElement extends ArchTraceElement {

	/**
	 * The url of the element
	 */
	private String url;
    
    /**
	 * The connector that this element uses to load its data
	 */
	private ArchTraceConnector connector;
	
	/**
	 * Construct the root element
	 * @throws ConnectionException
	 */
	public RootElement(String name, String url, ArchTraceConnector connector) {
	    super(name.trim());
		
		// Verify if the name is valid for future directory creations
        File directory = new File(System.getProperty("java.io.tmpdir"), name.trim());
        if (directory.mkdir()) {
            directory.delete();
        } else {
            throw new RuntimeException("The name should follow the rules of file system directory names.");
        }
		
		this.url = url;
		this.connector = connector;
	}
	
	/**
	 * Provides the url of the root element
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * @return Returns the connector.
	 */
	public ArchTraceConnector getConnector() {
		// TODO: should invert the relationship, allowing the connectors to know their root elements
		return connector;
	}
	
	/**
	 * Restore the root element
	 */
	public void restore() throws ConnectionException {
		connector.restore(this);
	}
	
	/**
	 * Update the root element
	 */
	public void update() throws ConnectionException {
		connector.update(this);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.model.ArchTraceElement#mayHaveChildren()
	 */
	public boolean mayHaveChildren() {
		return true;
	}
}
package edu.uci.ics.archtrace.persistence;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import edu.uci.ics.archcm.model.WorkspaceMetadata;
import edu.uci.ics.archtrace.model.Repository;

/**
 * This class provides support for storing WorkspaceMetadata objects using XMLEncoder
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public class WorkspaceMetadataPersistenceDelegate extends DefaultPersistenceDelegate {
	 
	/**
	 * @see java.beans.PersistenceDelegate#initialize(java.lang.Class, java.lang.Object, java.lang.Object, java.beans.Encoder)
	 */
	protected void initialize(Class type, Object oldInstance, Object newInstance, Encoder out) {
		// Needed for properties
		super.initialize(type, oldInstance, newInstance, out);
		
		WorkspaceMetadata metadata = (WorkspaceMetadata)oldInstance;
		
		for (Repository repository : metadata.getRepositories()) {
		    out.writeStatement(new Statement(metadata, "add", new Object[] { repository, metadata.getWorkspaceFiles(repository) }));
		}
	}
}

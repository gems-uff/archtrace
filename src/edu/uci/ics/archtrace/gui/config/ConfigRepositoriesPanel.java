package edu.uci.ics.archtrace.gui.config;

import java.util.Collection;

import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * Allow configuration of repositories
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class ConfigRepositoriesPanel extends ConfigListPanel {

	/**
	 * Creates the panel
	 */
	public ConfigRepositoriesPanel() {
		super(PersistenceManager.getInstance().getRepositories(), ConnectorManager.getInstance().getCMConnectorNames());
	}

	@Override
	protected void addElement(RootElement element) {
		PersistenceManager.getInstance().getRepositories().add((Repository)element);
	}
	
	@Override
	protected void removeElement(RootElement element) {
		PersistenceManager.getInstance().getRepositories().remove((Repository)element);
	}
	
	@Override
	protected void addTraces(RootElement element) {
		Repository repository = (Repository)element;
		Architectures architectures = PersistenceManager.getInstance().getArchitectures(); 
		Collection<Trace> traces = architectures.getTraces(repository);
		TraceManager.getInstance().addTraces(traces);		
	}
}

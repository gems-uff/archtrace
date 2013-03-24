package edu.uci.ics.archtrace.gui.config;

import java.util.Collection;

import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.model.RootElement;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * Allows configuration of architectures
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class ConfigArchitecturesPanel extends ConfigListPanel {

	/**
	 * Creates the panel
	 */
	public ConfigArchitecturesPanel() {
		super(PersistenceManager.getInstance().getArchitectures(), ConnectorManager.getInstance().getArchConnectorNames());
	}

	@Override
	protected void addElement(RootElement element) {
		PersistenceManager.getInstance().getArchitectures().add((Architecture)element);
	}
	
	@Override
	protected void removeElement(RootElement element) {
		PersistenceManager.getInstance().getArchitectures().remove((Architecture)element);
	}
	
	@Override
	protected void addTraces(RootElement element) {
		Architecture architecture = (Architecture)element;
		Repositories repositories = PersistenceManager.getInstance().getRepositories();
		Collection<Trace> traces = architecture.getTraces(repositories);
		TraceManager.getInstance().addTraces(traces);
	}
}

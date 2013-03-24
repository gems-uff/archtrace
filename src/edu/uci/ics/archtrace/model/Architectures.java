package edu.uci.ics.archtrace.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import edu.uci.ics.archtrace.trace.Trace;

/**
 * Represent a collection of Architecture objects
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 3, 2004
 */
public class Architectures extends RootElementCollection<Architecture> {

	/**
	 * Constructs the collection
	 */
	public Architectures() {
		super("Architectures");
	}

	/**
	 * Get all traces of all architectures to any of the given repositories 
	 */
	public Collection<Trace> getTraces(Repositories repositories) {
		Collection<Trace> traces = new ArrayList<Trace>();

		for (Architecture architecture : getChildren()) {
			traces.addAll(architecture.getTraces(repositories));
		}

		return traces;
	}
	
	/**
	 * Get all traces of all architectures to any of the given repository
	 */
	public Collection<Trace> getTraces(Repository repository) {
		Collection<Trace> traces = new ArrayList<Trace>();

		for (Architecture architecture : getChildren()) {
			traces.addAll(architecture.getTraces(repository));
		}

		return traces;
	}

	/**
	 * Save all trace changes in all architectures
	 */
	public void save() {
		for (Architecture architecture : getChildren()) {
			try {
				architecture.save();
			} catch (Exception e) {
				Logger.global.info("Could not save architecture " + architecture.getName());
			}
		}
	}
}

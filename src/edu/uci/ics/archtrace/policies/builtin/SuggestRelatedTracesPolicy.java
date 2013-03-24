package edu.uci.ics.archtrace.policies.builtin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrj.cos.lens.datamining.DataMiner;
import br.ufrj.cos.lens.datamining.MinedElement;
import br.ufrj.cos.lens.datamining.MinedElements;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.PostTracePolicy;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * Suggest related traces
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class SuggestRelatedTracesPolicy implements PostTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Suggest related traces when a new trace is added.";

	/**
	 * Support threshold
	 * (shows suggestions with support values higher or equal this threshold)
	 */
	private static final float SUPPORT_THRESHOLD = 0.2f;
	
	/**
	 * Confidence threshold
	 * (shows suggestions with confidence values higher or equal this threshold)
	 */
	private static final float CONFIDENCE_THRESHOLD = 0.5f;

	/**
	 * @see edu.uci.ics.archtrace.policies.PostTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			
			List<MinedElement<ConfigurationItem>> minedElements = getMinedElements(ae, ci);
			
			if (!minedElements.isEmpty()) {
				StringBuffer message = new StringBuffer();
				message.append(trace)
					   .append(" has been created. However, architectural elements that have traces to ")
					   .append(ci)
					   .append(" usually have traces also to the following configuration items.");
				Collection<MinedElement<ConfigurationItem>> selectedMinedElements = GUIManager.getInstance().showSuggestionDialog(message.toString(), minedElements);
				
				if (!selectedMinedElements.isEmpty()) {
					for (MinedElement<ConfigurationItem> minedElement : selectedMinedElements) {
						Trace newTrace = new Trace(ae, minedElement.getElement());
						if (ae.getArchitecture().addTrace(newTrace))
							GUIManager.getInstance().addPolicyMessage(newTrace + " added due to data mining suggestion.");
					}
				}

			}
		}
	}

	/**
	 * Provides all mined elements
	 */
	private List<MinedElement<ConfigurationItem>> getMinedElements(ArchitecturalElement ae, ConfigurationItem ci) {
		// Create a set of cis that already have traces
		Set<ConfigurationItem> currentTransaction = new HashSet<ConfigurationItem>();
		for (Trace otherTrace : TraceManager.getInstance().getTraces(ae)) {
			currentTransaction.add(otherTrace.getConfigurationItem());
		}

		// Create the transactions (excluding the transaction of the current architectural element
		// and excluding all configuration items that are already in the current transaction)
		// TODO if it gets slow, the gereric mining algorithm can be made specific to this problem
		Collection<Collection<ConfigurationItem>> transactions = new ArrayList<Collection<ConfigurationItem>>();
		for (Trace trace : TraceManager.getInstance().getTraces(ci)) {
			ArchitecturalElement otherAe = trace.getArchitecturalElement();
			if (otherAe != ae) {
				Collection<ConfigurationItem> transaction = new ArrayList<ConfigurationItem>();
				for (Trace otherAeTrace : TraceManager.getInstance().getTraces(otherAe)) {
					ConfigurationItem otherCi = otherAeTrace.getConfigurationItem();
					if (!currentTransaction.contains(otherCi))
						transaction.add(otherCi);						
				}
				transactions.add(transaction);
			}
		}
		
		// Detect the total number of transactions (in other words: number of architectural elements)
		int totalTransactions = 0;
		Architectures architectures = PersistenceManager.getInstance().getArchitectures();
		for (int i = 0; i < architectures.getChildCount(); i++) {
			Architecture arch = (Architecture)architectures.getChild(i);
			totalTransactions += arch.getComponents().getChildCount() +
								 arch.getInterfaces().getChildCount() +
								 arch.getConnectors().getChildCount();
		}
		
		// Run the mining algorithm
		MinedElements<ConfigurationItem> minedElements = DataMiner.getInstance().mine(ci, transactions, totalTransactions);
		
		return minedElements.get(SUPPORT_THRESHOLD, CONFIDENCE_THRESHOLD);
	}

	/**
	 * @see edu.uci.ics.archtrace.policies.ArchTracePolicy#getDescription()
	 */
	public String getDescription() {
		return DESCRIPTION;
	}

	/**
	 * @see edu.uci.ics.archtrace.policies.ArchTracePolicy#getRationale()
	 */
	public String getRationale() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Other architectural elements that have traces to the configuration item also have traces to other configuration items. Data-mining techniques can be used to detect these relationships, avoiding incomplete traces.")
			  .append("\n(this policy is interactive)");
		return buffer.toString();
	}

}

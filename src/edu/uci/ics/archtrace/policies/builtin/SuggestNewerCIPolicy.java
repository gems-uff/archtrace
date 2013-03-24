package edu.uci.ics.archtrace.policies.builtin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PreTracePolicy;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * Suggest traces to the most recent versions of the configuration item
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 8, 2004
 */
public class SuggestNewerCIPolicy implements PreTracePolicy {

	/**
	 * The policy description
	 */
	public static final String DESCRIPTION = "Suggest traces to the most recent versions of the configuration item.";

	/**
	 * @see edu.uci.ics.archtrace.policies.PreTracePolicy#execute(edu.uci.ics.archtrace.trace.Trace, byte)
	 */
	public void execute(Trace trace, byte action) throws TraceAbortedException {
		if (action == ArchTracePolicy.ADD_ACTION) {
			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			
			if (!ci.getPosterities().isEmpty()) {
				List<ConfigurationItem> newerVersions = getNewerVersions(ci);
				StringBuffer message = new StringBuffer();
				message.append(trace)
					   .append(" is being created. However, the configuration items below are newer than ")
					   .append(ci)
					   .append(".");
				Collection<ConfigurationItem> selectedVersions = GUIManager.getInstance().showSuggestionDialog(message.toString(), newerVersions);
				
				if (!selectedVersions.isEmpty()) {
					for (ConfigurationItem newCi : selectedVersions) {
						Trace newTrace = new Trace(ae, newCi);
						if (ae.getArchitecture().addTrace(newTrace))
							GUIManager.getInstance().addPolicyMessage(trace + " replaced by " + newTrace + ".");
					}
					throw new TraceAbortedException("Suggestions accepted.");
				}
			}
		}
	}
	
	private List<ConfigurationItem> getNewerVersions(ConfigurationItem c) {
		List<ConfigurationItem> result = new ArrayList<ConfigurationItem>();
		
		Collection<Configuration> posterities = c.getPosterities();
		if (posterities.isEmpty()) {
			result.add(c);
		} else {
			for (Configuration posterity : posterities) {
				result.addAll(getNewerVersions((ConfigurationItem)posterity));
			}
		}
		
		return result;
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
		buffer.append("Sometimes, especially when the configuration item versions have different names or paths, a trace is selected to an old version of the configuration item because the user does not know that there are newer versions available.")
			  .append("\n(this policy is interactive)");
		return buffer.toString();
	}
}

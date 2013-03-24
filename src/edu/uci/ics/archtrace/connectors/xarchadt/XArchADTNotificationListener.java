package edu.uci.ics.archtrace.connectors.xarchadt;

import java.util.ArrayList;
import java.util.Collection;

import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.Types;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.PolicyManager;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.xadlutils.XadlUtils;
import edu.uci.ics.xarchutils.ObjRef;
import edu.uci.ics.xarchutils.XArchFlatEvent;
import edu.uci.ics.xarchutils.XArchFlatInterface;
import edu.uci.ics.xarchutils.XArchFlatListener;
import edu.uci.isr.xarch.cmimplementation.ConfigurationManagementImplementationImpl;
import edu.uci.isr.xarch.types.ArchTypesImpl;
import edu.uci.isr.xarch.types.ComponentTypeImpl;
import edu.uci.isr.xarch.types.ConnectorTypeImpl;
import edu.uci.isr.xarch.types.InterfaceTypeImpl;
import edu.uci.isr.xarch.versions.NodeImpl;

/**
 * This class is responsible for receiving notifications from xArchADT and
 * applying these notifications in the ArchTrace model.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 7, 2004
 */
public class XArchADTNotificationListener implements XArchFlatListener {

	/**
	 * The real connector to the xADL file
	 */
	private XArchFlatInterface xArchADT;
	
	/**
	 * Creates the notification listener
	 */
	public XArchADTNotificationListener(XArchFlatInterface xArchADT) {
		this.xArchADT = xArchADT;
	}

	/**
	 * @see edu.uci.ics.xarchutils.XArchFlatListener#handleXArchFlatEvent(edu.uci.ics.xarchutils.XArchFlatEvent)
	 */
	public void handleXArchFlatEvent(XArchFlatEvent event) {
		ObjRef sourceRef = event.getSource();
		ObjRef xArchRef = xArchADT.getXArch(sourceRef);
		Architectures architectures = PersistenceManager.getInstance().getArchitectures();
		
		synchronized (architectures) {
			Architecture architecture = architectures.get(xArchADT.getXArchURL(xArchRef));
			
			if ((event.getIsAttached()) && (architecture != null)) {
				if ((xArchADT.isInstanceOf(sourceRef, ConfigurationManagementImplementationImpl.class.getName())) &&
					("configurationItem".equals(event.getTargetName()))) {
					// Trace added or removed
					
					ObjRef ciRef = (ObjRef)event.getTarget();
					ObjRef aeRef = xArchADT.getParent(sourceRef);
					switch (event.getEventType()) {
						case XArchFlatEvent.ADD_EVENT:
							addTrace(architecture, aeRef, ciRef);
							break;
						case XArchFlatEvent.REMOVE_EVENT:
							removeTrace(architecture, aeRef, ciRef);
							break;
					}
				} else if ((xArchADT.isInstanceOf(sourceRef, ArchTypesImpl.class.getName())) &&
						   (("componentType".equals(event.getTargetName())) ||
						   	("interfaceType".equals(event.getTargetName())) ||
							("connectorType".equals(event.getTargetName())))) {
					// Architectural element added or removed
					
					ObjRef aeRef = (ObjRef)event.getTarget();
					switch (event.getEventType()) {
						case XArchFlatEvent.ADD_EVENT:
							this.addArchitecturalElement(architecture, xArchRef, aeRef, event.getTargetName());
							break;
						case XArchFlatEvent.REMOVE_EVENT:
							this.removeArchitecturalElement(architecture, aeRef, event.getTargetName());
							break;
					}
				} else if (((xArchADT.isInstanceOf(sourceRef, ComponentTypeImpl.class.getName())) ||
						    (xArchADT.isInstanceOf(sourceRef, InterfaceTypeImpl.class.getName())) ||
						    (xArchADT.isInstanceOf(sourceRef, ConnectorTypeImpl.class.getName()))) &&
						   (event.getEventType() == XArchFlatEvent.SET_EVENT)) {
					// Architectural element changed
					
					changeArchitecturalElement(architecture, sourceRef);
	
				} else if ((xArchADT.isInstanceOf(sourceRef, NodeImpl.class.getName())) &&
						   (event.getEventType() == XArchFlatEvent.SET_EVENT)) {
					// Architectural element changed
					
					ObjRef nodeRef = sourceRef;
					ObjRef[] xmlLinks = xArchADT.getReferences(xArchRef, XadlUtils.getID(xArchADT, nodeRef));
					for (ObjRef xmlLink : xmlLinks) {
						ObjRef parentRef = xArchADT.getParent(xmlLink);
						if ((xArchADT.isInstanceOf(parentRef, ComponentTypeImpl.class.getName())) ||
						    (xArchADT.isInstanceOf(parentRef, InterfaceTypeImpl.class.getName())) ||
						    (xArchADT.isInstanceOf(parentRef, ConnectorTypeImpl.class.getName()))) {
	
							changeArchitecturalElement(architecture, parentRef);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds a new trace
	 */
	private void addTrace(Architecture architecture, ObjRef aeRef, ObjRef ciRef) {
		Trace trace = createTrace(architecture, aeRef, ciRef);
		TraceManager.getInstance().addTrace(trace);
		
		PolicyManager.getInstance().executePostTracePolicies(trace, ArchTracePolicy.ADD_ACTION);
	}
	
	/**
	 * Removes an existing trace
	 */
	private void removeTrace(Architecture architecture, ObjRef aeRef, ObjRef ciRef) {
		Trace trace = createTrace(architecture, aeRef, ciRef);
		TraceManager.getInstance().removeTrace(trace);
		
		PolicyManager.getInstance().executePostTracePolicies(trace, ArchTracePolicy.REMOVE_ACTION);
	}

	/**
	 * Add an architectural element to ArchTrace
	 */
	private void addArchitecturalElement(Architecture architecture, ObjRef xArchRef, ObjRef aeRef, String targetName) {
		// TODO need to test this code!		
		String id = XadlUtils.getID(xArchADT, aeRef);
		String name = XadlUtils.getDescription(xArchADT, aeRef);
		String version = null;
		boolean immutable = false;
		Collection<String> ancestryIds = new ArrayList<String>();
		
		// Try to get the version and immutable state of the architectural element
		try {
			ObjRef versionGraphNodeRef = XadlUtils.resolveXLink(xArchADT, aeRef, "VersionGraphNode");
			ObjRef versionIDRef = (ObjRef)xArchADT.get(versionGraphNodeRef, "VersionID");
			version = (String)xArchADT.get(versionIDRef, "value");
			immutable = Boolean.valueOf((String)xArchADT.get(versionGraphNodeRef, "immutable")).booleanValue();
			
			ObjRef[] parentRefs = xArchADT.getAll(versionGraphNodeRef, "Parent");
			for (ObjRef parentRef : parentRefs) {
				ObjRef parentNodeRef = XadlUtils.resolveXLink(xArchADT, parentRef);				
				ObjRef[] xmlLinks = xArchADT.getReferences(xArchRef, XadlUtils.getID(xArchADT, parentNodeRef));
				for (ObjRef xmlLink : xmlLinks) {
					ObjRef ancestryRef = xArchADT.getParent(xmlLink);
					if ((xArchADT.isInstanceOf(ancestryRef, ComponentTypeImpl.class.getName())) ||
					    (xArchADT.isInstanceOf(ancestryRef, InterfaceTypeImpl.class.getName())) ||
					    (xArchADT.isInstanceOf(ancestryRef, ConnectorTypeImpl.class.getName()))) {
	
						ancestryIds.add(XadlUtils.getID(xArchADT, ancestryRef));
					}
				}
			}
		} catch (Exception e) {
			// Not versioned element - Ignore!
		}
		
		ArchitecturalElement ae = null;
		if ("componentType".equals(targetName)) {
			ae = new ArchitecturalElement(name, Types.COMPONENT, version, immutable, id, ancestryIds);
			architecture.getComponents().add(ae);
		} else if ("interfaceType".equals(targetName)) {
			ae = new ArchitecturalElement(name, Types.INTERFACE, version, immutable, id, ancestryIds);
			architecture.getInterfaces().add(ae);
		} else if ("connectorType".equals(targetName)) {
			ae = new ArchitecturalElement(name, Types.CONNECTOR, version, immutable, id, ancestryIds);
			architecture.getConnectors().add(ae);
		}

		PolicyManager.getInstance().executeArchitecturalElementEvolutionPolicies(ae, ArchTracePolicy.ADD_ACTION);
	}

	/**
	 * Remove an architectural element from ArchTrace
	 */
	private void removeArchitecturalElement(Architecture architecture, ObjRef aeRef, String targetName) {
		// TODO need to test this code!
		ArchitecturalElement ae = architecture.getArchitecturalElement(XadlUtils.getID(xArchADT, aeRef));
		if ("componentType".equals(targetName))
			architecture.getComponents().remove(ae);
		else if ("interfaceType".equals(targetName))
			architecture.getInterfaces().remove(ae);
		else if ("connectorType".equals(targetName))
			architecture.getConnectors().remove(ae);

		PolicyManager.getInstance().executeArchitecturalElementEvolutionPolicies(ae, ArchTracePolicy.REMOVE_ACTION);
	}
	
	/**
	 * Change an existing architectural element
	 */
	private void changeArchitecturalElement(Architecture architecture, ObjRef aeRef) {
		// TODO need to test this code!
		ArchitecturalElement ae = architecture.getArchitecturalElement(XadlUtils.getID(xArchADT, aeRef));
		ae.setName(XadlUtils.getDescription(xArchADT, aeRef));
		
		// Try to set the version and immutable state of the architectural element
		try {
			ObjRef versionGraphNodeRef = XadlUtils.resolveXLink(xArchADT, aeRef, "VersionGraphNode");
			ObjRef versionIDRef = (ObjRef)xArchADT.get(versionGraphNodeRef, "VersionID");
			ae.setVersion((String)xArchADT.get(versionIDRef, "value"));
			ae.setImmutable(Boolean.valueOf((String)xArchADT.get(versionGraphNodeRef, "immutable")).booleanValue());
		} catch (Exception e) {
			// Not versioned element - Ignore 
		}
		
		PolicyManager.getInstance().executeArchitecturalElementEvolutionPolicies(ae, ArchTracePolicy.CHANGE_ACTION);
	}

	/**
	 * Create a trace
	 */
	private Trace createTrace(Architecture architecture, ObjRef aeRef, ObjRef ciRef) {
		ConfigurationItem ci = getConfigurationItem(ciRef);
		ArchitecturalElement ae = architecture.getArchitecturalElement(XadlUtils.getID(xArchADT, aeRef));
		return new Trace(ae, ci);
	}
	
	/**
	 * Gets an existing configuration item
	 */
	private ConfigurationItem getConfigurationItem(ObjRef ciRef) {
		Repository repository = PersistenceManager.getInstance().getRepositories().get((String)xArchADT.get(ciRef, "repository"));
		return repository.getConfigurationItem((String)xArchADT.get(ciRef, "name"), (String)xArchADT.get(ciRef, "version"));
	}
}

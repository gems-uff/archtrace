package edu.uci.ics.archtrace.connectors.xarchadt;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.xml.sax.SAXException;

import edu.uci.ics.archtrace.connectors.ArchConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchTraceCollection;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;
import edu.uci.ics.archtrace.model.Types;
import edu.uci.ics.archtrace.policies.ArchTracePolicy;
import edu.uci.ics.archtrace.policies.TraceAbortedException;
import edu.uci.ics.archtrace.policies.PolicyManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.xadlutils.XadlUtils;
import edu.uci.ics.xarchutils.ObjRef;
import edu.uci.ics.xarchutils.XArchFlatImpl;
import edu.uci.ics.xarchutils.XArchFlatInterface;
import edu.uci.isr.xarch.cmimplementation.ConfigurationManagementImplementationImpl;
import edu.uci.isr.xarch.implementation.InterfaceTypeImplImpl;
import edu.uci.isr.xarch.implementation.VariantComponentTypeImplImpl;
import edu.uci.isr.xarch.implementation.VariantConnectorTypeImplImpl;
import edu.uci.isr.xarch.types.ComponentTypeImpl;
import edu.uci.isr.xarch.types.ConnectorTypeImpl;
import edu.uci.isr.xarch.types.InterfaceTypeImpl;
import edu.uci.isr.xarch.variants.VariantComponentTypeImpl;
import edu.uci.isr.xarch.variants.VariantConnectorTypeImpl;

/**
 * Encapsulates the necessary code to connect to xArchADT and read/write
 * architectural elements.
 * This connector uses cache with reference counting mechanism.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 21, 2004
 */
public class XArchADTConnector extends ArchConnector {

	/**
	 * xArchADT proxy (one instance for all connectors)
	 */
	private static XArchFlatInterface xArchADT;
	
	/**
	 * Constructs a new xArchADT connector. 
	 */
	public XArchADTConnector() {
		if (xArchADT == null) {
			XArchFlatImpl xArchADTImpl = new XArchFlatImpl();
			xArchADTImpl.addXArchFlatListener(new XArchADTNotificationListener(xArchADTImpl));
			xArchADT = xArchADTImpl;
		}
	}

	/**
	 * Constructs the connector with a specific XArchFlatInterface
	 * This is used when integrated with ArchStudio
	 */
	public XArchADTConnector(XArchFlatInterface xArchADT) {
		XArchADTConnector.xArchADT = xArchADT;
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#restore(edu.uci.ics.archtrace.model.RootElement)
	 */
	public void restore(RootElement element) throws ConnectionException {
		// TODO rethink this!
		update(element);
	}
		
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#update(edu.uci.ics.archtrace.model.RootElement)
	 */
	public void update(RootElement element) throws ConnectionException {
		Architecture architecture = (Architecture)element;
		try {
			ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + architecture.getUrl());
			
			updateArchitecturalElements(architecture, architecture.getComponents(), Types.COMPONENT, "Component");
			ConnectorManager.getInstance().fireConnectionProgress(architecture.getComponents().getChildCount() + " components loaded from " + architecture.getUrl());
			
			updateArchitecturalElements(architecture, architecture.getInterfaces(), Types.INTERFACE, "Interface");
			ConnectorManager.getInstance().fireConnectionProgress(architecture.getInterfaces().getChildCount() + " interfaces loaded from " + architecture.getUrl());
			
			updateArchitecturalElements(architecture, architecture.getConnectors(), Types.CONNECTOR, "Connector");
			ConnectorManager.getInstance().fireConnectionProgress(architecture.getConnectors().getChildCount() + " connectors loaded from " + architecture.getUrl());
		} catch (Exception e) {
			throw new ConnectionException("Could not load architectural elements from " + architecture.getUrl(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from " + architecture.getUrl());
		}
	}
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#getArchitecturalElements(edu.uci.ics.archtrace.model.ArchTraceView)
	 */
	public void updateArchitecturalElements(Architecture arch, ArchTraceCollection<ArchitecturalElement> view, int type, String typeName) throws MalformedURLException, IOException, SAXException {
		view.clear();		

		ObjRef xArchRef = xArchADT.parseFromURL(arch.getUrl());
		ObjRef typesContextRef = xArchADT.createContext(xArchRef, "Types");

		ObjRef[] archTypesRefs = xArchADT.getAllElements(typesContextRef, "ArchTypes", xArchRef);
		for (ObjRef archTypesRef : archTypesRefs) {
			ObjRef[] elementTypeRefs = xArchADT.getAll(archTypesRef, typeName + "Type");
			for (ObjRef elementTypeRef : elementTypeRefs) {
				view.add(getArchitecturalElement(xArchRef, elementTypeRef, type));
			}
		}
	}

	/**
	 * Provides an architectural element with a given id
	 */
	private ArchitecturalElement getArchitecturalElement(ObjRef xArchRef, ObjRef elementTypeRef, int type) {
		String id = XadlUtils.getID(xArchADT, elementTypeRef);
		String name = XadlUtils.getDescription(xArchADT, elementTypeRef);
		String version = null;
		boolean immutable = false;
		Collection<String> ancestryIds = new ArrayList<String>();
		
		// Try to get the version and immutable state of the architectural element
		try {
			ObjRef versionGraphNodeRef = XadlUtils.resolveXLink(xArchADT, elementTypeRef, "VersionGraphNode");
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
			// Not versioned element - Ignore 
		}
		
		return new ArchitecturalElement(name, type, version, immutable, id, ancestryIds);
	}
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#addTrace(edu.uci.ics.archtrace.model.Architecture, edu.uci.ics.archtrace.trace.Trace)
	 */
	public boolean addTrace(Architecture architecture, Trace trace) {
		try {
			PolicyManager.getInstance().executePreTracePolicies(trace, ArchTracePolicy.ADD_ACTION);

			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			
			// Get the references to the architecture and to the architectural element
			ObjRef xArchRef = xArchADT.getOpenXArch(architecture.getUrl());
			ObjRef aeRef = xArchADT.getByID(ae.getId());
			
			// Promote the architectural element, if necessary
			promote(xArchRef, aeRef, ae.getType());

			// Create the CMImplementation context
			ObjRef cmContextRef = xArchADT.createContext(xArchRef, "Cmimplementation");
			
			// Get (or create if necessary) the ConfigurationManagementImplementation element
			ObjRef cmImplRef = null;
			ObjRef[] cmImplRefs = xArchADT.getAll(aeRef, "Implementation");
			for (ObjRef implRef : cmImplRefs) {
				if (xArchADT.isInstanceOf(implRef, ConfigurationManagementImplementationImpl.class.getName())) {
					cmImplRef = implRef;
					break;
				}				
			}
			if (cmImplRef == null) {
				cmImplRef = xArchADT.create(cmContextRef, "ConfigurationManagementImplementation");
				xArchADT.add(aeRef, "Implementation", cmImplRef);
			}
			
			// Create and configure the ConfigurationItem element
			ObjRef ciRef = xArchADT.create(cmContextRef, "ConfigurationItem");
			xArchADT.set(ciRef, "name", ci.getPath());
			xArchADT.set(ciRef, "version", ci.getVersion().getName());		
			xArchADT.set(ciRef, "repository", ci.getRepository().getUrl());
			
			// Add the ConfigurationItem element to the ConfigurationManagementImplementation element
			xArchADT.add(cmImplRef, "ConfigurationItem", ciRef);
			return true;
		} catch (TraceAbortedException e) {
			// TODO Should be a listener (too coupled with GUI)
			StringBuffer buffer = new StringBuffer();
			buffer.append(e.getMessage())
				  .append(" ").append(trace)
				  .append(" aborted!");
			GUIManager.getInstance().addPolicyMessage(buffer.toString());
			return false;
		}
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#removeTrace(edu.uci.ics.archtrace.model.Architecture, edu.uci.ics.archtrace.trace.Trace)
	 */
	public boolean removeTrace(Trace trace) {
		try {
			PolicyManager.getInstance().executePreTracePolicies(trace, ArchTracePolicy.REMOVE_ACTION);

			ArchitecturalElement ae = trace.getArchitecturalElement();
			ConfigurationItem ci = trace.getConfigurationItem();
			
			// Get the references to the architecture and to the architectural element
			ObjRef aeRef = xArchADT.getByID(ae.getId());
			
			// Get all (it should be only one, but...) ConfigurationManagementImplementation element and remove the trace
			// TODO Think about an ID in the trace to spped up this algorithm
			ObjRef[] cmImplRefs = xArchADT.getAll(aeRef, "Implementation");
			for (ObjRef implRef : cmImplRefs) {
				if (xArchADT.isInstanceOf(implRef, ConfigurationManagementImplementationImpl.class.getName())) {
					ObjRef[] ciRefs = xArchADT.getAll(implRef, "ConfigurationItem");
					for (ObjRef ciRef : ciRefs) {
						if ((ci.getPath().equals(xArchADT.get(ciRef, "name"))) &&
							(ci.getVersion().getName().equals(xArchADT.get(ciRef, "version"))) &&
							(ci.getRepository().getUrl().equals(xArchADT.get(ciRef, "repository")))) {
	
							xArchADT.remove(implRef, "ConfigurationItem", ciRef);
						}
					}
				}				
			}
			return true;
		} catch (TraceAbortedException e) {
			// TODO Should be a listener (too coupled with GUI)
			StringBuffer buffer = new StringBuffer();
			buffer.append(e.getMessage())
				  .append(" ").append(trace)
				  .append(" aborted!");
			GUIManager.getInstance().addPolicyMessage(buffer.toString());
			return false;
		}
	}
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#getTraces(edu.uci.ics.archtrace.model.Architecture, Repositories)
	 */
	public Collection<Trace> getTraces(Architecture architecture, Repositories repositories) {
		try {
			return getTraces(architecture, null, repositories);
		} catch (Exception e) {
			return new ArrayList<Trace>();
		}
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#getTraces(edu.uci.ics.archtrace.model.Architecture, edu.uci.ics.archtrace.model.Repository)
	 */
	public Collection<Trace> getTraces(Architecture architecture, Repository repository) {
		try {
			return getTraces(architecture, repository, null);
		} catch (Exception e) {
			return new ArrayList<Trace>();
		}
	}	

	/**
	 * Get all traces from all architectural elements inside the given architecture to the given
	 * repository. If the repository is null, ask for a repository in the given repositories collection. 
	 */
	private Collection<Trace> getTraces(Architecture architecture, Repository repository, Repositories repositories) {
		Collection<Trace> traces = new ArrayList<Trace>();
		
		// Get all architectural elements
		List<ArchitecturalElement> architecturalElements = new ArrayList<ArchitecturalElement>();
		ArchTraceCollection components = architecture.getComponents();
		for (int i = 0; i < components.getChildCount(); i++)
			architecturalElements.add((ArchitecturalElement)components.getChild(i));
		ArchTraceCollection interfaces = architecture.getInterfaces();
		for (int i = 0; i < interfaces.getChildCount(); i++)
			architecturalElements.add((ArchitecturalElement)interfaces.getChild(i));
		ArchTraceCollection connectors = architecture.getConnectors();
		for (int i = 0; i < connectors.getChildCount(); i++)
			architecturalElements.add((ArchitecturalElement)connectors.getChild(i));
		
		// Try to get traces for each architectural element
		for (ArchitecturalElement ae : architecturalElements) {

			// Get the reference to the architectural element			
			ObjRef aeRef = xArchADT.getByID(ae.getId());
			
			// Try to verify if each trace is related to CIs inside the given repository
			try {
				ObjRef[] cmImplRefs = xArchADT.getAll(aeRef, "Implementation");
				for (ObjRef implRef : cmImplRefs) {
					if (xArchADT.isInstanceOf(implRef, ConfigurationManagementImplementationImpl.class.getName())) {
						ObjRef[] ciRefs = xArchADT.getAll(implRef, "ConfigurationItem");
						for (ObjRef ciRef : ciRefs) {
							String repositoryUrl = (String)xArchADT.get(ciRef, "repository");
							if (repositories == null) {
								if (repository.getUrl().equals(repositoryUrl)) {
									ConfigurationItem ci = repository.getConfigurationItem((String)xArchADT.get(ciRef, "name"), (String)xArchADT.get(ciRef, "version"));
									if (ci != null)
									    traces.add(new Trace(ae, ci));
								}							
							} else {
								repository = repositories.get(repositoryUrl);
								if (repository != null) {
									ConfigurationItem ci = repository.getConfigurationItem((String)xArchADT.get(ciRef, "name"), (String)xArchADT.get(ciRef, "version"));
									if (ci != null)
									    traces.add(new Trace(ae, ci));
								}
							}

						}
					}				
				}
			} catch (RuntimeException e) {
				// Probably the architectural element is not a "Impl" element (no implementation element
				// inside). Just ignore.
			}
		}

		return traces;
	}

	/**
	 * Promote the architectural element, if necessary
	 */
	private void promote(ObjRef xArchRef, ObjRef aeRef, int type) {
		ObjRef implementationContextRef = xArchADT.createContext(xArchRef, "Implementation");
		ObjRef variantsContextRef = xArchADT.createContext(xArchRef, "Variants");
		switch (type) {
			case Types.COMPONENT:
				if (!xArchADT.isInstanceOf(aeRef, VariantComponentTypeImplImpl.class.getName())) {
					if (!xArchADT.isInstanceOf(aeRef, VariantComponentTypeImpl.class.getName())) {
						xArchADT.promoteTo(variantsContextRef, "VariantComponentType", aeRef);
					}
					xArchADT.promoteTo(implementationContextRef, "VariantComponentTypeImpl", aeRef);
				}
				break;
			case Types.INTERFACE:
				if (!xArchADT.isInstanceOf(aeRef, InterfaceTypeImplImpl.class.getName())) {
					xArchADT.promoteTo(implementationContextRef, "InterfaceTypeImpl", aeRef);
				}
				break;
			case Types.CONNECTOR:
				if (!xArchADT.isInstanceOf(aeRef, VariantConnectorTypeImplImpl.class.getName())) {
					if (!xArchADT.isInstanceOf(aeRef, VariantConnectorTypeImpl.class.getName())) {
						xArchADT.promoteTo(variantsContextRef, "VariantConnectorType", aeRef);						
					}
					xArchADT.promoteTo(implementationContextRef, "VariantConnectorTypeImpl", aeRef);
				}
				break;
		}
	}
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchConnector#save(edu.uci.ics.archtrace.model.Architecture)
	 */
	public void save(Architecture architecture) throws ConnectionException {
		try {
			ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + architecture.getUrl());
			ObjRef xArchRef = xArchADT.getOpenXArch(architecture.getUrl());
			URL url = new URL(architecture.getUrl());
			ConnectorManager.getInstance().fireConnectionProgress("Saving " + architecture.getUrl());
			xArchADT.writeToFile(xArchRef, url.getFile());
		} catch (Exception e) {
			throw new ConnectionException("Could not save " + architecture.getName(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from " + architecture.getUrl());
		}
	}
}

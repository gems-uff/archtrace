package edu.uci.ics.archtrace.hooks.archstudio;

import javax.swing.JOptionPane;

import archstudio.invoke.InvokableBrick;
import archstudio.invoke.InvokeMessage;
import archstudio.invoke.InvokeUtils;
import c2.fw.Identifier;
import c2.fw.Message;
import c2.fw.MessageProcessor;
import c2.fw.NamedPropertyMessage;
import c2.legacy.AbstractC2DelegateBrick;
import c2.pcwrap.EBIWrapperUtils;
import edu.uci.ics.archtrace.ArchTrace;
import edu.uci.ics.archtrace.connectors.xarchadt.XArchADTConnector;
import edu.uci.ics.archtrace.connectors.xarchadt.XArchADTNotificationListener;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.xarchutils.XArchFlatEvent;
import edu.uci.ics.xarchutils.XArchFlatInterface;

/**
 * ArchTrace ArchStudio component
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 23, 2004
 */
public class ArchTraceC2Component extends AbstractC2DelegateBrick implements InvokableBrick {

	/**
	 * Instance created by ArchStudio
	 * It is not an implementation of the singleton pattern
	 */
	private static ArchTraceC2Component instance;
	
	private XArchADTConnector xArchADTConnector;
	
	private XArchADTNotificationListener xArchADTNotificationListener;
	
	private Architectures architectures;
	
	/**
	 * Constructs the component
	 */
	public ArchTraceC2Component(Identifier id){
		super(id);

		instance = this;
		XArchFlatInterface xArchADT = (XArchFlatInterface)EBIWrapperUtils.addExternalService(this, topIface, XArchFlatInterface.class);
		xArchADTConnector = new XArchADTConnector(xArchADT);

		xArchADTNotificationListener = new XArchADTNotificationListener(xArchADT);
		this.addMessageProcessor(new MessageProcessor() {
			public void handle(Message message) {
				if (ArchTrace.getInstance().isRunning() && (message instanceof NamedPropertyMessage)) {
					NamedPropertyMessage namedPropertyMessage = (NamedPropertyMessage)message;
					try{
						if(namedPropertyMessage.getBooleanParameter("stateChangeMessage")){
							Object eventObject = namedPropertyMessage.getParameter("paramValue0");

							if(eventObject instanceof XArchFlatEvent){
								XArchFlatEvent event = (XArchFlatEvent)eventObject;
								xArchADTNotificationListener.handleXArchFlatEvent(event);
							}
						}
					}
					catch(Exception e){}
				}
			}
		});
		
		InvokeUtils.deployInvokableService(this, bottomIface, "ArchTrace", "ArchTrace for ArchStudio 3.");
	}
	
	/**
	 * Provides the component instance or null if no instance has been created
	 */
	public static ArchTraceC2Component getInstance() {
		return instance;
	}
	
	/**
	 * @see archstudio.invoke.InvokableBrick#invoke(archstudio.invoke.InvokeMessage)
	 */
	public void invoke(InvokeMessage m){
		String architectureURL = m.getArchitectureURL();

		if (architectureURL == null) {
        	JOptionPane.showMessageDialog(null, "No file selected", "ArchTrace", JOptionPane.ERROR_MESSAGE);
        } else {
    		architectures = new Architectures();
   			architectures.add(new Architecture("ArchStudio selected architecture", architectureURL, xArchADTConnector));
           	ArchTrace.getInstance().start(ArchTrace.ARCHSTUDIO_MODE);
        }
	}

	/**
	 * Provides the collection of architectures
	 * This implementation gives only the architecture selected in the file manager
	 */
	public Architectures getArchitectures() {
		return architectures;
	}
}

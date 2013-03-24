package edu.uci.ics.archtrace.connectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import edu.uci.ics.archtrace.connectors.subversion.SVNKitConnector;
import edu.uci.ics.archtrace.connectors.subversion.SubversionCLIConnector;
import edu.uci.ics.archtrace.connectors.subversion.SubversionJNIConnector;
import edu.uci.ics.archtrace.connectors.xarchadt.XArchADTConnector;

/**
 * Control access to all available connectors
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 29, 2004
 */
public class ConnectorManager {
	
	// Names of connectors
    public static final String SUBVERSION_JNI = "Subversion via JNI (requires svn libraries)";
	public static final String SUBVERSION_CLI = "Subversion via command line (requires svn in the PATH variable)";
	public static final String SUBVERSION_SVNKIT = "Subversion via SVNKIT";
	public static final String XARCHADT = "xArchADT";
	
	private CMNotificationListener cmNotificationListener;
		
	/**
	 * Singleton instance
	 */
	private static ConnectorManager instance;
	
	/**
	 * Map of available architectural connectors indexed by names
	 */
	private Map<String, Class> archConnectors;
	
	/**
	 * Map of available cm connectors indexed by names
	 */
	private Map<String, Class> cmConnectors;
	
	/**
	 * Names of existing connectors indexed by connector classes
	 */
	private Map<Class, String> names;
	
	/**
	 * Connection Listeners
	 */
	private Collection<ConnectionListener> connectionListeners;

	
	/**
	 * Constructs the connector manager registering all connectors 
	 */
	private ConnectorManager() {
		archConnectors = new HashMap<String, Class>();
		cmConnectors = new HashMap<String, Class>();
		names = new HashMap<Class, String>();
		connectionListeners = new ArrayList<ConnectionListener>();

		// Try to register connectors
		// TODO put try/catch inside register and catch the static error on JNIConnector
		try {
			registerCMConnector(SUBVERSION_JNI, SubversionJNIConnector.class);
		} catch (Error e) {
			StringBuffer message = new StringBuffer();
			message.append("Unable to register connector ").append(SUBVERSION_JNI);
			message.append(" (").append(e.getMessage()).append(")");
			Logger.global.warning(message.toString());
		}
		registerCMConnector(SUBVERSION_CLI, SubversionCLIConnector.class);
		registerCMConnector(SUBVERSION_SVNKIT, SVNKitConnector.class);
		registerArchConnector(XARCHADT, XArchADTConnector.class);
	}

	/**
	 * Provides the singleton instance
	 */
	public static ConnectorManager getInstance() {
		if (instance == null)
			instance = new ConnectorManager();
		return instance;
	}
	
	/**
	 * Resets the singleton instance
	 */
	public static synchronized void resetInstance() {
		instance = null;
	}
	
	/**
	 * Register a architectural connector in the manager
	 */
	private void registerArchConnector(String name, Class connectorClass) {
		archConnectors.put(name, connectorClass);
		names.put(connectorClass, name);
	}
	
	/**
	 * Register a cm connector in the manager
	 */
	private void registerCMConnector(String name, Class connectorClass) {
		cmConnectors.put(name, connectorClass);	
		names.put(connectorClass, name);
	}
	
	/**
	 * Get all architectural connectors available
	 */
	public Object[] getArchConnectorNames() {
		return archConnectors.keySet().toArray();
	}
	
	/**
	 * Get all cm connectors available
	 */
	public Object[] getCMConnectorNames() {
		return cmConnectors.keySet().toArray();
	}
	
	/**
	 * Get the name of a specific connector
	 * TODO: put the connector name inside the connector itself
	 */
	public String getConnectorName(ArchTraceConnector connector) {
		return names.get(connector.getClass());
	}
	
	/**
	 * Create a new connector by it name
	 */
	public ArchTraceConnector createConnector(String name) throws ConnectionException {
		Class connectorClazz = archConnectors.get(name);
		if (connectorClazz == null)
			connectorClazz = cmConnectors.get(name);
		
		if (connectorClazz != null)
			try {
				return (ArchTraceConnector)connectorClazz.newInstance();
			} catch (Exception e) {
				throw new ConnectionException("Could not create connector " + name, e);
			}
		else
			throw new ConnectionException("Could not find connector " + name);		
	}
	
	/**
	 * Adds a new connection listener to this connector
	 */
	public void addConnectionListener(ConnectionListener l) {
		connectionListeners.add(l);
	}

	/**
	 * Removes a connection listener from this connector
	 */
	public void removeConnectionListener(ConnectionListener l) {
		connectionListeners.remove(l);
	}
	
	/**
	 * Notify that a connection just started
	 */
	public void fireConnectionStarted(String message) {
		for (ConnectionListener listener : connectionListeners) {
			listener.connectionStarted(message);
		}
	}

	/**
	 * Notify that a connection just started
	 */
	public void fireConnectionProgress(String message) {
		for (ConnectionListener listener : connectionListeners) {
			listener.connectionProgress(message);
		}
	}
	
	/**
	 * Notify that a connection just started
	 */
	public void fireConnectionFinished(String message) {
		for (ConnectionListener listener : connectionListeners) {
			listener.connectionFinished(message);
		}
	}
	
	/**
	 * Start a listener of CM events
	 */
	public synchronized void startCMNotificationListener() {
		try {
			cmNotificationListener = new CMNotificationListener();
			Thread thread = new Thread(cmNotificationListener);
			thread.start();
		} catch (Exception e) {
			Logger.global.info("Could not start CM notification listener (" + e.getMessage() + ").");
		}		
	}

	/**
	 * Stops the CM notification listener
	 */
	public synchronized void stopCMNotificationListener() {
		if (cmNotificationListener != null) {
			cmNotificationListener.stop();
			cmNotificationListener = null;
		}
	}
}

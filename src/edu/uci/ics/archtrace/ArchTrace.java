package edu.uci.ics.archtrace;

import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.gui.ArchTraceWindow;
import edu.uci.ics.archtrace.gui.config.ConfigDialog;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.gui.utils.SplashScreen;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.policies.PolicyManager;
import edu.uci.ics.archtrace.trace.TraceManager;
import edu.uci.ics.archtrace.trace.Trace;

/**
 * This class starts ArchTrace  
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 21, 2004
 */
public class ArchTrace {
	
	/**
	 * Singleton instance
	 */
	private static ArchTrace instance;
	
	/**
	 * Not running
	 */
	private static final byte NOT_RUNNING = 0;
	
	/**
	 * Running standalone
	 */
	public static final byte STANDALONE_MODE = 1;

	/**
	 * Running as ArchStudio component
	 */
	public static final byte ARCHSTUDIO_MODE = 2;
	
	/**
	 * Running mode
	 */
	private byte runningMode;

	/**
	 * Singleton constructor
	 */
	private ArchTrace() {
		runningMode = NOT_RUNNING;
	}
	
	/**
	 * Provides the singleton instance
	 */
	public synchronized static ArchTrace getInstance() {
		if (instance == null)
			instance = new ArchTrace();
		return instance;
	}
	
	/**
	 * Start ArchTrace
	 */
	public synchronized void start(byte runningMode) {
		if (isRunning())
			stop();
		this.runningMode = runningMode;
		
		// Show splash window
		SplashScreen splash = new SplashScreen();
		splash.setVisible(true);
		
		splash.setStatus("Starting subsystems...");
		final PersistenceManager persistenceManager = PersistenceManager.getInstance();
		final PolicyManager policyManager = PolicyManager.getInstance();
		final GUIManager guiManager = GUIManager.getInstance();
		final ConnectorManager connectorManager = ConnectorManager.getInstance();
		final TraceManager traceManager = TraceManager.getInstance();

		splash.setStatus("Loading configuration...");
		persistenceManager.load();

		splash.setStatus("Restoring repositories...");
		Repositories repositories = persistenceManager.getRepositories();
		repositories.restore();
		
		splash.setStatus("Restoring architectures...");
		Architectures architectures = persistenceManager.getArchitectures();
		architectures.restore();
		
		splash.setStatus("Restoring traces...");
		Collection<Trace> traces = architectures.getTraces(repositories);
		
		splash.setStatus("Registering traces...");
		traceManager.addTraces(traces);		
		
		splash.setStatus("Enabling policies...");
		policyManager.setEnabled(true);

		splash.setStatus("Constructing the main window...");
		final ArchTraceWindow mainWindow = new ArchTraceWindow(persistenceManager.getArchitectures().getModel(), persistenceManager.getRepositories().getModel());
		guiManager.setMainWindow(mainWindow);
		
		splash.setVisible(false);
		
		// Showing the main window
		mainWindow.setVisible(true);	
		
		// Updating the repositories and turning on the CM notification
		guiManager.run(new Runnable() {
			public void run() {
				guiManager.setSplashScreenStatus("Starting CM notification listening...");
				connectorManager.startCMNotificationListener();
				
				guiManager.setSplashScreenStatus("Updating repositories...");
				persistenceManager.getRepositories().update();
				
				guiManager.setSplashScreenStatus("Constructing the configuration dialog...");
				ConfigDialog configDialog = new ConfigDialog(mainWindow);
				guiManager.setConfigDialog(configDialog);
			}		
		});
		
		// Showing the config window if needed
		if (persistenceManager.getPreferences().isShownAutomatically())
			guiManager.showConfigDialog();
	}
	
	/**
	 * Informs if ArchTrace is running
	 */
	public synchronized boolean isRunning() {
		return (runningMode != NOT_RUNNING);
	}
	
	/**
	 * Informs the running mode of ArchTrace
	 */
	public byte getRunningMode() {
		return runningMode;
	}

	/**
	 * Stops the execution of ArchTrace
	 */
	public synchronized void stop() {		
		final PersistenceManager persistenceManager = PersistenceManager.getInstance();
		final GUIManager guiManager = GUIManager.getInstance();
		final ConnectorManager connectorManager = ConnectorManager.getInstance();
		
		guiManager.run(new Runnable() {
			public void run() {
				guiManager.setSplashScreenStatus("Stopping CM notification listening...");
				connectorManager.stopCMNotificationListener();
				
				if (persistenceManager.getPreferences().isSaveAutomatically()) {
					guiManager.setSplashScreenStatus("Saving configuration...");
					persistenceManager.store();
				}

				guiManager.setSplashScreenStatus("Stopping subsystems...");
				PersistenceManager.resetInstance();
				PolicyManager.resetInstance();
				GUIManager.resetInstance();
				ConnectorManager.resetInstance();
				TraceManager.resetInstance();
			}
		});
		
		if (runningMode == STANDALONE_MODE)
			System.exit(0);
		else {
			guiManager.disposeMainWindow();
			runningMode = NOT_RUNNING;
		}
	}
	
	/**
	 * Start ArchTrace
	 */
	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		
		System.out.println("Starting ArchTrace...");
		ArchTrace.getInstance().start(STANDALONE_MODE);		
	}
}
package edu.uci.ics.archtrace.connectors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.trace.TraceManager;


/**
 * Listen for notifications coming from subversion
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 31, 2004
 */
public class CMNotificationListener implements Runnable {
	
	/**
	 * ArchTrace port
	 */
	private static final int PORT = 1234;
	
	/**
	 * The sockets server
	 */
	private ServerSocket server;
	
	/**
	 * Creates the listener
	 */
	public CMNotificationListener() {
		try {
			server = new ServerSocket(PORT);
		} catch (IOException e) {
			Logger.global.info("Could not set the CM notification listener (" + e.getMessage() + ").");
		}
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			while (true) {
				Socket client = server.accept();
				GUIManager.getInstance().run(new ClientProcessor(client));
			}
		} catch (IOException e) {
			Logger.global.info("Not listening CM notifications (" + e.getMessage() + ").");
		}
	}

	/**
	 * Stops the CM notification listener
	 */
	public void stop() {
		try {
			server.close();
		} catch (IOException e) {
			Logger.global.info("Could not close sockets.");
		}
	}
}

/**
 * Process each client that send notifications
 */
class ClientProcessor implements Runnable {

	/**
	 * The name of the repository
	 */
	private String repositoryName;
	
	/**
	 * The name of the new configuration
	 */
	private String configurationName;
	
	/**
	 * Constructs the processor of notification connections
	 */
	public ClientProcessor(Socket client) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
		this.repositoryName = reader.readLine();
		this.configurationName = reader.readLine();
		reader.close();		
		client.close();
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			Repositories repositories = PersistenceManager.getInstance().getRepositories();
			synchronized (repositories) {
				Repository repository = repositories.get(repositoryName);
				if (repository != null) {
					// TODO use configurationName information to speed-up the update
					repository.update();
					Configuration configuration = repository.getConfiguration(configurationName);
					TraceManager.getInstance().updateIndirectTraces(configuration);
				}
			}
		} catch (ConnectionException e) {
			GUIManager.getInstance().showErrorDialog(e);
		}		
	}
}

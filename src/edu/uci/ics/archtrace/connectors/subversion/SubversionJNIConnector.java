package edu.uci.ics.archtrace.connectors.subversion;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.tigris.subversion.javahl.ChangePath;
import org.tigris.subversion.javahl.LogMessage;
import org.tigris.subversion.javahl.Revision;
import org.tigris.subversion.javahl.SVNClient;

import edu.uci.ics.archtrace.connectors.CMConnector;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.connectors.ConnectorManager;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElement;

/**
 * Encapsulates the Subversion connection/parsing mechanism
 * This connector uses cache with reference counting mechanism.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 26, 2004
 */
public class SubversionJNIConnector extends CMConnector {

	/**
     * load the needed native library.
     * This order is important!!!
     * It needs to load dependent library first. Otherwise, java webstart will not
     * be able to detect that the main lib (nativelly called by java code) needs
     * other libs.
     * I suspect that the dependencies are in this order:
     * libsvnjavahl-1 -> intl
     * libsvnjavahl-1 -> ssleay32
     * ssleay32 -> libeay32
     * libsvnjavahl-1 -> libaprutil
     * libaprutil -> libdb42
     * libaprutil -> libapriconv
     * libapriconv -> libapr
     * 
     * TODO put a try/catch here. Throw ConnectionException. Test removing the lib.
     */
    static {
        System.loadLibrary("libapr");
        System.loadLibrary("libapriconv");        
        System.loadLibrary("libdb42");
        System.loadLibrary("libaprutil");
        System.loadLibrary("libeay32");
    	System.loadLibrary("ssleay32");
    	System.loadLibrary("intl");
    	System.loadLibrary("libsvnjavahl-1");
    }

	/**
	 * Subversion client
	 */
	private SVNClient svn; 
	
	/**
	 * Constructs a connector to Subversion
	 */
	public SubversionJNIConnector() {
		svn = new SVNClient();
	}
	
	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#restore(edu.uci.ics.archtrace.model.RootElement)
	 */
	public synchronized void restore(RootElement element) throws ConnectionException {
		Repository repository = (Repository)element;
		
		load(repository, 1, repository.getLastProcessedConfigurationNumber());
	}

	/**
	 * @see edu.uci.ics.archtrace.connectors.ArchTraceConnector#update(edu.uci.ics.archtrace.model.RootElement)
	 */
	public synchronized void update(RootElement element) throws ConnectionException {
		Repository repository = (Repository)element;
		long firstConfiguration = repository.getLastProcessedConfigurationNumber() + 1;
		long lastConfiguration = getLastConfiguration(repository.getUrl());
		load(repository, firstConfiguration, lastConfiguration);
	}
	
	/**
	 * Provides the last existing revision in the repository
	 */
	private long getLastConfiguration(String url) throws ConnectionException {
	    long result;
	    
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + url);
		try {
		    LogMessage[] logs = svn.logMessages(url, Revision.HEAD, Revision.HEAD);
		    result = logs[0].getRevisionNumber();
		    
		} catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to access repository ").append(url);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + url);
		}
		
		return result;
	}
	
	/**
	 * Load a set of configurations from the repository
	 * (from firstRevision up to lastRevision, inclusive on both ends)
	 */
	private void load(Repository repository, long firstRevision, long lastRevision) throws ConnectionException {		
		if (firstRevision <= lastRevision) {
			String url = repository.getUrl();
	
			ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + url);
			try {
			    LogMessage[] logMessages = svn.logMessages(url, Revision.getInstance(firstRevision), Revision.getInstance(lastRevision), true, true);
			    for (int i = 0; i < logMessages.length; i++) {
			        LogMessage logMessage = logMessages[i];
					String revision = String.valueOf(logMessage.getRevisionNumber());
					Configuration configuration = new Configuration(revision, repository.getLastConfiguration());
					configuration.setAuthor(logMessage.getAuthor());
					configuration.setDate(logMessage.getDate());
					repository.add(configuration);
					
					ChangePath[] changePaths = logMessage.getChangedPaths();
					if (changePaths != null) {
						SortedSet<Action> actions = new TreeSet<Action>(new Comparator<Action>() {
							public int compare(Action a1, Action a2) {
								return a1.getPath().compareTo(a2.getPath());
							}
						});
						
						for (ChangePath changePath : changePaths) {
						    Action action = null;
							switch (changePath.getAction()) {
							case 'M':
								action = new ModifyAction(configuration);
								break;
							case 'A':
								action = new AddAction(configuration, repository.getConfigurationItem(changePath.getCopySrcPath(), String.valueOf(changePath.getCopySrcRevision())));
								break;
							case 'R':
								action = new ReplaceAction(configuration, repository.getConfigurationItem(changePath.getCopySrcPath(), String.valueOf(changePath.getCopySrcRevision())));
								break;
							case 'D':
								action = new DeleteAction(configuration);
								break;
							}
							action.setPath(changePath.getPath());
							actions.add(action);
						}
						
						for (Action action : actions) {
							action.run();
						}
					}
					
					ConnectorManager.getInstance().fireConnectionProgress((i + 1) + " of " + logMessages.length + " configurations loaded from " + repository.getUrl());
			    }
			} catch (Exception e) {
				StringBuffer errorMessage = new StringBuffer("Not able to access repository ").append(url);
				errorMessage.append(" (").append(e.getMessage()).append(")");

				throw new ConnectionException(errorMessage.toString(), e);
			} finally {
				ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + url);
			}
		}
	}

    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#checkout(edu.uci.ics.archtrace.model.Repository, edu.uci.ics.archtrace.model.Configuration, String)
     */
    public void checkout(Repository repository, Configuration configuration, String workspace) throws ConnectionException {
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + repository);
		try {
		    ConnectorManager.getInstance().fireConnectionProgress("Checking-out " + configuration + " from " + repository);
            svn.checkout(repository.getUrl(), workspace, Revision.getInstance(configuration.getNumber()), true);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to check-out ").append(configuration).append(" from repository ").append(repository);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + repository);
		}
    }

    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#checkin(edu.uci.ics.archtrace.model.Repository, java.io.File, java.lang.String)
     */
    public long checkin(Repository repository, String workspace, String message) throws ConnectionException {
		ConnectorManager.getInstance().fireConnectionStarted("Connecting to " + repository);
		try {
		    ConnectorManager.getInstance().fireConnectionProgress("Checking-in to " + repository);
            return svn.commit(new String[] { workspace }, message, true);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to check-in to repository ").append(repository);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} finally {
			ConnectorManager.getInstance().fireConnectionFinished("Disconnected from "  + repository);
		}
    }

    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#add(java.lang.String)
     */
    public void add(String path) throws ConnectionException {
		try {
            svn.add(path, false);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to add file ").append(path);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		} 
    }
    
    /**
     * @see edu.uci.ics.archtrace.connectors.CMConnector#remove(java.lang.String)
     */
    public void remove(String path) throws ConnectionException {
		try {
            svn.remove(new String[] { path }, "", true);
        } catch (Exception e) {
			StringBuffer errorMessage = new StringBuffer("Not able to remove file ").append(path);
			errorMessage.append(" (").append(e.getMessage()).append(")");

			throw new ConnectionException(errorMessage.toString(), e);
		}
    }
    
//    /**
//     * Sets the notification callback used to send processing information back
//     * to the calling program.
//     * @param notify listener that the SVN library should call on many
//     *               file operations.
//     */
//    public native void notification(Notify notify);

}

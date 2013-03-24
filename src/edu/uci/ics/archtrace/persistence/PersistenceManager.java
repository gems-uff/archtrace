package edu.uci.ics.archtrace.persistence;

import java.beans.DefaultPersistenceDelegate;
import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import edu.uci.ics.archcm.model.WorkspaceFile;
import edu.uci.ics.archcm.model.WorkspaceMetadata;
import edu.uci.ics.archtrace.ArchTrace;
import edu.uci.ics.archtrace.hooks.archstudio.ArchTraceC2Component;
import edu.uci.ics.archtrace.model.Architecture;
import edu.uci.ics.archtrace.model.Architectures;
import edu.uci.ics.archtrace.model.Repositories;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.model.RootElementCollection;
import edu.uci.ics.archtrace.policies.Policies;

/**
 * Manages access to persisted ArchTrace information 
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 30, 2004
 */
public class PersistenceManager {

	/**
	 * Persistence dir name
	 */
	public static final File PERSISTENCE_DIR = new File(System.getProperty("user.home") + System.getProperty("file.separator") + ".archtrace");
	
	/**
	 * Repositories file
	 */
	private static final File REPOSITORIES_FILE = new File(PERSISTENCE_DIR, "repositories.xml");
	
	/**
	 * Architectures file
	 */
	private static final File ARCHITECTURES_FILE = new File(PERSISTENCE_DIR, "architectures.xml");
	
	/**
	 * Preferences file
	 */
	private static final File PREFERENCES_FILE = new File(PERSISTENCE_DIR, "preferences.xml");
	
	/**
	 * Policies file
	 */
	private static final File POLICIES_FILE = new File(PERSISTENCE_DIR, "policies.xml");
	
	/**
	 * Singleton persistence manager
	 */
	private static PersistenceManager instance;
	
	/**
	 * The user preferences
	 */
	private Preferences preferences;
	
	/**
	 * The collection of policies
	 */
	private Policies policies;
	
	/**
	 * The collection of Architecture objects
	 */
	private Architectures architectures;

	/**
	 * The collection of Repository objects
	 */
	private Repositories repositories;
	
	/**
	 * Creates a persistence manager loading the persisted elements
	 */
	private PersistenceManager() {}
	
	/**
	 * Provides the singleton instance
	 */
	public synchronized static PersistenceManager getInstance() {
		if (instance == null)
			instance = new PersistenceManager();
		return instance;
	}
	
	/**
	 * Resets the singleton instance
	 */
	public static synchronized void resetInstance() {
		instance = null;
	}
	
	/**
	 * Provide the current user preferences
	 */
	public Preferences getPreferences() {
		return preferences;
	}
	
	/**
	 * Provide the set of policies
	 */
	public Policies getPolicies() {
		return policies;
	}
	
	/**
	 * Provide the architectures
	 */
	public Architectures getArchitectures() {
		return architectures;
	}
	
	/**
	 * Provide the architectures
	 */
	public Repositories getRepositories() {
		return repositories;
	}
	
	/**
	 * Stores the current information
	 */
	public void store() {
		// Assert that XMLEncoder will use the current class classloader
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		
		if (!PERSISTENCE_DIR.exists())
			PERSISTENCE_DIR.mkdirs();

		architectures.save();
		store(preferences, PREFERENCES_FILE);
		store(policies, POLICIES_FILE);
		store(repositories, REPOSITORIES_FILE);
		if (ArchTrace.getInstance().getRunningMode() != ArchTrace.ARCHSTUDIO_MODE) {
			store(architectures, ARCHITECTURES_FILE);
		}

		// Return the context classloader to the current thread
		Thread.currentThread().setContextClassLoader(contextClassLoader);
	}
	
	/**
	 * Load all information
	 */
	public void load() {
		// Assert that XMLDecoder will use the current class classloader
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
		
		repositories = load(REPOSITORIES_FILE, Repositories.class);
		if (repositories == null)
			repositories = new Repositories();
		
		if (ArchTrace.getInstance().getRunningMode() == ArchTrace.ARCHSTUDIO_MODE) {
			ArchTraceC2Component archTraceC2Component = ArchTraceC2Component.getInstance();
			if (archTraceC2Component != null)
				architectures = archTraceC2Component.getArchitectures();
		} else {
			architectures = load(ARCHITECTURES_FILE, Architectures.class);
		}
		if (architectures == null)
			architectures = new Architectures();
		
		preferences = load(PREFERENCES_FILE, Preferences.class);
		if (preferences == null)
			preferences = new Preferences();			

		policies = load(POLICIES_FILE, Policies.class);
		if (policies == null)
			policies = Policies.createBuiltInPolicies();
		
		// Return the context classloader to the current thread
		Thread.currentThread().setContextClassLoader(contextClassLoader);
	}
	
	/**
	 * Loads the information from a file
	 */
	public <T> T load(File file, Class<T> type) {
		T object = null;
		try {
			XMLDecoder xmlDecoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)), null, new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					throw new RuntimeException(e);
				}
			});
			object = type.cast(xmlDecoder.readObject());
			xmlDecoder.close();
		} catch (Throwable t) {
			Logger.global.info("Could not load file " + file);
		}
		return object;
	}
	
	/**
	 * Store an object in a specific xml file
	 */
	public void store(Object object, File file) {
		try {
		    file.getParentFile().mkdirs();
		    file.createNewFile();
			XMLEncoder xmlEncoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
			xmlEncoder.setExceptionListener(new ExceptionListener() {
				public void exceptionThrown(Exception e) {
					throw new RuntimeException(e);
				}
			});
			xmlEncoder.setPersistenceDelegate(RootElementCollection.class, new RootElementCollectionPersistenceDelegate());
			xmlEncoder.setPersistenceDelegate(Policies.class, new PoliciesPersistenceDelegate());
			xmlEncoder.setPersistenceDelegate(Repository.class, new DefaultPersistenceDelegate(new String[] { "name", "url", "lastProcessedConfigurationNumber", "connector" }));
			xmlEncoder.setPersistenceDelegate(Architecture.class, new DefaultPersistenceDelegate(new String[] { "name", "url", "connector" }));
			xmlEncoder.setPersistenceDelegate(WorkspaceFile.class, new DefaultPersistenceDelegate(new String[] { "path", "timestamp" }));
			xmlEncoder.setPersistenceDelegate(WorkspaceMetadata.class, new WorkspaceMetadataPersistenceDelegate());
			xmlEncoder.writeObject(object);
			xmlEncoder.close();
		} catch (Exception e) {
		    e.printStackTrace();
			Logger.global.info("Could not store file " + file);
		}
	}
}

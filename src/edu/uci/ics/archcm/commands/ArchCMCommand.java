package edu.uci.ics.archcm.commands;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.archcm.model.WorkspaceFile;
import edu.uci.ics.archcm.model.WorkspaceMetadata;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.persistence.PersistenceManager;
import edu.uci.ics.archtrace.trace.Trace;
import edu.uci.ics.archtrace.trace.TraceManager;

/**
 * Generic ArchCM command
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 26, 2004
 */
public abstract class ArchCMCommand implements Runnable {

    /**
     * Architectural element to be processed by the command
     */
    private ArchitecturalElement ae;
    
    /**
     * Internal workspace
     */
    private File internalWorkspace;
    
    /**
     * User's workspace
     */
    private File userWorkspace;
    
    /**
     * ArchCM control directory name
     */
    private static final String ARCHCM_DIRECTORY_NAME = ".archcm";
    
    /**
     * Metadata file name
     */
    private static final String METADATA_FILE_NAME = "metadata.xml";
    
    /**
     * Timestamp file name
     */
    private static final String TIMESTAMP_FILE_NAME = "timestamp.xml";
    
    /**
     * Constructs the ArchCM command
     */
    public ArchCMCommand(ArchitecturalElement ae) {
        this.ae = ae;
        internalWorkspace = new File(PersistenceManager.PERSISTENCE_DIR, ae.toString());
        userWorkspace = new File(PersistenceManager.getInstance().getPreferences().getUserWorkspace(), ae.toString());
    }
    
    /**
     * Provides the architectural element that should be processed by the command
     */
    protected ArchitecturalElement getArchitecturalElement() {
        return ae;
    }
    
    /**
     * Provides the internal workspace
     */
    protected File getInternalWorkspace() {
        return internalWorkspace;
    }

    /**
     * Provides the user workspace
     */
    protected File getUserWorkspace() {
        return userWorkspace;
    }
    
    /**
     * Provides the metadata file
     */
    protected File getMetadataFile() {
        return new File(new File(userWorkspace, ARCHCM_DIRECTORY_NAME), METADATA_FILE_NAME);
    }

    /**
     * Provides the timestamp file
     */
    protected File getTimestampFile() {
        return new File(new File(userWorkspace, ARCHCM_DIRECTORY_NAME), TIMESTAMP_FILE_NAME);
    }
    
    /**
     * Provide the sets of configuration items organized by repositories that are
     * mapped to the selected architectural element.
     */
    protected Map<Repository, Set<ConfigurationItem>> getConfigurationItemsByRepository() {
        Map<Repository, Set<ConfigurationItem>> configurationItemsByRepository = new HashMap<Repository, Set<ConfigurationItem>>();
        for (Trace trace : TraceManager.getInstance().getTraces(ae)) {
            ConfigurationItem ci = trace.getConfigurationItem();
            
            Repository repository = ci.getRepository();
            Set<ConfigurationItem> configurationItems = configurationItemsByRepository.get(repository);
            if (configurationItems == null) {
                configurationItems = new HashSet<ConfigurationItem>();
                configurationItemsByRepository.put(repository, configurationItems);
            } 
            
            configurationItems.add(ci);
        }
        return configurationItemsByRepository;
    }
    
    /**
     * Provide the user workspace metadata
     */
    protected WorkspaceMetadata getMetadata(Set<Repository> repositories) {
        WorkspaceMetadata metadata = new WorkspaceMetadata();
        
        for (Repository repository : repositories) {
            File repWS = new File(getUserWorkspace(), repository.getName());
            for (File subFile : repWS.listFiles()) {
                addMetadata(repository, repWS, "/" + subFile.getName(), metadata);
            }
        }
        
        return metadata;
    }
    
    /**
     * Add a specific file to the metadata, recursively.
     */
    private void addMetadata(Repository repository, File workspace, String path, WorkspaceMetadata metadata) {
        File file = new File(workspace, path);
        metadata.add(repository, new WorkspaceFile(path, file.lastModified()));
        
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                addMetadata(repository, workspace, path + "/" + subFile.getName(), metadata);
            }
        }
    }
    
    /**
     * Delete a file or a directory, even if it is not empty.
     */
    protected void delete(File element) {
        if (element.isDirectory()) {
            for (File subElement : element.listFiles()) {
                delete(subElement);
            }
        }

        element.delete();
    }
    
    /**
     * Copy a specific file to another file.
     */
    protected void copy(File fromFile, File toFile) throws FileNotFoundException, IOException {
        if (fromFile.isDirectory()) {
            toFile.mkdirs();
        } else {
	        InputStream in = new BufferedInputStream(new FileInputStream(fromFile));
	        toFile.getParentFile().mkdirs();
	        OutputStream out = new FileOutputStream(toFile);
	        
	        byte[] buffer = new byte[8 * 1024];
	        int length = in.read(buffer);
	        while (length != -1) {
	            out.write(buffer, 0, length);
	            length = in.read(buffer);
	        }
	        out.close();
	        in.close();
        }
    }
    
    /**
     * Deep copy a specific path in a directory to another diretory.
     * @return the set of copied paths
     */
    protected void deepCopy(File fromDirectory, String path, File toDirectory) throws FileNotFoundException, IOException {
        File fromFile = new File(fromDirectory, path);
        // TODO: This hidden test is not 100% ok. It is necessary to prevent the copy of .svn to user WS
        // This code should be located in the connector itself
        if (!fromFile.isHidden()) {
	        File toFile = new File(toDirectory, path);
	
	        if (fromFile.isDirectory()) {
	            toFile.mkdirs();
	            for (File subFromFile : fromFile.listFiles()) {
	                deepCopy(fromDirectory, path + "/" + subFromFile.getName(), toDirectory);
	            }
	        } else {
	            copy(fromFile, toFile);
	        }
        }
    }
}

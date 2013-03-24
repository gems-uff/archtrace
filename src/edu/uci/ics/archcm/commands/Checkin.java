package edu.uci.ics.archcm.commands;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.archcm.ArchCMException;
import edu.uci.ics.archcm.model.WorkspaceFile;
import edu.uci.ics.archcm.model.WorkspaceMetadata;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.persistence.PersistenceManager;

/**
 * The check-in command
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 19, 2004
 */
public class Checkin extends ArchCMCommand {

    /**
     * Check-in message
     */
    private String message;
    
    /**
     * Number of the configuration that has been created by this check-in.
     * -1 means no configuration created.
     */
    private long configurationNumber;

    /**
     * Creates the check-in command over a specific architectural element
     */
    public Checkin(ArchitecturalElement ae, String message) {
        super(ae);
        this.message = message;
        this.configurationNumber = -1;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        GUIManager guiManager = GUIManager.getInstance();
        
        try {
            guiManager.setSplashScreenStatus("Verifying existing check-out...");
            verifyWorkspaces(getInternalWorkspace(), getUserWorkspace());
            
            guiManager.setSplashScreenStatus("Reading previous user's workspace metadata...");
            WorkspaceMetadata checkoutMetadata = PersistenceManager.getInstance().load(getMetadataFile(), WorkspaceMetadata.class);
            
            guiManager.setSplashScreenStatus("Collecting user's workspace metadata...");
            WorkspaceMetadata checkinMetadata = getMetadata(checkoutMetadata.getRepositories());

            guiManager.setSplashScreenStatus("Synchronizing the user's workspace...");
            synchronizeWorkspaces(checkoutMetadata, checkinMetadata, getInternalWorkspace(), getUserWorkspace());
            
            guiManager.setSplashScreenStatus("Checking-in...");
            checkin(getInternalWorkspace());

            guiManager.setSplashScreenStatus("Deleting the user's workspace...");
            delete(getInternalWorkspace());
            delete(getUserWorkspace());
        } catch (Exception e) {
            guiManager.showErrorDialog(e);
        }
    }

    /**
     * Verify if the internal and user workspaces are in a stable state to perform the check-in
     */
    private void verifyWorkspaces(File internalWorkspace, File userWorkspace) throws ArchCMException {
        if ((!internalWorkspace.exists()) || (!userWorkspace.exists())) {
            // Possible inconsistent state -> reset
            delete(internalWorkspace);
            delete(userWorkspace);
            
            throw new ArchCMException(getArchitecturalElement() + " has not been checked-out.");
        } 
    }
    
    /**
     * Synchronize the user workspace with the internal workspace
     * @throws 
     */
    private void synchronizeWorkspaces(WorkspaceMetadata checkoutMetadata, WorkspaceMetadata checkinMetadata, File internalWorkspace, File userWorkspace) throws ArchCMException {
        // TODO: This code should be made generic (calling the connector) to avoid direct dependences to file system representation
        // This change is necessary, for example, in the case of Odyssey-VCS

        // TODO: Think about what to do when checkoutMetadata has e different set of repositories
        // of checkinMetadata
        for (Repository repository : checkinMetadata.getRepositories()) {
            File userWorkspaceRep = new File(userWorkspace, repository.getName());
            File internalWorkspaceRep = new File(internalWorkspace, repository.getName());

            Set<WorkspaceFile> checkoutWorkspaceFiles = checkoutMetadata.getWorkspaceFiles(repository);
            Set<WorkspaceFile> checkinWorkspaceFiles = checkinMetadata.getWorkspaceFiles(repository);
            
            // ADDED = checkinWorkspaceFiles - checkoutWorkspaceFiles
            Set<WorkspaceFile> addedWorkspaceFiles = new HashSet<WorkspaceFile>(checkinWorkspaceFiles);
            addedWorkspaceFiles.removeAll(checkoutWorkspaceFiles);
            
            for (WorkspaceFile wsFile : addedWorkspaceFiles) {
                File fromFile = new File(userWorkspaceRep, wsFile.getPath());
                File toFile = new File(internalWorkspaceRep, wsFile.getPath());
System.out.println("Added: " + fromFile + " -> " + toFile); // TODO: Remove!
                try {
                    copy(fromFile, toFile);
                    repository.add(toFile.getPath());
                } catch (Exception e) {
                    throw new ArchCMException("Could not add " + fromFile + " in repository " + repository, e);
                } 
            }

            // REMOVED = checkoutWorkspaceFiles - checkinWorkspaceFiles
            Set<WorkspaceFile> removedWorkspaceFiles = new HashSet<WorkspaceFile>(checkoutWorkspaceFiles);
            removedWorkspaceFiles.removeAll(checkinWorkspaceFiles);
            
            for (WorkspaceFile wsFile : removedWorkspaceFiles) {
                File file = new File(internalWorkspaceRep, wsFile.getPath());
System.out.println("Removed: " + file); // TODO: Remove!
                try {           
                    repository.remove(file.getPath());
                } catch (Exception e) {
                    throw new ArchCMException("Could not remove " + file + " from repository " + repository, e);
                }   
            }            

            // MAINTAINED = checkoutWorkspaceFiles ? checkinWorkspaceFiles
            Set<WorkspaceFile> maintainedWorkspaceFiles = new HashSet<WorkspaceFile>(checkoutWorkspaceFiles);
            maintainedWorkspaceFiles.retainAll(checkinWorkspaceFiles);

            for (WorkspaceFile wsFile : maintainedWorkspaceFiles) {
                File fromFile = new File(userWorkspaceRep, wsFile.getPath());
                
                // Copy only the modified files
                if (wsFile.getTimestamp() != fromFile.lastModified()) {
                    File toFile = new File(internalWorkspaceRep, wsFile.getPath());
System.out.println("Changed: " + fromFile + " -> " + toFile); // TODO: Remove!
	                try {
	                    copy(fromFile, toFile);
	                } catch (Exception e) {
                        throw new ArchCMException("Could not update " + fromFile + " on repository " + repository, e);
	                }
                }
            }
        }            
    }

    /**
     * Check-in the workspace to the repository
     */
    private void checkin(File workspace) throws ArchCMException {
        PersistenceManager persistenceManager = PersistenceManager.getInstance();
        for (File workspaceRep : workspace.listFiles()) {
            Repository repository = persistenceManager.getRepositories().get(workspaceRep.getName());
            
            try {                
                configurationNumber = repository.checkin(workspaceRep.getPath(), message);
            } catch (ConnectionException e) {
                throw new ArchCMException("Could not check-in " + getArchitecturalElement() + " to repository " + repository, e);
            }
        }
    }
    
    /**
     * Provides the number of the configuration that has been created by this check-in.
     * -1 means no configuration created
     */
    public long getConfigurationNumber() {
        return configurationNumber;
    }
}
package edu.uci.ics.archcm.commands;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.archcm.ArchCMException;
import edu.uci.ics.archcm.model.WorkspaceMetadata;
import edu.uci.ics.archtrace.connectors.ConnectionException;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.model.Configuration;
import edu.uci.ics.archtrace.model.ConfigurationItem;
import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.persistence.PersistenceManager;

/**
 * The check-out command
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 19, 2004
 */
public class Checkout extends ArchCMCommand {

    /**
     * Creates the check-out command over a specific architectural element
     */
    public Checkout(ArchitecturalElement ae) {
        super(ae);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {
        GUIManager guiManager = GUIManager.getInstance();
        
        try {
            // TODO: Should use the timestamp to allow multiple check-outs of the same element

            guiManager.setSplashScreenStatus("Verifying existing check-outs...");
            verifyWorkspaces(getInternalWorkspace(), getUserWorkspace());
            
            guiManager.setSplashScreenStatus("Finding configuration items to be checked-out...");
            Map<Repository, Set<ConfigurationItem>> configurationItemsByRepository = getConfigurationItemsByRepository();
            
            guiManager.setSplashScreenStatus("Finding compatible configurations...");
            Map<Repository, Configuration> compatibleConfigurationByRepository = getCompatibleConfigurationByRepository(configurationItemsByRepository);
            
            // TODO: It may be necessary to inform the exact configuration items that should be checked-out in the case of Odyssey-VCS
            guiManager.setSplashScreenStatus("Checking-out the selected configurations...");
            checkout(compatibleConfigurationByRepository, getInternalWorkspace());

            guiManager.setSplashScreenStatus("Creating the user's workspace...");
            createUserWorkspace(configurationItemsByRepository, getInternalWorkspace(), getUserWorkspace());
            
            guiManager.setSplashScreenStatus("Collecting user's workspace metadata...");
            WorkspaceMetadata metadata = getMetadata(configurationItemsByRepository.keySet());
            
            guiManager.setSplashScreenStatus("Writing user's workspace metadata...");            
            PersistenceManager.getInstance().store(metadata, getMetadataFile());
        } catch (Exception e) {
            guiManager.showErrorDialog(e);
        }
    }

    /**
     * Verify if the internal and user workspaces are in a stable state to perform the check-out
     */
    private void verifyWorkspaces(File internalWorkspace, File userWorkspace) throws ArchCMException {
        if ((internalWorkspace.exists()) && (userWorkspace.exists())) {
            throw new ArchCMException(getArchitecturalElement() + " has already been checked-out to " + userWorkspace + ".");
        } else if ((internalWorkspace.exists()) ^ (userWorkspace.exists())) {
            // Inconsistent state -> reset
            delete(internalWorkspace);
            delete(userWorkspace);
        } 

        if (!internalWorkspace.mkdirs()) {
            throw new ArchCMException("Could not create internal workspace at " + internalWorkspace);
        }
        
        if (!userWorkspace.mkdirs()) {
            delete(internalWorkspace);
            throw new ArchCMException("Could not create user workspace at " + userWorkspace);
        }
    }

    /**
     * Provide the compatible configuration organized by repositories for a given architectural element.
     * A compatible configuration means a configuration that holds all configuration items which trace from
     * the architectural element to a specific repository.
     */
    private Map<Repository, Configuration> getCompatibleConfigurationByRepository(Map<Repository, Set<ConfigurationItem>> configurationItemsByRepository) throws ArchCMException {
        Map<Repository, Configuration> configurationByRepository = new HashMap<Repository, Configuration>();
        
        for (Repository repository : configurationItemsByRepository.keySet()) {
            Set<Configuration> configurations = null;
            
            for (ConfigurationItem ci : configurationItemsByRepository.get(repository)) {
                if (configurations == null) {
                    configurations = new HashSet<Configuration>();
                    configurations.addAll(ci.getRootConfigurations());
                } else {
                    configurations.retainAll(ci.getRootConfigurations());
                    
                    if (configurations.isEmpty()) {
                        delete(getInternalWorkspace());
                        delete(getUserWorkspace());
                        throw new ArchCMException("Could not find a compatible configuration in repository " + repository + " for " + getArchitecturalElement());
                    }
                }
            }

            Configuration newerConfiguration = null;
            for (Configuration configuration : configurations) {
                if ((newerConfiguration == null) || (configuration.getNumber() > newerConfiguration.getNumber()))
                    newerConfiguration = configuration;                           
            }
            configurationByRepository.put(repository, newerConfiguration);
        }
        
        if (configurationByRepository.isEmpty()) {
            delete(getInternalWorkspace());
            delete(getUserWorkspace());
            throw new ArchCMException("Could not find traces in " + getArchitecturalElement());
        } 
        
        return configurationByRepository;
    }

    /**
     * Check-out all selected configurations to a specific workspace
     */
    private void checkout(Map<Repository, Configuration> configurationByRepository, File workspace) throws ArchCMException {
        for (Repository repository : configurationByRepository.keySet()) {
            File repositoryWorkspace = new File(workspace, repository.getName());
            repositoryWorkspace.mkdir();
            
            Configuration configuration = configurationByRepository.get(repository);
            try {                
                configuration.checkout(repositoryWorkspace.getPath());
            } catch (ConnectionException e) {
                delete(getInternalWorkspace());
                delete(getUserWorkspace());
                throw new ArchCMException("Could not check-out " + configuration + " of repository " + repository, e);
            }
        }
    }
    
    /**
     * Create the user workspace based on files checked-out in the internal workspace
     */
    private void createUserWorkspace(Map<Repository, Set<ConfigurationItem>> configurationItemsByRepository, File internalWorkspace, File userWorkspace) throws ArchCMException {
        // TODO: This code should be made generic (calling the connector) to avoid direct dependences to file system representation
        // This change is necessary, for example, in the case of Odyssey-VCS

        for (Repository repository : configurationItemsByRepository.keySet()) {
            File internalWorkspaceRep = new File(internalWorkspace, repository.getName());
            File userWorkspaceRep = new File(userWorkspace, repository.getName());

            for (ConfigurationItem ci : configurationItemsByRepository.get(repository)) {
                try {
                    // Add the path itself and its subpaths, recursively.
                    deepCopy(internalWorkspaceRep, ci.getPath(), userWorkspaceRep);
                } catch (Exception e) {
                    delete(getInternalWorkspace());
                    delete(getUserWorkspace());
                    throw new ArchCMException("Could not create file " + ci.getPath() + " in the user workspace.", e);
                }
            }
        }
    }
}
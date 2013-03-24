package edu.uci.ics.archcm.model;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.uci.ics.archtrace.model.Repository;
import edu.uci.ics.archtrace.utils.ArchTraceComparator;


/**
 * Represents all the workspace metadata
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 28, 2004
 */
public class WorkspaceMetadata {

    /**
     * Set of workspace files indexed by their repositories
     */
    Map<Repository, Set<WorkspaceFile>> workspaceFilesByRepositories;
    
    /**
     * Constructs the workspace metadata object
     */
    public WorkspaceMetadata() {
        workspaceFilesByRepositories = new TreeMap<Repository, Set<WorkspaceFile>>(ArchTraceComparator.getInstance());
    }
    
    /**
     * Add a set of workspace files that ware obteined from a giver repository
     */
    public void add(Repository repository, Set<WorkspaceFile> wsFiles) {
        for (WorkspaceFile wsFile : wsFiles) {
            add(repository, wsFile);
        }
    }
    
    /**
     * Add a new workspace file that was obteined from a giver repository
     */
    public void add(Repository repository, WorkspaceFile wsFile) {
        Set<WorkspaceFile> workspaceFiles = workspaceFilesByRepositories.get(repository);
        if (workspaceFiles == null) {
            workspaceFiles = new TreeSet<WorkspaceFile>();
            workspaceFilesByRepositories.put(repository, workspaceFiles);
        }
        workspaceFiles.add(wsFile);
    }
    
    /**
     * Provide all existing repositories
     */
    public Set<Repository> getRepositories() {
        return workspaceFilesByRepositories.keySet();
    }
    
    /**
     * Provide all workspace files for a given repository
     */
    public Set<WorkspaceFile> getWorkspaceFiles(Repository repository) {
        return workspaceFilesByRepositories.get(repository);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return workspaceFilesByRepositories.toString();
    }
}
package edu.uci.ics.archcm.model;



/**
 * Represents a file in the user workspace
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 28, 2004
 */
public class WorkspaceFile implements Comparable {

    /**
     * Path to the file
     */
    private String path;
    
    /**
     * Timestamp of the file
     */
    private long timestamp;
    
    /**
     * Constructs the workspace file
     */
    public WorkspaceFile(String path, long timestamp) {
        this.path = path;
        this.timestamp = timestamp;
    }
    
    /**
     * @return The path of the workspace file
     */
    public String getPath() {
        return path;
    }
    
    /**
     * @return The timesttamp of the workspace file
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        try {
            WorkspaceFile otherWSFile = (WorkspaceFile)obj;
            return this.getPath().equals(otherWSFile.getPath());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return path.hashCode();
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return path;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj) {
        try {
            WorkspaceFile otherWSFile = (WorkspaceFile)obj;
            return this.getPath().compareTo(otherWSFile.getPath());
        } catch (Exception e) {
            return this.toString().compareTo(obj.toString());
        }
    }
}
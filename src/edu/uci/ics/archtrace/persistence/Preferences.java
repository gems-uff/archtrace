package edu.uci.ics.archtrace.persistence;

/**
 * Represents the user preferences
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 30, 2004
 */
public class Preferences {

	/**
	 * Stores if the configuration window should be shown autimatically when ArchTrace starts
	 */
	private boolean shownAutomatically;
	
	/**
	 * Stores if the traces and user configuration should be saved automaticallt when ArchTrace exits
	 */
	private boolean saveAutomatically;
	
	/**
	 * Stores the user workspace
	 */
	private String userWorkspace;
	
	/**
	 * Construct this class with default values
	 */
	public Preferences() {
		shownAutomatically = true;
		saveAutomatically = true;
		userWorkspace = System.getProperty("user.home") + System.getProperty("file.separator") + "ArchCM Workspace";
	}

	/**
	 * @return Returns the shownAutomatically.
	 */
	public boolean isShownAutomatically() {
		return shownAutomatically;
	}
	
	/**
	 * @param shownAutomatically The shownAutomatically to set.
	 */
	public void setShownAutomatically(boolean shownAutomatically) {
		this.shownAutomatically = shownAutomatically;
	}

	/**
	 * @return Returns the saveAutomatically.
	 */
	public boolean isSaveAutomatically() {
		return saveAutomatically;
	}
	
	/**
	 * @param saveAutomatically The saveAutomatically to set.
	 */
	public void setSaveAutomatically(boolean saveAutomatically) {
		this.saveAutomatically = saveAutomatically;
	}

    /**
     * @return the userWorkspace
     */
    public String getUserWorkspace() {
        return userWorkspace;
    }

	/**
	 * @param userWorkspace The userWorkspace to set.
	 */
    public void setUserWorkspace(String userWorkspace) {
        this.userWorkspace = userWorkspace;
    }
}
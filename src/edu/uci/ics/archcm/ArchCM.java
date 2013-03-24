package edu.uci.ics.archcm;

import edu.uci.ics.archcm.commands.Checkin;
import edu.uci.ics.archcm.commands.Checkout;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;

/**
 * The ArchCM facade
 * Provide CM services using architectural metaphor
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Oct 19, 2004
 */
public class ArchCM {

    /**
     * The singleton instance
     */
    private static ArchCM instance;
    
    /**
     * Constructs the singleton instance
     */
    private ArchCM() {}
    
    /**
     * Provides the singleton instance
     */
    public static synchronized ArchCM getInstance() {
        if (instance == null)
            instance = new ArchCM();
        return instance;
    }
    
    public void checkout(ArchitecturalElement ae) {
        GUIManager.getInstance().run(new Checkout(ae));
    }
    
    public long checkin(ArchitecturalElement ae, String message) {
        Checkin checkin = new Checkin(ae, message);
        GUIManager.getInstance().run(checkin);
        return checkin.getConfigurationNumber();
    }
}

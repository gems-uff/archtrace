package edu.uci.ics.archtrace.gui.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import edu.uci.ics.archtrace.model.Types;

/**
 * This singleton class loads and provide all existing ArchTrace icons.
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 22, 2004
 */
public class IconManager {
	
	//Icons not related to specific objects
	public static final String CHECKBOX_SELECTED = "/icons/checkbox-selected.gif";
	public static final String CHECKBOX_SEMISELECTED = "/icons/checkbox-semiselected.gif";
	public static final String CHECKBOX_UNSELECTED = "/icons/checkbox-unselected.gif";

	/**
	 * Singleton instance
	 */
	private static IconManager instance;
	
	/**
	 * Map between icon names and the icons themselves
	 */
	private Map<Object, ImageIcon> icons;
	
	/**
	 * Constructs the icon manager registering all available icons
	 */
	private IconManager() {
		icons  = new HashMap<Object, ImageIcon>();
		
		// Register types with specific icons
		register(Types.COLLECTION, "/icons/generic.gif");
		
		register(Types.ARCHITECTURE, "/icons/architecture.gif");
		register(Types.COMPONENTS_VIEW, "/icons/components.gif");
		register(Types.COMPONENT, "/icons/component.gif");
		register(Types.INTERFACES_VIEW, "/icons/interfaces.gif");
		register(Types.INTERFACE, "/icons/interface.gif");
		register(Types.CONNECTORS_VIEW, "/icons/connectors.gif");
		register(Types.CONNECTOR, "/icons/connector.gif");
		
		register(Types.REPOSITORY, "/icons/repository.gif");
		register(Types.CONFIGURATION, "/icons/configuration.gif");
		register(Types.DIRECTORY, "/icons/directory.gif");
		register(Types.FILE, "/icons/file.gif");
		
		// Register strings with specific icons
		register(CHECKBOX_SELECTED);
		register(CHECKBOX_SEMISELECTED);
		register(CHECKBOX_UNSELECTED);
	}
	
	/**
	 * @return Singleton instance of IconManager
	 */
	public static synchronized IconManager getInstance() {
		if (instance == null)
			instance = new IconManager();
		return instance;
	}
	
	/**
	 * Register an icon for a given type
	 */
	private void register(int type, String iconName) {
		register(new Integer(type), iconName);
	}

	/**
	 * Register an icon
	 */
	private void register(String iconName) {
		register(iconName, iconName);
	}
	
	/**
	 * Register an icon 
	 */
	private void register(Object id, String iconName) {
		URL iconURL = IconManager.class.getResource(iconName);
		if (iconURL != null)
			icons.put(id, new ImageIcon(iconURL));
		else
		   	Logger.global.warning("Could not find file: " + iconName);
	}

	/**
	 * Provides the icon of a given text
	 */
	public Icon getIcon(String id) {
		return icons.get(id);
	}
	
	/**
	 * Provides the icon of a given type
	 */
	public Icon getIcon(int type) {
		return icons.get(new Integer(type));
	}
}

package edu.uci.ics.archtrace.persistence;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;

import edu.uci.ics.archtrace.model.RootElementCollection;

/**
 * This class provides support for storing RootElementCollection objects using XMLEncoder
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Aug 27, 2004
 */
public class RootElementCollectionPersistenceDelegate extends DefaultPersistenceDelegate {
	 
	public RootElementCollectionPersistenceDelegate() {
		super(new String[] { "name" });
	}
	
	/**
	 * @see java.beans.PersistenceDelegate#initialize(java.lang.Class, java.lang.Object, java.lang.Object, java.beans.Encoder)
	 */
	protected void initialize(Class type, Object oldInstance, Object newInstance, Encoder out) {
		// Needed for properties
		super.initialize(type, oldInstance, newInstance, out);
		
		RootElementCollection collection = (RootElementCollection)oldInstance;
		
		for (int i = 0; i < collection.getChildCount(); i++)
			out.writeStatement(new Statement(collection, "add", new Object[] { collection.getChild(i) }));
	}
}

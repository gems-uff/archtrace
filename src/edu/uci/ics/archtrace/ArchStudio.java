package edu.uci.ics.archtrace;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;

import archstudio.Bootstrap;
import edu.uci.ics.archtrace.persistence.PersistenceManager;

/**
 * This class is responsible for starting archstudio using a architecture descriptor that
 * has ArchTrace as a component
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 27, 2004
 */
public class ArchStudio {
	
	private static final String ARCHITECTURE = "archstudio-ArchTrace.xml"; 

	/**
	 * Start the ArchStudio using an architecture that has ArchTrace as a component
	 */
	public ArchStudio() {
		File file = new File(PersistenceManager.PERSISTENCE_DIR, ARCHITECTURE);
		if (!file.exists()) {
			try {
				URL url = Thread.currentThread().getContextClassLoader().getResource(ARCHITECTURE);
				InputStream in = url.openStream();
				
				FileOutputStream out = new FileOutputStream(file);
				byte[] buffer = new byte[8 * 1024];
				int length = in.read(buffer);
				while (length != -1) {
				    out.write(buffer, 0, length);
				    length = in.read(buffer);
				}
				out.close();
			} catch (Exception e) {
				Logger.global.warning("Could not create architecture file (" + e.getMessage() + ")");
			}
	    }
		
		Bootstrap.main(new String[] { file.getPath() });
	}
	
	/**
	 * Start ArchStudio
	 */
	public static void main(String[] args) throws Exception {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		new ArchStudio();
	}
}

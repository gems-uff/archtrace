package edu.uci.ics.archtrace.gui;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import edu.uci.ics.archcm.ArchCM;
import edu.uci.ics.archtrace.ArchTrace;
import edu.uci.ics.archtrace.gui.utils.GUIManager;
import edu.uci.ics.archtrace.model.ArchitecturalElement;
import edu.uci.ics.archtrace.persistence.PersistenceManager;

/**
 * This is the main window of ArchTrace
 * 
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Jul 21, 2004
 */
public class ArchTraceWindow extends JFrame {	
	
	/**
	 * Vertical split panel (main panel)
	 */
	private JSplitPane verticalSplitPane;
	
	/**
	 * Horizontal split pane
	 */
	private JSplitPane horizontalSplitPane;	

	/**
	 * Tree of architectural elements
	 */
	private JTree archTree;
	
	/**
	 * Tree of configuration items
	 */
	private JTree cmTree;
	
	/**
	 * Model used by policies to post messages to the user.
	 */
	private DefaultListModel messages;
	
	/**
	 * Creates the ArchTrace Mapper window and show to the user 
	 */
	public ArchTraceWindow(ArchTraceTreeModel archTreeModel, ArchTraceTreeModel cmTreeModel) {
		super("ArchTrace");
		
		// Creates the trees
		archTree = new JTree(archTreeModel);
		cmTree = new JTree(cmTreeModel);
		
		// Configures the arch tree
		archTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		archTree.addTreeSelectionListener(new ArchTraceTreeSelectionListener(cmTree));
		archTree.addTreeWillExpandListener(ArchTraceTreeExpansionListener.getInstance());
		archTree.addTreeExpansionListener(ArchTraceTreeExpansionListener.getInstance());
		archTree.expandRow(0);
		archTree.setRootVisible(false);
		archTree.setShowsRootHandles(true);
		archTree.setEditable(true);
		
		// Add popup menu to the arch tree
		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(new AbstractAction("Check-out") {
	        public void actionPerformed(ActionEvent event) {
	            ArchitecturalElement ae = (ArchitecturalElement)archTree.getSelectionPath().getLastPathComponent();
	            ArchCM.getInstance().checkout(ae);
	        } 
		});		
		popupMenu.add(new AbstractAction("Check-in") {
	        public void actionPerformed(ActionEvent event) {
	            ArchitecturalElement ae = (ArchitecturalElement)archTree.getSelectionPath().getLastPathComponent();
	            String message = JOptionPane.showInputDialog("What did you do in this architectural element?");
	            long configurationNumber = ArchCM.getInstance().checkin(ae, message);
	            if (configurationNumber == -1)
	                JOptionPane.showMessageDialog(null, "No new configuration has been created!");
	            else
	                JOptionPane.showMessageDialog(null, "Configuration number " + configurationNumber + " has been created!");
	        } 
		});
		archTree.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }
		    public void mouseReleased(MouseEvent e) {
		        maybeShowPopup(e);
		    }
		    private void maybeShowPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            TreePath treePath = archTree.getSelectionPath();
		            if (treePath != null) {
		                if (ArchitecturalElement.class.isInstance(archTree.getSelectionPath().getLastPathComponent())) {
		                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
		                }
		            }
		        }
		    }
		});

		// Configures the cm tree
		cmTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		cmTree.addTreeSelectionListener(new ArchTraceTreeSelectionListener(archTree));
		cmTree.addTreeWillExpandListener(ArchTraceTreeExpansionListener.getInstance());
		cmTree.addTreeExpansionListener(ArchTraceTreeExpansionListener.getInstance());
		cmTree.expandRow(0);
		cmTree.setRootVisible(false);
		cmTree.setShowsRootHandles(true);
		cmTree.setEditable(true);

		// Set the arch tree controller
		ArchTraceTreeController archController = new ArchTraceTreeController(cmTree);
		archTree.setCellRenderer(archController);
		archTree.setCellEditor(archController);
		
		// Set the cm tree controller
		ArchTraceTreeController cmController = new ArchTraceTreeController(archTree);
		cmTree.setCellRenderer(cmController);
		cmTree.setCellEditor(cmController);

		// Creates the panels
		JScrollPane archPanel = new JScrollPane(archTree);
		archPanel.setBorder(BorderFactory.createTitledBorder("Architectures"));
		JScrollPane cmPanel = new JScrollPane(cmTree);
		cmPanel.setBorder(BorderFactory.createTitledBorder("Repositories"));
		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, archPanel, cmPanel);
		
		// Creates the message area
		messages = new DefaultListModel();
		JList messagesList = new JList(messages);
		JScrollPane messagePanel = new JScrollPane(messagesList);
		messagePanel.setBorder(BorderFactory.createTitledBorder("Messages posted by policies"));
		verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, horizontalSplitPane, messagePanel);
		
		// Configure the main window		
		this.setContentPane(verticalSplitPane);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ArchTrace.getInstance().stop();
			}
		});
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		
		// Menubar
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		
		// File menu
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		fileMenu.add(new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e) {
				GUIManager.getInstance().run(new Runnable() {
					public void run() {
						PersistenceManager.getInstance().store();
					}
				});
			}
		});
		
		// Config menu
		JMenu configMenu = new JMenu("Config");
		menuBar.add(configMenu);
		configMenu.add(new AbstractAction("Preferences") {
			public void actionPerformed(ActionEvent e) {
				GUIManager.getInstance().showConfigDialog();
			}
		});
		
		// Help menu
		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		helpMenu.add(new AbstractAction("About") {
			public void actionPerformed(ActionEvent e) {
				GUIManager.getInstance().showAboutDialog();
			}
		});
	}
	
	/**
	 * @param string
	 */
	public void addPolicyMessage(String message) {
		messages.add(0, new Date() + " - " + message);
	}

	/**
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		verticalSplitPane.setDividerLocation(0.8);
		verticalSplitPane.setResizeWeight(0.8);
		horizontalSplitPane.setDividerLocation(0.5);
		horizontalSplitPane.setResizeWeight(0.5);
	}
}
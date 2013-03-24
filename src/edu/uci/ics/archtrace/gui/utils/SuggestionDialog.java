package edu.uci.ics.archtrace.gui.utils;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Display suggestions to the user
 *
 * @author Leo Murta (murta@ics.uci.edu, murta@cos.ufrj.br) - Sep 17, 2004
 */
public class SuggestionDialog extends JDialog {

	/**
	 * Message to be shown to the user
	 */
	private JLabel messageLabel;
	
	/**
	 * List of suggestions
	 */
	private JList suggestionsList;
	
	/**
	 * Constructs the dialog
	 */
	public SuggestionDialog(Frame owner) {
		super(owner, "ArchTrace Suggestion", true);
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Message
		messageLabel = new JLabel();
		messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(messageLabel);
		contentPanel.add(Box.createVerticalStrut(5));
		
		// Suggestions list
		suggestionsList = new JList();
		contentPanel.add(new JScrollPane(suggestionsList));
		contentPanel.add(Box.createVerticalStrut(5));
		
		// Instructions
		JLabel instructionsLabel = new JLabel("<html>If you want to accept some suggestions, select the desired suggestions (use Ctrl or Shift to select more than one) and then click the \"Accept the selected suggestions\" button. Otherwise, just click the \"Ignore suggestions and just establish the link\" button.</html>");
		instructionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		contentPanel.add(instructionsLabel);
		contentPanel.add(Box.createVerticalStrut(5));
		
		// Buttons
		JPanel buttonsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		JButton acceptButton = new JButton("Accept the selected suggestions");
		acceptButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		acceptButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		buttonsPanel.add(acceptButton);
		JButton ignoreButton = new JButton("Ignore suggestions and just establish the link");
		ignoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		ignoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				suggestionsList.getSelectionModel().clearSelection();
				setVisible(false);
			}
		});
		buttonsPanel.add(ignoreButton);
		contentPanel.add(buttonsPanel);
		
		this.setContentPane(contentPanel);
		this.setSize(500, 300);
		this.setResizable(false);
	}
	
	/**
	 * Show a dialog with a set of elements and a message
	 * @param message Message to be shown in the dialog
	 * @param sugestions Elements that may be selected by the user
	 * @return Collection of selected elements
	 */
	public <E> Collection<E> show(String message, final List<E> suggestions) {
		Collection<E> result = new ArrayList<E>();
		
		messageLabel.setText("<html>" + message + "</html>");
		SuggestionListModel<E> suggestionListModel = new SuggestionListModel<E>(suggestions); 
		suggestionsList.setModel(suggestionListModel);
		
		this.setLocationRelativeTo(this.getOwner());
		this.setVisible(true);
		
		// This code avoid cast to E[] (suggestionsList.getSelectedValues())
		for (int index : suggestionsList.getSelectedIndices()) {
			result.add(suggestionListModel.getElementAt(index));
		}

		return result;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

		String message = "";
		List<String> suggestions = new ArrayList<String>();
		
		// Prototype for SuggestNewerCIPolicy.java
		message = "Trace from Output (version 1.0, mutable) to Action.java (version 1) is being created. However, the configuration items below are newer than Action.java (version 1).";
		suggestions.add("Command.java (version 2)");
		
//		// Prototype for SuggestRelatedTracesPolicy.java
//		message = "Trace from State Modeling (version 0.0.0, mutable) to Icone.java (version 2)  has been created. However, architectural elements that have traces to Icone.java (version 2) usually have traces also to the following configuration items.";
//		suggestions.add("IAmbienteModelagem.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoesLexicas.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("TratadorNovaBase.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoes.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("Categoria.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoesLexicas.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoesLexicas.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoesLexicas.java (version 2) (support: 35%, confidence: 100%)");
//		suggestions.add("FabricaRepresentacoesLexicas.java (version 2) (support: 35%, confidence: 100%)");
		
		SuggestionDialog suggestionDialog = new SuggestionDialog(null);
		System.out.println(suggestionDialog.show(message, suggestions));
	}
}

class SuggestionListModel<E> extends AbstractListModel {

	/**
	 * The list of suggestions
	 */
	private List<E> suggestions;
	
	/**
	 * Creates the model based on a list
	 */
	public SuggestionListModel(List<E> suggestions) {
		this.suggestions = suggestions;
	}
	
	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
        return suggestions.size();
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public E getElementAt(int index) {
        return suggestions.get(index);
	}
}
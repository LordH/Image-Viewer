package se.viewer.image.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class InformationPanel extends JPanel {
	
	private static final long serialVersionUID = -5522680457220488022L;

	public InformationPanel(String client) {
		setLayout(new GridBagLayout());
		
		JLabel label = new JLabel("Text");
		
		GridBagConstraints c = new GridBagConstraints();
		
		add(label, c);
	}
	
}

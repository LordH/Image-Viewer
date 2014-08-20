package se.viewer.image.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import se.viewer.image.server.ClientConnection;

/**
 * Class for panel presenting a client's information
 * @author Harald Brege
 */
public class InformationPanel extends JPanel implements Observer {
	
	private static final long serialVersionUID = -5522680457220488022L;
	private ClientConnection client;
	private int index;

	private JLabel name;
	private JPanel log;
	private JPanel settings;
	
	public InformationPanel(int index, ClientConnection client) {
		this.client = client;
		this.client.addObserver(this);
		this.index = index;

		setLayout(new GridBagLayout());
		setupBasics();
		setupPreferences();
	}
	
	private void setupBasics() {
		GridBagConstraints c = new GridBagConstraints();
		
		//Setting up basic components
		name = new JLabel(client.getClientId());
		name.setFont(new Font("Arial", Font.BOLD, 18));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(10, 0, 0, 0);
		add(name, c);

		JLabel label = new JLabel("Message log:");
		label.setFont(new Font("Trebuchet MS", Font.BOLD + Font.ITALIC, 14));
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(0, 25, 5, 5);
		add(label, c);
		
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(10, 10, 10, 10);
		add(sep, c);
		
		label = new JLabel("Settings");
		label.setFont(new Font("Trebuchet MS", Font.BOLD + Font.ITALIC, 14));
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		c.gridheight = 1;
		c.ipadx = 200;
		c.weighty = 0;
		c.insets = new Insets(0, 25, 5, 5);
		add(label, c);
		
		log = new JPanel();
		log.setLayout(new GridBagLayout());
		JScrollPane scroll = new JScrollPane(log);
		scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.ipadx = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 10, 10, 0);
		add(scroll, c);
		
		settings = new JPanel();
		settings.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(0, 0, 10, 10);
		add(settings, c);
	}
	
	private void setupPreferences() {
		
	}
	
	//=======================================
	//	DISPLAY METHODS
	//---------------------------------------

	@Override
	public void update(Observable o, Object arg) {
		name.setText(client.getClientId());
		repaint();
	}
	
	public int getIndex() {
		return index;
	}
}

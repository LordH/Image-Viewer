package se.viewer.image.gui.components.login;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.gui.style.Style;

/**
 * Class that handles a panel with server selection for the login frame
 * @author Harald Brege
 */
public class ServerSelectPanel extends JPanel implements Observer {

	private static final long serialVersionUID = -6965406875825467992L;

	private JComboBox<String> serverField;
	private JLabel serverStatusLabel;
	
	public ServerSelectPanel() {
		ServerCommunicator.instance().addObserver(this);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//Server label and input field
		JLabel serverLabel = new JLabel();
		serverLabel.setFont(Style.FONT_SERVER);
		serverLabel.setText("Server IP");
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(10, 15, 0, 15);
		add(serverLabel, c);
		
		//TODO Get favorite servers from config class
		String[] servers = {"192.168.1.3", "127.0.0.1"};
		serverField = new JComboBox<String>();
		serverField.setFont(Style.FONT_TEXTFIELD);
		for(String server : servers)
			serverField.addItem(server);
		serverField.setEditable(true);
		serverField.addActionListener(new Listener());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1;
		c.insets = new Insets(10, 0, 0, 0);
		add(serverField, c);

		//Server status label
		serverStatusLabel = new JLabel();
		serverStatusLabel.setFont(Style.FONT_SERVER);
		serverStatusLabel.setForeground(Color.GRAY);
		serverStatusLabel.setText("W");
		c.fill = GridBagConstraints.NONE;
		c.gridx = 2;
		c.weightx = 0;
		c.insets = new Insets(10, 15, 0, 15);
		add(serverStatusLabel, c);

		//Box and label for adding favorite servers and
		JCheckBox favoriteServerBox = new JCheckBox();
		favoriteServerBox.setFont(Style.FONT_BUTTON);
		favoriteServerBox.setText("Add to favourites");
		favoriteServerBox.setSelected(false);
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 1;
		c.gridy = 3;
		c.insets = new Insets(5, 0, 0, 0);
		add(favoriteServerBox, c);
		
		serverField.getActionListeners()[0].actionPerformed(null);
	}	

	@Override
	public void update(Observable o, Object arg) {
		boolean success = (boolean) arg;
		if(success) {
			serverStatusLabel.setForeground(Color.GREEN);
			serverStatusLabel.setText("Y");
		}
		else {
			serverStatusLabel.setForeground(Color.RED);
			serverStatusLabel.setText("N");
		}
	}

	
	//=======================================
	//	INTERNAL CLASSES
	//---------------------------------------
	
	private class Listener implements ActionListener {

		private String current = "";
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String address = (String) serverField.getSelectedItem();
			
			if(isValid(address) && !address.equals(current)) {
				serverStatusLabel.setForeground(Color.GRAY);
				serverStatusLabel.setText("W");

				current = address;
				ServerCommunicator.instance().connect(address, 2106);
				return;
			}
			else if(!isValid(address) && !address.equals(current)) {
				current = address;
				serverStatusLabel.setForeground(Color.RED);
				serverStatusLabel.setText("N");
			}
		}
		
		private boolean isValid(String address) {
			return address.matches("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}"
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		}
	}
}

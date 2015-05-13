package se.viewer.image.gui.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import se.viewer.image.communication.ServerCommunicator;

/**
 * Class for displaying the login prompt when the application launches
 * @author Harald Brege
 */
public class LoginFrame1 extends JFrame implements LoginInterface {
	
	private static final long serialVersionUID = -3060670952519287670L;
	
	private JTextField userField;
	private JTextField passField;
	private JTextField serverField;
	private JLabel message;
	
	private Font fontField;
	private Font fontLabel;
	private Font fontButton;
	private Font fontMessage;
	
	/**
	 * Creates a new login frame
	 */
	public LoginFrame1() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLayout(new GridBagLayout());
		setMinimumSize(new Dimension(400, 300));
		setLocation((screen.width-getSize().width) / 2, (screen.height-getSize().height) / 2);
		setTitle("ImageViewer - Login");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		fontField = new Font("Trebuchet MS", Font.PLAIN, 14);
		fontLabel = new Font("Trebuchet MS", Font.BOLD, 14);
		fontButton = new Font("Trebuchet MS", Font.BOLD, 12);
		fontMessage = new Font("Trebuchet MS", Font.PLAIN, 12);
		
		//Setting up all components
		JLabel serverLabel = new JLabel();
		serverLabel.setFont(fontLabel);
		serverLabel.setText("Server IP address");
		
		serverField = new JTextField();
		serverField.setFont(fontField);
		serverField.setText("192.168.1.3");
		serverField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(0, 2, 0, 5)));
		
		JLabel messageLabel = new JLabel();
		messageLabel.setFont(fontLabel);
		messageLabel.setText("Server Message");
		
		message = new JLabel();
		message.setFont(fontMessage);
		
		//Adding components to the frame
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(15, 15, 0, 0);
		add(serverLabel, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1;
		c.insets = new Insets(15, 15, 0, 15);
		add(serverField, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 10, 0, 10);
		add(new JSeparator(SwingConstants.HORIZONTAL), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		add(getLoginPanel(), c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 10, 0, 10);
		add(new JSeparator(SwingConstants.HORIZONTAL), c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(10, 15, 0, 15);
		add(messageLabel, c);
		
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridy = 5;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		c.insets = new Insets(5, 15, 15, 15);
		add(message, c);
		
		pack();
	}
	
	private JPanel getLoginPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		//Setting up content
		JLabel userLabel = new JLabel();
		userLabel.setFont(fontLabel);
		userLabel.setText("User name");
		
		JLabel passLabel = new JLabel();
		passLabel.setFont(fontLabel);
		passLabel.setText("Password");
		
		userField = new JTextField();
		userField.setFont(fontField);
		userField.setText("User name");
		userField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(0, 2, 0, 5)));

		passField = new JPasswordField();
		passField.setFont(fontField);
		passField.setText("Password");
		passField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(0, 2, 0, 5)));
		
		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new loginListener());
		loginButton.setFont(fontButton);
		loginButton.setPreferredSize(new Dimension(90, 25));
		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new registerListener());
		registerButton.setFont(fontButton);
		registerButton.setPreferredSize(new Dimension(90, 25));
		
		//Adding content to panel
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(15, 15, 0, 0);
		panel.add(userLabel, c);

		c.gridy = 1;
		panel.add(passLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1;
		c.insets = new Insets(15, 15, 0, 15);
		panel.add(userField, c);
		
		c.gridy = 1;
		panel.add(passField, c);
		
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0.5;
		c.insets = new Insets(15, 15, 15, 15);
		panel.add(loginButton, c);
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		panel.add(registerButton, c);
		
		return panel;
	}
	
	//=======================================
	//	INTERNAL CLASSES
	//---------------------------------------

	@Override
	public void setMessage(String message) {
		this.message.setText(message);
	}
		
	//=======================================
	//	INTERNAL CLASSES
	//---------------------------------------

	private class loginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!ServerCommunicator.instance().isConnected()) {
				String host = serverField.getText();
				ServerCommunicator.instance().connect(host, 2106);
			}
			if(ServerCommunicator.instance().isConnected()) {
				String userName = userField.getText();
				String password = passField.getText();
				ServerCommunicator.instance().login(userName, password);
			}
		}
	}
	
	private class registerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!ServerCommunicator.instance().isConnected()) {
				String host = serverField.getText();
				ServerCommunicator.instance().connect(host, 2106);
			}
			if(ServerCommunicator.instance().isConnected()) {
				String userName = userField.getText();
				String password = passField.getText();
				ServerCommunicator.instance().registerUser(userName, password);
			}
		}
	}
}

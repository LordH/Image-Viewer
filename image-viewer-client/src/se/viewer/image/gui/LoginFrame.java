package se.viewer.image.gui;

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
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import se.viewer.image.launcher.ServerCommunicator;

/**
 * Class for displaying the login prompt when the application launches
 * @author Harald Brege
 */
public class LoginFrame extends JFrame implements LoginInterface {
	
	private static final long serialVersionUID = -3060670952519287670L;
	private JTextField userField;
	private JTextField passField;
	private JLabel message;
	
	/**
	 * Creates a new login frame
	 */
	public LoginFrame() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setVisible(true);
		setLayout(new GridBagLayout());
		setMinimumSize(new Dimension(400, 250));
		setLocation((screen.width-getSize().width) / 2, (screen.height-getSize().height) / 2);
		setTitle("ImageViewer - Login");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Font fontP = new Font("Trebuchet MS", Font.PLAIN, 14);
		Font fontB = new Font("Trebuchet MS", Font.BOLD, 14);
		Font fontM = new Font("Trebuchet MS", Font.BOLD + Font.ITALIC, 14);
		Font fontS = new Font("Trebuchet MS", Font.PLAIN, 12);
		
		//Setting up all components
		JLabel userLabel = new JLabel();
		userLabel.setFont(fontB);
		userLabel.setText("User name");
		
		JLabel passLabel = new JLabel();
		passLabel.setFont(fontB);
		passLabel.setText("Password");
		
		userField = new JTextField();
		userField.setText("User name");
		userField.setFont(fontP);
		userField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(0, 2, 0, 5)));

		passField = new JPasswordField();
		passField.setText("Password");
		passField.setFont(fontP);
		passField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GRAY), 
				BorderFactory.createEmptyBorder(0, 2, 0, 5)));

		JButton loginButton = new JButton("Login");
		loginButton.addActionListener(new loginListener());
		loginButton.setFont(fontB);
		loginButton.setPreferredSize(new Dimension(90, 25));
		
		JButton registerButton = new JButton("Register");
		registerButton.addActionListener(new registerListener());
		registerButton.setFont(fontB);
		registerButton.setPreferredSize(new Dimension(90, 25));
		
		JLabel messageLabel = new JLabel();
		messageLabel.setFont(fontM);
		messageLabel.setText("Server Message");
		
		message = new JLabel();
		message.setFont(fontS);
		
		//Adding components to the frame
		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.insets = new Insets(15, 15, 0, 0);
		add(userLabel, c);

		c.gridy = 1;
		add(passLabel, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.weightx = 1;
		c.insets = new Insets(15, 15, 0, 15);
		add(userField, c);
		
		c.gridy = 1;
		add(passField, c);

		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		c.weightx = 0.5;
		add(loginButton, c);
		
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 2;
		add(registerButton, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(15, 10, 0, 10);
		add(new JSeparator(SwingConstants.HORIZONTAL), c);
		
		c.gridx = 0;
		c.gridy = 4;
		c.weightx = 1;
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
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.LoginInterface#setMessage(java.lang.String)
	 */
	@Override
	public void setMessage(String message) {
		this.message.setText(message);
	}
	
	/* (non-Javadoc)
	 * @see se.viewer.image.gui.LoginInterface#success()
	 */
	@Override
	public void success() {
		setVisible(false);
	}
	
	//=======================================
	//	INTERNAL CLASSES
	//---------------------------------------

	private class loginListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String userName = userField.getText();
			String password = passField.getText();
			
			ServerCommunicator.instance().login(userName, password);
		}
	}
	
	private class registerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String userName = userField.getText();
			String password = passField.getText();
			
			ServerCommunicator.instance().registerUser(userName, password);
		}
	}
}

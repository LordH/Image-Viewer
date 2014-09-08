package se.viewer.image.gui.login;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * Improved implementation of the login frame interface.
 * Handles logging in to servers and registering new users.
 * @author Harald Brege
 */
public class LoginFrame2 extends JFrame implements LoginInterface {
	
	private static final long serialVersionUID = 6273156355241808454L;
	
	private JPanel cardPanel;
	private CardLayout card;
	
	public LoginFrame2() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setMinimumSize(new Dimension(300, 520));
		setLocation((screen.width-getSize().width) / 2, (screen.height-getSize().height) / 2);
		setTitle("ImageViewer - Login");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Container content = getContentPane();
		content.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//Setting up and adding application logo
		JPanel logoPanel = new JPanel();
		logoPanel.setPreferredSize(new Dimension(200, 150));
		logoPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.insets = new Insets(20, 0, 20, 0);
		content.add(logoPanel, c);

		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 10, 0, 10);
		content.add(sep, c);
		
		//Setting up server panel
		ServerSelectPanel server = new ServerSelectPanel();
		c.fill = GridBagConstraints.BOTH;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 0);
		content.add(server, c);
		
		sep = new JSeparator(SwingConstants.HORIZONTAL);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = 3;
		c.insets = new Insets(10, 10, 0, 10);
		content.add(sep, c);
		
		//Creating card panel
		card = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(card);
		cardPanel.add(new LoginPanel(this), "LOGIN");
		cardPanel.add(new RegisterPanel(this), "REGISTER");
		c.gridy = 4;
		c.weighty = 1;
		c.insets = new Insets(0, 0, 0, 0);
		content.add(cardPanel, c);
		
		//Setting up display
		card.show(cardPanel, "LOGIN");
		
	}
	
	//=======================================
	//	VIEWER MODE
	//---------------------------------------

	@Override
	public void setMessage(String message) {
		
	}

	/**
	 * Sets the frame to show the login dialogs
	 */
	protected void loginMode() {
		card.show(cardPanel, "LOGIN");
		setTitle("ImageViewer - Login");
	}
	
	/**
	 * Sets the frame to show the register user dialogs
	 */
	protected void registerMode() {
		card.show(cardPanel, "REGISTER");
		setTitle("ImageViewer - Register");
	}
}
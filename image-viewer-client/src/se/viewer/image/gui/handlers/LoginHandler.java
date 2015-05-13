package se.viewer.image.gui.handlers;

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

import se.viewer.image.gui.components.login.LoginPanel;
import se.viewer.image.gui.components.login.RegisterPanel;
import se.viewer.image.gui.components.login.ServerSelectPanel;

/**
 * As an implementation of the LoginFrameInterface, this class acts
 * as a middle layer between the GUI components and the application in general.
 * @author Harald Brege
 */
public class LoginHandler implements LoginHandlerInterface{

	private JFrame frame = null;
	private JPanel cardPanel;
	private CardLayout card;
	
	@Override
	public boolean setupFrame() {		
		frame = new JFrame();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setMinimumSize(new Dimension(300, 520));
		frame.setLocation((screen.width-frame.getSize().width) / 2, 
				(screen.height-frame.getSize().height) / 2);
		frame.setTitle("ImageViewer - Login");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Container content = frame.getContentPane();
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
		frame.pack();
		frame.setVisible(true);
		
		return true;
	}

	@Override
	public boolean removeFrame() {
		frame.dispose();
		frame = null;
		return true;
	}
	
	@Override
	public boolean setMessage(String message) {
		System.out.println(message);
		return true;
	}
	/**
	 * Sets the frame to show the login dialogs
	 */
	public void loginMode() {
		card.show(cardPanel, "LOGIN");
		frame.setTitle("ImageViewer - Login");
	}
	
	/**
	 * Sets the frame to show the register user dialogs
	 */
	public void registerMode() {
		card.show(cardPanel, "REGISTER");
		frame.setTitle("ImageViewer - Register");
	}
}

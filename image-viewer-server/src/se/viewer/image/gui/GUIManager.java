package se.viewer.image.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.server.Server;

/**
 * Main class for managing the GUI of the server application
 * @author Harald Brege
 */
public class GUIManager implements GUIManagerInterface {
	
	private static GUIManagerInterface instance;
	
	private ClientList clients;
	
	private JFrame frame;
	private JPanel connectionPanel;
	private JPanel cardPanel;
	
	private CardLayout card;
	private InformationPanel currentInfo;
	
	private GUIManager() {
		clients = new ClientList();
	}

	/**
	 * Called to get the singleton instance of the class
	 * @return The instance
	 */
	protected static GUIManagerInterface instance() {
		if(instance == null) {
			instance = new GUIManager();
			instance.initialise();
		}
		return instance;
	}
	
	//=======================================
	//	INITIALISATION
	//---------------------------------------
	
	@Override
	public void initialise() {
		//Setting up the frame
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new ShutdownListener());
		frame.setTitle("Image Viewer - Server");
		frame.setMinimumSize(new Dimension(1000, 800));
		
		// Setting up menu bar and menus
		JMenuBar menubar = new JMenuBar();
		frame.setJMenuBar(menubar);
		
		JMenu menu = new JMenu("System");
		menu.setMnemonic(KeyEvent.VK_S);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menubar.add(menu);
		
		JMenuItem exitbutton = new JMenuItem();
		exitbutton.setText("Exit");
		exitbutton.setMnemonic(KeyEvent.VK_E);
		exitbutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Server.instance().serverShutdown();
			}
		});
		menu.add(exitbutton);
		
		//Set up the top tier content and add to the frame
		frame.getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		connectionPanel = new JPanel();
		connectionPanel.setLayout(new GridBagLayout());
		JScrollPane scroll = new JScrollPane(connectionPanel);
		scroll.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		c.weighty = 1;
		c.insets = new Insets(10, 10, 10, 10);
		frame.getContentPane().add(scroll, c);
		
		JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.insets = new Insets(10, 0, 10, 0);
		frame.getContentPane().add(sep, c);
		
		card = new CardLayout();
		cardPanel = new JPanel();
		cardPanel.setLayout(card);
		cardPanel.add(new JPanel(), "empty");
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 2;
		c.weightx = 1;
		c.insets = new Insets(0, 0, 0, 0);
		frame.getContentPane().add(cardPanel, c);
		
		//Setting up connections panel
		c = new GridBagConstraints();
		JLabel title = new JLabel("Connections");
		title.setName("title");
		title.setFont(new Font("Trebuchet MS", Font.BOLD, 18));
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		c.ipadx = 100;
		c.insets = new Insets(10, 10, 10, 10);
		connectionPanel.add(title, c);
		
		JPanel fill = new JPanel();
		fill.setName("fill");
		c.gridy = 1;
		c.weighty = 1;
		c.ipadx = 0;
		c.insets = new Insets(0, 0, 0, 0);
		connectionPanel.add(fill, c);
		
		//Finalising 
		frame.pack();
		frame.setVisible(true);
		card.show(cardPanel, "empty");
	}
	
	//=======================================
	//	INFORMATION DISPLAY METHODS
	//---------------------------------------
	
	@Override
	public void add(ClientConnection client) {
		clients.add(client);
		cardPanel.add(clients.getPanel(client), clients.indexOf(client));
		updateList();
	}

	@Override
	public void remove(ClientConnection client) {
		if(currentInfo == clients.getPanel(client)) {
			card.show(cardPanel, "empty");
			currentInfo = null;
		}

		cardPanel.remove(clients.getPanel(client));
		clients.remove(client);
		updateList();
	}
	
	@Override
	public void setFocused(ClientConnection client) {
		currentInfo = clients.getPanel(client);
		card.show(cardPanel, clients.indexOf(client));
	}
	
	//=======================================
	//	INTERNAL METHODS
	//---------------------------------------
	
	/**
	 * Called to set up the list of current client connections
	 */
	private void updateList() {
		for(Component comp : connectionPanel.getComponents()) {
			String name = comp.getName();
			if(name != "title")
				connectionPanel.remove(comp);
		}
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.insets = new Insets(0, 10, 5, 0);
		
		int i=1;
		for(ClientLabel label : clients.getLabels()) {
			c.gridy = i++;
			connectionPanel.add(label, c);
		}
		
		JPanel fill = new JPanel();
		fill.setName("fill");
		
		c.gridy = clients.size()+2;
		c.weighty = 1;
		connectionPanel.add(fill, c);
		
		frame.validate();
		frame.repaint();
	}
	
	//=======================================
	//	LISTENERS
	//---------------------------------------

	/**
	 * Internal class that handles window closing properly
	 * @author Harald Brege
	 */
	private class ShutdownListener implements WindowListener {

		@Override
		public void windowClosed(WindowEvent e) {
			Server.instance().serverShutdown();
		}

		@Override
		public void windowActivated(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {}

		@Override
		public void windowDeactivated(WindowEvent e) {}

		@Override
		public void windowDeiconified(WindowEvent e) {}

		@Override
		public void windowIconified(WindowEvent e) {}

		@Override
		public void windowOpened(WindowEvent e) {}
	}
}

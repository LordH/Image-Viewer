package se.viewer.image.gui;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import se.viewer.image.server.ClientConnection;
import se.viewer.image.tokens.Messages;

/**
 * Class for handling the client name labels in the server application
 * @author Harald Brege
 */
public class ClientLabel extends JLabel implements Observer {
	
	private static final long serialVersionUID = 2894670788615269299L;
	private ClientConnection client;
	private int id;

	public ClientLabel(int id, ClientConnection client) {
		this.client = client;
		this.client.addObserver(this);
		this.id = id;
		
		addMouseListener(new ClientLabelMouseListener());
		setText(this.client.getClientId());
	}
	
	//=======================================
	//	DISPLAY METHODS
	//---------------------------------------

	/**
	 * Called to get the id number of this label
	 * @return The id number of this label
	 */
	public int getID() {
		return id;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		int message = (int) arg;
		
		if(message == Messages.LOGIN_SUCCESS) 
			setText(client.getClientId());
		else if(message == Messages.DISCONNECTED)
			GUIManager.instance().remove(client);
	}
	
	//=======================================
	//	DISPLAY METHODS
	//---------------------------------------
	
	/**
	 * Internal listener class that handles mouse events
	 * @author Harald Brege
	 */
	private class ClientLabelMouseListener implements MouseListener {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			GUIManager.instance().setFocused(client);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setForeground(Color.BLUE);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setForeground(Color.BLACK);
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
}

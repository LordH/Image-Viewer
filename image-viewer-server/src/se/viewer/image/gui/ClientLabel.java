package se.viewer.image.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;

import se.viewer.image.server.ClientConnection;

public class ClientLabel extends JLabel implements Observer {
	
	private static final long serialVersionUID = 2894670788615269299L;

	public ClientLabel(ClientConnection client) {
		addMouseListener(new ClientLabelMouseListener());
		
		setText(client.getClientId());
	}
	
	@Override
	public void update(Observable o, Object arg) {
		ClientConnection client = (ClientConnection) o;
		setText(client.getClientId());
		repaint();
	}
	
	private class ClientLabelMouseListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
		
	}
}

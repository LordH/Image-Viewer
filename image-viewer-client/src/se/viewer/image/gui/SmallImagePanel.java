package se.viewer.image.gui;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.viewer.image.launcher.Client;

public class SmallImagePanel extends JPanel {
	
	private static final long serialVersionUID = -8246229816617048252L;
	private String name;
	private BufferedImage image;
	
	public SmallImagePanel(String name, BufferedImage image) {
		this.name = name;
		this.image = image;
		setPreferredSize(new Dimension(200, 200));
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				Client.instance().getImage(getName());
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int x = (int)((200-image.getWidth())/2.0);
		int y = (int)((200-image.getHeight())/2.0);
		g.drawImage(image, x, y, null); 	
	}
}
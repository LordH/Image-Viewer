package se.viewer.image.gui.handlers;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import se.viewer.image.communication.ServerCommunicator;
import se.viewer.image.gui.components.viewer.ViewerInterface;
import se.viewer.image.gui.components.viewer.ViewerPanel;

/**
 * Class for handling the GUI components for the Image Viewer client
 * @author Harald Brege
 */
public class ViewerHandler implements ViewerHandlerInterface {
	
	private JFrame frame;
	
	/**
	 * Creates a new GUI handler 
	 */
	public ViewerHandler() {
		//Initializing all objects needed to construct frame
		frame = new JFrame();
		
		Toolkit tool = Toolkit.getDefaultToolkit();
		Dimension screen = tool.getScreenSize();
		Dimension window = new Dimension(screen.width, screen.height-47);
		Point location = new Point((int)(screen.getWidth()-window.getWidth())/2, 0);
		WindowListener winListener = new ClientWindowListener();
		
		//Setting basic properties of the frame
		frame.setLocation(location);
		frame.setMinimumSize(new Dimension(800, 600));
		frame.addWindowListener(winListener);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("Image Viewer");
		
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
				ServerCommunicator.instance().disconnect();
				System.out.println("Client shutting down");
				System.exit(0);
			}
		});
		menu.add(exitbutton);
		
		System.out.println("Client frame created.");
	}

	@Override
	public ViewerInterface viewerMode() {
		ViewerPanel imagePanel = new ViewerPanel();
		
		frame.getContentPane().removeAll();
		frame.getContentPane().add(imagePanel);
		frame.pack();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		System.out.println("Content added.");
		
		return imagePanel;
	}
	
	@Override
	public void setTitle(String title) {
		frame.setTitle(title);
	}
	
	//=======================================
	//	LISTENERS
	//---------------------------------------

	/**
	 * Window listener to handle window operations, such as shutting down
	 * @author Harald Brege
	 */
	private class ClientWindowListener implements WindowListener {

		@Override
		public void windowClosed(WindowEvent e) {}

		@Override
		public void windowClosing(WindowEvent e) {
			ServerCommunicator.instance().disconnect();
			System.out.println("Client shutting down");
			System.exit(0);
		}

		@Override
		public void windowActivated(WindowEvent e) {}

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

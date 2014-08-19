package se.viewer.image.gui;

import javax.swing.JFrame;

/**
 * An interface for creating factories to make frames for the application
 * 
 * @author Harald Brege
 */
public interface GUIFactoryInterface {

	public JFrame getFrame();

	public ApplicationPanel setViewerMode(JFrame frame);

	public LoginFrame getLoginFrame();
	
}

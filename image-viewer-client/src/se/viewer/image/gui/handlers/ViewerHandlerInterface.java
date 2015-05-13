package se.viewer.image.gui.handlers;

import se.viewer.image.gui.components.viewer.ViewerInterface;


/**
 * An interface for classes handling the GUI
 * @author Harald Brege
 */
public interface ViewerHandlerInterface {

	/**
	 * Called to create the frame for the main application
	 * @return Success or failure of operation
	 */
	public boolean setupFrame();
	
	public boolean setupPanel();
	
	/**
	 * Called to change the title of the main frame
	 * @param title The new title
	 */
	public void setTitle(String title);
	
	public ViewerInterface getPanel();
}

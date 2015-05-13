package se.viewer.image.gui.handlers;

import se.viewer.image.gui.components.viewer.ViewerInterface;

/**
 * An interface for classes handling the GUI
 * @author Harald Brege
 */
public interface ViewerHandlerInterface {

	/**
	 * Called to set the GUI into image viewing mode
	 * @return The main panel to display all relevant content
	 */
	public abstract ViewerInterface viewerMode();
	
	/**
	 * Called to change the title of the main frame
	 * @param title The new title
	 */
	public abstract void setTitle(String title);
}

package se.viewer.image.gui;


/**
 * An interface for classes handling the GUI
 * @author Harald Brege
 */
public interface GUIHandlerInterface {

	//=======================================
	//	LOGIN MODE
	//---------------------------------------
	
	/**
	 * Called to set the GUI to login mode
	 */
	public abstract void loginMode();
	
	/**
	 * Called to set the message displayed in the login frame
	 * @param message The message to be displayed
	 */
	public abstract void loginMessage(String message);
	
	/**
	 * Called to inform the login frame of login success
	 */
	public abstract void loginSuccessful();
	
	//=======================================
	//	VIEWING MODE
	//---------------------------------------

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
